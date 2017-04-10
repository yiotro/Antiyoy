package yio.tro.antiyoy.behaviors.editor;

import yio.tro.antiyoy.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;

public class RbEditorHideConfirmClearLevelMenu extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.hideEditorConfirmClearLevelMenu();
    }
}
