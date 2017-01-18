package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 03.12.2015.
 */
public class RbCloseSettingsMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Settings.getInstance().saveSettings();
        buttonYio.menuControllerYio.createMainMenu();
        Settings.getInstance().loadSettings();
    }
}
