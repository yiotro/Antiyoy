package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneEditorInstruments extends AbstractScene{


    public static final double ICON_SIZE = 0.07;
    private ButtonYio optionsButtons;
    boolean readyToShrink;
    private Reaction rbShowEditorChecks;


    public SceneEditorInstruments(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        readyToShrink = true;

        rbShowEditorChecks = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneEditorChecks.onTumblerButtonPressed();
            }
        };
    }


    @Override
    public void create() {
        beginCreation();

        createMenuButton();
        createBasePanel();
        createIcons();

        endCreation();
    }


    private void beginCreation() {
        menuControllerYio.beginMenuCreation();

        cachePanels();
    }


    private void endCreation() {
        for (int i = 141; i <= 149; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;

            buttonYio.appearFactor.appear(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimation(Animation.DOWN);
        }

        menuControllerYio.endMenuCreation();
    }


    private void createIcons() {
        createIcon(142, 0, "hex_black.png", EditorReactions.rbShowHexPanel);
        createIcon(143, 1, "icon_move.png", Reaction.rbInputModeMove);
        createIcon(144, 2, "field_elements/man0_low.png", EditorReactions.rbShowObjectPanel);
//        createIcon(147, 3, "menu/editor/coin.png", EditorReactions.rbEditorShowMoneyPanel);

        createIconFromRight(148, 2, "menu/editor/chk.png", rbShowEditorChecks);
        createIconFromRight(146, 1, "menu/editor/automation_icon.png", EditorReactions.rbShowAutomationPanel);
        createIconFromRight(145, 0, "menu/editor/params_icon.png", EditorReactions.rbShowEditorParams);

        readyToShrink = false;
    }


    private void createBasePanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 0.07), 141, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);
    }


    private void createMenuButton() {
        ButtonYio menuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07), 140, null);
        menuControllerYio.loadButtonOnce(menuButton, "menu_icon.png");
        menuButton.setReaction(EditorReactions.rbEditorActionsMenu);
        menuButton.setAnimation(Animation.UP);
        menuButton.enableRectangularMask();
        menuButton.disableTouchAnimation();
    }


    private void cachePanels() {
        Scenes.sceneEditorHexPanel.create();
        Scenes.sceneEditorObjectPanel.create();
        Scenes.sceneEditorParams.create();
        Scenes.sceneEditorAutomationPanel.create();
//        Scenes.sceneEditorMoneyPanel.create();
        Scenes.sceneEditorChecks.create();

        menuControllerYio.hideAllEditorPanels();
    }


    private void createIcon(int id, int place, String texturePath, Reaction rb) {
        ButtonYio iconButton = buttonFactory.getButton(generateSquare(place * ICON_SIZE / YioGdxGame.screenRatio, 0, ICON_SIZE), id, null);
        menuControllerYio.loadButtonOnce(iconButton, texturePath);
        iconButton.setReaction(rb);

        shrinkIcon(iconButton);
    }


    private void createIconFromRight(int id, int place, String texturePath, Reaction rb) {
        ButtonYio iconButton = buttonFactory.getButton(generateSquare(1 - (place + 1) * ICON_SIZE / YioGdxGame.screenRatio, 0, ICON_SIZE), id, null);
        menuControllerYio.loadButtonOnce(iconButton, texturePath);
        iconButton.setReaction(rb);

        shrinkIcon(iconButton);
    }


    private void shrinkIcon(ButtonYio buttonYio) {
        if (!readyToShrink) return;

        RectangleYio pos = buttonYio.position;
        float delta = (float) (0.12f * pos.width);

        pos.x += delta;
        pos.y += delta;
        pos.width -= 2 * delta;
        pos.height -= 2 * delta;
        buttonYio.setPosition(pos);
        buttonYio.setTouchOffset(delta);
    }
}