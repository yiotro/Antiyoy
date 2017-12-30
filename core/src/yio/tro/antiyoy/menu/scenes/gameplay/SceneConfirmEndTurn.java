package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public class SceneConfirmEndTurn extends AbstractGameplayScene {


    public SceneConfirmEndTurn(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.12, 0.9, 0.2), 320, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_end_turn"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.FIXED_DOWN);

        ButtonYio confirmButton = buttonFactory.getButton(generateRectangle(0.5, 0.12, 0.45, 0.07), 321, getString("yes"));
        confirmButton.setReaction(Reaction.rbEndTurn);
        confirmButton.setShadow(false);
        confirmButton.setAnimation(Animation.FIXED_DOWN);
        confirmButton.disableTouchAnimation();

        ButtonYio cancelButton = buttonFactory.getButton(generateRectangle(0.05, 0.12, 0.45, 0.07), 322, getString("cancel"));
        cancelButton.setReaction(Reaction.rbHideEndTurnConfirm);
        cancelButton.setShadow(false);
        cancelButton.setAnimation(Animation.FIXED_DOWN);
        cancelButton.disableTouchAnimation();

        for (int i = 30; i <= 32; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            buttonYio.setTouchable(false);
        }
        for (int i = 38; i <= 39; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.setTouchable(false);
        }
    }


    @Override
    public void hide() {
        for (int i = 320; i <= 322; i++) {
            ButtonYio b = menuControllerYio.getButtonById(i);
            b.destroy();
            b.appearFactor.setValues(0, 0);
        }

        for (int i = 30; i <= 32; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            buttonYio.setTouchable(true);
        }
        for (int i = 38; i <= 39; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.setTouchable(true);
        }
    }
}