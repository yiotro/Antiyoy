package yio.tro.antiyoy.menu.save_slot_selector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.data_storage.GameSaver;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SaveSystem {

    public static final String SAVE_SLOT_PREFS = "antiyoy.slot_prefs";
    public static final String AUTOSAVE_KEY = "autosave";

    GameController gameController;
    public GameSaver gameSaver;


    public SaveSystem(GameController gameController) {
        this.gameController = gameController;
        gameSaver = new GameSaver(gameController);

        checkToImportOldSaves();
    }


    private void checkToImportOldSaves() {
        Preferences preferences = getPreferences(SAVE_SLOT_PREFS);
        boolean importedOldSaves = preferences.getBoolean("imported_old_saves", false);
        if (importedOldSaves) return;

        preferences.putBoolean("imported_old_saves", true);
        preferences.flush();

        performImportOldSaves();
    }


    private void performImportOldSaves() {
        tryToImportOldSave("save_slot4");
        tryToImportOldSave("save_slot3");
        tryToImportOldSave("save_slot2");
        tryToImportOldSave("save_slot1");
        tryToImportOldSave("save_slot0");
        tryToImportOldSave("save");
    }


    private void tryToImportOldSave(String key) {
        Preferences preferences = getPreferences(key);
        String activeHexes = preferences.getString("save_active_hexes", "");
        if (activeHexes.length() < 3) return; // empty slot

        addKey(key, SAVE_SLOT_PREFS);

        SaveSlotInfo saveSlotInfo = new SaveSlotInfo();
        saveSlotInfo.name = getNameString(preferences);
        if (saveSlotInfo.name.length() < 3) {
            saveSlotInfo.name = "slot";
        }
        saveSlotInfo.description = getDescriptionString(preferences);
        if (saveSlotInfo.description.length() < 3) {
            saveSlotInfo.description = "old save";
        }
        saveSlotInfo.key = key;

        editSlot(key, saveSlotInfo, SAVE_SLOT_PREFS);
    }


    public static String getNameString(Preferences slotPrefs) {
        String multiMode = "";
        if (slotPrefs.getInteger("save_player_number") > 1) {
            multiMode = " [" + slotPrefs.getInteger("save_player_number") + "x]";
        }

        if (slotPrefs.getBoolean("save_campaign_mode")) {
            return LanguagesManager.getInstance().getString("choose_game_mode_campaign") + " " + slotPrefs.getInteger("save_current_level");
        } else {
            return LanguagesManager.getInstance().getString("choose_game_mode_skirmish") + multiMode;
        }
    }


    public static String getDescriptionString(Preferences slotPrefs) {
        return slotPrefs.getString("date");
    }


    public ArrayList<String> getKeys(String prefs) {
        Preferences preferences = getPreferences(prefs);
        String keys = getKeysString(preferences);
        StringTokenizer tokenizer = new StringTokenizer(keys, " ");
        ArrayList<String> result = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }

        return result;
    }


    private String getKeysString(Preferences preferences) {
        return preferences.getString("keys", getDefaultKeys());
    }


    private String getDefaultKeys() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < SaveSlotSelector.MIN_ITEMS_NUMBER; i++) {
            stringBuilder.append("def_slot_").append(i).append(" ");
        }

        return stringBuilder.toString();
    }


    public SaveSlotInfo getSlotInfo(String key, String prefsName) {
        Preferences preferences = getPreferences(prefsName);
        SaveSlotInfo info = new SaveSlotInfo();
        info.name = preferences.getString(key + ":name");
        info.description = preferences.getString(key + ":desc");
        info.key = key;
        return info;
    }


    public String getKeyForNewSlot(String prefs) {
        int id;
        String key;

        while (true) {
            id = YioGdxGame.random.nextInt(10000);
            key = prefs + "_" + id;

            if (!containsKey(key, prefs)) break;
        }

        return key;
    }


    public boolean containsKey(String key, String prefs) {
        for (String s : getKeys(prefs)) {
            if (s.equals(key)) {
                return true;
            }
        }
        return false;
    }


    public void addKey(String newKey, String prefs) {
        if (containsKey(newKey, prefs)) return;

        Preferences preferences = getPreferences(prefs);
        String keys = getKeysString(preferences);
        preferences.putString("keys", newKey + " " + keys);
        preferences.flush();
    }


    public void deleteSlot(String key, String prefs) {
        Preferences preferences = getPreferences(prefs);

        StringBuilder newKeys = new StringBuilder();
        for (String k : getKeys(prefs)) {
            if (k.equals(key)) continue;
            newKeys.append(k).append(" ");
        }

        preferences.putString("keys", newKeys.toString());
        preferences.flush();
    }


    public void editSlot(String key, SaveSlotInfo saveSlotInfo, String prefs) {
        Preferences preferences = getPreferences(prefs);
        preferences.putString(key + ":name", saveSlotInfo.name);
        preferences.putString(key + ":desc", saveSlotInfo.getDescription());
        preferences.flush();
    }


    private Preferences getPreferences(String prefs) {
        return Gdx.app.getPreferences(prefs);
    }


    public void performAutosave() {
        String autosaveKey = AUTOSAVE_KEY;

        if (!containsKey(autosaveKey, SAVE_SLOT_PREFS)) {
            addKey(autosaveKey, SAVE_SLOT_PREFS);
        }

        SaveSlotInfo saveSlotInfo = new SaveSlotInfo();
        saveSlotInfo.name = LanguagesManager.getInstance().getString("autosave");
        saveSlotInfo.description = Yio.getDate();
        editSlot(autosaveKey, saveSlotInfo, SAVE_SLOT_PREFS);

        saveGame(autosaveKey);
        moveAutosaveKeyToFirstPlace();
    }


    public void moveAutosaveKeyToFirstPlace() {
        if (!containsKey(AUTOSAVE_KEY, SAVE_SLOT_PREFS)) return;

        ArrayList<String> keys = getKeys(SAVE_SLOT_PREFS);
        keys.remove(AUTOSAVE_KEY);
        keys.add(0, AUTOSAVE_KEY);

        StringBuilder newKeys = new StringBuilder();
        for (String key : keys) {
            newKeys.append(key).append(" ");
        }

        Preferences preferences = getPreferences(SAVE_SLOT_PREFS);
        preferences.putString("keys", newKeys.toString());
        preferences.flush();
    }


    public void saveGame(String prefsName) {
        gameSaver.saveGame(prefsName);
    }


    public void loadGame(String prefsName) {
        gameSaver.loadGame(prefsName);
    }


    public void loadTopSlot() {
        ArrayList<String> keys = getKeys(SAVE_SLOT_PREFS);
        if (keys.size() == 0) return;

        for (String key : keys) {
            loadGame(key);
            break;
        }
    }

}
