package yio.tro.antiyoy.behaviors.gameplay;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 09.04.2016.
 */
public class RbHideEndTurnConfirm extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        buttonLighty.menuControllerLighty.hideConfirmEndTurnMenu();
    }
}
