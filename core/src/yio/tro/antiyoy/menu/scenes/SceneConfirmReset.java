package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneConfirmReset extends AbstractScene{


    private ButtonYio basePanel;
    private ButtonYio yesButton;
    private ButtonYio noButton;


    public SceneConfirmReset(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.15), 410, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_reset"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.FROM_CENTER);

        yesButton = buttonFactory.getButton(generateRectangle(0.5, 0.4, 0.45, 0.05), 411, getString("menu_reset"));
        yesButton.setReaction(Reaction.rbResetProgress);
        yesButton.setShadow(false);
        yesButton.disableTouchAnimation();
        yesButton.setVisualHook(basePanel);
        yesButton.setAnimation(Animation.FROM_CENTER);

        noButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.45, 0.05), 412, getString("cancel"));
        noButton.setReaction(Reaction.rbMoreSettings);
        noButton.setShadow(false);
        noButton.disableTouchAnimation();
        noButton.setVisualHook(basePanel);
        noButton.setAnimation(Animation.FROM_CENTER);

        menuControllerYio.endMenuCreation();
    }
}