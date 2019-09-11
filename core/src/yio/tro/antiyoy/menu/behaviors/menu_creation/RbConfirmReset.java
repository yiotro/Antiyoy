package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbConfirmReset extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneMoreSettings.onDestroy();
        Scenes.sceneConfirmReset.create();
    }
}
