package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 06.08.14.
 */
public class RbResumeGame extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).gameView.beginSpawnProcess();
        Scenes.sceneGameOverlay.create();
        getYioGdxGame(buttonYio).setGamePaused(false);
        getYioGdxGame(buttonYio).setAnimToResumeButtonSpecial();
    }
}
