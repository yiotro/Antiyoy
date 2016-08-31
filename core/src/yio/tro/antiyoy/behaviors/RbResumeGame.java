package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonYio;

/**
 * Created by ivan on 06.08.14.
 */
public class RbResumeGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).gameView.beginSpawnProcess();
        buttonYio.menuControllerYio.createGameOverlay();
        getYioGdxGame(buttonYio).setGamePaused(false);
        getYioGdxGame(buttonYio).setAnimToResumeButtonSpecial();
    }
}
