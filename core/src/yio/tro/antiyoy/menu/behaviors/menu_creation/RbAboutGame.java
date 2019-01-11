package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 05.08.14.
 */
public class RbAboutGame extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneSettingsMenu.onDestroy();
        Scenes.sceneAboutGame.create();
    }
}
