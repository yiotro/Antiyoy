package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 10.07.2015.
 */
public class RbDebugActions extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).debugActions();
    }
}
