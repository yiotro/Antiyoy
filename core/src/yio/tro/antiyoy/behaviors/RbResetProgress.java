package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;

public class RbResetProgress extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).resetProgress();
        buttonYio.menuControllerYio.levelSelector.update();
        buttonYio.menuControllerYio.createCampaignMenu();
    }
}
