package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbEditorShowConfirmRandomize extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneEditorConfirmRandomize.create();
    }
}
