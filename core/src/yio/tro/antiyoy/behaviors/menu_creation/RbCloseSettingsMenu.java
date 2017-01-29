package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.LanguagesManager;
import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 03.12.2015.
 */
public class RbCloseSettingsMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        boolean needToRestart = Settings.getInstance().saveSettings();
        buttonYio.menuControllerYio.createMainMenu();
        if (needToRestart) {
            buttonYio.menuControllerYio.showNotification(LanguagesManager.getInstance().getString("restart_app"), true);
        }
        Settings.getInstance().loadSettings();
    }
}
