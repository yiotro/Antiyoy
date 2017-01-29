package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;

public class RbForceEnableShroomArts extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.specialActionController.forceEnableShroomArts();
    }
}
