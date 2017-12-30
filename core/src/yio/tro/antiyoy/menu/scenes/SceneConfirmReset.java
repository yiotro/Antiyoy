package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneConfirmReset extends AbstractScene{


    public SceneConfirmReset(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.2), 410, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_reset"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.FROM_CENTER);

        ButtonYio restartButton = buttonFactory.getButton(generateRectangle(0.5, 0.4, 0.45, 0.07), 411, getString("menu_reset"));
        restartButton.setReaction(Reaction.rbResetProgress);
        restartButton.setShadow(false);
        restartButton.setAnimation(Animation.FROM_CENTER);

        ButtonYio cancelButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.45, 0.07), 412, getString("cancel"));
        cancelButton.setReaction(Reaction.rbMoreSettings);
        cancelButton.setShadow(false);
        cancelButton.setAnimation(Animation.FROM_CENTER);

        menuControllerYio.endMenuCreation();
    }
}