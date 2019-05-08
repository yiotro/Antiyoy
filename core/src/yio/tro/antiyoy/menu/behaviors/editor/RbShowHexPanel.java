package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 27.11.2015.
 */
public class RbShowHexPanel extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneEditorHexPanel.onTumblerButtonPressed();
    }
}
