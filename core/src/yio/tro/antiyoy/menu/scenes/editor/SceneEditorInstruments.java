package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SceneEditorInstruments extends AbstractScene{


    private ButtonYio optionsButtons;


    public SceneEditorInstruments(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
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

            buttonYio.appearFactor.beginSpawning(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimType(ButtonYio.ANIM_DOWN);
        }

        menuControllerYio.endMenuCreation();
    }


    private void createIcons() {
        createIcon(142, 0, "hex_black.png", EditorReactions.rbShowHexPanel);
        createIcon(143, 1, "icon_move.png", ReactBehavior.rbInputModeMove);
        createIcon(144, 2, "field_elements/man0_low.png", EditorReactions.rbShowObjectPanel);
//        createIcon(147, 3, "menu/editor/coin.png", EditorReactions.rbEditorShowMoneyPanel);

        createIconFromRight(146, 1, "menu/editor/automation_icon.png", EditorReactions.rbShowAutomationPanel);
        createIconFromRight(145, 0, "opened_level_icon.png", EditorReactions.rbShowOptionsPanel);
    }


    private void createBasePanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 0.07), 141, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);
    }


    private void createMenuButton() {
        ButtonYio menuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07), 140, null);
        menuControllerYio.loadButtonOnce(menuButton, "menu_icon.png");
        menuButton.setReactBehavior(EditorReactions.rbEditorActionsMenu);
        menuButton.setAnimType(ButtonYio.ANIM_UP);
        menuButton.enableRectangularMask();
        menuButton.disableTouchAnimation();
    }


    private void cachePanels() {
        Scenes.sceneEditorHexPanel.create();
        Scenes.sceneEditorObjectPanel.create();
        Scenes.sceneEditorOptionsPanel.create();
        Scenes.sceneEditorAutomationPanel.create();
//        Scenes.sceneEditorMoneyPanel.create();

        menuControllerYio.hideAllEditorPanels();
    }


    private void createIcon(int id, int place, String texturePath, ReactBehavior rb) {
        ButtonYio iconButton = buttonFactory.getButton(generateSquare(place * 0.07 / YioGdxGame.screenRatio, 0, 0.07), id, null);
        menuControllerYio.loadButtonOnce(iconButton, texturePath);
        iconButton.setReactBehavior(rb);
    }


    private void createIconFromRight(int id, int place, String texturePath, ReactBehavior rb) {
        ButtonYio iconButton = buttonFactory.getButton(generateSquare(1 - (place + 1) * 0.07 / YioGdxGame.screenRatio, 0, 0.07), id, null);
        menuControllerYio.loadButtonOnce(iconButton, texturePath);
        iconButton.setReactBehavior(rb);
    }
}