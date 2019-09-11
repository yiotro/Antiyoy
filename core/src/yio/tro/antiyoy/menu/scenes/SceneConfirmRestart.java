package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

public class SceneConfirmRestart extends AbstractScene{


    private ButtonYio basePanel;
    private ButtonYio restartButton;
    private ButtonYio cancelButton;


    public SceneConfirmRestart(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.15), 220, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_restart"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.from_center);

        restartButton = buttonFactory.getButton(generateRectangle(0.5, 0.4, 0.45, 0.05), 221, getString("in_game_menu_restart"));
        restartButton.setReaction(Reaction.rbRestartGame);
        restartButton.setShadow(false);
        restartButton.setVisualHook(basePanel);
        restartButton.setAnimation(Animation.from_center);

        cancelButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.45, 0.05), 222, getString("cancel"));
        cancelButton.setReaction(Reaction.rbPauseMenu);
        cancelButton.setShadow(false);
        cancelButton.setVisualHook(basePanel);
        cancelButton.setAnimation(Animation.from_center);

        menuControllerYio.endMenuCreation();
    }
}