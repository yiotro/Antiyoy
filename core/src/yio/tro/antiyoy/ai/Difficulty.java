package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.stuff.LanguagesManager;

public class Difficulty {

    public static final int EASY = 0;
    public static final int NORMAL = 1;
    public static final int HARD = 2;
    public static final int EXPERT = 3;
    public static final int BALANCER = 4;
    public static final int MASTER = 5;


    public static String convertToString(int sliderIndex) {
        String localizedString = getLocalizedString(sliderIndex);
        return localizedString.substring(0, 1).toLowerCase() + localizedString.substring(1);
    }


    private static String getLocalizedString(int sliderIndex) {
        LanguagesManager instance = LanguagesManager.getInstance();
        switch (sliderIndex) {
            default:
            case EASY:
                return instance.getString("easy");
            case NORMAL:
                return instance.getString("normal");
            case HARD:
                return instance.getString("hard");
            case EXPERT:
                return instance.getString("expert");
            case BALANCER:
                return instance.getString("balancer");
            case MASTER:
                return instance.getString("master");
        }
    }
}
