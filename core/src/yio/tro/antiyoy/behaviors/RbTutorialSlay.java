package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.gameplay.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 12.11.2015.
 */
public class RbTutorialSlay extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        GameRules.slay_rules = true;
        getGameController(buttonYio).initTutorial();
    }
}
