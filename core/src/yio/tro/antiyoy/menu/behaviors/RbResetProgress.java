package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbResetProgress extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getGameController(buttonYio).resetProgress();
        Scenes.sceneCampaignMenu.updateLevelSelector();

        Scenes.sceneCampaignMenu.create();
    }
}
