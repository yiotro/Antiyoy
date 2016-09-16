package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

public class RbMoreSkirmishOptions extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.saveSkirmishSettings();
        buttonYio.menuControllerYio.createMoreSkirmishOptionsMenu(ReactBehavior.rbSaveMoreSkirmishOptions);
        buttonYio.menuControllerYio.loadMoreSkirmishOptions();
    }
}
