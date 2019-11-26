package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.Unit;
import yio.tro.antiyoy.stuff.PointYio;

import java.util.ArrayList;


public class AiExpertSlayRules extends ArtificialIntelligence {

    private final Hex tempHex;
    private ArrayList<Hex> hexesInPerimeter;


    public AiExpertSlayRules(GameController gameController, int fraction) {
        super(gameController, fraction);
        tempHex = new Hex(0, 0, new PointYio(), gameController.fieldManager);
        hexesInPerimeter = new ArrayList<Hex>();
    }


    @Override
    public void makeMove() {
        moveUnits();

        spendMoneyAndMergeUnits();

        moveAfkUnits();
    }


    @Override
    void decideAboutUnit(Unit unit, ArrayList<Hex> moveZone, Province province) {
        // cleaning palms has highest priority
        if (unit.strength <= 2 && checkToCleanSomePalms(unit, moveZone, province)) return;

        ArrayList<Hex> attackableHexes = findAttackableHexes(unit.getFraction(), moveZone);
        if (attackableHexes.size() > 0) { // attack something
            tryToAttackSomething(unit, province, attackableHexes);
        } else { // nothing to attack
            boolean cleanedTrees = checkToCleanSomeTrees(unit, moveZone, province);
            if (!cleanedTrees) {
                if (unit.currentHex.isInPerimeter()) {
                    pushUnitToBetterDefense(unit, province);
                }
            }
        }
    }


    protected boolean isHexDefendedBySomethingElse(Hex hex, Unit unit) {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && adjHex.sameFraction(hex)) {
                if (adjHex.containsBuilding()) return true;
                if (adjHex.containsUnit() && adjHex.unit != unit) return true;
            }
        }
        return false;
    }


    protected boolean unitCanMoveSafely(Unit unit) {
        int leftBehindNumber = 0;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = unit.currentHex.getAdjacentHex(i);
            if (    adjHex.active &&
                    adjHex.sameFraction(unit.currentHex) &&
                    !isHexDefendedBySomethingElse(adjHex, unit) &&
                    adjHex.isInPerimeter())
                leftBehindNumber++;
        }
        return leftBehindNumber <= 3;
    }


    protected void tryToAttackSomething(Unit unit, Province province, ArrayList<Hex> attackableHexes) {
        if (!unitCanMoveSafely(unit)) return;
        Hex mostAttackableHex = findMostAttractiveHex(attackableHexes, province, unit.strength);
        gameController.moveUnit(unit, mostAttackableHex, province);
    }


    boolean hexHasFriendlyBuildingNearby(Hex hex) {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && adjHex.sameFraction(hex) && adjHex.containsBuilding()) return true;
        }
        return false;
    }


    @Override
    Hex findMostAttractiveHex(ArrayList<Hex> attackableHexes, Province province, int strength) {
        if (strength == 3 || strength == 4) {
            Hex hex = findHexAttractiveToBaron(attackableHexes, strength);
            if (hex != null) return hex;
        }

        // special fix for expert difficulty
//        if (province.hexList.size() < 20) {
//            for (Hex attackableHex : attackableHexes) {
//                if (hexHasFriendlyBuildingNearby(attackableHex)) {
//                    return attackableHex;
//                }
//            }
//        }

        Hex result = null;
        int currMax = -1;
        for (Hex attackableHex : attackableHexes) {
            int currNum = getAttackAllure(attackableHex, province.getFraction());
            if (currNum > currMax) {
                currMax = currNum;
                result = attackableHex;
            }
        }
        return result;
    }


    private Hex findRandomHexInPerimeter(Province province) {
        hexesInPerimeter.clear();
        for (Hex hex : province.hexList) {
            if (hex.isInPerimeter()) hexesInPerimeter.add(hex);
        }
        if (hexesInPerimeter.size() == 0) return null;
        return hexesInPerimeter.get(random.nextInt(hexesInPerimeter.size()));
    }


    @Override
    void moveAfkUnit(Province province, Unit unit) {
        Hex hexToMove = findRandomHexInPerimeter(province);
        if (hexToMove == null) return;
        tempHex.set(unit.currentHex);
        gameController.fieldManager.massMarchManager.performForSingleUnit(unit, hexToMove);
        if (tempHex.equals(unit.currentHex)) super.moveAfkUnit(province, unit); // to prevent infinite loop
    }


    private boolean provinceHasEnoughIncomeForUnit(Province province, int strength) {
//        int newIncome = province.getIncome() - province.getTaxes() - Unit.getTax(strength);
//        if (newIncome >= -2) return true;
//        return false;
        return province.canAiAffordUnit(strength);
    }


    @Override
    void tryToBuildUnits(Province province) {
        tryToBuildUnitsOnPalms(province);

        for (int i = 1; i <= 4; i++) {
            if (!provinceHasEnoughIncomeForUnit(province, i)) break;
            boolean successfullyAttacked = false;
            if (canProvinceBuildUnit(province, i)) {
                successfullyAttacked = tryToAttackWithStrength(province, i);
            }
            if (successfullyAttacked) i = 0;
        }

        // this is to kick start province
        if (canProvinceBuildUnit(province, 1) && howManyUnitsInProvince(province) <= 1)
            tryToAttackWithStrength(province, 1);
    }


    @Override
    boolean needTowerOnHex(Hex hex) {
        if (!hex.active) return false;
        if (!hex.isFree()) return false;

        updateNearbyProvinces(hex);
        if (nearbyProvinces.size() == 0) return false; // build towers only at front line

        return getPredictedDefenseGainByNewTower(hex) >= 3;
    }
}
