package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneConfirmEndTurn extends AbstractModalScene {


    private ButtonYio basePanel;
    private ButtonYio confirmButton;
    private ButtonYio cancelButton;
    ButtonYio invisibleButton;


    public SceneConfirmEndTurn(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        createInvisibleButton();
        createBasePanel();
        createConfirmButton();
        createCancelButton();

//        forceElementsToTop();
    }


    private void createInvisibleButton() {
        invisibleButton = buttonFactory.getButton(generateRectangle(-0.1, -0.1, 0.01, 0.01), 323, " ");
        invisibleButton.setReaction(Reaction.rbNothing);
        invisibleButton.setShadow(false);
        invisibleButton.setAnimation(Animation.none);
        invisibleButton.setTouchOffset(2 * GraphicsYio.height);
    }


    private void forceElementsToTop() {
        forceElementToTop(basePanel);
        forceElementToTop(confirmButton);
        forceElementToTop(cancelButton);
    }


    private void createCancelButton() {
        cancelButton = buttonFactory.getButton(generateRectangle(0.1, 0.12, 0.4, 0.06), 322, getString("cancel"));
        cancelButton.setReaction(Reaction.rbHideEndTurnConfirm);
        cancelButton.setShadow(false);
        cancelButton.setAnimation(Animation.fixed_down);
    }


    private void createConfirmButton() {
        confirmButton = buttonFactory.getButton(generateRectangle(0.5, 0.12, 0.4, 0.06), 321, getString("yes"));
        confirmButton.setReaction(Reaction.rbEndTurn);
        confirmButton.setShadow(false);
        confirmButton.setAnimation(Animation.fixed_down);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.12, 0.8, 0.2), 320, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_end_turn"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.fixed_down);
    }


    @Override
    public void hide() {
        for (int i = 320; i <= 329; i++) {
            ButtonYio b = menuControllerYio.getButtonById(i);
            if (b == null) continue;

            b.destroy();
            b.appearFactor.setValues(0, 0);
        }
    }
}