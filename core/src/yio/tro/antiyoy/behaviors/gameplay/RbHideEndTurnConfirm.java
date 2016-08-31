package yio.tro.antiyoy.behaviors.gameplay;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 09.04.2016.
 */
public class RbHideEndTurnConfirm extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.hideConfirmEndTurnMenu();
    }
}
