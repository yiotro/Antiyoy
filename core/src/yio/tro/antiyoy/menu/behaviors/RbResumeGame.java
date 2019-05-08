package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 06.08.14.
 */
public class RbResumeGame extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).gameView.appear();
        Scenes.sceneGameOverlay.create();
        getYioGdxGame(buttonYio).setGamePaused(false);
        getYioGdxGame(buttonYio).setAnimToResumeButtonSpecial();
    }
}
