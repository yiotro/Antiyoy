package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonLighty;

/**
 * Created by ivan on 24.03.2016.
 */
public class RbNextLevel extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getGameController(buttonLighty).loadCampaignLevel(getGameController(buttonLighty).getNextLevelIndex());
    }
}
