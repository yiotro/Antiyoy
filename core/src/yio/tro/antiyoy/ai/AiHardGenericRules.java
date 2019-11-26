package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;

public class AiHardGenericRules extends ArtificialIntelligenceGeneric{


    public AiHardGenericRules(GameController gameController, int fraction) {
        super(gameController, fraction);
    }


    @Override
    public void makeMove() {
        moveUnits();

        spendMoneyAndMergeUnits();

        moveAfkUnits();
    }


    @Override
    void tryToBuildTowers(Province province) {
        while (province.hasMoneyForTower()) {
            Hex hex = findHexThatNeedsTower(province);
            if (hex == null) return;

            if (province.hasMoneyForStrongTower()) {
                gameController.fieldManager.buildStrongTower(province, hex);
                continue;
            }
            gameController.fieldManager.buildTower(province, hex);
        }
    }
}
