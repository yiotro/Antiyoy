package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.GameController;

public class RbTutorialGeneric extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        GameController.slay_rules = false;
        getGameController(buttonYio).initTutorial();
    }
}
