package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbSetLanguage extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneLanguageMenu.onLanguageButtonPressed(buttonYio);
    }
}
