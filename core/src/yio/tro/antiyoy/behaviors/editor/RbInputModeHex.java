package yio.tro.antiyoy.behaviors.editor;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.LevelEditor;
import yio.tro.antiyoy.behaviors.ReactBehavior;

import java.util.Random;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbInputModeHex extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getGameController(buttonLighty).getLevelEditor().setInputMode(LevelEditor.MODE_SET_HEX);
        getGameController(buttonLighty).getLevelEditor().setInputColor(buttonLighty.id - 150);
    }
}
