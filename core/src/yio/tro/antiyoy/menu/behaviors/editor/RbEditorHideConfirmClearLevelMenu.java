package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbEditorHideConfirmClearLevelMenu extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneEditorConfirmClear.hide();
    }
}
