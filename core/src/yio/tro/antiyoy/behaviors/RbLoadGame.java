package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.YioGdxGame;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbLoadGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (YioGdxGame.interface_type == YioGdxGame.INTERFACE_SIMPLE) {
            getGameController(buttonYio).loadGame();
        } else { // complicated
            buttonYio.menuControllerYio.createSaveSlotsMenu(true);
        }
    }
}
