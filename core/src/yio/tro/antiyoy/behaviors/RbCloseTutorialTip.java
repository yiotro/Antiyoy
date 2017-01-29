package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 05.10.2014.
 */
public class RbCloseTutorialTip extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).setGamePaused(false);
        if (getGameController(buttonYio).tutorialScript != null)
            getGameController(buttonYio).tutorialScript.setTipIsCurrentlyShown(false);

        buttonYio.menuControllerYio.closeTutorialTip();
    }
}
