package yio.tro.antiyoy.behaviors.help;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 26.11.2015.
 */
public class RbArticleUnits extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.createInfoMenu("help_units_article", ReactBehavior.rbHelpIndex, 18);
    }
}
