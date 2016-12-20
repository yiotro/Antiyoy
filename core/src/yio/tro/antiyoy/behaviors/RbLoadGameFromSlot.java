package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 16.12.2015.
 */
public class RbLoadGameFromSlot extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).getGameSaver().loadGameFromSlot(buttonYio.id - 212);
    }
}
