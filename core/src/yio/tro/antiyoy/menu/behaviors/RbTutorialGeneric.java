package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;

public class RbTutorialGeneric extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        GameRules.setSlayRules(false);
        Settings.fastConstruction = false;

        getGameController(buttonYio).initTutorial();
    }
}
