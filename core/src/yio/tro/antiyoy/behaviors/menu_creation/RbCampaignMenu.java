package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 05.08.14.
 */
public class RbCampaignMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).setGamePaused(true);
        buttonYio.menuControllerYio.createCampaignMenu();
        buttonYio.menuControllerYio.loadMoreCampaignOptions();
        getYioGdxGame(buttonYio).setAnimToPlayButtonSpecial();
    }
}
