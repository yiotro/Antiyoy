package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ivan on 11.11.2015.
 */
public class GameSaver {

    private GameController gameController;
    String tokenSeparator, activeHexesString;
    private Preferences prefs;
    private ArrayList<String> hexStrings;


    public GameSaver(GameController gameController) {
        this.gameController = gameController;
        tokenSeparator = "#";
    }


    private void saveBasicInfo() {
        prefs.putInteger("save_turn", gameController.turn);
        prefs.putInteger("save_color_number", GameController.colorNumber);
        prefs.putInteger("save_level_size", gameController.levelSize);
        prefs.putInteger("save_player_number", gameController.playersNumber);
        prefs.putBoolean("save_campaign_mode", gameController.campaignMode);
        prefs.putInteger("save_current_level", gameController.currentLevelIndex);
        prefs.putInteger("save_difficulty", gameController.difficulty);
        prefs.putInteger("save_color_offset", gameController.colorIndexViewOffset);
        prefs.putBoolean("slay_rules", GameController.slay_rules);
    }


    private void saveStatistics() {
        Statistics statistics = gameController.statistics;
        prefs.putInteger("save_stat_turns_made", statistics.turnsMade);
        prefs.putInteger("save_stat_units_died", statistics.unitsDied);
        prefs.putInteger("save_stat_units_produced", statistics.unitsProduced);
        prefs.putInteger("save_stat_money_spent", statistics.moneySpent);
    }


    private String getHexString(Hex hex) {
        // index1 - index2 - colorIndex - objectInside - unitStrength - unitReadyToMove - money

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("" + hex.index1);
        stringBuffer.append(" " + hex.index2);
        stringBuffer.append(" " + hex.colorIndex);
        stringBuffer.append(" " + hex.objectInside);

        if (hex.containsUnit()) {
            stringBuffer.append(" " + hex.unit.strength);
            if (hex.unit.isReadyToMove()) {
                stringBuffer.append(" " + 1);
            } else {
                stringBuffer.append(" " + 0);
            }
        } else {
            stringBuffer.append(" " + 0);
            stringBuffer.append(" " + 0);
        }

        Province province = gameController.getProvinceByHex(hex);
        if (province != null) {
            stringBuffer.append(" " + province.money);
        } else {
            stringBuffer.append(" " + 10);
        }
        return stringBuffer.toString();
    }


