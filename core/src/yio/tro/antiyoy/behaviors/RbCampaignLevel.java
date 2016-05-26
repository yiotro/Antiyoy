package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonLighty;

/**
 * Created by ivan on 18.11.2015.
 */
public class RbCampaignLevel extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getGameController(buttonLighty).loadCampaignLevel(getYioGdxGame(buttonLighty).getSelectedLevelIndex());
    }
}
