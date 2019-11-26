package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.gameplay.rules.GameRules;

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

        if (colorOffset <= GameRules.NEUTRAL_FRACTION && GameRules.fractionsQuantity <= GameRules.NEUTRAL_FRACTION) {
            return getLegacyColorByFraction(fraction);
        }

        fraction += colorOffset;

        fraction = getLimitedByMaxFractionsValue(fraction);
        if (fraction == GameRules.NEUTRAL_FRACTION) {
            return getExcludedByNeutralColor();
        }

        fraction = getLimitedByMaxFractionsValue(fraction);

        return fraction;
    }


    private int getLimitedByMaxFractionsValue(int fraction) {
        if (fraction >= GameRules.MAX_FRACTIONS_QUANTITY) {
            fraction -= GameRules.MAX_FRACTIONS_QUANTITY;
        }
        return fraction;
    }


    private int getExcludedByNeutralColor() {
        int color = GameRules.NEUTRAL_FRACTION + colorOffset;

        color = getLimitedByMaxFractionsValue(color);

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

        ArrayList<Hex> activeHexes = gameController.fieldManager.activeHexes;
        for (Hex activeHex : activeHexes) {
            if (!GameRules.slayRules && activeHex.isNeutral()) continue;

            activeHex.fraction = gameController.colorsManager.getFractionByColor(activeHex.fraction);
        }

        gameController.fieldManager.detectProvinces();
        gameController.stopAllUnitsFromJumping();
        gameController.prepareCertainUnitsToMove();
    }


    public void takeControlOverColor(int targetColor) {
        int targetFraction = getFractionByColor(targetColor);
        shiftColors(-targetFraction);
        setColorOffset(targetColor);
    }


    public void shiftColors(int delta) {
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
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
        ArrayList<Hex> activeHexes = gameController.fieldManager.activeHexes;
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
