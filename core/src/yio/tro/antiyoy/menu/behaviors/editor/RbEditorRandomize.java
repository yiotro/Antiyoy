package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 27.11.2015.
 */
public class RbEditorRandomize extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getGameController(buttonYio).getLevelEditor().randomize();
        Scenes.sceneEditorConfirmRandomize.hide();
    }
}
