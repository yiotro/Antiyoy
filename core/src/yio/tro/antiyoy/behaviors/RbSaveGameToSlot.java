package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonYio;

/**
 * Created by ivan on 16.12.2015.
 */
public class RbSaveGameToSlot extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).getGameSaver().saveGameToSlot(buttonYio.id - 212);
        buttonYio.menuControllerYio.showNotification(buttonYio.menuControllerYio.languagesManager.getString("game_saved"), true);
    }
}
