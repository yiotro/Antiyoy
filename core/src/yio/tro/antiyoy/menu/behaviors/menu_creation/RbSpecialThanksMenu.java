package yio.tro.antiyoy.menu.behaviors.menu_creation;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbSpecialThanksMenu extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
//        Scenes.sceneInfoMenu.create("special_thanks", Reaction.rbInfo, 312837182);
        Scenes.sceneSpecialThanks.create();
    }
}
