package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 03.12.2015.
 */
public class RbCloseSettingsMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        boolean needToRestart = Settings.getInstance().saveSettings();
        Scenes.sceneMainMenu.create();
        if (needToRestart) {
            Scenes.sceneNotification.showNotification(LanguagesManager.getInstance().getString("restart_app"), true);
        }
        Settings.getInstance().loadSettings();
    }
}
