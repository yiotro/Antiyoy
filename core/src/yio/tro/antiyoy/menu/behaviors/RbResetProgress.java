package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbResetProgress extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getGameController(buttonYio).resetProgress();
        buttonYio.menuControllerYio.levelSelector.update();
        Scenes.sceneCampaignMenu.create();
    }
}
