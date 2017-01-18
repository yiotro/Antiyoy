package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.gameplay.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;

public class RbTutorialGeneric extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        GameRules.slay_rules = false;
        getGameController(buttonYio).initTutorial();
    }
}
