package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.YioGdxGame;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbSaveGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        if (YioGdxGame.interface_type == YioGdxGame.INTERFACE_SIMPLE) {
            getGameController(buttonLighty).saveGame();
            buttonLighty.menuControllerLighty.showNotification(buttonLighty.menuControllerLighty.languagesManager.getString("game_saved"), true);
        } else { // complicated
            buttonLighty.menuControllerLighty.createSaveSlotsMenu(false);
        }
    }
}
