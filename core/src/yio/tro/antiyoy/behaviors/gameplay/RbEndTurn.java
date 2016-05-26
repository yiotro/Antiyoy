package yio.tro.antiyoy.behaviors.gameplay;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 26.10.2014.
 */
public class RbEndTurn extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        if (YioGdxGame.ask_to_end_turn) {
            if (buttonLighty.id == 321) {
                buttonLighty.menuControllerLighty.hideConfirmEndTurnMenu();
                getGameController(buttonLighty).endTurnButtonPressed();
            } else {
                buttonLighty.menuControllerLighty.createConfirmEndTurnMenu();
            }
        } else {
            getGameController(buttonLighty).endTurnButtonPressed();
        }
    }
}
