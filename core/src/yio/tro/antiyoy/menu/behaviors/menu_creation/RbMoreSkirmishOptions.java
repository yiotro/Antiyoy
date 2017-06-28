package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbMoreSkirmishOptions extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneSkirmishMenu.saveSkirmishSettings();
        Scenes.sceneMoreSkirmishOptions.create();
        buttonYio.menuControllerYio.loadMoreSkirmishOptions();
    }
}
