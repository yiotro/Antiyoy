package yio.tro.antiyoy.behaviors.editor;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.LevelEditor;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbInputModeDelete extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getGameController(buttonLighty).getLevelEditor().setInputMode(LevelEditor.MODE_DELETE);
    }
}
