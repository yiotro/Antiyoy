package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbSaveGame extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneSaveLoad.create();
        Scenes.sceneSaveLoad.setOperationType(false);
    }
}
