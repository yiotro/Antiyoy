package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.editor.EditorSaveSystem;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneEditorOverlay extends AbstractScene{


    public static final double PANEL_HEIGHT = GraphicsYio.convertToHeight(0.11);
    private ButtonYio optionsButtons;
    boolean readyToShrink;
    private Reaction rbShowEditorChecks;
    private Reaction rbPauseMenu;
    private Reaction rbEditProvinces;
    private Reaction rbEditDiplomacy;


    public SceneEditorOverlay(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        readyToShrink = true;
        initReactions();
    }


    @Override
    public void create() {
        beginCreation();

        createPauseMenuButton();
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
            buttonYio.setAnimation(Animation.down);
        }

        menuControllerYio.endMenuCreation();
    }


    private void createIcons() {
        createIcon(142, 0, "hex_black.png", EditorReactions.rbShowHexPanel);
        createIcon(143, 1, "icon_move.png", Reaction.rbInputModeMove);
        createIcon(144, 2, "field_elements/man0_low.png", EditorReactions.rbShowObjectPanel);

        createIconFromRight(149, 4, "menu/editor/editor_flag.png", rbEditDiplomacy);
        createIconFromRight(147, 3, "menu/editor/editor_money_icon.png", rbEditProvinces);
        createIconFromRight(148, 2, "menu/editor/chk.png", rbShowEditorChecks);
        createIconFromRight(146, 1, "menu/editor/automation_icon.png", EditorReactions.rbShowAutomationPanel);
        createIconFromRight(145, 0, "menu/editor/params_icon.png", EditorReactions.rbShowEditorParams);

        readyToShrink = false;
    }


    private void createBasePanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0, 0, 1, PANEL_HEIGHT), 141, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);
    }


    private void createPauseMenuButton() {
        ButtonYio menuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07), 140, null);
        menuControllerYio.loadButtonOnce(menuButton, "menu_icon.png");
        menuButton.setReaction(rbPauseMenu);
        menuButton.setAnimation(Animation.up);
        menuButton.enableRectangularMask();
    }


    private void initReactions() {
        rbShowEditorChecks = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneEditorGameRulesPanel.onTumblerButtonPressed();
            }
        };
        rbPauseMenu = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onPauseMenuButtonPressed();
            }
        };
        rbEditProvinces = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                getGameController(buttonYio).levelEditorManager.onEditProvincesButtonPressed();
            }
        };
        rbEditDiplomacy = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                getGameController(buttonYio).levelEditorManager.onEditDiplomacyButtonPressed();
            }
        };
    }


    private void onPauseMenuButtonPressed() {
        YioGdxGame yioGdxGame = menuControllerYio.yioGdxGame;
        EditorSaveSystem editorSaveSystem = yioGdxGame.gameController.editorSaveSystem;
        yioGdxGame.gameController.levelEditorManager.onExitedToPauseMenu();
        editorSaveSystem.saveSlot(GameRules.editorSlotNumber);
        Scenes.sceneEditorPauseMenu.create();
        yioGdxGame.setGamePaused(true);
    }


    private void cachePanels() {
        Scenes.sceneEditorHexPanel.create();
        Scenes.sceneEditorObjectPanel.create();
        Scenes.sceneEditorParams.create();
        Scenes.sceneEditorAutomationPanel.create();
        Scenes.sceneEditorGameRulesPanel.create();
        Scenes.sceneEditorDiplomacy.create();

        menuControllerYio.hideAllEditorPanels();
    }


    private void createIcon(int id, int place, String texturePath, Reaction rb) {
        ButtonYio iconButton = buttonFactory.getButton(generateSquare(place * GraphicsYio.convertToWidth(PANEL_HEIGHT), 0, PANEL_HEIGHT), id, null);
        menuControllerYio.loadButtonOnce(iconButton, texturePath);
        iconButton.setReaction(rb);

        shrinkIcon(iconButton);
    }


    private void createIconFromRight(int id, int place, String texturePath, Reaction rb) {
        ButtonYio iconButton = buttonFactory.getButton(generateSquare(1 - (place + 1) * GraphicsYio.convertToWidth(PANEL_HEIGHT), 0, PANEL_HEIGHT), id, null);
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