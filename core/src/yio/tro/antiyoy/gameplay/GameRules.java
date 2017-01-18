package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.menu.SliderYio;

public class GameRules {

    public static final int MAX_COLOR_NUMBER = 7;
    public static final int UNIT_MOVE_LIMIT = 4;
    public static final int PRICE_UNIT = 10;
    public static final int PRICE_TOWER = 15;
    public static final int PRICE_FARM = 12;
    public static final int PRICE_STRONG_TOWER = 50;
    public static int colorNumber = 5;
    public static boolean slay_rules = false;
    public static boolean tutorialMode;
    public static boolean campaignMode;
    public static boolean inEditorMode;
    static int difficulty;


    public static void setColorNumber(int colorNumber) {
        GameRules.colorNumber = colorNumber;
    }


    public static void setColorNumberBySlider(SliderYio slider) {
        setColorNumber(slider.getCurrentRunnerIndex() + 2);
    }


    public static void setDifficulty(int difficulty) {
        GameRules.difficulty = difficulty;
    }


    public static void setDifficultyBySlider(SliderYio slider) {
        difficulty = slider.getCurrentRunnerIndex();
    }
}
