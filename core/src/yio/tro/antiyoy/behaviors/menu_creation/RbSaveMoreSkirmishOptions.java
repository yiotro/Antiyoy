package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

public class RbSaveMoreSkirmishOptions extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.createSkirmishMenu();
        buttonYio.menuControllerYio.saveMoreSkirmishOptions();
    }
}
