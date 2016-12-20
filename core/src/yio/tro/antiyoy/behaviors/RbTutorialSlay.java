package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.GameController;

/**
 * Created by ivan on 12.11.2015.
 */
public class RbTutorialSlay extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        GameController.slay_rules = true;
        getGameController(buttonYio).initTutorial();
    }
}
