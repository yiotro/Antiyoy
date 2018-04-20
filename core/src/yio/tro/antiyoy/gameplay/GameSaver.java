package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyInfoCondensed;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingMode;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.Yio;

import java.util.*;


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
        prefs.putInteger("save_color_number", GameRules.colorNumber);
        prefs.putInteger("save_level_size", gameController.fieldController.levelSize);
        prefs.putInteger("save_player_number", gameController.playersNumber);
        prefs.putBoolean("save_campaign_mode", GameRules.campaignMode);
        prefs.putInteger("save_current_level", CampaignProgressManager.getInstance().currentLevelIndex);
        prefs.putInteger("save_difficulty", GameRules.difficulty);
        prefs.putInteger("save_color_offset", gameController.colorIndexViewOffset);
        prefs.putBoolean("slay_rules", GameRules.slayRules);
        prefs.putString("date", Yio.getDate());
        prefs.putBoolean("fog_of_war", GameRules.fogOfWarEnabled);
        prefs.putBoolean("diplomacy", GameRules.diplomacyEnabled);
        prefs.putBoolean("user_level_mode", GameRules.userLevelMode);
        if (GameRules.ulKey != null) {
            prefs.putString("ul_key", GameRules.ulKey);
        }
    }


    private void saveStatistics() {
        MatchStatistics matchStatistics = gameController.matchStatistics;
        prefs.putInteger("save_stat_turns_made", matchStatistics.turnsMade);
        prefs.putInteger("save_stat_units_died", matchStatistics.unitsDied);
        prefs.putInteger("save_stat_units_produced", matchStatistics.unitsProduced);
        prefs.putInteger("save_stat_money_spent", matchStatistics.moneySpent);
        prefs.putInteger("save_stat_time_count", matchStatistics.timeCount);
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
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            String hexString = getHexString(activeHex);
            stringBuffer.append(hexString);
            stringBuffer.append(tokenSeparator);
        }
        if (stringBuffer.length() > 0) {
            stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
        }
        return stringBuffer.toString();
    }


    public void saveGameToSlot(int slotIndex) {
        // this is for old save slots menu

//        saveGame("save_slot" + slotIndex);
//        String dateString = Yio.getDate();
//        prefs.putString("date", dateString);
//        prefs.flush();
//        Scenes.sceneSaveSlotsOld.updateSaveSlotButton(slotIndex);
    }


    void saveGame() {
        saveGame("save"); // default save prefs
    }


    public void saveGame(String prefsName) {
        prefs = Gdx.app.getPreferences(prefsName);
        saveBasicInfo();
        saveStatistics();
        saveDiplomacy();
        saveInitialLevelString();
        prefs.putString("save_active_hexes", getActiveHexesString());
        prefs.flush();
    }


    private void saveInitialLevelString() {
        prefs.putString("initial_level", gameController.fieldController.initialLevelString);
    }


    private void saveDiplomacy() {
        if (!GameRules.diplomacyEnabled) return;

        DiplomacyManager diplomacyManager = gameController.fieldController.diplomacyManager;
        DiplomacyInfoCondensed instance = DiplomacyInfoCondensed.getInstance();
        instance.update(diplomacyManager);

        prefs.putString("diplomacy_info", instance.getFull());
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
        Hex hex = gameController.fieldController.field[index1][index2];
        hex.active = true;
        hex.setColorIndex(snapshot[2]);
        ListIterator activeIterator = gameController.fieldController.activeHexes.listIterator();
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


    public void createHexStrings() {
        StringTokenizer tokenizer = new StringTokenizer(activeHexesString, tokenSeparator);
        hexStrings = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            hexStrings.add(token);
        }
    }


    public void recreateMap() {
        for (String hexString : hexStrings) {
            activateHexByString(hexString);
        }
        gameController.fieldController.detectProvinces();
        for (Province province : gameController.fieldController.provinces) {
            Hex hex = province.hexList.get(0);
            province.money = hex.moveZoneNumber;
            province.updateName();
        }
    }


    private void loadStatistics() {
        MatchStatistics matchStatistics = gameController.matchStatistics;
        matchStatistics.turnsMade = prefs.getInteger("save_stat_turns_made", matchStatistics.turnsMade);
        matchStatistics.unitsDied = prefs.getInteger("save_stat_units_died", matchStatistics.unitsDied);
        matchStatistics.unitsProduced = prefs.getInteger("save_stat_units_produced", matchStatistics.unitsProduced);
        matchStatistics.moneySpent = prefs.getInteger("save_stat_money_spent", matchStatistics.moneySpent);
        matchStatistics.timeCount = prefs.getInteger("save_stat_time_count", matchStatistics.timeCount);
    }


    public void beginRecreation() {
        gameController.fieldController.createFieldMatrix();
        createHexStrings();
        recreateMap();
    }


    public void loadGame(String prefsName) {
        prefs = Gdx.app.getPreferences(prefsName);
        activeHexesString = prefs.getString("save_active_hexes", "");
        if (activeHexesString.length() < 3) return;

        LoadingParameters instance = LoadingParameters.getInstance();
        instance.mode = LoadingMode.LOAD_GAME;
        instance.applyPrefs(prefs);
        instance.activeHexes = activeHexesString;
        LoadingManager.getInstance().startGame(instance);
        loadStatistics();
        loadDiplomacy();
        loadInitialLevelString();
    }


    private void loadInitialLevelString() {
        gameController.fieldController.initialLevelString = prefs.getString("initial_level", null);
    }


    private void loadDiplomacy() {
        if (!GameRules.diplomacyEnabled) return;

        DiplomacyManager diplomacyManager = gameController.fieldController.diplomacyManager;
        DiplomacyInfoCondensed instance = DiplomacyInfoCondensed.getInstance();
        instance.setFull(prefs.getString("diplomacy_info", "-"));
        instance.apply(diplomacyManager);
    }


    public void detectRules() {
        GameRules.setSlayRules(true);
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (doesHexRequireGenericRules(activeHex)) {
                GameRules.setSlayRules(false);
                System.out.println("detected generic rules");
                return;
            }
        }
    }


    private boolean doesHexRequireGenericRules(Hex activeHex) {
        if (activeHex.colorIndex == FieldController.NEUTRAL_LANDS_INDEX) return true;
        if (activeHex.objectInside == Obj.FARM) return true;
        if (activeHex.objectInside == Obj.STRONG_TOWER) return true;

        return false;
    }
}
