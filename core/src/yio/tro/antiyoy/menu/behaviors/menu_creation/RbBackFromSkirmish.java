package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbBackFromSkirmish extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneSkirmishMenu.saveSkirmishSettings();
        Scenes.sceneChoodeGameModeMenu.create();
    }
}
