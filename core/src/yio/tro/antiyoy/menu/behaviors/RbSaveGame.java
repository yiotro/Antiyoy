package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbSaveGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (Settings.interface_type == Settings.INTERFACE_SIMPLE) {
            getGameController(buttonYio).saveGame();
            Scenes.sceneNotification.showNotification(LanguagesManager.getInstance().getString("game_saved"), true);
        } else { // complicated
            Scenes.sceneSaveSlots.create(false);
        }
    }
}
