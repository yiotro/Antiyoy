package yio.tro.antiyoy.gameplay.replays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ReplaySaveSystem {

    public static final String REPLAYS_PREFS = "antiyoy.replays";
    private static ReplaySaveSystem instance;
    private Preferences prefs;
    private ArrayList<String> keys;
    GameController gameController;
    HashMap<String, String> renames;


    public ReplaySaveSystem() {
        keys = new ArrayList<>();
        gameController = null;
        renames = new HashMap<>();
        loadRenames();
    }


    public static void initialize() {
        instance = null;
    }


    public static ReplaySaveSystem getInstance() {
        if (instance == null) {
            instance = new ReplaySaveSystem();
        }

        return instance;
    }


    private void loadKeys() {
        updatePrefs();

        String source = prefs.getString("keys");
        StringTokenizer tokenizer = new StringTokenizer(source, " ");

        keys.clear();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            keys.add(token);
        }
    }


    private void updatePrefs() {
        prefs = Gdx.app.getPreferences(REPLAYS_PREFS);
    }


    private int getMaxKey() {
        int max = 0;

        for (String key : keys) {
            int value = Integer.valueOf(key);
            if (value > max) {
                max = value;
            }
        }

        return max;
    }


    private String getKeyForNewSlot() {
        return "" + (getMaxKey() + 1);
    }


    private void saveKeys() {
        updatePrefs();

        StringBuilder builder = new StringBuilder();

        for (String key : keys) {
            builder.append(key).append(" ");
        }

        prefs.putString("keys", builder.toString());
        prefs.flush();
    }


    public void clearKeys() {
        loadKeys();
        keys.clear();
        saveKeys();
    }


    public void saveReplay(Replay replay) {
        String keyForNewSlot = addKey();

        RepSlot repSlot = new RepSlot(gameController, keyForNewSlot);
        repSlot.campaignMode = GameRules.campaignMode;
        repSlot.levelIndex = CampaignProgressManager.getInstance().getCurrentLevelIndex();
        repSlot.numberOfHumans = replay.realNumberOfHumans;
        repSlot.replay = replay;
        repSlot.save();
    }


    public void removeReplay(String key) {
        keys.remove(key);

        saveKeys();
    }


    public RepSlot getSlotByKey(String key) {
        RepSlot repSlot = new RepSlot(gameController, key);
        repSlot.load();

        return repSlot;
    }


    private String addKey() {
        loadKeys();
        String keyForNewSlot = getKeyForNewSlot();
        keys.add(0, keyForNewSlot);
        saveKeys();

        return keyForNewSlot;
    }


    public void saveRenames() {
        updatePrefs();
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : renames.entrySet()) {
            builder.append(entry.getKey()).append("@").append(entry.getValue()).append("#");
        }
        prefs.putString("renames", builder.toString());
        prefs.flush();
    }


    public boolean isSlotRenamed(String key) {
        return renames.keySet().contains(key);
    }


    public String getCustomSlotName(String key) {
        if (!isSlotRenamed(key)) return null;
        return renames.get(key);
    }


    public void applySlotRename(String key, String value) {
        renames.put(key, value);
        saveRenames();
    }


    public void loadRenames() {
        updatePrefs();
        renames.clear();
        String source = prefs.getString("renames", "");
        if (source.length() == 0) return;
        for (String token : source.split("#")) {
            String[] split = token.split("@");
            String key = split[0];
            String value = split[1];
            renames.put(key, value);
        }
    }


    public ArrayList<String> getKeys() {
        loadKeys();
        return keys;
    }


    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
