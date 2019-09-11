package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 09.04.2016.
 */
public class RbMoreSettings extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneSettings.onDestroy();
        Scenes.sceneMoreSettings.create();
    }
}
