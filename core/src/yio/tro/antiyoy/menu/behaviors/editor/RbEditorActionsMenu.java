package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbEditorActionsMenu extends Reaction {

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
        Scenes.sceneEditorActions.create();
    }
}
