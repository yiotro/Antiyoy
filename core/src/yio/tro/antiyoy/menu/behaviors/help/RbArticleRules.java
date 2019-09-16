package yio.tro.antiyoy.menu.behaviors.help;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbArticleRules extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        Scenes.sceneArticle.create("article_rules", Reaction.rbHelpIndex, 18);
    }
}
