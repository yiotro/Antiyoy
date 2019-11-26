package yio.tro.antiyoy.gameplay.data_storage;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.rules.GameRules;

public class LegacyExportManager {

    GameController gameController;


    public LegacyExportManager(GameController gameController) {
        this.gameController = gameController;
    }


    public String getFullLevelString() {
        return getBasicInfoString() + "/" + getActiveHexesString();
    }


    private String getBasicInfoString() {
        return String.valueOf(GameRules.difficulty) + " " +
                gameController.levelSizeManager.levelSize + " " +
                gameController.playersNumber + " " +
                GameRules.fractionsQuantity;
    }


    public String getActiveHexesString() {
        StringBuilder builder = new StringBuilder();
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            String hexString = getHexString(activeHex);
            builder.append(hexString);
            builder.append("#");
        }
        if (builder.length() > 0) {
            builder.delete(builder.length() - 1, builder.length());
        }
        return builder.toString();
    }


    private String getHexString(Hex hex) {
        // index1 - index2 - fraction - objectInside - unitStrength - unitReadyToMove - money

        StringBuilder builder = new StringBuilder();
        builder.append(hex.index1);
        builder.append(" ").append(hex.index2);
        builder.append(" ").append(hex.fraction);
        builder.append(" ").append(hex.objectInside);

        if (hex.containsUnit()) {
            builder.append(" ").append(hex.unit.strength);
            if (hex.unit.isReadyToMove()) {
                builder.append(" " + 1);
            } else {
                builder.append(" " + 0);
            }
        } else {
            builder.append(" " + 0);
            builder.append(" " + 0);
        }

        Province province = gameController.getProvinceByHex(hex);
        if (province != null) {
            builder.append(" ").append(province.money);
        } else {
            builder.append(" " + 10);
        }

        return builder.toString();
    }
}
