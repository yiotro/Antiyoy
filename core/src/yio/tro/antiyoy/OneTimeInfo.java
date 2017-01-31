package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class OneTimeInfo {

    public static final String PREFS_ONE_TIME = "antiyoy.one_time_info";
    private static OneTimeInfo instance = null;

    public boolean aboutShroomArts;


    public static OneTimeInfo getInstance() {
        if (instance == null) {
            instance = new OneTimeInfo();
            instance.loadValues();
        }

        return instance;
    }


    void loadValues() {
        Preferences preferences = Gdx.app.getPreferences(PREFS_ONE_TIME);

        aboutShroomArts = preferences.getBoolean("shroom_arts", false);
    }


    public void save() {
        Preferences preferences = Gdx.app.getPreferences(PREFS_ONE_TIME);

        preferences.putBoolean("shroom_arts", aboutShroomArts);

        preferences.flush();
    }
}
