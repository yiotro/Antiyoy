package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 15.08.2015.
 */
public class RbChooseGameModeMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getYioGdxGame(buttonLighty).setGamePaused(true);
        buttonLighty.menuControllerLighty.createChooseGameModeMenu();
        getYioGdxGame(buttonLighty).setAnimToPlayButtonSpecial();
    }
}
