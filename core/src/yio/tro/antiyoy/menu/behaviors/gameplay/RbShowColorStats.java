package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

/**
 * Created by ivan on 29.12.2015.
 */
public class RbShowColorStats extends Reaction {



    @Override
    public void reactAction(ButtonYio buttonYio) {
        Scenes.sceneColorStats.create();

        ColorStatsRenderer colorStatsRenderer = new ColorStatsRenderer(buttonYio.menuControllerYio);
        ButtonYio statButton = buttonYio.menuControllerYio.getButtonById(56321);
        int[] playerHexCount = getGameController(buttonYio).fieldController.getPlayerHexCount();
        colorStatsRenderer.renderStatButton(statButton, playerHexCount);
    }
}
