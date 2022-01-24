package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class OneTimeInfo {

    public static final String PREFS_ONE_TIME = "antiyoy.one_time_info";
    private static OneTimeInfo instance = null;

    public boolean newGameRelease;
    public boolean quickExchangeTutorial;
    public boolean iosCheckMyGames;
    public boolean antiyoyOnline;


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

        newGameRelease = preferences.getBoolean("ng_release", false);
        quickExchangeTutorial = preferences.getBoolean("quick_exchange_tutorial", false);
        iosCheckMyGames = preferences.getBoolean("ios_check_my_games", false);
        antiyoyOnline = preferences.getBoolean("antiyoy_online", false);

        newGameRelease = true; // to disable it
    }


    public void save() {
        Preferences preferences = Gdx.app.getPreferences(PREFS_ONE_TIME);

        preferences.putBoolean("ng_release", newGameRelease);
        preferences.putBoolean("quick_exchange_tutorial", quickExchangeTutorial);
        preferences.putBoolean("ios_check_my_games", iosCheckMyGames);
        preferences.putBoolean("antiyoy_online", antiyoyOnline);

        preferences.flush();
    }
}
