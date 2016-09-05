package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 15.08.2015.
 */
public class RbChooseGameModeMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.createChooseGameModeMenu();
        getYioGdxGame(buttonYio).setGamePaused(true);
        getYioGdxGame(buttonYio).setAnimToPlayButtonSpecial();
    }
}
