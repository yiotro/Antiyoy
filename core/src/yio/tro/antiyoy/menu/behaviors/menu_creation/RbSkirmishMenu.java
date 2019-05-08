package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 15.08.2015.
 */
public class RbSkirmishMenu extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).setGamePaused(true);
        Scenes.sceneSkirmishMenu.create();
        getYioGdxGame(buttonYio).setAnimToPlayButtonSpecial();
    }
}
