package yio.tro.antiyoy.behaviors.editor;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 27.11.2015.
 */
public class RbEditorRandomize extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getGameController(buttonLighty).getLevelEditor().randomize();
    }
}
