package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

public class SceneConfirmSkipLevel extends AbstractScene{


    private Reaction rbSkipLevel;
    private ButtonYio basePanel;
    private ButtonYio yesButton;
    private ButtonYio noButton;
    private Reaction rbNo;
    private ButtonYio restartButton;


    public SceneConfirmSkipLevel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        initReactions();
    }


    private void initReactions() {
        rbSkipLevel = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                CampaignProgressManager instance = CampaignProgressManager.getInstance();
                int currentLevelIndex = instance.currentLevelIndex;
                instance.markLevelAsCompleted(currentLevelIndex);
                Scenes.sceneCampaignMenu.updateLevelSelector();

                int nextLevelIndex = instance.getNextLevelIndex();
                getYioGdxGame(buttonYio).campaignLevelFactory.createCampaignLevel(nextLevelIndex);
            }
        };

        rbNo = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.scenePauseMenu.create();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        createBasePanel();
        createYesButton();
        createNoButton();
        createRestartButton();

        menuControllerYio.endMenuCreation();
    }


    private void createNoButton() {
        noButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.45, 0.05), 862, getString("no"));
        noButton.setReaction(rbNo);
        noButton.setShadow(false);
        noButton.setVisualHook(basePanel);
        noButton.setAnimation(Animation.from_center);
    }


    private void createYesButton() {
        yesButton = buttonFactory.getButton(generateRectangle(0.5, 0.4, 0.45, 0.05), 861, getString("yes"));
        yesButton.setReaction(rbSkipLevel);
        yesButton.setShadow(false);
        yesButton.setVisualHook(basePanel);
        yesButton.setAnimation(Animation.from_center);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.15), 860, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_skip_level"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.from_center);
    }


    private void createRestartButton() {
        restartButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 863, getString("in_game_menu_restart"));
        restartButton.setReaction(Reaction.rbRestartGame);
        restartButton.setAnimation(Animation.up);
    }
}
