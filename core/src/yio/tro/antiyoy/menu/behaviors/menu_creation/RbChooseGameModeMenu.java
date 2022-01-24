package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 15.08.2015.
 */
public class RbChooseGameModeMenu extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneChooseGameMode.create();
        getYioGdxGame(buttonYio).setGamePaused(true);
        getYioGdxGame(buttonYio).setAnimToPlayButtonSpecial();
    }
}
