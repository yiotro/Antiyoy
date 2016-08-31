package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 15.08.2015.
 */
public class RbSkirmishMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).setGamePaused(true);
        buttonYio.menuControllerYio.createSkirmishMenu();
        getYioGdxGame(buttonYio).setAnimToPlayButtonSpecial();
    }
}
