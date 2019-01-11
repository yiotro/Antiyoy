package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbUnlockLevels extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        DebugFlags.unlockLevels = true;

        Scenes.sceneCampaignMenu.create();

        Scenes.sceneNotification.show("Levels unlocked");
    }
}
