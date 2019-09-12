package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class ColorsManager {

    GameController gameController;
    public int colorOffset;


    public ColorsManager(GameController gameController) {
        this.gameController = gameController;
    }


    public int getColorByFraction(int fraction) {
        if (GameRules.inEditorMode) return fraction;
        if (fraction == GameRules.NEUTRAL_FRACTION) return fraction;
        if (colorOffset == 0) return fraction;

        if (GameRules.fractionsQuantity <= GameRules.NEUTRAL_FRACTION) {
            return getLegacyColorByFraction(fraction);
        }

        fraction += colorOffset;

        if (fraction == GameRules.NEUTRAL_FRACTION) {
            return getExcludedByNeutralColor();
        }

        if (fraction >= GameRules.MAX_FRACTIONS_QUANTITY) {
            fraction -= GameRules.MAX_FRACTIONS_QUANTITY;
        }

        return fraction;
    }


    private int getExcludedByNeutralColor() {
        int color = GameRules.NEUTRAL_FRACTION + colorOffset;

        if (color >= GameRules.MAX_FRACTIONS_QUANTITY) {
            color -= GameRules.MAX_FRACTIONS_QUANTITY;
        }

        return color;
    }


    private int getLegacyColorByFraction(int fraction) {
        fraction += colorOffset;

        if (fraction >= GameRules.NEUTRAL_FRACTION) {
            fraction -= GameRules.NEUTRAL_FRACTION;
        }

        return fraction;
    }


    public int getFractionByColor(int color) {
        for (int fraction = 0; fraction < GameRules.MAX_FRACTIONS_QUANTITY; fraction++) {
            if (getColorByFraction(fraction) != color) continue;
            return fraction;
        }

        return -1;
    }


    public void defaultValues() {
        colorOffset = 0;
    }


    public void applyEditorChosenColorFix() {
        gameController.updateRuleset();

        ArrayList<Hex> activeHexes = gameController.fieldController.activeHexes;
        for (Hex activeHex : activeHexes) {
            if (!GameRules.slayRules && activeHex.isNeutral()) continue;

            activeHex.fraction = gameController.colorsManager.getFractionByColor(activeHex.fraction);
        }

        gameController.fieldController.detectProvinces();
        gameController.stopAllUnitsFromJumping();
        gameController.prepareCertainUnitsToMove();
    }


    public void takeControlOverColor(int targetColor) {
        int targetFraction = getFractionByColor(targetColor);
        shiftColors(-targetFraction);
        setColorOffset(targetColor);
    }


    public void shiftColors(int delta) {
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (!GameRules.slayRules && activeHex.isNeutral()) continue;

            activeHex.fraction = getShiftedColor(activeHex.fraction, delta);
        }
    }


    private int getShiftedColor(int color, int delta) {
        color += delta;

        if (color >= GameRules.fractionsQuantity) {
            color -= GameRules.fractionsQuantity;
        }

        if (color < 0) {
            color += GameRules.fractionsQuantity;
        }

        return color;
    }


    public void doShiftFractionsInEditorMode() {
        ArrayList<Hex> activeHexes = gameController.fieldController.activeHexes;
        for (Hex activeHex : activeHexes) {
            if (activeHex.isNeutral()) continue;

            activeHex.fraction++;
            if (activeHex.fraction >= GameRules.MAX_FRACTIONS_QUANTITY) {
                activeHex.fraction -= GameRules.MAX_FRACTIONS_QUANTITY;
            }
        }

        gameController.yioGdxGame.gameView.updateCacheLevelTextures();
    }


    public void setColorOffset(int colorOffset) {
        this.colorOffset = colorOffset;
    }


    public String getColorNameForPlayerByFraction(int fraction) {
        return getColorNameByFraction(fraction, "_player");
    }


    public String getColorNameByFraction(int fraction, String keyModifier) {
        return getColorName(getColorByFraction(fraction), keyModifier);
    }


    public String getColorName(int color, String keyModifier) {
        switch (color) {
            default:
                return "unknown";
            case 6:
            case 0:
                return LanguagesManager.getInstance().getString("green" + keyModifier);
            case 1:
            case 5:
                return LanguagesManager.getInstance().getString("red" + keyModifier);
            case 2:
                return LanguagesManager.getInstance().getString("magenta" + keyModifier);
            case 3:
                return LanguagesManager.getInstance().getString("cyan" + keyModifier);
            case 4:
                return LanguagesManager.getInstance().getString("yellow" + keyModifier);
            case 7:
                return LanguagesManager.getInstance().getString("gray" + keyModifier);
        }
    }


    public static String getMenuColorNameByIndex(int index) {
        switch (index) {
            default:
            case 0:
                return LanguagesManager.getInstance().getString("random");
            case 1:
                return LanguagesManager.getInstance().getString("green_menu");
            case 2:
                return LanguagesManager.getInstance().getString("red_menu");
            case 3:
                return LanguagesManager.getInstance().getString("magenta_menu");
            case 4:
                return LanguagesManager.getInstance().getString("cyan_menu");
            case 5:
                return LanguagesManager.getInstance().getString("yellow_menu");
            case 6:
                return LanguagesManager.getInstance().getString("red_menu") + "+";
            case 7:
                return LanguagesManager.getInstance().getString("green_menu") + "+";
        }
    }


    public void doShowInConsole() {
        System.out.println();
        System.out.println("ColorsManager.doShowInConsole");
        System.out.println("GameRules.MAX_FRACTIONS_QUANTITY = " + GameRules.MAX_FRACTIONS_QUANTITY);
        System.out.println("GameRules.NEUTRAL_FRACTION = " + GameRules.NEUTRAL_FRACTION);
        System.out.println("GameRules.fractionsQuantity = " + GameRules.fractionsQuantity);
        System.out.println("colorOffset = " + gameController.colorsManager.colorOffset);
        for (int fraction = 0; fraction < GameRules.fractionsQuantity; fraction++) {
            int colorByFraction = gameController.colorsManager.getColorByFraction(fraction);
            System.out.println(fraction + " -> " + colorByFraction);
        }
    }


    public void doShowColorInfoAboutHex(Hex hex) {
        System.out.println();
        System.out.println("ColorsManager.doShowColorInfoAboutHex");
        System.out.println("hex = " + hex);
        int colorByFraction = getColorByFraction(hex.fraction);
        System.out.println("colorByFraction = " + colorByFraction);
    }
}
