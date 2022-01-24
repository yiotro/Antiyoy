package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.SkipLevelManager;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.TextLabelElement;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class ScenePauseMenu extends AbstractScene{


    public ButtonYio resumeButton;
    private double bHeight;
    private double bottomY;
    private double x;
    private double bWidth;
    private ButtonYio cheatsButton;
    private ButtonYio restartButton;
    private ButtonYio chooseLevelButton;
    private ButtonYio mainMenuButton;
    private ButtonYio basePanel;
    private Reaction rbCheats;
    private double y;
    private ButtonYio specialActionButton;
    private Reaction rbSkipLevelMenu;
    TextLabelElement levelLabel;


    public ScenePauseMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        levelLabel = null;
        initReactions();
    }


    private void initReactions() {
        rbCheats = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneCheatsMenu.create();
            }
        };

        rbSkipLevelMenu = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneConfirmSkipLevel.create();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        initMetrics();
        basePanel = buttonFactory.getButton(generateRectangle(x, bottomY, bWidth, 4 * bHeight), 40, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimation(Animation.from_center);

        y = bottomY;

        mainMenuButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 42, getString("in_game_menu_main_menu"));
        mainMenuButton.setReaction(Reaction.rbMainMenu);
        mainMenuButton.setShadow(false);
        mainMenuButton.setAnimation(Animation.from_center);
        mainMenuButton.setVisualHook(basePanel);
        y += bHeight;

        chooseLevelButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 43, getString("in_game_menu_save"));
        chooseLevelButton.setReaction(Reaction.rbSaveGame);
        chooseLevelButton.setShadow(false);
        chooseLevelButton.setAnimation(Animation.from_center);
        chooseLevelButton.setVisualHook(basePanel);
        y += bHeight;

        createThirdButton();

        resumeButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 45, getString("in_game_menu_resume"));
        resumeButton.setReaction(Reaction.rbResumeGame);
        resumeButton.setShadow(false);
        resumeButton.setAnimation(Animation.from_center);
        resumeButton.setVisualHook(basePanel);
        y += bHeight;

        checkToAddCheatsStuff();
        createLevelLabel();

        menuControllerYio.yioGdxGame.gameController.resetTouchMode();
        menuControllerYio.endMenuCreation();
    }


    private void createLevelLabel() {
        initLevelLabel();
        levelLabel.appear();
        updateLevelLabel();
    }


    private void updateLevelLabel() {
        if (GameRules.campaignMode && !GameRules.replayMode) {
            levelLabel.setTitle(getString("menu_level") + ": " + CampaignProgressManager.getInstance().currentLevelIndex);
            return;
        }
        levelLabel.setTitle("");
    }


    private void initLevelLabel() {
        if (levelLabel != null) return;
        levelLabel = new TextLabelElement(menuControllerYio);
        levelLabel.setPosition(generateRectangle(0, 0.95, 1, 0.05));
        levelLabel.setAnimation(Animation.up);
        menuControllerYio.addElementToScene(levelLabel);
    }


    private void createThirdButton() {
        if (GameRules.replayMode) {
            createSpecialActionButtonInReplayMode();
            return;
        }

        SkipLevelManager skipLevelManager = getGameController().skipLevelManager;
        int currentLevelIndex = CampaignProgressManager.getInstance().currentLevelIndex;
        if (GameRules.campaignMode && skipLevelManager.canSkipLevel(currentLevelIndex)) {
            createSkipLevelButton();
            return;
        }

        restartButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 44, getString("in_game_menu_restart"));
        restartButton.setReaction(Reaction.rbRestartGame);
        restartButton.setShadow(false);
        restartButton.setAnimation(Animation.from_center);
        restartButton.setVisualHook(basePanel);
        y += bHeight;
    }


    private void createSkipLevelButton() {
        specialActionButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 47, null);
        specialActionButton.setTextLine(getString("next"));
        specialActionButton.setReaction(rbSkipLevelMenu);
        menuControllerYio.buttonRenderer.renderButton(specialActionButton);
        specialActionButton.setShadow(false);
        specialActionButton.setAnimation(Animation.from_center);
        specialActionButton.setVisualHook(basePanel);
        y += bHeight;
    }


    private void createSpecialActionButtonInReplayMode() {
        int currentLevelIndex = CampaignProgressManager.getInstance().currentLevelIndex;
        specialActionButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 47, null);
        specialActionButton.cleatText();
        if (currentLevelIndex > 0) {
            specialActionButton.setTextLine(getString("next"));
            specialActionButton.setReaction(Reaction.rbNextLevel);
        } else {
            specialActionButton.setTextLine(getString("edit"));
            specialActionButton.setReaction(new Reaction() {
                @Override
                public void perform(ButtonYio buttonYio) {
                    onSpecialActionButtonPressed();
                }
            });
        }
        menuControllerYio.buttonRenderer.renderButton(specialActionButton);
        specialActionButton.setShadow(false);
        specialActionButton.setAnimation(Animation.from_center);
        specialActionButton.setVisualHook(basePanel);
        y += bHeight;
    }


    private void onSpecialActionButtonPressed() {
        getGameController().getLevelEditor().launchEditLevelMode();
    }


    private void checkToAddCheatsStuff() {
        if (!DebugFlags.cheatsEnabled) return;
        if (GameRules.replayMode) return;
        if (getGameController().playersNumber != 1) return;

        double oieWidth = 0.4;
        double oieHeight = 0.055;
        cheatsButton = buttonFactory.getButton(generateRectangle(0.5 - oieWidth / 2, 0.02, oieWidth, oieHeight), 46, "Cheats");
        cheatsButton.setReaction(rbCheats);
        cheatsButton.setAnimation(Animation.fixed_down);
        cheatsButton.setTouchOffset(0.05f * GraphicsYio.width);
    }


    private void initMetrics() {
        bHeight = 0.09;
        bottomY = 0.3;
        bWidth = 0.76;
        x = (1 - bWidth) / 2;
    }
}