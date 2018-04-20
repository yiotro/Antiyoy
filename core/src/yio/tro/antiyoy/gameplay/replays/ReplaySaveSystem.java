package yio.tro.antiyoy.gameplay.replays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ReplaySaveSystem {

    public static final String REPLAYS_PREFS = "antiyoy.replays";
    private static ReplaySaveSystem instance;
    private Preferences prefs;
    private ArrayList<String> keys;
    GameController gameController;


    public ReplaySaveSystem() {
        keys = new ArrayList<>();
        gameController = null;
    }


    public static void resetInstance() {
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


    public ArrayList<String> getKeys() {
        loadKeys();
        return keys;
    }


    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
