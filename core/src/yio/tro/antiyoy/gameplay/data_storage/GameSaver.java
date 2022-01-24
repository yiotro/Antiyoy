package yio.tro.antiyoy.gameplay.data_storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyInfoCondensed;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;


public class GameSaver {

    private GameController gameController;
    public String tokenSeparator, activeHexesString;
    private Preferences prefs;
    private ArrayList<String> hexStrings;
    public LegacyExportManager legacyExportManager;
    public LegacyImportManager legacyImportManager;


    public GameSaver(GameController gameController) {
        this.gameController = gameController;
        legacyExportManager = new LegacyExportManager(gameController);
        legacyImportManager = new LegacyImportManager(gameController);
        tokenSeparator = "#";
    }


    private void saveBasicInfo() {
        prefs.putInteger("save_turn", gameController.turn);
        prefs.putInteger("save_color_number", GameRules.fractionsQuantity);
        prefs.putInteger("save_level_size", gameController.fieldManager.getLevelSize());
        prefs.putInteger("save_player_number", gameController.playersNumber);
        prefs.putBoolean("save_campaign_mode", GameRules.campaignMode);
        prefs.putInteger("save_current_level", getCurrentLevelIndexForSave());
        prefs.putInteger("save_difficulty", GameRules.difficulty);
        prefs.putInteger("save_color_offset", gameController.colorsManager.colorOffset);
        prefs.putBoolean("slay_rules", GameRules.slayRules);
        prefs.putString("date", Yio.getDate());
        prefs.putBoolean("fog_of_war", GameRules.fogOfWarEnabled);
        prefs.putBoolean("diplomacy", GameRules.diplomacyEnabled);
        prefs.putBoolean("user_level_mode", GameRules.userLevelMode);
        if (GameRules.ulKey != null) {
            prefs.putString("ul_key", GameRules.ulKey);
        }
        prefs.putBoolean("editor_color_fix_applied", GameRules.editorColorFixApplied);
        prefs.putBoolean("lock_relations", GameRules.diplomaticRelationsLocked);
    }


    private int getCurrentLevelIndexForSave() {
        if (!GameRules.campaignMode) {
            return -1; 
        }

        return CampaignProgressManager.getInstance().currentLevelIndex;
    }


    private void saveStatistics() {
        MatchStatistics matchStatistics = gameController.matchStatistics;
        prefs.putInteger("save_stat_turns_made", matchStatistics.turnsMade);
        prefs.putInteger("save_stat_units_died", matchStatistics.unitsDied);
        prefs.putInteger("save_stat_units_produced", matchStatistics.unitsProduced);
        prefs.putInteger("save_stat_money_spent", matchStatistics.moneySpent);
        prefs.putInteger("save_stat_time_count", matchStatistics.timeCount);
    }


    public void saveGame(String prefsName) {
        prefs = Gdx.app.getPreferences(prefsName);
        saveBasicInfo();
        saveStatistics();
        saveDiplomacy();
        saveInitialLevelString();
        saveNamings();
        saveGoal();
        saveDebts();
        prefs.putString("save_active_hexes", legacyExportManager.getActiveHexesString());
        prefs.flush();
    }


    private void saveDebts() {
        String debtsString = gameController.fieldManager.diplomacyManager.encodeDebts();
        prefs.putString("debts", debtsString);
    }


    private void saveGoal() {
        String goalString = gameController.finishGameManager.encode();
        prefs.putString("goal", goalString);
    }


    private void saveNamings() {
        String saveString = gameController.namingManager.saveToString();
        prefs.putString("namings", saveString);
    }


    private void saveInitialLevelString() {
        prefs.putString("initial_level", gameController.fieldManager.initialLevelString);
    }


    private void saveDiplomacy() {
        if (!GameRules.diplomacyEnabled) return;

        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
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
            if (i >= snapshot.length) break;
            snapshot[i] = value;
            i++;
        }
        return snapshot;
    }


    private void activateHexByString(String hexString) {
        int snapshot[] = getHexSnapshotByString(hexString);
        int index1 = snapshot[0], index2 = snapshot[1];
        Hex hex = gameController.fieldManager.field[index1][index2];
        hex.active = true;
        hex.setFraction(snapshot[2]);
        ListIterator activeIterator = gameController.fieldManager.activeHexes.listIterator();
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
        gameController.fieldManager.detectProvinces();
        for (Province province : gameController.fieldManager.provinces) {
            Hex hex = province.hexList.get(0);
            province.money = hex.moveZoneNumber;
            province.updateName();
        }
    }


    public void loadStatistics() {
        MatchStatistics matchStatistics = gameController.matchStatistics;
        matchStatistics.turnsMade = prefs.getInteger("save_stat_turns_made", matchStatistics.turnsMade);
        matchStatistics.unitsDied = prefs.getInteger("save_stat_units_died", matchStatistics.unitsDied);
        matchStatistics.unitsProduced = prefs.getInteger("save_stat_units_produced", matchStatistics.unitsProduced);
        matchStatistics.moneySpent = prefs.getInteger("save_stat_money_spent", matchStatistics.moneySpent);
        matchStatistics.timeCount = prefs.getInteger("save_stat_time_count", matchStatistics.timeCount);
    }


    public void beginRecreation() {
        gameController.fieldManager.createFieldMatrix();
        createHexStrings();
        recreateMap();
    }


    public void loadGame(String prefsName) {
        prefs = Gdx.app.getPreferences(prefsName);
        activeHexesString = prefs.getString("save_active_hexes", "");
        if (activeHexesString.length() < 3) return;

        LoadingParameters instance = LoadingParameters.getInstance();
        instance.loadingType = LoadingType.load_game;
        instance.loadBasicInfo(prefs);
        instance.activeHexes = activeHexesString;
        LoadingManager.getInstance().startGame(instance);
        loadNamings();
        loadStatistics();
        loadDiplomacy();
        loadInitialLevelString();
        loadGoal();
        loadDebts();
    }


    private void loadDebts() {
        gameController.fieldManager.diplomacyManager.decodeDebts(prefs.getString("debts", ""));
    }


    private void loadGoal() {
        gameController.finishGameManager.decode(prefs.getString("goal", ""));
    }


    public void loadNamings() {
        gameController.namingManager.loadFromString(prefs.getString("namings"));
    }


    public void loadInitialLevelString() {
        gameController.fieldManager.initialLevelString = prefs.getString("initial_level", null);
    }


    public void loadDiplomacy() {
        if (!GameRules.diplomacyEnabled) return;

        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        DiplomacyInfoCondensed instance = DiplomacyInfoCondensed.getInstance();
        instance.setFull(prefs.getString("diplomacy_info", "-"));
        instance.apply(diplomacyManager);
    }


    public void detectRules() {
        GameRules.setSlayRules(true);
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (!doesHexRequireGenericRules(activeHex)) continue;
            GameRules.setSlayRules(false);
            return;
        }
    }


    private boolean doesHexRequireGenericRules(Hex activeHex) {
        if (activeHex.isNeutral()) return true;
        if (activeHex.objectInside == Obj.FARM) return true;
        if (activeHex.objectInside == Obj.STRONG_TOWER) return true;

        return false;
    }
}
