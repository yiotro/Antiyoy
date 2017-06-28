package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbLanguageMenu extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneLanguageMenu.create();
    }
}
