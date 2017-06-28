package yio.tro.antiyoy.menu.behaviors.help;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 26.11.2015.
 */
public class RbArticleTrees extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneInfoMenu.create("help_trees_article", ReactBehavior.rbHelpIndex, 18);
    }
}
