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
import yio.tro.antiyoy.menu.color_picking.ColorHolderElement;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
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
    ColorHolderElement colorHolderElement;


    public SceneAfterGameMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        pos = new RectangleYio(0.05, 0.35, 0.9, 0.25);
        winnerFraction = 0;
        playerIsWinner = true;
        colorHolderElement = null;
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
        createColorHolder();

        menuControllerYio.endMenuCreation();
    }


    private void createColorHolder() {
        initColorHolder();
        colorHolderElement.appear();
        int colorByFraction = getGameController().colorsManager.getColorByFraction(winnerFraction);
        colorHolderElement.setValueIndex(colorByFraction + 1);
    }


    private void initColorHolder() {
        if (colorHolderElement != null) return;
        colorHolderElement = new ColorHolderElement(menuControllerYio);
        colorHolderElement.setTitle(LanguagesManager.getInstance().getString("winner") + ":");
        colorHolderElement.setAnimation(Animation.from_center);
        colorHolderElement.setPosition(generateRectangle(pos.x, pos.y + pos.height - 0.095, pos.width, 0.08));
        colorHolderElement.setTouchable(false);
        menuControllerYio.addElementToScene(colorHolderElement);
    }


    private void createTextPanel() {
        textPanel = buttonFactory.getButton(generateRectangle(pos.x, pos.y, pos.width, pos.height), 60, null);
        textPanel.setTextOffset((float) GraphicsYio.convertToWidth(0.03) * GraphicsYio.width);
        textPanel.cleatText();
        textPanel.addTextLine(" ");
        textPanel.addTextLine(" ");
        textPanel.addTextLine(getTextPanelMessage());
        textPanel.applyNumberOfLines(5);
        menuControllerYio.getButtonRenderer().renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimation(Animation.from_center);
    }


    private String getTextPanelMessage() {
        if (CampaignProgressManager.getInstance().areCampaignLevelCompletionConditionsSatisfied(winnerFraction)) {
            return getString("level_complete");
        }

        if (GameRules.diplomacyEnabled && isVictoryDiplomatic()) {
            return getString("reason") + ": " + getString("friends_with_everybody");
        }

        return " ";
    }


    private boolean isVictoryDiplomatic() {
        if (!areThereAtLeastTwoDifferentAliveColorsOnMap()) return false;

        GameController gameController = getGameController();
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        DiplomaticEntity winner = diplomacyManager.getEntity(winnerFraction);
        if (winner == null) return false;
        if (!winner.hasOnlyFriends()) return false;

        return true;
    }


    private boolean areThereAtLeastTwoDifferentAliveColorsOnMap() {
        GameController gameController = getGameController();
        ArrayList<Province> provinces = gameController.fieldManager.provinces;
        if (provinces.size() < 2) return false;

        int firstColor = provinces.get(0).getFraction();

        for (Province province : provinces) {
            if (province.getFraction() != firstColor) return true;
        }

        return false;
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