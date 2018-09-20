package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class OneTimeInfo {

    public static final String PREFS_ONE_TIME = "antiyoy.one_time_info";
    private static OneTimeInfo instance = null;

    public boolean iosPortDone;


    public static void initialize() {
        instance = null;
    }


    public static OneTimeInfo getInstance() {
        if (instance == null) {
            instance = new OneTimeInfo();
            instance.load();
        }

        return instance;
    }


    void load() {
        Preferences preferences = Gdx.app.getPreferences(PREFS_ONE_TIME);

        iosPortDone = preferences.getBoolean("ios_port_done", true);
    }


    public void save() {
        Preferences preferences = Gdx.app.getPreferences(PREFS_ONE_TIME);

        preferences.putBoolean("ios_port_done", iosPortDone);

        preferences.flush();
    }
}
