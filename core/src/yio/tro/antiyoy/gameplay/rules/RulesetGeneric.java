package yio.tro.antiyoy.gameplay.rules;

import yio.tro.antiyoy.gameplay.*;

import static yio.tro.antiyoy.gameplay.rules.GameRules.*;

public class RulesetGeneric extends Ruleset{


    public RulesetGeneric(GameController gameController) {
        super(gameController);
    }


    @Override
    public boolean canSpawnPineOnHex(Hex hex) {
        return hex.isFree() && howManyTreesNearby(hex) >= 2 && hex.hasPineReadyToExpandNearby() && gameController.getRandom().nextDouble() < 0.2;
    }


    @Override
    public boolean canSpawnPalmOnHex(Hex hex) {
        return hex.isFree() && hex.isNearWater() && hex.hasPalmReadyToExpandNearby() && gameController.getRandom().nextDouble() < 0.3;
    }


    @Override
    public void onUnitAdd(Hex hex) {
        if (!hex.containsTree()) {
            return;
        }

        Province provinceByHex = gameController.fieldManager.getProvinceByHex(hex);
        if (provinceByHex != null) {
            provinceByHex.money += GameRules.TREE_CUT_REWARD;
        }
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

        if (hex.objectInside == Obj.FARM) {
            return GameRules.FARM_INCOME + 1;
        }

        return 1;
    }


    @Override
    public int getHexTax(Hex hex) {
        if (hex.containsUnit()) {
            return getUnitTax(hex.unit.strength);
        }

        if (hex.objectInside == Obj.TOWER) return GameRules.TAX_TOWER;
        if (hex.objectInside == Obj.STRONG_TOWER) return GameRules.TAX_STRONG_TOWER;

        return 0;
    }


    @Override
    public int getUnitTax(int strength) {
        switch (strength) {
            default:
            case 1:
                return TAX_UNIT_GENERIC_1;
            case 2:
                return TAX_UNIT_GENERIC_2;
            case 3:
                return TAX_UNIT_GENERIC_3;
            case 4:
                return TAX_UNIT_GENERIC_4;
        }

    }


    @Override
    public boolean canBuildUnit(Province province, int strength) {
        return province.money >= GameRules.PRICE_UNIT * strength;
    }


    @Override
    public void onUnitMoveToHex(Unit unit, Hex hex) {
        if (!hex.containsTree()) return;

        Province provinceByHex = gameController.getProvinceByHex(hex);
        if (provinceByHex != null) {
            provinceByHex.money += GameRules.TREE_CUT_REWARD;
        }
    }


    @Override
    public boolean canUnitAttackHex(int unitStrength, Hex hex) {
        if (unitStrength == 4) return true;

        return unitStrength > hex.getDefenseNumber();
    }


}
