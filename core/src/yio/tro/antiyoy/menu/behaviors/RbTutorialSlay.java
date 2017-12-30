package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 12.11.2015.
 */
public class RbTutorialSlay extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        GameRules.setSlayRules(true);
        Settings.fastConstruction = false;

        getGameController(buttonYio).initTutorial();
    }
}
