package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.TextViewElement;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;

import java.util.ArrayList;

public class SceneAreaSelectionUI extends AbstractGameplayScene {


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
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        FieldController fieldController = gameController.fieldController;
        DiplomacyManager diplomacyManager = fieldController.diplomacyManager;
        diplomacyManager.disableAreaSelectionMode();

        ArrayList<Hex> moveZone = fieldController.moveZoneManager.moveZone;
        if (moveZone.size() == 0) return;

        if (moveZone.get(0).colorIndex == gameController.turn) {
            int asFilterColor = gameController.selectionManager.getAsFilterColor();
            DiplomaticEntity entity = diplomacyManager.getEntity(asFilterColor);

            Scenes.sceneHexSaleDialog.create();
            Scenes.sceneHexSaleDialog.dialog.setData(entity, moveZone);
        } else {
            Scenes.sceneHexPurchaseDialog.create();
            Scenes.sceneHexPurchaseDialog.dialog.setData(diplomacyManager.getMainEntity(), moveZone);
        }
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
        textViewTitle.setTextValue(getString("hex_trade"));
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
