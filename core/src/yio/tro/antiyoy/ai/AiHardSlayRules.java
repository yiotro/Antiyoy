package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.GameController;
import yio.tro.antiyoy.Unit;

import java.util.ArrayList;

/**
 * Created by ivan on 24.11.2015.
 */
public class AiHardSlayRules extends ArtificialIntelligence {

    public AiHardSlayRules(GameController gameController, int color) {
        super(gameController, color);
    }


    @Override
    public void makeMove() {
        ArrayList<Unit> unitsReadyToMove = detectUnitsReadyToMove();

        moveUnits(unitsReadyToMove);

        spendMoneyAndMergeUnits();

        moveAfkUnits();
    }
}
