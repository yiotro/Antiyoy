package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

/**
 * Created by yiotro on 31.05.2015.
 */
public class RbUndo extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getGameController(buttonYio).undoAction();
    }
}
