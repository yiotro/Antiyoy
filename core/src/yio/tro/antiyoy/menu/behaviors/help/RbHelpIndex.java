package yio.tro.antiyoy.menu.behaviors.help;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 26.11.2015.
 */
public class RbHelpIndex extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneHelpIndex.create();
    }
}
