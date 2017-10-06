package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 16.12.2015.
 */
public class RbSaveGameToSlot extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).getGameSaver().saveGameToSlot(buttonYio.id - 212);
        Scenes.sceneNotification.showNotification(LanguagesManager.getInstance().getString("game_saved"), true);
    }
}
