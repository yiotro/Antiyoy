package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 26.10.2014.
 */
public class RbEndTurn extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (haveToAskToEndTurn(buttonYio)) {
            if (buttonYio.id == 321) {
                Scenes.sceneConfirmEndTurn.hide();
                getGameController(buttonYio).endTurnButtonPressed();
            } else {
                Scenes.sceneConfirmEndTurn.create();
            }
        } else {
            getGameController(buttonYio).endTurnButtonPressed();
        }
    }


    private boolean haveToAskToEndTurn(ButtonYio buttonYio) {
        if (GameRules.tutorialMode) return false;

        return Settings.ask_to_end_turn && getGameController(buttonYio).fieldController.atLeastOneUnitIsReadyToMove();
    }
}
