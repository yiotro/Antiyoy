package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by yiotro on 10.07.2015.
 */
public class RbDebugActions extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getGameController(buttonYio).debugActions();
    }
}
