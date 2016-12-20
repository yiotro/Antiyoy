package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbLoadGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (Settings.interface_type == Settings.INTERFACE_SIMPLE) {
            getGameController(buttonYio).loadGame();
        } else { // complicated
            buttonYio.menuControllerYio.createSaveSlotsMenu(true);
        }
    }
}
