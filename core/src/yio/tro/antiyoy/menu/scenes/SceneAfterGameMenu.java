package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class SceneAfterGameMenu extends AbstractScene {


    private ButtonYio replayButton;
    int winnerFraction;
    boolean playerIsWinner;
    private ButtonYio textPanel;
    private ButtonYio okButton;
    private ButtonYio statisticsButton;
    private final RectangleYio pos;


    public SceneAfterGameMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        pos = new RectangleYio(0.05, 0.35, 0.9, 0.2);
        winnerFraction = 0;
        playerIsWinner = true;
    }


    public void create(int winFraction, boolean playerIsWinner) {
        this.winnerFraction = winFraction;
        this.playerIsWinner = playerIsWinner;

        menuControllerYio.beginMenuCreation();

        menuControllerYio.yioGdxGame.setGamePaused(true);
        menuControllerYio.yioGdxGame.beginBackgroundChange(3, true, false);

        createTextPanel();
        createOkButton();
        createStatisticsButton();
        createReplayButton();

        menuControllerYio.endMenuCreation();
    }


    private void createTextPanel() {
        textPanel = buttonFactory.getButton(generateRectangle(pos.x, pos.y, pos.width, pos.height), 60, null);
        textPanel.cleatText();
        textPanel.addTextLine(getTextPanelMessage());
        checkToAddAdditionalTextLines();
        textPanel.applyNumberOfLines(4);
        menuControllerYio.getButtonRenderer().renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimation(Animation.from_center);
    }


    private void checkToAddAdditionalTextLines() {
        if (!GameRules.diplomacyEnabled) return;

        if (isVictoryDiplomatic()) {
            textPanel.addTextLine(getString("reason") + ": " + getString("friends_with_everybody"));
        }
    }


    private boolean isVictoryDiplomatic() {
        if (!areThereAtLeastTwoDifferentAliveColorsOnMap()) return false;

        GameController gameController = getGameController();
        DiplomacyManager diplomacyManager = gameController.fieldController.diplomacyManager;
        DiplomaticEntity winner = diplomacyManager.getEntity(winnerFraction);
        if (!winner.hasOnlyFriends()) return false;

        return true;
    }


    private boolean areThereAtLeastTwoDifferentAliveColorsOnMap() {
        GameController gameController = getGameController();
        ArrayList<Province> provinces = gameController.fieldController.provinces;
        if (provinces.size() < 2) return false;

        int firstColor = provinces.get(0).getFraction();

        for (Province province : provinces) {
            if (province.getFraction() != firstColor) return true;
        }

        return false;
    }


    private String getTextPanelMessage() {
        if (CampaignProgressManager.getInstance().areCampaignLevelCompletionConditionsSatisfied(winnerFraction)) {
            return getString("level_complete");
        }

        if (playerIsWinner) {
            return menuControllerYio.getColorsManager().getColorNameByFraction(winnerFraction, "_player") + " " +
                    getString("player") + " " +
                    getString("won") + ".";
        }

        String aiColorName = menuControllerYio.getColorsManager().getColorNameByFraction(winnerFraction, "_ai");
        if (aiColorName.equals("unknown")) {
            return castFirstLetterUpperCase(getString("ai")) + " " + getString("won") + ".";
        }

        return aiColorName + " " +
                getString("ai") + " " +
                getString("won") + ".";
    }


    private String castFirstLetterUpperCase(String src) {
        return src.substring(0, 1).toUpperCase() + src.substring(1);
    }


    private void createOkButton() {
        double bw = 0.4;
        okButton = buttonFactory.getButton(generateRectangle(pos.x + pos.width - bw, pos.y, bw, 0.05), 62, null);
        okButton.setTextLine(getString(getOkButtonTextKey()));
        menuControllerYio.getButtonRenderer().renderButton(okButton);
        okButton.setShadow(false);
        okButton.setReaction(getOkButtonReaction());
        okButton.setVisualHook(textPanel);
        okButton.setAnimation(Animation.from_center);
    }


    private Reaction getOkButtonReaction() {
        if (CampaignProgressManager.getInstance().areCampaignLevelCompletionConditionsSatisfied(winnerFraction)) {
            return Reaction.rbNextLevel;
        }

        return Reaction.rbChooseGameModeMenu;
    }


    private String getOkButtonTextKey() {
        if (CampaignProgressManager.getInstance().areCampaignLevelCompletionConditionsSatisfied(winnerFraction)) {
            return "next";
        }

        if (playerIsWinner) {
            return "end_game_ok";
        }

        return "end_game_okay";
    }


    private void createStatisticsButton() {
        statisticsButton = buttonFactory.getButton(generateRectangle(pos.x, pos.y, 0.5, 0.05), 61, getString("statistics"));
        statisticsButton.setShadow(false);
        statisticsButton.setReaction(Reaction.rbStatisticsMenu);
        statisticsButton.setVisualHook(textPanel);
        statisticsButton.setAnimation(Animation.from_center);
    }


    private void createReplayButton() {
        if (!SettingsManager.replaysEnabled) return;

        replayButton = buttonFactory.getButton(generateRectangle(0.6, 0.9, 0.35, 0.055), 63, getString("replay"));
        replayButton.setReaction(Reaction.rbStartInstantReplay);
        replayButton.setAnimation(Animation.up);
        replayButton.setTouchOffset(0.05f * GraphicsYio.width);
    }


    @Override
    public void create() {
        create(winnerFraction, playerIsWinner);
    }
}