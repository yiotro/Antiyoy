package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneAfterGameMenu extends AbstractScene{


    private ButtonYio replayButton;


    public SceneAfterGameMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    public void create(int whoWon, boolean playerIsWinner) {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().setGamePaused(true);
        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, false);

        String message = "ERROR";
        if (playerIsWinner) {
            message = menuControllerYio.getColorNameByIndex(whoWon, "_player") + " " +
                    getString("player") + " " +
                    getString("won") + ".";
        } else {
            message = menuControllerYio.getColorNameByIndex(whoWon, "_ai") + " " +
                    getString("ai") + " " +
                    getString("won") + ".";
        }
        if (CampaignProgressManager.getInstance().completedCampaignLevel(whoWon))
            message = getString("level_complete");
        if (DebugFlags.CHECKING_BALANCE_MODE && menuControllerYio.getYioGdxGame().gamesPlayed() % 50 == 0) {
            YioGdxGame.say(menuControllerYio.getYioGdxGame().gamesPlayed() + " : " + menuControllerYio.getYioGdxGame().getBalanceIndicatorString());
        }
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.2), 60, null);
        textPanel.cleatText();
        textPanel.addTextLine(message);
        textPanel.addTextLine("");
        textPanel.addTextLine("");
        menuControllerYio.getButtonRenderer().renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

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
        okButton.setReactBehavior(ReactBehavior.rbChooseGameModeMenu);
        if (CampaignProgressManager.getInstance().completedCampaignLevel(whoWon))
            okButton.setReactBehavior(ReactBehavior.rbNextLevel);
        okButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio statisticsButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.5, 0.07), 61, getString("statistics"));
        statisticsButton.setShadow(false);
        statisticsButton.setReactBehavior(ReactBehavior.rbStatisticsMenu);
        statisticsButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        createReplayButton();

        menuControllerYio.endMenuCreation();
    }


    private void createReplayButton() {
        if (!Settings.replaysEnabled) return;

        replayButton = buttonFactory.getButton(generateRectangle(0.6, 0.9, 0.35, 0.05), 63, getString("replay"));
        replayButton.setReactBehavior(ReactBehavior.rbStartInstantReplay);
        replayButton.setAnimType(ButtonYio.ANIM_UP);
        replayButton.setTouchOffset(0.05f * GraphicsYio.width);
        replayButton.disableTouchAnimation();
    }


    @Override
    public void create() {

    }
}