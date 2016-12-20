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

        buttonYio.menuControllerYio.getButtonById(50).destroy();
        buttonYio.menuControllerYio.getButtonById(53).destroy();
        buttonYio.menuControllerYio.getButtonById(50).factorModel.beginDestroying(1, 3);
        buttonYio.menuControllerYio.getButtonById(53).factorModel.beginDestroying(1, 3);

        if (buttonYio.menuControllerYio.getButtonById(54) != null) { // help index button
            buttonYio.menuControllerYio.getButtonById(54).destroy();
            buttonYio.menuControllerYio.getButtonById(54).factorModel.beginDestroying(1, 3);
        }

        buttonYio.menuControllerYio.getButtonById(30).setTouchable(true);
        buttonYio.menuControllerYio.getButtonById(31).setTouchable(true);
        buttonYio.menuControllerYio.getButtonById(32).setTouchable(true);
    }
}
