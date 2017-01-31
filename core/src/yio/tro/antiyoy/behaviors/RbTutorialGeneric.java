package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;

public class RbTutorialGeneric extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        GameRules.setSlayRules(false);
        getGameController(buttonYio).initTutorial();
    }
}
