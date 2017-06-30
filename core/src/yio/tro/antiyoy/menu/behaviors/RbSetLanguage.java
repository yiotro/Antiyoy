package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.CustomLanguageLoader;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbSetLanguage extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneLanguageMenu.onLanguageButtonPressed(buttonYio);
    }
}