    public String getActiveHexesString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Hex activeHex : gameController.activeHexes) {
            String hexString = getHexString(activeHex);
            stringBuffer.append(hexString);
            stringBuffer.append(tokenSeparator);
        }
        if (stringBuffer.length() > 0)
            stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
        return stringBuffer.toString();
    }


    public void saveGameToSlot(int slotIndex) {
        saveGame("save_slot" + slotIndex);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        prefs.putString("date", dateString);
        prefs.flush();
        gameController.yioGdxGame.menuControllerYio.updateSaveSlotButton(slotIndex);
    }


    void saveGame() {
        saveGame("save"); // default save prefs
    }


    private void saveGame(String prefsName) {
        prefs = Gdx.app.getPreferences(prefsName);
        saveBasicInfo();
        saveStatistics();
        prefs.putString("save_active_hexes", getActiveHexesString());
        prefs.flush();
    }


    public void setActiveHexesString(String activeHexesString) {
        this.activeHexesString = activeHexesString;
    }


    private String getHexStringBySnapshot(int snapshot[]) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < snapshot.length; i++) {
            stringBuffer.append("" + snapshot[i]);
            if (i != snapshot.length - 1) stringBuffer.append(" ");
        }
        return stringBuffer.toString();
    }


    private int[] getHexSnapshotByString(String hexString) {
        int snapshot[] = new int[7];
        StringTokenizer stringTokenizer = new StringTokenizer(hexString, " ");
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            int value = Integer.valueOf(token);
            snapshot[i] = value;
            i++;
        }
        return snapshot;
    }


    private void activateHexByString(String hexString) {
        int snapshot[] = getHexSnapshotByString(hexString);
        int index1 = snapshot[0], index2 = snapshot[1];
        Hex hex = gameController.field[index1][index2];
        hex.active = true;
        hex.setColorIndex(snapshot[2]);
        ListIterator activeIterator = gameController.activeHexes.listIterator();
        int objectInside = snapshot[3];
        if (objectInside > 0) {
            gameController.addSolidObject(hex, objectInside);
        }
        int unitStrength = snapshot[4];
        if (unitStrength > 0) {
            gameController.addUnit(hex, unitStrength);
            if (snapshot[5] == 1) { // ready to move
                hex.unit.setReadyToMove(true);
                hex.unit.startJumping();
            } else {
                hex.unit.setReadyToMove(false);
                hex.unit.stopJumping();
            }
        }
        hex.moveZoneNumber = snapshot[6]; // this is actually money on hex
        activeIterator.add(hex);
    }


    private void createHexStrings() {
        StringTokenizer tokenizer = new StringTokenizer(activeHexesString, tokenSeparator);
        hexStrings = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            hexStrings.add(token);
        }
    }


    private void recreateMap() {
        for (String hexString : hexStrings) {
            activateHexByString(hexString);
        }
        gameController.detectProvinces();
        for (Province province : gameController.provinces) {
            Hex hex = province.hexList.get(0);
            province.money = hex.moveZoneNumber;
            province.updateName();
        }
    }


    void setBasicInfo(int turn, int playerNumber, int colorNumber, int levelSize, int difficulty) {
        gameController.turn = turn;
        gameController.setPlayersNumber(playerNumber);
        GameController.setColorNumber(colorNumber);
        gameController.setLevelSize(levelSize);
        gameController.setDifficulty(difficulty);
    }


    private void loadStatistics() {
        Statistics statistics = gameController.statistics;
        statistics.turnsMade = prefs.getInteger("save_stat_turns_made", statistics.turnsMade);
        statistics.unitsDied = prefs.getInteger("save_stat_units_died", statistics.unitsDied);
        statistics.unitsProduced = prefs.getInteger("save_stat_units_produced", statistics.unitsProduced);
        statistics.moneySpent = prefs.getInteger("save_stat_money_spent", statistics.moneySpent);
    }


    private void loadBasicInfo() {
        setBasicInfo(prefs.getInteger("save_turn"),
                prefs.getInteger("save_player_number"),
                prefs.getInteger("save_color_number"),
                prefs.getInteger("save_level_size"),
                prefs.getInteger("save_difficulty"));
        gameController.campaignMode = prefs.getBoolean("save_campaign_mode");
        gameController.setCurrentLevelIndex(prefs.getInteger("save_current_level"));
        gameController.colorIndexViewOffset = prefs.getInteger("save_color_offset", 0);
        GameController.slay_rules = prefs.getBoolean("slay_rules", true);
    }


    private boolean isOverTheTop() {
        for (String hexString : hexStrings) {
            int snapshot[] = getHexSnapshotByString(hexString);
            float posY = gameController.fieldPos.y + gameController.hexStep1 * snapshot[0] + gameController.hexStep2 * snapshot[1] * gameController.cos60;
            if (posY > gameController.boundHeight - gameController.hexSize) {
                return true;
            }
        }
        return false;
    }


    private boolean canMoveDown() {
        for (String hexString : hexStrings) {
            int snapshot[] = getHexSnapshotByString(hexString);
            float posY = gameController.fieldPos.y + gameController.hexStep1 * snapshot[0] + gameController.hexStep2 * snapshot[1] * gameController.cos60;
            if (posY < gameController.hexSize) return false;
            if (snapshot[0] < 1) return false;
        }
        return true;
    }


    private void supportForWideScreens() {
        while (isOverTheTop() && canMoveDown()) {
            ArrayList<String> copy = new ArrayList<String>(hexStrings);
            hexStrings = new ArrayList<String>();
            for (String oldHexString : copy) {
                int snapshot[] = getHexSnapshotByString(oldHexString);
                snapshot[0]--;
                hexStrings.add(getHexStringBySnapshot(snapshot));
            }
            gameController.compensationOffsetY--;
        }
    }


    void beginRecreation() {
        beginRecreation(true);
    }


    void beginRecreation(boolean readParametersFromSliders) {
        gameController.yioGdxGame.startGame(false, readParametersFromSliders);
        gameController.createFieldMatrix();
        createHexStrings();
        if (YioGdxGame.isScreenVeryWide()) supportForWideScreens();
        recreateMap();
    }


    void endRecreation() {
        gameController.createCamera();
        gameController.yioGdxGame.gameView.updateCacheLevelTextures();
        gameController.yioGdxGame.gameView.updateAnimationTexture();
        gameController.createAiList();
        gameController.updateLevelInitialString();
    }


    public void loadGameFromSlot(int slotIndex) {
        loadGame("save_slot" + slotIndex);
    }


    void loadGame() {
        loadGame("save"); // default save prefs
    }


    private void loadGame(String prefsName) {
        prefs = Gdx.app.getPreferences(prefsName);
        activeHexesString = prefs.getString("save_active_hexes", "");
        if (activeHexesString.length() < 3) return;
        beginRecreation();
        loadBasicInfo();
        loadStatistics();
        endRecreation();
    }
}
