package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneConfirmRestart extends AbstractScene{


    public SceneConfirmRestart(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.2), 220, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_restart"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.FROM_CENTER);

        ButtonYio restartButton = buttonFactory.getButton(generateRectangle(0.5, 0.4, 0.45, 0.07), 221, getString("in_game_menu_restart"));
        restartButton.setReaction(Reaction.rbRestartGame);
        restartButton.setShadow(false);
        restartButton.setAnimation(Animation.FROM_CENTER);

        ButtonYio cancelButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.45, 0.07), 222, getString("cancel"));
        cancelButton.setReaction(Reaction.rbPauseMenu);
        cancelButton.setShadow(false);
        cancelButton.setAnimation(Animation.FROM_CENTER);

        menuControllerYio.endMenuCreation();
    }
}