package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.TextViewElement;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneAreaSelectionUI extends AbstractGameplayScene{


    private Reaction rbCancel;
    private Reaction rbApply;
    private ButtonYio cancelButton;
    private ButtonYio applyButton;
    private TextViewElement textViewTitle;


    public SceneAreaSelectionUI(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        textViewTitle = null;

        initReactions();
    }


    private void initReactions() {
        rbCancel = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                FieldController fieldController = menuControllerYio.yioGdxGame.gameController.fieldController;
                fieldController.diplomacyManager.disableAreaSelectionMode();
            }
        };

        rbApply = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onApplyButtonPressed();
            }
        };
    }


    private void onApplyButtonPressed() {
        FieldController fieldController = menuControllerYio.yioGdxGame.gameController.fieldController;
        fieldController.diplomacyManager.disableAreaSelectionMode();

        if (fieldController.moveZoneManager.moveZone.size() == 0) return;

        Scenes.sceneHexPurchaseDialog.create();
        Scenes.sceneHexPurchaseDialog.dialog.setData(
                fieldController.diplomacyManager.getMainEntity(),
                fieldController.moveZoneManager.moveZone
        );
    }


    @Override
    public void create() {
        createButtons();
        createTextView();
    }


    private void createTextView() {
        initTextView();

        textViewTitle.appear();
    }


    private void initTextView() {
        if (textViewTitle != null) return;

        textViewTitle = new TextViewElement();
        textViewTitle.setPosition(generateRectangle(0.45, 0.95, 0.1, 0.02));
        textViewTitle.setTextValue(getString("hex_purchase"));
        textViewTitle.setAnimation(Animation.UP);
        menuControllerYio.addElementToScene(textViewTitle);
    }


    private void createButtons() {
        double bw = 0.12;

        cancelButton = buttonFactory.getButton(generateRectangle(0.5 - bw - bw / 2, 0.86, bw, GraphicsYio.convertToHeight(bw)), 870, null);
        menuControllerYio.loadButtonOnce(cancelButton, "game/cancel.png");
        cancelButton.setAnimation(Animation.UP);
        cancelButton.disableTouchAnimation();
        cancelButton.setTouchOffset(0.01f * GraphicsYio.width);
        cancelButton.setReaction(rbCancel);

        applyButton = buttonFactory.getButton(generateRectangle(0.5 + bw - bw / 2, 0.86, bw, GraphicsYio.convertToHeight(bw)), 871, null);
        menuControllerYio.loadButtonOnce(applyButton, "game/ok_icon.png");
        applyButton.setAnimation(Animation.UP);
        applyButton.disableTouchAnimation();
        applyButton.setTouchOffset(0.01f * GraphicsYio.width);
        applyButton.setReaction(rbApply);
    }


    @Override
    public void hide() {
        destroyByIndex(870, 879);
        textViewTitle.destroy();
    }
}
