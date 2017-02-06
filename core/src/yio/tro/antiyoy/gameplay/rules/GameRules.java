package yio.tro.antiyoy.gameplay.rules;

import yio.tro.antiyoy.menu.SliderYio;

public class GameRules {

    public static final int MAX_COLOR_NUMBER = 7;
    public static final int UNIT_MOVE_LIMIT = 4;

    public static final int PRICE_UNIT = 10;
    public static final int PRICE_TOWER = 15;
    public static final int PRICE_FARM = 12;
    public static final int PRICE_STRONG_TOWER = 35;

    public static final int PRICE_TREE = 10;
    public static final int FARM_INCOME = 4;
    public static final int TREE_CUT_REWARD = 3;

    public static final int TAX_TOWER = 1;
    public static final int TAX_STRONG_TOWER = 6;
    public static final int TAX_UNIT_GENERIC_1 = 2;
    public static final int TAX_UNIT_GENERIC_2 = 6;
    public static final int TAX_UNIT_GENERIC_3 = 18;
    public static final int TAX_UNIT_GENERIC_4 = 36;

    public static int colorNumber = 5;
    public static boolean slay_rules = false;
    public static boolean tutorialMode;
    public static boolean campaignMode;
    public static boolean inEditorMode;
    public static int difficulty;


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


    public static void setSlayRules(boolean slay_rules) {
        GameRules.slay_rules = slay_rules;
    }
}
