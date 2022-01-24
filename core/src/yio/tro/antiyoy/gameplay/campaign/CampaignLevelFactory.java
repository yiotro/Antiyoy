package yio.tro.antiyoy.gameplay.campaign;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.ai.Difficulty;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.LevelSize;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.color_picking.ColorHolderElement;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;


public class CampaignLevelFactory {

    public GameController gameController;
    public static final int NORMAL_LEVELS_START = 9;
    public static final int HARD_LEVELS_START = 24;
    public static final int EXPERT_LEVELS_START = 60;
    int index;
    AbstractLevelPack levelPacks[];
    private final LevelPackOne levelPackOne; // pack one is special


    public CampaignLevelFactory(GameController gameController) {
        this.gameController = gameController;

        levelPackOne = new LevelPackOne(this);
        levelPacks = new AbstractLevelPack[]{
                new LevelPack2(this),
                new LevelPack3(this),
                new LevelPack4(this),
                new LevelPack5(this),
                new LevelPack6(this),
                new LevelPack7(this),
                new LevelPack8(this),
                new LevelPack9(this),
                new LevelPack10(this),
                new LevelPack11(this),
                new LevelPack12(this),
                new LevelPack13(this),
                new LevelPack14(this),
                new LevelPack15(this),
                new LevelPack16(this),
                new LevelPack17(this),
                new LevelPack18(this),
                new LevelPack19(this),
                new LevelPack20(this),
                new LevelPack21(this),
                new LevelPack22(this),
                new LevelPack23(this),
                new LevelPack24(this),
        };
        index = -1;
    }


    public boolean createCampaignLevel(int index) {
        this.index = index;

        // to avoid crash
        Scenes.sceneMoreCampaignOptions.prepare();

        CampaignProgressManager.getInstance().setCurrentLevelIndex(index);
        gameController.getYioGdxGame().setSelectedLevelIndex(index);
        updateRules(); // used for pack two

        if (checkForTutorial()) return true;
        if (CampaignProgressManager.getInstance().isLevelLocked(index)) return false;
        if (levelPackOne.check()) return true;
        for (AbstractLevelPack levelPack : levelPacks) {
            if (levelPack.check()) return true;
        }

        createLevelWithPredictableRandom();

        return true;
    }


    private void updateRules() {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");
        GameRules.slayRules = prefs.getBoolean("slay_rules", false);
    }


    private boolean checkForTutorial() {
        if (index != 0) return false;
        gameController.initTutorial();
        return true;
    }


    private void createLevelWithPredictableRandom() {
        LoadingParameters instance = LoadingParameters.getInstance();
        instance.loadingType = LoadingType.campaign_random;
        instance.levelSize = getLevelSizeByIndex(index);
        instance.playersNumber = 1;
        instance.fractionsQuantity = getFractionsQuantityByIndex(index);
        instance.difficulty = getDifficultyByIndex(index);
        instance.colorOffset = readColorOffsetFromHolder(instance.fractionsQuantity);
        instance.slayRules = GameRules.slayRules;
        instance.campaignLevelIndex = index;
        LoadingManager.getInstance().startGame(instance);

        checkForHelloMessage(index);
    }


    public int readColorOffsetFromHolder(int fractionsQuantity) {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");
        int valueIndex = prefs.getInteger("color_offset", 0);
        return ColorHolderElement.getColor(valueIndex, fractionsQuantity);
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
        if (index >= 136) return Difficulty.MASTER;
        if (index <= 8) return Difficulty.EASY;
        if (index <= 23) return Difficulty.NORMAL;
        if (index >= 60) return Difficulty.EXPERT;
        return Difficulty.HARD;
    }


    private int getFractionsQuantityByIndex(int index) {
        if (index <= 4 || index == 20) return 3;
        if (index <= 7) return 4;
        if (index >= 10 && index <= 13) return 4;
        return 5;
    }


    private int getLevelSizeByIndex(int index) {
        if (index == 4 || index == 7) return LevelSize.MEDIUM;
        if (index == 15) return LevelSize.SMALL;
        if (index == 20 || index == 30 || index == 35) return LevelSize.BIG;
        if (index >= 60 && index <= 64) return LevelSize.MEDIUM;
        if (index > 50 && index <= 53) return LevelSize.MEDIUM;

        if (index <= 10) {
            return LevelSize.SMALL;
        } else if (index <= 40) {
            return LevelSize.MEDIUM;
        } else {
            return LevelSize.BIG;
        }
    }


}
