package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.stuff.TimeMeasureYio;
import yio.tro.antiyoy.stuff.Yio;
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
            if (activeHex.colorIndex != 0) continue;
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
            activeHex.colorIndex = FieldController.NEUTRAL_LANDS_INDEX;
        }

        flagNeutrals = true;
    }


    private void spawnProvinces() {
        for (int i = 0; i < numberOfProvincesByLevelSize(); i++) {
            for (int colorIndex = 0; colorIndex < GameRules.colorNumber; colorIndex++) {
                Hex hex = findGoodPlaceForNewProvince();
                if (hex == null) continue;

                hex.setColorIndex(colorIndex);
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
                adjHex.colorIndex = activeHex.colorIndex;
                c--;
                if (c == 0) break;
            }
        }
    }


    @Override
    protected void applyBalanceMeasures() {
        applyChanges(getChangesArray());
    }


    private double[] getChangesArray() {
        switch (GameRules.colorNumber) {
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
        double adv_delta = adv_start / GameRules.colorNumber;
        adv_delta /= 2;
        adv_delta += 0.01;
        double dis_start = 0.25;
        double dis_delta = 0.15;

        double adv = adv_start;
        for (int colorIndex = GameRules.colorNumber - 1; colorIndex >= 0; colorIndex--) {
            giveAdvantageToPlayer(colorIndex, adv);
            adv -= adv_delta;
            if (adv <= 0) break;
        }

        double dis = dis_start;
        for (int colorIndex = 0; colorIndex < GameRules.colorNumber; colorIndex++) {
            giveDisadvantageToPlayer(colorIndex, dis);
            dis += dis_delta;
            if (colorIndex > GameRules.colorNumber / 3) break;
        }
    }


    private void oldBalanceMeasures() {
        switch (gameController.levelSizeManager.levelSize) {
            default:
            case LevelSize.MEDIUM:
                giveAdvantageToPlayer(GameRules.colorNumber - 1, 0.28); // last
                giveAdvantageToPlayer(GameRules.colorNumber - 2, 0.15);

                if (GameRules.colorNumber >= 5) {
                    giveAdvantageToPlayer(GameRules.colorNumber - 3, 0.07);
                } else { // if color number == 4
                    giveAdvantageToPlayer(1, 0.03);
                    giveAdvantageToPlayer(GameRules.colorNumber - 1, 0.05);
                }

                giveDisadvantageToPlayer(0, 0.17);
                giveDisadvantageToPlayer(1, 0.1);
                break;
            case LevelSize.BIG:
            case LevelSize.HUGE:
                giveAdvantageToPlayer(GameRules.colorNumber - 1, 0.35); // last
                giveAdvantageToPlayer(GameRules.colorNumber - 2, 0.2);

                if (GameRules.colorNumber >= 5) {
                    giveAdvantageToPlayer(GameRules.colorNumber - 3, 0.04);
                } else { // if color number == 4
                    giveAdvantageToPlayer(1, 0.03);
                    giveAdvantageToPlayer(GameRules.colorNumber - 1, 0.05);
                }

                giveDisadvantageToPlayer(0, 0.07);
                giveDisadvantageToPlayer(1, 0.17);
                break;
        }
    }


    @Override
    protected void decreaseProvince(ArrayList<Hex> provinceList, double power) {
        int num = (int) (power * provinceList.size());
        for (int i = 0; i < num; i++) {
            Hex hex = findHexToExcludeFromProvince(provinceList);
            provinceList.remove(hex);
            hex.colorIndex = FieldController.NEUTRAL_LANDS_INDEX;
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
            hex.colorIndex = spawnHex.colorIndex;
            if (hex.genPotential == 0) continue;
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (!propagationList.contains(adjHex) && adjHex.active && adjHex.colorIndex == FieldController.NEUTRAL_LANDS_INDEX) {
                    adjHex.genPotential = hex.genPotential - 1;
                    propagationList.add(adjHex);
                }
            }
        }
    }


    @Override
    protected void reduceProvinceSize(ArrayList<Hex> provinceList) {
        int provinceColor = provinceList.get(0).colorIndex;
        while (provinceList.size() > SMALL_PROVINCE_SIZE) {
            Hex hex = findHexToExcludeFromProvince(provinceList);
//            System.out.println("removed: " + hex);
            provinceList.remove(hex);
            hex.colorIndex = FieldController.NEUTRAL_LANDS_INDEX;
        }
    }


    @Override
    protected boolean activateHex(Hex hex, int color) {
        if (hex.active) return false;
        hex.active = true;
        hex.setColorIndex(FieldController.NEUTRAL_LANDS_INDEX);
        ListIterator activeIterator = getActiveHexes().listIterator();
        activeIterator.add(hex);
        return true;
    }


    double distanceToClosestProvince(Hex hex) {
        double minDistance = -1, currentDistance;

        for (Hex activeHex : getActiveHexes()) {
            if (activeHex.isNeutral()) continue;
            currentDistance = Yio.distance(hex.index1, hex.index2, activeHex.index1, activeHex.index2);
            if (minDistance == -1 || currentDistance < minDistance) {
                minDistance = currentDistance;
            }
        }

        return minDistance;
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


    private Hex oldMethodToFindGoodPlaceForNewProvince() {
        // this method is really slow

        double maxDistance = 0, currentDistance;
        Hex bestHex = null;

        for (Hex activeHex : getActiveHexes()) {
            if (!activeHex.isNeutral()) continue;
            currentDistance = distanceToClosestProvince(activeHex);
            if (bestHex == null || currentDistance > maxDistance) {
                bestHex = activeHex;
                maxDistance = currentDistance;
            }
        }

        return bestHex;
    }


    private ArrayList<Hex> getActiveHexes() {
        return gameController.fieldController.activeHexes;
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


    protected int numberOfProvincesByLevelSize() {
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
