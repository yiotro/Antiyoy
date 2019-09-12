package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 29.12.2015.
 */
public class RbShowIncomeGraph extends Reaction {


    @Override
    public void perform(ButtonYio buttonYio) {
        GameController gameController = getGameController(buttonYio);

        if (gameController.playersNumber == 0) {
            gameController.speedManager.applyPause();
        }

        Scenes.sceneIncomeGraph.create();
    }

}
