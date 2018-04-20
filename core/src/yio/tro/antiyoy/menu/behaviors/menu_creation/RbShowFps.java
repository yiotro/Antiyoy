package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

public class RbShowFps extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        DebugFlags.showFpsInfo = true;
    }
}
