package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbStartEditorMode extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
//        getYioGdxGame(buttonLighty).startInEditorMode();
        getGameController(buttonYio).getLevelEditor().loadSlot();
    }
}
