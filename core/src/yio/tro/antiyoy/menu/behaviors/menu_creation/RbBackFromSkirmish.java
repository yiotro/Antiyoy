package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbBackFromSkirmish extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        if (Scenes.sceneSkirmishMenu.startButton.selectionFactor.get() > 0.5) return;

        Scenes.sceneSkirmishMenu.saveValues();
        Scenes.sceneChooseGameMode.create();
    }
}
