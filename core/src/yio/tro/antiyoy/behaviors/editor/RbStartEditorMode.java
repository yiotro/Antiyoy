package yio.tro.antiyoy.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbStartEditorMode extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
//        getYioGdxGame(buttonLighty).startInEditorMode();
        getGameController(buttonYio).getLevelEditor().loadSlot();
    }
}
