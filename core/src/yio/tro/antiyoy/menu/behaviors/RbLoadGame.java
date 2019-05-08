package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 11.11.2015.
 */
public class RbLoadGame extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneSaveLoad.create();
        Scenes.sceneSaveLoad.setOperationType(true);
    }
}
