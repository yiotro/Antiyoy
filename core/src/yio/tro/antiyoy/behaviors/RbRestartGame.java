package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbRestartGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (buttonYio.id == 221) { // restart game confirmed
            getYioGdxGame(buttonYio).restartGame();
        } else {
            buttonYio.menuControllerYio.createConfirmRestartMenu();
        }
    }
}
