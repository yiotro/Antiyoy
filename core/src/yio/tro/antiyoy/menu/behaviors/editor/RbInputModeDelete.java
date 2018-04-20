package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbInputModeDelete extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getGameController(buttonYio).getLevelEditor().setInputMode(LevelEditor.MODE_DELETE);
        Scenes.sceneEditorHexPanel.hide();
    }
}
