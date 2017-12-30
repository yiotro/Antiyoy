package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbEditorSlotMenu extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneEditorSlotsMenu.create();
        getGameController(buttonYio).turnOffEditorMode();
    }
}
