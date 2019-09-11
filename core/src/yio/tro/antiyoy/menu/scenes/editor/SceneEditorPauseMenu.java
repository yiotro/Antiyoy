package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.data_storage.EncodeManager;
import yio.tro.antiyoy.gameplay.editor.EditorSaveSystem;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SceneEditorPauseMenu extends AbstractScene{

    public ButtonYio resumeButton;
    private double bHeight;
    private double bottomY;
    private double x;
    private double bWidth;
    public ButtonYio mainMenuButton;
    private ButtonYio basePanel;
    private double y;
    private Reaction rbResume;
    private ButtonYio exportButton;
    private ButtonYio playButton;
    private Reaction rbPlay;
    private Reaction rbExport;


    public SceneEditorPauseMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        initReactions();
    }


    private void initReactions() {
        rbResume = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onResumeButtonPressed();
            }
        };
        rbPlay = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onPlayButtonPressed();
            }
        };
        rbExport = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onExportButtonPressed();
            }
        };
    }


    private void onExportButtonPressed() {
        EncodeManager encodeManager = getGameController().encodeManager;
        encodeManager.performToClipboard();
        if (encodeManager.isCurrentLevelTooBig()) {
            Scenes.sceneMapTooBig.create();
        }
        Scenes.sceneNotification.show("exported");
    }


    private void onPlayButtonPressed() {
        GameController gameController = getGameController();
        EditorSaveSystem editorSaveSystem = gameController.editorSaveSystem;
        editorSaveSystem.playLevel(GameRules.editorSlotNumber);
    }


    private void onResumeButtonPressed() {
        menuControllerYio.yioGdxGame.gameView.appear();
        Scenes.sceneEditorOverlay.create();
        menuControllerYio.yioGdxGame.setGamePaused(false);
        menuControllerYio.yioGdxGame.setAnimToResumeButtonSpecial();
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        initMetrics();
        basePanel = buttonFactory.getButton(generateRectangle(x, bottomY, bWidth, 4 * bHeight), 250, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimation(Animation.from_center);

        y = bottomY;

        mainMenuButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 252, getString("in_game_menu_main_menu"));
        mainMenuButton.setReaction(Reaction.rbMainMenu);
        mainMenuButton.setShadow(false);
        mainMenuButton.setAnimation(Animation.from_center);
        mainMenuButton.tagAsBackButton();
        mainMenuButton.setVisualHook(basePanel);
        y += bHeight;

        exportButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 253, getString("export"));
        exportButton.setReaction(rbExport);
        exportButton.setShadow(false);
        exportButton.setAnimation(Animation.from_center);
        exportButton.setVisualHook(basePanel);
        y += bHeight;

        playButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 254, getString("play"));
        playButton.setReaction(rbPlay);
        playButton.setShadow(false);
        playButton.setAnimation(Animation.from_center);
        playButton.setVisualHook(basePanel);
        y += bHeight;

        resumeButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 255, getString("in_game_menu_resume"));
        resumeButton.setReaction(rbResume);
        resumeButton.setShadow(false);
        resumeButton.setAnimation(Animation.from_center);
        resumeButton.setVisualHook(basePanel);
        y += bHeight;

        menuControllerYio.yioGdxGame.gameController.resetTouchMode();
        menuControllerYio.endMenuCreation();
    }


    private void initMetrics() {
        bHeight = 0.09;
        bottomY = 0.3;
        bWidth = 0.76;
        x = (1 - bWidth) / 2;
    }
}
