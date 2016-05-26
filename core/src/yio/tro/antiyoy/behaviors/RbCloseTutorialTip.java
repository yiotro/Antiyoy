package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonLighty;

/**
 * Created by ivan on 05.10.2014.
 */
public class RbCloseTutorialTip extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getYioGdxGame(buttonLighty).setGamePaused(false);
        if (getGameController(buttonLighty).tutorialScript != null)
            getGameController(buttonLighty).tutorialScript.setTipIsCurrentlyShown(false);

        buttonLighty.menuControllerLighty.getButtonById(50).destroy();
        buttonLighty.menuControllerLighty.getButtonById(53).destroy();
        buttonLighty.menuControllerLighty.getButtonById(50).factorModel.beginDestroying(1, 3);
        buttonLighty.menuControllerLighty.getButtonById(53).factorModel.beginDestroying(1, 3);

        if (buttonLighty.menuControllerLighty.getButtonById(54) != null) { // help index button
            buttonLighty.menuControllerLighty.getButtonById(54).destroy();
            buttonLighty.menuControllerLighty.getButtonById(54).factorModel.beginDestroying(1, 3);
        }

        buttonLighty.menuControllerLighty.getButtonById(30).setTouchable(true);
        buttonLighty.menuControllerLighty.getButtonById(31).setTouchable(true);
        buttonLighty.menuControllerLighty.getButtonById(32).setTouchable(true);
    }
}
