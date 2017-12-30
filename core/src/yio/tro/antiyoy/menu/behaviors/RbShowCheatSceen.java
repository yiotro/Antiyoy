package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbShowCheatSceen extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneSecretScreen.create();
    }
}
