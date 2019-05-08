package yio.tro.antiyoy.menu.behaviors.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

/**
 * Created by yiotro on 27.11.2015.
 */
public class RbEditorExport extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getGameController(buttonYio).getLevelEditor().exportLevel();
    }
}
