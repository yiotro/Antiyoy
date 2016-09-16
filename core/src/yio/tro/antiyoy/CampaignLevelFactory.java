package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;

import java.util.Random;

/**
 * Created by ivan on 18.11.2015.
 */
class CampaignLevelFactory {

    private final GameController gameController;
    public static final int NORMAL_LEVELS_START = 9;
    public static final int HARD_LEVELS_START = 24;
    public static final int EXPERT_LEVELS_START = 60;
    private final String[] levels;


    public CampaignLevelFactory(GameController gameController) {
        this.gameController = gameController;
        levels = new String[YioGdxGame.INDEX_OF_LAST_LEVEL + 1];
//        setLevels();
    }


    public void createCampaignLevel(int index) {
        createLevelWithPredictableRandom(index);
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


    private void createLevelWithPredictableRandom(int index) {
        gameController.setLevelSize(getLevelSizeByIndex(index));
        gameController.setPlayersNumber(1);
        GameController.setColorNumber(getColorNumberByIndex(index));
        gameController.setDifficulty(getDifficultyByIndex(index));
        GameController.slay_rules = gameController.yioGdxGame.menuControllerYio.getCheckButtonById(6).isChecked();
        gameController.yioGdxGame.startGame(index, false, false);

        if (GameController.slay_rules) {
            generateMapForSlayRules();
        } else {
            generateMapForGenericRules();
        }

        gameController.readColorOffsetFromSlider();
        gameController.yioGdxGame.gameView.updateCacheLevelTextures();
        gameController.yioGdxGame.gameView.beginSpawnProcess();
    }


    private void generateMapForSlayRules() {
        while (true) {
            gameController.clearAnims();
            gameController.createField(true);
            if (gameController.getPredictionForWinner() == 0) break;
        }
    }


    private void generateMapForGenericRules() {
        int c = 0;
        while (c < 6) {
            gameController.clearAnims();
            gameController.createField(true);
            if (gameController.areConditionsGoodForPlayer()) break;
            c++;
        }
    }


    static int getDifficultyByIndex(int index) {
        if (index <= 8) return GameController.EASY;
        if (index <= 23) return GameController.NORMAL;
        if (index >= 60) return GameController.EXPERT;
        return GameController.HARD;
    }


    private int getColorNumberByIndex(int index) {
        if (index <= 4 || index == 20) return 3;
        if (index <= 7) return 4;
        if (index >= 10 && index <= 13) return 4;
        return 5;
    }


    private int getLevelSizeByIndex(int index) {
        if (index == 4 || index == 7) return GameController.SIZE_MEDIUM;
        if (index == 15) return GameController.SIZE_SMALL;
        if (index == 20 || index == 30 || index == 35) return GameController.SIZE_BIG;
        if (index >= 60 && index <= 64) return GameController.SIZE_MEDIUM;
        if (index > 50 && index <= 53) return GameController.SIZE_MEDIUM;

        if (index <= 10) {
            return GameController.SIZE_SMALL;
        } else if (index <= 40) {
            return GameController.SIZE_MEDIUM;
        } else {
            return GameController.SIZE_BIG;
        }
    }


    void generateLevels() {
        MapGenerator mapGenerator = new MapGenerator(gameController);
        GameSaver gameSaver = new GameSaver(gameController);
        for (int i = 0; i < levels.length; i++) {
            gameController.setLevelSize(getLevelSizeByIndex(i));
            gameController.setPlayersNumber(1);
            GameController.setColorNumber(getColorNumberByIndex(i));
            while (true) {
                gameController.createField(true);
                if (gameController.getPredictionForWinner() == 0) break;
            }
            String levelString = gameSaver.getActiveHexesString();
            YioGdxGame.say("levels[" + i + "] = \"" + levelString + "\";");
        }
    }
}
