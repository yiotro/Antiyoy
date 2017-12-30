package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbBackFromSkirmish extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneSkirmishMenu.saveValues();
        Scenes.sceneChoodeGameModeMenu.create();
    }
}
