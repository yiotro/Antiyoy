package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 06.08.14.
 */
public class RbInGameMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getGameController(buttonLighty).deselectAll(); // stupid bug fix to prevent flickering of selection
        buttonLighty.menuControllerLighty.createInGameMenu();
        getYioGdxGame(buttonLighty).setGamePaused(true);
    }
}
