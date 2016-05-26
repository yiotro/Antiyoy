package yio.tro.antiyoy.behaviors.help;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 26.11.2015.
 */
public class RbArticleMoney extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        buttonLighty.menuControllerLighty.createInfoMenu("help_money_article", ReactBehavior.rbHelpIndex, 18);
    }
}
