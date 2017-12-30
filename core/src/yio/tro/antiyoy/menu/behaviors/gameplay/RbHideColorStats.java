package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 29.12.2015.
 */
public class RbHideColorStats extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneColorStats.hide();
    }
}
