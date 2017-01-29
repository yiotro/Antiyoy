package yio.tro.antiyoy.behaviors.editor;

import yio.tro.antiyoy.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;

public class RbSwitchFilterOnlyLand extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).getLevelEditor().switchFilterOnlyLand();
    }
}
