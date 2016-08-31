package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonYio;

/**
 * Created by ivan on 12.11.2015.
 */
public class RbTutorial extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).initTutorial();
    }
}
