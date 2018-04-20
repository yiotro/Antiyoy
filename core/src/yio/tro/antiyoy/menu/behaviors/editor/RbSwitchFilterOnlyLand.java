package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;

public class RbSwitchFilterOnlyLand extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getGameController(buttonYio).getLevelEditor().switchFilterOnlyLand();
    }
}
