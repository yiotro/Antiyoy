package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonYio;

/**
 * Created by ivan on 24.03.2016.
 */
public class RbNextLevel extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).loadCampaignLevel(getGameController(buttonYio).getNextLevelIndex());
    }
}
