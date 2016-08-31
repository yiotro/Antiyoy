package yio.tro.antiyoy.behaviors.gameplay;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 26.10.2014.
 */
public class RbEndTurn extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (YioGdxGame.ask_to_end_turn) {
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
