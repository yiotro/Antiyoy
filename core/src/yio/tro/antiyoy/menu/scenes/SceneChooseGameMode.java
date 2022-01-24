package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.GlobalStatistics;
import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.Yio;

public class SceneChooseGameMode extends AbstractScene{


    public ButtonYio skirmishButton;
    private Reaction rbUserLevels;
    private ButtonYio basePanel;
    private ButtonYio userLevelsButton;
    private ButtonYio campaignButton;
    private ButtonYio loadGameButton;
    private ButtonYio editorButton;
    private ButtonYio secretScreenButton;
    private Reaction rbEditor;


    public SceneChooseGameMode(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        initReactions();
    }


    private void initReactions() {
        rbUserLevels = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneUserLevels.create();
            }
        };
        rbEditor = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneEditorLobby.create();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, true, true);

        createBasePanel();

        createSkirmishButton();
        createUserLevelsButton();
        createCampaignButton();
        createLoadGameButton();
        createEditorButton();

        createInvisibleButton();
        createMyOtherGamesButton();

        menuControllerYio.spawnBackButton(76, Reaction.rbMainMenu);

        menuControllerYio.endMenuCreation();
    }


    private void createMyOtherGamesButton() {
        if (SingleMessages.checkOutMyOtherGames) return;
        if (GlobalStatistics.getInstance().timeInGame < 8 * 60 * 60 * 60) return; // 8 hours

        ButtonYio myOtherGamesButton = buttonFactory.getButton(generateRectangle(0.2, 0.05, 0.6, 0.06), 79, getString("my_games"));
        myOtherGamesButton.setAnimation(Animation.down);
        myOtherGamesButton.setReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                SceneHelpIndex.createMyGamesArticle();

                SingleMessages.checkOutMyOtherGames = true;
                SingleMessages.save();
            }
        });
    }


    private void createInvisibleButton() {
        double size = 0.07;
        secretScreenButton = buttonFactory.getButton(generateSquare(1 - size, 1 - GraphicsYio.convertToHeight(size), size), 78, null);
        secretScreenButton.setVisible(false);
        secretScreenButton.setReaction(Reaction.rbShowCheatSceen);
    }


    private void createEditorButton() {
        editorButton = buttonFactory.getButton(generateRectangle(0.1, 0.54, 0.8, 0.08), 77, getString("editor"));
        editorButton.setReaction(rbEditor);
        finishMakingInnerButton(editorButton);
    }


    private void createLoadGameButton() {
        loadGameButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.08), 75, getString("choose_game_mode_load"));
        loadGameButton.setReaction(Reaction.rbLoadGame);
        finishMakingInnerButton(loadGameButton);
    }


    private void createCampaignButton() {
        campaignButton = buttonFactory.getButton(generateRectangle(0.1, 0.38, 0.8, 0.08), 74, getString("choose_game_mode_campaign"));
        campaignButton.setReaction(Reaction.rbCampaignMenu);
        finishMakingInnerButton(campaignButton);
    }


    private void createUserLevelsButton() {
        userLevelsButton = buttonFactory.getButton(generateRectangle(0.1, 0.46, 0.8, 0.08), 73, getString("user_levels"));
        userLevelsButton.setReaction(rbUserLevels);
        finishMakingInnerButton(userLevelsButton);
    }


    private void createSkirmishButton() {
        skirmishButton = buttonFactory.getButton(generateRectangle(0.1, 0.62, 0.8, 0.08), 72, getString("choose_game_mode_skirmish"));
        skirmishButton.setReaction(Reaction.rbSkirmishMenu);
        finishMakingInnerButton(skirmishButton);
    }


    private void finishMakingInnerButton(ButtonYio buttonYio) {
        buttonYio.setShadow(false);
        buttonYio.setAnimation(Animation.from_center);
        buttonYio.setVisualHook(basePanel);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 70, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimation(Animation.from_center);
    }
}