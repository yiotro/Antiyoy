package yio.tro.antiyoy.gameplay.rules;

public class GameRules {

    public static final int MAX_COLOR_NUMBER = 7;
    public static final int UNIT_MOVE_LIMIT = 4;
    public static final int TURNS_LIMIT = 300;

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
    public static boolean slayRules = false;
    public static boolean tutorialMode;
    public static boolean campaignMode;
    public static boolean inEditorMode;
    public static int difficulty;
    public static boolean aiOnlyMode;
    public static boolean replayMode;


    public static void defaultValues() {
        tutorialMode = false;
        campaignMode = false;
        inEditorMode = false;
        aiOnlyMode = false;
        replayMode = false;
    }


    public static void setColorNumber(int colorNumber) {
        if (colorNumber < 0) {
            colorNumber = 0;
        }

        GameRules.colorNumber = colorNumber;
    }


    public static void setDifficulty(int difficulty) {
        GameRules.difficulty = difficulty;
    }


    public static void setSlayRules(boolean slay_rules) {
        GameRules.slayRules = slay_rules;
    }
}
