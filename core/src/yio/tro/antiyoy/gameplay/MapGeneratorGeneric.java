package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

public class MapGeneratorGeneric extends MapGenerator {

    boolean flagNeutrals;


    public MapGeneratorGeneric(GameController gameController) {
        super(gameController);
    }


    @Override
    public void generateMap(Random random, Hex[][] field) {
        setValues(random, field);

        beginGeneration();

        createLand();
        addTrees();

        while (!hasGreenProvince()) {
            genericBalance();
        }

        endGeneration();
    }


    // not actually needed right now
    private boolean hasGreenProvince() {
        for (Hex activeHex : getActiveHexes()) {
            if (activeHex.fraction != 0) continue;
            if (activeHex.numberOfFriendlyHexesNearby() < 2) continue;

            return true;
        }

        return false;
    }


    private void genericBalance() {
        makeAllActiveHexesNeutral();
        spawnProvinces();
        cutProvincesToSmallSizes();
        makeSingleHexesIntoProvinces();
        applyBalanceMeasures();
    }


    private void makeAllActiveHexesNeutral() {
        for (Hex activeHex : getActiveHexes()) {
            activeHex.fraction = GameRules.NEUTRAL_FRACTION;
        }

        flagNeutrals = true;
    }


    private void spawnProvinces() {
        int quantity = getProvincesQuantity();
        for (int i = 0; i < quantity; i++) {
            for (int fraction = 0; fraction < GameRules.fractionsQuantity; fraction++) {
                Hex hex = findGoodPlaceForNewProvince();
                if (hex == null) continue;

                hex.setFraction(fraction);
                spawnProvince(hex, 2);
            }
        }
    }


    private void makeSingleHexesIntoProvinces() {
        for (Hex activeHex : getActiveHexes()) {
            if (activeHex.isNeutral()) continue;
            if (activeHex.numberOfFriendlyHexesNearby() > 0) continue;
            int c = 3;
            for (int i = 0; i < 6; i++) {
                Hex adjHex = activeHex.getAdjacentHex(i);
                if (!adjHex.active || !adjHex.isNeutral()) continue;
                adjHex.fraction = activeHex.fraction;
                c--;
                if (c == 0) break;
            }
        }
    }


    @Override
    protected void applyBalanceMeasures() {
        double[] changesArray = getChangesArray();
        if (changesArray == null) return;
        applyChanges(changesArray);
    }


    private double[] getChangesArray() {
        switch (GameRules.fractionsQuantity) {
            default:
                return null;
            case 2:
                return new double[]{-0.4, 0.3};
            case 3:
                return new double[]{-0.5, 0.2, 0.4};
            case 4:
                return new double[]{-0.6, 0.12, 0.3, 0.6};
            case 5:
                return new double[]{-0.5, -0.35, 0.15, 0.32, 0.5};
            case 6:
                return new double[]{-0.6, -0.75, 0.15, 0.25, 0.4, 0.6};
            case 7:
                return new double[]{-0.4, -0.9, 0.2, 0.3, 0.45, 0.6, 0.8};
        }
    }


    private void applyChanges(double[] changes) {
        for (int i = 0; i < changes.length; i++) {
            if (changes[i] == 0) continue;

            if (changes[i] > 0) {
                giveAdvantageToPlayer(i, changes[i]);
            } else {
                giveDisadvantageToPlayer(i, changes[i]);
            }
        }
    }


    private void newButBadBalanceMeasures() {
        double adv_start = 0.3;
        double adv_delta = adv_start / GameRules.fractionsQuantity;
        adv_delta /= 2;
        adv_delta += 0.01;
        double dis_start = 0.25;
        double dis_delta = 0.15;

        double adv = adv_start;
        for (int fraction = GameRules.fractionsQuantity - 1; fraction >= 0; fraction--) {
            giveAdvantageToPlayer(fraction, adv);
            adv -= adv_delta;
            if (adv <= 0) break;
        }

        double dis = dis_start;
        for (int fraction = 0; fraction < GameRules.fractionsQuantity; fraction++) {
            giveDisadvantageToPlayer(fraction, dis);
            dis += dis_delta;
            if (fraction > GameRules.fractionsQuantity / 3) break;
        }
    }


