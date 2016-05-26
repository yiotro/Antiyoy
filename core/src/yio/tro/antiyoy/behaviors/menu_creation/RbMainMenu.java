package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 05.08.14.
 */
public class RbMainMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getYioGdxGame(buttonLighty).setGamePaused(true);
        buttonLighty.menuControllerLighty.createMainMenu();
    }
}
