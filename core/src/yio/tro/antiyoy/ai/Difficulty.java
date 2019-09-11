package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.stuff.LanguagesManager;

public class Difficulty {

    public static final int EASY = 0;
    public static final int NORMAL = 1;
    public static final int HARD = 2;
    public static final int EXPERT = 3;
    public static final int BALANCER = 4;


    public static String convertToString(int sliderIndex) {
        switch (sliderIndex) {
            default:
            case EASY:
                return LanguagesManager.getInstance().getString("easy");
            case NORMAL:
                return LanguagesManager.getInstance().getString("normal");
            case HARD:
                return LanguagesManager.getInstance().getString("hard");
            case EXPERT:
                return LanguagesManager.getInstance().getString("expert");
            case BALANCER:
                return LanguagesManager.getInstance().getString("balancer");
        }
    }
}
