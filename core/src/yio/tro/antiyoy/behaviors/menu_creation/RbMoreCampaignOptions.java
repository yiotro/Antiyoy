package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

public class RbMoreCampaignOptions extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.createMoreSkirmishOptionsMenu(ReactBehavior.rbExitToCampaign);
        buttonYio.menuControllerYio.sliders.get(2).setRunnerValueByIndex(3);
    }
}
