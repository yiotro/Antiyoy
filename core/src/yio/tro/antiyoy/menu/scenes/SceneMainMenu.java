package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.campaign.CampaignLevelFactory;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
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

        exitButton = buttonFactory.getButton(generateSquare(0.8, 0.87, 0.15 * YioGdxGame.screenRatio), 1, null);
        menuControllerYio.loadButtonOnce(exitButton, "shut_down.png");
        exitButton.setShadow(true);
        exitButton.setAnimation(Animation.UP);
        exitButton.setReaction(Reaction.rbExit);
        exitButton.setTouchOffset(0.05f * GraphicsYio.width);
        exitButton.disableTouchAnimation();

        settingsButton = buttonFactory.getButton(generateSquare(0.05, 0.87, 0.15 * YioGdxGame.screenRatio), 2, null);
        menuControllerYio.loadButtonOnce(settingsButton, "settings_icon.png");
        settingsButton.setShadow(true);
        settingsButton.setAnimation(Animation.UP);
        settingsButton.setReaction(Reaction.rbSettingsMenu);
        settingsButton.setTouchOffset(0.05f * GraphicsYio.width);
        settingsButton.disableTouchAnimation();

        playButton = buttonFactory.getButton(generateSquare(0.3, 0.35, 0.4 * YioGdxGame.screenRatio), 3, null);
        menuControllerYio.loadButtonOnce(playButton, "play_button.png");
        playButton.setReaction(playButtonReaction);
        playButton.disableTouchAnimation();
        playButton.selectionFactor.setValues(1, 0);

        checkToCreateResumeButton();

        menuControllerYio.endMenuCreation();
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

        resumeButton = buttonFactory.getButton(generateRectangle(0.2, 0.05, 0.6, 0.055), 5, getString("in_game_menu_resume"));
        resumeButton.setAnimation(Animation.DOWN);
        resumeButton.setTouchOffset(0.05f * GraphicsYio.width);
        resumeButton.setReaction(loadLastSaveReaction);
        resumeButton.disableTouchAnimation();
    }


    private void onPlayButtonPressed() {
        if (!CampaignProgressManager.getInstance().isAtLeastOneLevelCompleted()) {
            CampaignLevelFactory campaignLevelFactory = menuControllerYio.yioGdxGame.campaignLevelFactory;
            campaignLevelFactory.createCampaignLevel(0);
            return;
        }

        Scenes.sceneChoodeGameModeMenu.create();
        menuControllerYio.yioGdxGame.setGamePaused(true);
        menuControllerYio.yioGdxGame.setAnimToPlayButtonSpecial();
    }
}