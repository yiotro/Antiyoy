package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbSaveGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (Settings.interface_type == Settings.INTERFACE_SIMPLE) {
            getGameController(buttonYio).saveGame();
            buttonYio.menuControllerYio.showNotification(buttonYio.menuControllerYio.languagesManager.getString("game_saved"), true);
        } else { // complicated
            buttonYio.menuControllerYio.createSaveSlotsMenu(false);
        }
    }
}
