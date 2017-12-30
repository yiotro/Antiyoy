package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 09.11.2015.
 */
public class RbStatisticsMenu extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneStatisticsMenu.create(getGameController(buttonYio).matchStatistics);
    }
}
