package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;

public class RbSwitchFilterOnlyLand extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).getLevelEditor().switchFilterOnlyLand();
    }
}
