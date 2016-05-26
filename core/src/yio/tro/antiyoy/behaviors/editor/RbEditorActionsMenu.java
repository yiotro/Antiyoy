package yio.tro.antiyoy.behaviors.editor;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbEditorActionsMenu extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        if (buttonLighty.id != 140) {
//            String textLine = buttonLighty.getText().get(0);
//            char numberChar = textLine.charAt(5);
//            getGameController(buttonLighty).getLevelEditor().setCurrentSlotNumber((int)numberChar);

            getGameController(buttonLighty).getLevelEditor().setCurrentSlotNumber(buttonLighty.id - 130);
        } else {
            getGameController(buttonLighty).getLevelEditor().saveSlot();
        }
        buttonLighty.menuControllerLighty.createEditorActionsMenu();
    }
}
