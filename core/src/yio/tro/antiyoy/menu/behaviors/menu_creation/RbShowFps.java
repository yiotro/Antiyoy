package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbShowFps extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        DebugFlags.showFpsInfo = true;
    }
}
