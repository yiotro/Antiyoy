package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.gameplay.CampaignController;
import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 24.03.2016.
 */
public class RbNextLevel extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        CampaignController instance = CampaignController.getInstance();
        instance.loadCampaignLevel(instance.getNextLevelIndex());
    }
}
