package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 26.10.2014.
 */
public class RbEndTurn extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        if (haveToAskToEndTurn(buttonYio)) {
            if (buttonYio.id == 321) {
                Scenes.sceneConfirmEndTurn.hide();
                getGameController(buttonYio).onEndTurnButtonPressed();
            } else {
                Scenes.sceneConfirmEndTurn.create();
            }
        } else {
            getGameController(buttonYio).onEndTurnButtonPressed();
        }
    }


    private boolean haveToAskToEndTurn(ButtonYio buttonYio) {
        return getGameController(buttonYio).haveToAskToEndTurn();
    }
}
