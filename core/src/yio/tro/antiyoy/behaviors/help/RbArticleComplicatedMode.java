package yio.tro.antiyoy.behaviors.help;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 28.12.2015.
 */
public class RbArticleComplicatedMode extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.createInfoMenu("about_complicated_mode_article", ReactBehavior.rbSettingsMenu, 18);
    }
}
