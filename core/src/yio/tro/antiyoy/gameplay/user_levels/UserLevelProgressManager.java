package yio.tro.antiyoy.gameplay.user_levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;

public class UserLevelProgressManager {


    private static UserLevelProgressManager instance = null;
    private static String ULP_PREFS = "antiyoy.user_levels_progress";
    ArrayList<String> keys;


    public static void initialize() {
        instance = null;
    }


    public static UserLevelProgressManager getInstance() {
        if (instance == null) {
            instance = new UserLevelProgressManager();
        }

        return instance;
    }


    public UserLevelProgressManager() {
        keys = new ArrayList<>();
        load();
    }


    private void load() {
        Preferences preferences = Gdx.app.getPreferences(ULP_PREFS);
        String keysString = preferences.getString("keys");

        String[] split = keysString.split(" ");
        for (int i = 0; i < split.length; i++) {
            keys.add(split[i]);
        }
    }


    public boolean isLevelCompleted(String key) {
        return containsKey(key);
    }


    public ArrayList<String> getKeys() {
        return keys;
    }


    public void onLevelCompleted(String key) {
        if (containsKey(key)) return;

        keys.add(key);

        save();
    }


    public int getNumberOfCompletedLevels() {
        return keys.size();
    }


    boolean containsKey(String key) {
        for (String s : keys) {
            if (s == null) continue;

            if (s.equals(key)) {
                return true;
            }
        }

        return false;
    }


    private void save() {
        Preferences preferences = Gdx.app.getPreferences(ULP_PREFS);

        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            builder.append(key).append(" ");
        }
        preferences.putString("keys", builder.toString());

        preferences.flush();
    }
}
