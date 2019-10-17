package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.Unit;

import java.util.ArrayList;


public class AiEasy extends ArtificialIntelligence {

    public AiEasy(GameController gameController, int fraction) {
        super(gameController, fraction);
    }


    @Override
    public void makeMove() {
        moveUnits();

        spendMoneyAndMergeUnits();
    }


    @Override
    void decideAboutUnit(Unit unit, ArrayList<Hex> moveZone, Province province) {
        if (checkToCleanSomeTrees(unit, moveZone, province)) return;
        gameController.moveUnit(unit, moveZone.get(random.nextInt(moveZone.size())), province);
    }


    @Override
    void tryToBuildUnits(Province province) {
        for (int i = 1; i <= 4; i++) {
            if (!province.canAiAffordUnit(i)) break;
            while (canProvinceBuildUnit(province, i)) {
                if (!tryToBuiltUnitInsideProvince(province, i)) break;
            }
        }

        // this is to kick start province
        if (canProvinceBuildUnit(province, 1) && howManyUnitsInProvince(province) <= 1)
            tryToAttackWithStrength(province, 1);
    }


    @Override
    void tryToBuildTowers(Province province) {
        return; // easy AI can't build towers
    }


    @Override
    protected boolean mergeConditions(Province province, Unit unit, Hex hex) {
        return super.mergeConditions(province, unit, hex) && unit.strength == 1 && hex.unit.strength == 1;
    }


    @Override
    void mergeUnits(Province province) {
        if (random.nextDouble() < 0.25) {
            super.mergeUnits(province);
            return;
        }
    }
}
