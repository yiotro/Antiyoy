package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.YioGdxGame;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbLoadGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        if (YioGdxGame.interface_type == YioGdxGame.INTERFACE_SIMPLE) {
            getGameController(buttonLighty).loadGame();
        } else { // complicated
            buttonLighty.menuControllerLighty.createSaveSlotsMenu(true);
        }
    }
}
