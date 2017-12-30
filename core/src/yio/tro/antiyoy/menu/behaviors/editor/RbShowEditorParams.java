package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbShowEditorParams extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneEditorParams.onTumblerButtonPressed();
    }
}
