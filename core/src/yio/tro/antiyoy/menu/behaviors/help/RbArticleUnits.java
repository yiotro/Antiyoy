package yio.tro.antiyoy.menu.behaviors.help;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 26.11.2015.
 */
public class RbArticleUnits extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneAboutGame.create("help_units_article", Reaction.rbHelpIndex, 18);
    }
}
