package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.menu.behaviors.Reaction;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbInputModeMove extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).getLevelEditor().setInputMode(LevelEditor.MODE_MOVE);
        buttonYio.menuControllerYio.hideAllEditorPanels();
    }
}
