package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneAfterGameMenu extends AbstractScene {


    private ButtonYio replayButton;
    int whoWon;
    boolean playerIsWinner;


    public SceneAfterGameMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        whoWon = 0;
        playerIsWinner = true;
    }


    public void create(int whoWon, boolean playerIsWinner) {
        this.whoWon = whoWon;
        this.playerIsWinner = playerIsWinner;

        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().setGamePaused(true);
        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, false);

        String message;
        if (playerIsWinner) {
            message = menuControllerYio.getColorNameByIndexWithOffset(whoWon, "_player") + " " +
                    getString("player") + " " +
                    getString("won") + ".";
        } else {
            message = menuControllerYio.getColorNameByIndexWithOffset(whoWon, "_ai") + " " +
                    getString("ai") + " " +
                    getString("won") + ".";
        }

        if (CampaignProgressManager.getInstance().completedCampaignLevel(whoWon)) {
            message = getString("level_complete");
        }

        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.2), 60, null);
        textPanel.cleatText();
        textPanel.addTextLine(message);
        textPanel.addTextLine("");
        textPanel.addTextLine("");
        menuControllerYio.getButtonRenderer().renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimation(Animation.FROM_CENTER);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.55, 0.4, 0.4, 0.07), 62, null);
        if (CampaignProgressManager.getInstance().completedCampaignLevel(whoWon))
            okButton.setTextLine(getString("next"));
        else {
            if (playerIsWinner) {
                okButton.setTextLine(getString("end_game_ok"));
            } else {
                okButton.setTextLine(getString("end_game_okay"));
            }
        }
        menuControllerYio.getButtonRenderer().renderButton(okButton);
        okButton.setShadow(false);
        okButton.setReaction(Reaction.rbChooseGameModeMenu);
        if (CampaignProgressManager.getInstance().completedCampaignLevel(whoWon))
            okButton.setReaction(Reaction.rbNextLevel);
        okButton.setAnimation(Animation.FROM_CENTER);

        ButtonYio statisticsButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.5, 0.07), 61, getString("statistics"));
        statisticsButton.setShadow(false);
        statisticsButton.setReaction(Reaction.rbStatisticsMenu);
        statisticsButton.setAnimation(Animation.FROM_CENTER);

        createReplayButton();

        menuControllerYio.endMenuCreation();
    }


    private void createReplayButton() {
        if (!Settings.replaysEnabled) return;

        replayButton = buttonFactory.getButton(generateRectangle(0.6, 0.9, 0.35, 0.05), 63, getString("replay"));
        replayButton.setReaction(Reaction.rbStartInstantReplay);
        replayButton.setAnimation(Animation.UP);
        replayButton.setTouchOffset(0.05f * GraphicsYio.width);
        replayButton.disableTouchAnimation();
    }


    @Override
    public void create() {
        create(whoWon, playerIsWinner);
    }
}