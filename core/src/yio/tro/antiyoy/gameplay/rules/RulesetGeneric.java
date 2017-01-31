package yio.tro.antiyoy.gameplay.rules;

import yio.tro.antiyoy.LanguagesManager;
import yio.tro.antiyoy.OneTimeInfo;
import yio.tro.antiyoy.Yio;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.menu.MenuControllerYio;

import java.util.ArrayList;

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

        gameController.fieldController.getProvinceByHex(hex).money += GameRules.TREE_CUT_REWARD;
        gameController.fieldController.updateSelectedProvinceMoney();
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

        if (hex.objectInside == Hex.OBJECT_FARM) {
            return GameRules.FARM_INCOME + 1;
        }

        return 1;
    }


    @Override
    public int getHexTax(Hex hex) {
        if (hex.containsUnit()) {
            return hex.unit.getTax();
        }

        if (hex.objectInside == Hex.OBJECT_TOWER) return GameRules.TOWER_TAX;
        if (hex.objectInside == Hex.OBJECT_STRONG_TOWER) return GameRules.STRONG_TOWER_TAX;

        return 0;

    }


    @Override
    public boolean canBuildUnit(Province province, int strength) {
        return province.money >= GameRules.PRICE_UNIT * strength;
    }


    @Override
    public void onUnitMoveToHex(Unit unit, Hex hex) {
        if (!hex.containsTree()) return;

        gameController.getProvinceByHex(hex).money += 5;
        gameController.selectionController.updateSelectedProvinceMoney();
    }


    @Override
    public boolean canUnitAttackHex(int unitStrength, Hex hex) {
        if (unitStrength == 4) return true;

        return unitStrength > hex.getDefenseNumber();
    }


    @Override
    public int getColorIndexWithOffset(int srcIndex) {
        srcIndex += gameController.colorIndexViewOffset;

        // notice that last color index is for neutral lands
        if (srcIndex >= GameRules.colorNumber - 1) {
            srcIndex -= GameRules.colorNumber - 1;
        }

        return srcIndex;
    }
}
