package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

public class RbMoreCampaignOptions extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.createMoreSkirmishOptionsMenu(ReactBehavior.rbExitToCampaign);
    }
}
