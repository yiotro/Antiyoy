package yio.tro.antiyoy.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbEditorActionsMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (buttonYio.id != 140) {
//            String textLine = buttonLighty.getText().get(0);
//            char numberChar = textLine.charAt(5);
//            getGameController(buttonLighty).getLevelEditor().setCurrentSlotNumber((int)numberChar);

            getGameController(buttonYio).getLevelEditor().setCurrentSlotNumber(buttonYio.id - 130);
        } else {
            getGameController(buttonYio).getLevelEditor().saveSlot();
        }
        buttonYio.menuControllerYio.createEditorActionsMenu();
    }
}
