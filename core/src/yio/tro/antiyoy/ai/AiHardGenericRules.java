package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.GameController;
import yio.tro.antiyoy.Hex;
import yio.tro.antiyoy.Province;
import yio.tro.antiyoy.Unit;

import java.util.ArrayList;

public class AiHardGenericRules extends ArtificialIntelligenceGeneric{


    public AiHardGenericRules(GameController gameController, int color) {
        super(gameController, color);
    }


    @Override
    public void makeMove() {
        ArrayList<Unit> unitsReadyToMove = detectUnitsReadyToMove();

        moveUnits(unitsReadyToMove);

        spendMoneyAndMergeUnits();

        moveAfkUnits();
    }


    @Override
    void tryToBuildTowers(Province province) {
        while (province.hasMoneyForTower()) {
            Hex hex = findHexThatNeedsTower(province);
            if (hex == null) return;

            if (province.hasMoneyForStrongTower()) {
                gameController.buildStrongTower(province, hex);
                continue;
            }
            gameController.buildTower(province, hex);
        }
    }
}
