package yio.tro.antiyoy.behaviors.menu_creation;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 03.12.2015.
 */
public class RbSettingsMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        buttonLighty.menuControllerLighty.createSettingsMenu();
    }
}
