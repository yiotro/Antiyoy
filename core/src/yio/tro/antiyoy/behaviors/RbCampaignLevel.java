package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 18.11.2015.
 */
public class RbCampaignLevel extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        int selectedLevelIndex = getYioGdxGame(buttonYio).getSelectedLevelIndex();
        getYioGdxGame(buttonYio).campaignLevelFactory.createCampaignLevel(selectedLevelIndex);
    }
}
