package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneConfirmEndTurn extends AbstractScene {


    public SceneConfirmEndTurn(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


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
        basePanel.setAnimType(ButtonYio.ANIM_DOWN);

        ButtonYio confirmButton = buttonFactory.getButton(generateRectangle(0.5, 0.12, 0.45, 0.07), 321, getString("yes"));
        confirmButton.setReactBehavior(ReactBehavior.rbEndTurn);
        confirmButton.setShadow(false);
        confirmButton.setAnimType(ButtonYio.ANIM_DOWN);
        confirmButton.disableTouchAnimation();

        ButtonYio cancelButton = buttonFactory.getButton(generateRectangle(0.05, 0.12, 0.45, 0.07), 322, getString("cancel"));
        cancelButton.setReactBehavior(ReactBehavior.rbHideEndTurnConfirm);
        cancelButton.setShadow(false);
        cancelButton.setAnimType(ButtonYio.ANIM_DOWN);
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
}