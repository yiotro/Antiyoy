package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.Unit;

import java.util.ArrayList;

/**
 * Created by ivan on 24.11.2015.
 */
public class AiEasy extends ArtificialIntelligence {

    public AiEasy(GameController gameController, int color) {
        super(gameController, color);
    }


    @Override
    public void makeMove() {
        ArrayList<Unit> unitsReadyToMove = detectUnitsReadyToMove();

        moveUnits(unitsReadyToMove);

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
            if (!province.hasEnoughIncomeToAffordUnit(i)) break;
            while (province.hasMoneyForUnit(i)) {
                if (!tryToBuiltUnitInsideProvince(province, i)) break;
            }
        }

        // this is to kick start province
        if (province.hasMoneyForUnit(1) && howManyUnitsInProvince(province) <= 1)
            tryToAttackWithStrength(province, 1);
    }


    @Override
    void tryToBuildTowers(Province province) {
        return; // easy AI can't build towers
    }


    @Override
    void mergeUnits(Province province) {
        return; // easy AI can't merge units
    }
}
