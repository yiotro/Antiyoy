package yio.tro.antiyoy.gameplay.campaign;

import yio.tro.antiyoy.LanguagesManager;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.ai.ArtificialIntelligence;
import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.GameSaver;
import yio.tro.antiyoy.gameplay.MapGenerator;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

import java.util.ArrayList;

/**
 * Created by ivan on 18.11.2015.
 */
public class CampaignLevelFactory {

    public GameController gameController;
    public static final int NORMAL_LEVELS_START = 9;
    public static final int HARD_LEVELS_START = 24;
    public static final int EXPERT_LEVELS_START = 60;
    private final String[] levels;
    private final LevelPackOne levelPackOne;
    private final LevelPackTwo levelPackTwo;


    public CampaignLevelFactory(GameController gameController) {
        this.gameController = gameController;
        levels = new String[CampaignProgressManager.getIndexOfLastLevel() + 1];
//        setLevels();

        levelPackOne = new LevelPackOne(this);
        levelPackTwo = new LevelPackTwo(this);
    }


    public boolean createCampaignLevel(int index) {
        CampaignProgressManager.getInstance().setCurrentLevelIndex(index);
        gameController.getYioGdxGame().setSelectedLevelIndex(index);
        updateRules(); // used for pack two

        if (checkForTutorial(index)) return true;
        if (CampaignProgressManager.getInstance().isLevelLocked(index)) return false;
        if (levelPackOne.checkForLevelPackOne(index)) return true;
        if (levelPackTwo.checkForlevelPackTwo(index)) return true;

        createLevelWithPredictableRandom(index);

//        GameRules.campaignMode = true;
        return true;

//        if (YioGdxGame.isScreenVeryWide()) {
//            createLevelWithPredictableRandom(index);
//            return;
//        }
//        GameSaver gameSaver = gameController.gameSaver;
//        gameSaver.setActiveHexesString(levels[index]);
//        gameSaver.beginRecreation();
//        gameSaver.setBasicInfo(0, 1, getColorNumberByIndex(index), getLevelSizeByIndex(index), getDifficultyByIndex(index));
//        gameController.colorIndexViewOffset = (new Random(index)).nextInt(getColorNumberByIndex(index));
//        gameSaver.endRecreation();
    }


    private void updateRules() {
        GameRules.setSlayRules(gameController.yioGdxGame.menuControllerYio.getCheckButtonById(17).isChecked());
    }


    private boolean checkForTutorial(int index) {
        if (index == 0) { // tutorial level
//            GameRules.setSlayRules(false);
            gameController.initTutorial();
//            GameRules.campaignMode = true;
            return true;
        }
        return false;
    }


    private void createLevelWithPredictableRandom(int index) {
        LoadingParameters instance = LoadingParameters.getInstance();
        instance.mode = LoadingParameters.MODE_CAMPAIGN_RANDOM;
        instance.levelSize = getLevelSizeByIndex(index);
        instance.playersNumber = 1;
        instance.colorNumber = getColorNumberByIndex(index);
        instance.difficulty = getDifficultyByIndex(index);
        instance.colorOffset = readColorOffsetFromSlider(instance.colorNumber);
        instance.slayRules = GameRules.slay_rules;
        instance.campaignLevelIndex = index;
        LoadingManager.getInstance().startGame(instance);

//        gameController.setLevelSize(getLevelSizeByIndex(index));
//        gameController.setPlayersNumber(1);
//        GameRules.setColorNumber(getColorNumberByIndex(index));
//        GameRules.setDifficulty(getDifficultyByIndex(index));
//        gameController.yioGdxGame.startGame(index, false, false);

//        finishCreatingLevelWithPredictableRandom();

        checkForHelloMessage(index);
    }


    public int readColorOffsetFromSlider(int colorNumber) {
        return gameController.getColorOffsetBySlider(gameController.yioGdxGame.menuControllerYio.sliders.get(6), colorNumber);
    }


    private void finishCreatingLevelWithPredictableRandom() {
        GameRules.campaignMode = true;
        gameController.readColorOffsetFromSlider();
        gameController.yioGdxGame.gameView.updateCacheLevelTextures();
        gameController.yioGdxGame.gameView.beginSpawnProcess();
        gameController.selectionController.deselectAll();
    }


    public void checkForHelloMessage(int index) {
        if (index == 24) { // first hard level
            MenuControllerYio menuControllerYio = gameController.yioGdxGame.menuControllerYio;
            ArrayList<String> text = menuControllerYio.getArrayListFromString(LanguagesManager.getInstance().getString("level_24"));
            text.add(" ");
            text.add(" ");
            Scenes.sceneTutorialTip.createTutorialTip(text);
            return;
        }
    }





    public static int getDifficultyByIndex(int index) {
        if (index <= 8) return ArtificialIntelligence.DIFFICULTY_EASY;
        if (index <= 23) return ArtificialIntelligence.DIFFICULTY_NORMAL;
        if (index >= 60) return ArtificialIntelligence.DIFFICULTY_EXPERT;
        return ArtificialIntelligence.DIFFICULTY_HARD;
    }


    private int getColorNumberByIndex(int index) {
        if (index <= 4 || index == 20) return 3;
        if (index <= 7) return 4;
        if (index >= 10 && index <= 13) return 4;
        return 5;
    }


    private int getLevelSizeByIndex(int index) {
        if (index == 4 || index == 7) return FieldController.SIZE_MEDIUM;
        if (index == 15) return FieldController.SIZE_SMALL;
        if (index == 20 || index == 30 || index == 35) return FieldController.SIZE_BIG;
        if (index >= 60 && index <= 64) return FieldController.SIZE_MEDIUM;
        if (index > 50 && index <= 53) return FieldController.SIZE_MEDIUM;

        if (index <= 10) {
            return FieldController.SIZE_SMALL;
        } else if (index <= 40) {
            return FieldController.SIZE_MEDIUM;
        } else {
            return FieldController.SIZE_BIG;
        }
    }


}
