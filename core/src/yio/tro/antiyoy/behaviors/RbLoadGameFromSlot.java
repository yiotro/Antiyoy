package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonLighty;

/**
 * Created by ivan on 16.12.2015.
 */
public class RbLoadGameFromSlot extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getGameController(buttonLighty).getGameSaver().loadGameFromSlot(buttonLighty.id - 212);
    }
}
