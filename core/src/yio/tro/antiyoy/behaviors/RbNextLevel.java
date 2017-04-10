package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.gameplay.CampaignProgressManager;
import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 24.03.2016.
 */
public class RbNextLevel extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        CampaignProgressManager instance = CampaignProgressManager.getInstance();

        int nextLevelIndex = instance.getNextLevelIndex();
        getYioGdxGame(buttonYio).campaignLevelFactory.createCampaignLevel(nextLevelIndex);
    }
}
