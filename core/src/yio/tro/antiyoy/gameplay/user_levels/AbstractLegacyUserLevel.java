package yio.tro.antiyoy.gameplay.user_levels;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.data_storage.DecodeManager;

public abstract class AbstractLegacyUserLevel {


    GameController gameController;


    public abstract String getFullLevelString();


    public abstract String getMapName();


    public abstract String getAuthor();


    public abstract String getKey();


    public int getColorOffset() {
        return 0;
    }


    public boolean getFogOfWar() {
        return false;
    }


    public boolean getDiplomacy() {
        return false;
    }


    public boolean isHistorical() {
        return false;
    }


    public void onLevelLoaded(GameController gameController) {
        // nothing by default
    }


    public boolean isSinglePlayer() {
        String fullLevelString = getFullLevelString();
        if (fullLevelString.length() < 10) return false;

        String playersNumberString = fullLevelString.substring(0, 10).split(" ")[2];
        int playersNumber = Integer.valueOf(playersNumberString);
        return playersNumber == 1;
    }


    public boolean isMultiplayer() {
        String fullLevelString = getFullLevelString();
        if (fullLevelString.length() < 10) return false;

        String playersNumberString = fullLevelString.substring(0, 10).split(" ")[2];
        int playersNumber = Integer.valueOf(playersNumberString);
        return playersNumber > 1;
    }


    protected void setProvinceMoney(GameController gameController, int i, int j, int money) {
        FieldManager fieldManager = gameController.fieldManager;
        Hex hex = fieldManager.field[i][j];
        Province provinceByHex = fieldManager.getProvinceByHex(hex);
        provinceByHex.money = money;
    }


    public void setGameController(GameController gameController) {
        if (this.gameController == gameController) return;
        this.gameController = gameController;
        onGameControllerSet();
    }


    protected void onGameControllerSet() {
        //
    }


    public DecodeManager getDecodeManager() {
        return gameController.decodeManager;
    }


    public boolean isOlegLevel() {
        return false;
    }

}
