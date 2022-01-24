package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.OneTimeInfo;
import yio.tro.antiyoy.PlatformType;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.campaign.CampaignLevelFactory;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneMainMenu extends AbstractScene{


    public ButtonYio playButton;
    Reaction playButtonReaction;
    private ButtonYio settingsButton;
    private ButtonYio exitButton;
    private ButtonYio resumeButton;
    private Reaction loadLastSaveReaction;


    public SceneMainMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        initReactions();
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(0, false, true);

        createExitButton();
        createSettingsButton();
        createPlayButton();
        checkToCreateResumeButton();

        menuControllerYio.endMenuCreation();
    }


    private void createPlayButton() {
        playButton = buttonFactory.getButton(generateSquare(0.3, 0.35, 0.4 * YioGdxGame.screenRatio), 3, null);
        menuControllerYio.loadButtonOnce(playButton, "play_button.png");
        playButton.setReaction(playButtonReaction);
        playButton.selectionFactor.setValues(1, 0);
        playButton.selectionFactor.stopMoving();
    }


    private void createSettingsButton() {
        settingsButton = buttonFactory.getButton(generateSquare(0.05, 0.87, 0.15 * YioGdxGame.screenRatio), 2, null);
        menuControllerYio.loadButtonOnce(settingsButton, "settings_icon.png");
        settingsButton.setShadow(true);
        settingsButton.setAnimation(Animation.up);
        settingsButton.setReaction(Reaction.rbSettingsMenu);
        settingsButton.setTouchOffset(0.05f * GraphicsYio.width);
    }


    private void createExitButton() {
        if (YioGdxGame.platformType == PlatformType.ios) {
            createInfoButton();
            return;
        }

        exitButton = buttonFactory.getButton(generateSquare(0.8, 0.87, 0.15 * YioGdxGame.screenRatio), 1, null);
        menuControllerYio.loadButtonOnce(exitButton, "shut_down.png");
        exitButton.setShadow(true);
        exitButton.setAnimation(Animation.up);
        exitButton.setReaction(Reaction.rbExit);
        exitButton.setTouchOffset(0.05f * GraphicsYio.width);
    }


    private void createInfoButton() {
        ButtonYio infoButton = buttonFactory.getButton(generateSquare(0.8, 0.87, 0.15 * YioGdxGame.screenRatio), 1, null);
        menuControllerYio.loadButtonOnce(infoButton, "menu/info_icon.png");
        infoButton.setShadow(true);
        infoButton.setAnimation(Animation.up);
        infoButton.setTouchOffset(0.05f * GraphicsYio.width);
        infoButton.setReaction(Reaction.rbAboutGame);
    }


    private void initReactions() {
        playButtonReaction = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onPlayButtonPressed();
            }
        };

        loadLastSaveReaction = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                menuControllerYio.yioGdxGame.saveSystem.loadTopSlot();
            }
        };
    }


    public void checkToCreateResumeButton() {
        if (!SettingsManager.resumeButtonEnabled) return;

        double h = Math.max(0.055, GraphicsYio.convertToHeight(0.14));
        resumeButton = buttonFactory.getButton(generateRectangle(0.2, 0.05, 0.6, h), 5, getString("in_game_menu_resume"));
        resumeButton.setAnimation(Animation.down);
        resumeButton.setTouchOffset(0.05f * GraphicsYio.width);
        resumeButton.setReaction(loadLastSaveReaction);
    }


    private void onPlayButtonPressed() {
        if (!CampaignProgressManager.getInstance().isAtLeastOneLevelCompleted()) {
            CampaignLevelFactory campaignLevelFactory = menuControllerYio.yioGdxGame.campaignLevelFactory;
            campaignLevelFactory.createCampaignLevel(0);
            return;
        }

        Scenes.sceneChooseGameMode.create();
        menuControllerYio.yioGdxGame.setGamePaused(true);
        menuControllerYio.yioGdxGame.setAnimToPlayButtonSpecial();
        checkForAntiyoyOnlineAttraction();
    }


    private void checkForAntiyoyOnlineAttraction() {
        if (OneTimeInfo.getInstance().antiyoyOnline) return;
        Scenes.sceneAoButton.create();
    }
}