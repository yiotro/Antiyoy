package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 06.08.14.
 */
public class RbPauseMenu extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).selectionController.deselectAll(); // fix to prevent flickering of selection
        Scenes.scenePauseMenu.create();
        getYioGdxGame(buttonYio).setGamePaused(true);
    }
}
