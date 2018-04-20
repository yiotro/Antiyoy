package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbEditorActionsMenu extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        if (buttonYio.id != 140) {
            getGameController(buttonYio).getLevelEditor().setCurrentSlotNumber(buttonYio.id - 130);
        } else {
            getGameController(buttonYio).getLevelEditor().saveSlot();
        }
        Scenes.sceneEditorActions.create();
    }
}
