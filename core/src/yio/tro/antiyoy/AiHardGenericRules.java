package yio.tro.antiyoy;

import java.util.ArrayList;

public class AiHardGenericRules extends ArtificialIntelligenceGeneric{


    AiHardGenericRules(GameController gameController, int color) {
        super(gameController, color);
    }


    @Override
    void makeMove() {
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
