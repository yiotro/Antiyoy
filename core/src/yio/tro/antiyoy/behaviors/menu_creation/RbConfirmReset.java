package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;

public class RbConfirmReset extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.createConfirmResetMenu();
    }
}