    @Override
    protected void decreaseProvince(ArrayList<Hex> provinceList, double power) {
        int num = (int) (power * provinceList.size());
        for (int i = 0; i < num; i++) {
            Hex hex = findHexToExcludeFromProvince(provinceList);
            provinceList.remove(hex);
            hex.fraction = GameRules.NEUTRAL_FRACTION;
        }
    }


    @Override
    protected void spawnProvince(Hex spawnHex, int startingPotential) {
        spawnHex.genPotential = startingPotential;
        ArrayList<Hex> propagationList = new ArrayList<Hex>();
        propagationList.add(spawnHex);
        while (propagationList.size() > 0) {
            Hex hex = propagationList.get(0);
            propagationList.remove(0);
            if (random.nextInt(startingPotential) > hex.genPotential) continue;
            hex.fraction = spawnHex.fraction;
            if (hex.genPotential == 0) continue;
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (!propagationList.contains(adjHex) && adjHex.active && adjHex.fraction == GameRules.NEUTRAL_FRACTION) {
                    adjHex.genPotential = hex.genPotential - 1;
                    propagationList.add(adjHex);
                }
            }
        }
    }


    @Override
    protected void reduceProvinceSize(ArrayList<Hex> provinceList) {
        while (provinceList.size() > SMALL_PROVINCE_SIZE) {
            Hex hex = findHexToExcludeFromProvince(provinceList);
            provinceList.remove(hex);
            hex.fraction = GameRules.NEUTRAL_FRACTION;
        }
    }


    @Override
    protected boolean activateHex(Hex hex, int fraction) {
        if (hex.active) return false;
        hex.active = true;
        hex.setFraction(GameRules.NEUTRAL_FRACTION);
        ListIterator activeIterator = getActiveHexes().listIterator();
        activeIterator.add(hex);
        return true;
    }


    Hex findGoodPlaceForNewProvince() {
        if (allHexesAreNeutral()) {
            return getRandomFreeHex();
        }

        prepareHexesToFindNewProvincePlace();

        boolean expanded;
        int step = 0;
        while (true) {
            expanded = false;
            for (Hex hex : getActiveHexes()) {
                if (hex.moveZoneNumber != step) continue;
                for (int dir = 0; dir < 6; dir++) {
                    Hex adj = hex.getAdjacentHex(dir);
                    if (adj == null) continue;
                    if (adj.isNullHex()) continue;
                    if (!adj.active) continue;
                    if (adj.moveZoneNumber != -1) continue;

                    adj.moveZoneNumber = step + 1;
                    expanded = true;
                }
            }

            if (!expanded) break;
            step++;
        }

        return findHexWithHighestMoveZoneNumber();
    }


    private Hex findHexWithHighestMoveZoneNumber() {
        Hex result = null;

        for (Hex hex : getActiveHexes()) {
            if (result == null || hex.moveZoneNumber > result.moveZoneNumber) {
                result = hex;
            }
        }

        return result;
    }


    private void prepareHexesToFindNewProvincePlace() {
        for (Hex hex : getActiveHexes()) {
            if (hex.isNeutral()) {
                hex.moveZoneNumber = -1;
                continue;
            }

            hex.moveZoneNumber = 0;
        }
    }


    private ArrayList<Hex> getActiveHexes() {
        return gameController.fieldManager.activeHexes;
    }


    private boolean allHexesAreNeutral() {
        if (!flagNeutrals) return false;

        for (Hex activeHex : getActiveHexes()) {
            if (activeHex.isNeutral()) continue;

            flagNeutrals = false;
            return false;
        }

        return true;
    }


    protected Hex getRandomFreeHex() {
        return getActiveHexes().get(random.nextInt(getActiveHexes().size()));
    }


    protected int getProvincesQuantity() {
        if (GameRules.genProvinces > 0) return GameRules.genProvinces;

        switch (gameController.levelSizeManager.levelSize) {
            default:
            case LevelSize.SMALL:
                return 1;
            case LevelSize.MEDIUM:
                return 2;
            case LevelSize.BIG:
                return 3;
            case LevelSize.HUGE:
                return 4;
        }
    }
}
