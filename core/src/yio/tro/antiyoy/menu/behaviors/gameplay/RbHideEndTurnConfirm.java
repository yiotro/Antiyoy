package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 09.04.2016.
 */
public class RbHideEndTurnConfirm extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneConfirmEndTurn.hide();
    }
}
