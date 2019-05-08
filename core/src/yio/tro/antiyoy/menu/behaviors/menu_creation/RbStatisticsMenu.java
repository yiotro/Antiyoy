package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 09.11.2015.
 */
public class RbStatisticsMenu extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneMatchStatistics.create(getGameController(buttonYio).matchStatistics);
    }
}
