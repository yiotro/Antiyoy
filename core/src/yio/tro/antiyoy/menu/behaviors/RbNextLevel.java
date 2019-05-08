package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by yiotro on 24.03.2016.
 */
public class RbNextLevel extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        CampaignProgressManager instance = CampaignProgressManager.getInstance();

        if (instance.currentLevelIndex == CampaignProgressManager.getIndexOfLastLevel()) {
            Scenes.sceneFireworks.create();
            return;
        }

        int nextLevelIndex = instance.getNextLevelIndex();
        if (CampaignProgressManager.getInstance().isLevelLocked(nextLevelIndex)) return;

        getYioGdxGame(buttonYio).campaignLevelFactory.createCampaignLevel(nextLevelIndex);
    }
}
