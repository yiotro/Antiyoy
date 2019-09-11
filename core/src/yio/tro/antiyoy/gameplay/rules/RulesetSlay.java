package yio.tro.antiyoy.gameplay.rules;

import yio.tro.antiyoy.gameplay.*;

public class RulesetSlay extends Ruleset{

    public RulesetSlay(GameController gameController) {
        super(gameController);
    }


    @Override
    public boolean canSpawnPineOnHex(Hex hex) {
        return hex.isFree() && howManyTreesNearby(hex) >= 2 && hex.hasPineReadyToExpandNearby() && gameController.getRandom().nextDouble() < 0.8;
    }


    @Override
    public boolean canSpawnPalmOnHex(Hex hex) {
        return hex.isFree() && hex.isNearWater() && hex.hasPalmReadyToExpandNearby();
    }


    @Override
    public void onUnitAdd(Hex hex) {

    }


    @Override
    public void onTurnEnd() {

    }


    @Override
    public boolean canMergeUnits(Unit unit1, Unit unit2) {
        int mergedUnitStrength = gameController.mergedUnitStrength(unit1, unit2);

        return mergedUnitStrength <= 4;
    }


    @Override
    public int getHexIncome(Hex hex) {
        if (hex.containsTree()) {
            return 0;
        }
        return 1;
    }


    @Override
    public int getHexTax(Hex hex) {
        if (hex.containsUnit()) {
            return getUnitTax(hex.unit.strength);
        }

        return 0;
    }


    @Override
    public int getUnitTax(int strength) {
        switch (strength) {
            default:
            case 1:
                return 2;
            case 2:
                return 6;
            case 3:
                return 18;
            case 4:
                return 54;
        }
    }


    @Override
    public boolean canBuildUnit(Province province, int strength) {
        return province.money >= GameRules.PRICE_UNIT * strength;
    }


    @Override
    public void onUnitMoveToHex(Unit unit, Hex hex) {

    }


    @Override
    public boolean canUnitAttackHex(int unitStrength, Hex hex) {
        return unitStrength > hex.getDefenseNumber();
    }


}
