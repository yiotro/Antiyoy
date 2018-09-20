package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.replays.ReplayManager;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

public class RbStartInstantReplay extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        GameController gameController = buttonYio.getMenuControllerYio().yioGdxGame.gameController;
        ReplayManager replayManager = gameController.replayManager;
        int currentLevelIndex = CampaignProgressManager.getInstance().currentLevelIndex;
        replayManager.startInstantReplay();
        CampaignProgressManager.getInstance().setCurrentLevelIndex(currentLevelIndex);
    }
}
