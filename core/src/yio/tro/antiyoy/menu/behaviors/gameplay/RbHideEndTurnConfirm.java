package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 09.04.2016.
 */
public class RbHideEndTurnConfirm extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneConfirmEndTurn.hide();
    }
}
