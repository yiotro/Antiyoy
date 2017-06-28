package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbInputModeSetObject extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).getLevelEditor().setInputMode(LevelEditor.MODE_SET_OBJECT);
        getGameController(buttonYio).getLevelEditor().setInputObject(buttonYio.id - 162);
    }
}
