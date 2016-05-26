package yio.tro.antiyoy;

import java.util.ArrayList;

/**
 * Created by ivan on 24.11.2015.
 */
class AiHard extends ArtificialIntelligence {

    public AiHard(GameController gameController, int color) {
        super(gameController, color);
    }


    @Override
    void makeMove() {
        ArrayList<Unit> unitsReadyToMove = detectUnitsReadyToMove();

        moveUnits(unitsReadyToMove);

        spendMoneyAndMergeUnits();

        moveAfkUnits();
    }
}
