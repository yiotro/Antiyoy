package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.gameplay.CampaignController;
import yio.tro.antiyoy.menu.ButtonYio;

public class RbResetProgress extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        CampaignController.getInstance().resetProgress();
        buttonYio.menuControllerYio.levelSelector.update();
        buttonYio.menuControllerYio.createCampaignMenu();
    }
}
