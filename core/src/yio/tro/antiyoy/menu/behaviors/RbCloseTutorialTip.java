package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 05.10.2014.
 */
public class RbCloseTutorialTip extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).setGamePaused(false);
        if (getGameController(buttonYio).tutorialScript != null)
            getGameController(buttonYio).tutorialScript.setTipIsCurrentlyShown(false);

        Scenes.sceneTutorialTip.hide();
    }
}
