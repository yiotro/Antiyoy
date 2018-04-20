package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbMoreSkirmishOptions extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneSkirmishMenu.saveValues();
        Scenes.sceneMoreSkirmishOptions.create();
    }
}
