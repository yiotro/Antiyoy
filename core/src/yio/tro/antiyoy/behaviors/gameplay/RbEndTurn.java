package yio.tro.antiyoy.behaviors.gameplay;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 26.10.2014.
 */
public class RbEndTurn extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (Settings.ask_to_end_turn && getGameController(buttonYio).atLeastOneUnitIsReadyToMove()) {
            if (buttonYio.id == 321) {
                buttonYio.menuControllerYio.hideConfirmEndTurnMenu();
                getGameController(buttonYio).endTurnButtonPressed();
            } else {
                buttonYio.menuControllerYio.createConfirmEndTurnMenu();
            }
        } else {
            getGameController(buttonYio).endTurnButtonPressed();
        }
    }
}
