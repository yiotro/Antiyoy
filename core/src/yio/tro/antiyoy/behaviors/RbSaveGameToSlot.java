package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonLighty;

/**
 * Created by ivan on 16.12.2015.
 */
public class RbSaveGameToSlot extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getGameController(buttonLighty).getGameSaver().saveGameToSlot(buttonLighty.id - 212);
        buttonLighty.menuControllerLighty.showNotification(buttonLighty.menuControllerLighty.languagesManager.getString("game_saved"), true);
    }
}
