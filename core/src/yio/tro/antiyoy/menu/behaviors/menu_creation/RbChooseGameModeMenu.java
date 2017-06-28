package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 15.08.2015.
 */
public class RbChooseGameModeMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneChoodeGameModeMenu.create();
        getYioGdxGame(buttonYio).setGamePaused(true);
        getYioGdxGame(buttonYio).setAnimToPlayButtonSpecial();
    }
}
