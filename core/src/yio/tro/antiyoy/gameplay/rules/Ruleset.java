package yio.tro.antiyoy.gameplay.rules;

import yio.tro.antiyoy.gameplay.*;

public abstract class Ruleset {

    GameController gameController;


    public Ruleset(GameController gameController) {
        this.gameController = gameController;
    }


    public abstract boolean canSpawnPineOnHex(Hex hex);


    public abstract boolean canSpawnPalmOnHex(Hex hex);


    public abstract void onUnitAdd(Hex hex);


    public abstract void onTurnEnd();


    public abstract boolean canMergeUnits(Unit unit1, Unit unit2);


    public abstract int getHexIncome(Hex hex);


    public abstract int getHexTax(Hex hex);


    public abstract int getUnitTax(int strength);


    public abstract boolean canBuildUnit(Province province, int strength);


    public abstract void onUnitMoveToHex(Unit unit, Hex hex);


    public abstract boolean canUnitAttackHex(int unitStrength, Hex hex);


    public int howManyTreesNearby(Hex hex) {
        if (!hex.active) return 0;
        int c = 0;
        for (int i = 0; i < 6; i++)
            if (hex.getAdjacentHex(i).containsTree()) c++;
        return c;
    }
}
