package yio.tro.antiyoy.behaviors.editor;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbStartEditorMode extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
//        getYioGdxGame(buttonLighty).startInEditorMode();
        getGameController(buttonLighty).getLevelEditor().loadSlot();
    }
}
