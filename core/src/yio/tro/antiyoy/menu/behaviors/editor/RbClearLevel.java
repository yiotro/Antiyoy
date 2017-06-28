package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 03.12.2015.
 */
public class RbClearLevel extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneEditorConfirmClear.hide();
        getGameController(buttonYio).clearLevel();
    }
}
