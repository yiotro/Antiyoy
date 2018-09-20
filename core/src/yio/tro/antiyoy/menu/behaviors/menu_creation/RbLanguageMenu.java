package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbLanguageMenu extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Settings.getInstance().saveSettings();
        Settings.getInstance().saveMoreSettings();
        Scenes.sceneLanguages.create();
    }
}
