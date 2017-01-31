package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

public class RbMoreSkirmishOptions extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.saveSkirmishSettings();
        buttonYio.menuControllerYio.createMoreMatchOptionsMenu(ReactBehavior.rbSaveMoreSkirmishOptions);
        buttonYio.menuControllerYio.loadMoreSkirmishOptions();
    }
}
