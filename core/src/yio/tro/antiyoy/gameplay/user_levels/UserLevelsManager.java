package yio.tro.antiyoy.gameplay.user_levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.Arrays;

public class UserLevelsManager {


    private static UserLevelsManager instance = null;
    private static String ULP_PREFS = "antiyoy.user_levels_progress";
    ArrayList<String> keys;
    ArrayList<String> hiddenKeys;


    public static void initialize() {
        instance = null;
    }


    public static UserLevelsManager getInstance() {
        if (instance == null) {
            instance = new UserLevelsManager();
        }

        return instance;
    }


    public UserLevelsManager() {
        keys = new ArrayList<>();
        hiddenKeys = new ArrayList<>();
        load();
    }


    private void load() {
        Preferences preferences = Gdx.app.getPreferences(ULP_PREFS);

        String keysString = preferences.getString("keys");
        String[] split = keysString.split(" ");
        keys.clear();
        keys.addAll(Arrays.asList(split));

        String hiddenKeysString = preferences.getString("hidden_keys");
        hiddenKeys.clear();
        hiddenKeys.addAll(Arrays.asList(hiddenKeysString.split(" ")));
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


    public void hideLevel(String key) {
        if (isLevelHidden(key)) return;

        hiddenKeys.add(key);
        save();
    }


    public ArrayList<String> getHiddenKeys() {
        return hiddenKeys;
    }


    public boolean isLevelHidden(String key) {
        for (String s : hiddenKeys) {
            if (s == null) continue;

            if (s.equals(key)) {
                return true;
            }
        }

        return false;
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


    private String buildListAsString(ArrayList<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String key : list) {
            builder.append(key).append(" ");
        }
        return builder.toString();
    }


    private void save() {
        Preferences preferences = Gdx.app.getPreferences(ULP_PREFS);

        preferences.putString("keys", buildListAsString(keys));
        preferences.putString("hidden_keys", buildListAsString(hiddenKeys));

        preferences.flush();
    }
}
