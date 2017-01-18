package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.YioGdxGame;

public class CampaignController {

    static CampaignController instance;
    private GameController gameController;
    public int progress; // progress - index of unlocked level
    public int currentLevelIndex;
    private CampaignLevelFactory campaignLevelFactory;


    public static CampaignController getInstance() {
        if (instance == null) {
            instance = new CampaignController();
        }

        return instance;
    }


    public void init(GameController gameController) {
        this.gameController = gameController;

        campaignLevelFactory = new CampaignLevelFactory(gameController);
        progress = gameController.yioGdxGame.selectedLevelIndex;
    }


    public boolean completedCampaignLevel(int winColor) {
        return GameRules.campaignMode && winColor == 0;
    }


    public void setCurrentLevelIndex(int currentLevelIndex) {
        this.currentLevelIndex = currentLevelIndex;
    }


    public int getNextLevelIndex() {
        int nextLevelIndex = currentLevelIndex + 1;
        if (nextLevelIndex > YioGdxGame.INDEX_OF_LAST_LEVEL) nextLevelIndex = YioGdxGame.INDEX_OF_LAST_LEVEL;
        return nextLevelIndex;
    }


    public boolean loadCampaignLevel(int index) {
        setCurrentLevelIndex(index);
        gameController.getYioGdxGame().setSelectedLevelIndex(index);
        if (index == 0) { // tutorial level
            GameRules.slay_rules = false;
            gameController.initTutorial();
            GameRules.campaignMode = true;
            return true;
        }
        if (isLevelLocked(index)) return false;
        campaignLevelFactory.createCampaignLevel(index);
        GameRules.campaignMode = true;
        return true;
    }


    public boolean isLevelLocked(int index) {
        return gameController.getYioGdxGame().isLevelLocked(index);
    }


    public boolean isLevelComplete(int index) {
        return gameController.getYioGdxGame().isLevelComplete(index);
    }
}