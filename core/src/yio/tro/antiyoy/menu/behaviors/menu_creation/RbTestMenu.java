package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 19.08.2015.
 */
public class RbTestMenu extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneTestMenu.create();
    }
}
