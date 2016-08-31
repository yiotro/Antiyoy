package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonYio;

/**
 * Created by ivan on 10.07.2015.
 */
public class RbDebugActions extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).debugActions();
    }
}
