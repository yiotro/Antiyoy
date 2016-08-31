package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.YioGdxGame;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbSaveGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (YioGdxGame.interface_type == YioGdxGame.INTERFACE_SIMPLE) {
            getGameController(buttonYio).saveGame();
            buttonYio.menuControllerYio.showNotification(buttonYio.menuControllerYio.languagesManager.getString("game_saved"), true);
        } else { // complicated
            buttonYio.menuControllerYio.createSaveSlotsMenu(false);
        }
    }
}
