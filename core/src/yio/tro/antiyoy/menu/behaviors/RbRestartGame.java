package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbRestartGame extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (buttonYio.id == 221) { // restart game confirmed
            getYioGdxGame(buttonYio).restartGame();
        } else {
            Scenes.sceneConfirmRestart.create();
        }
    }
}
