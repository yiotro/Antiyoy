package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SingleMessages {

    public static final String PREFS = "antiyoy.messages";
    public static boolean achikapsRelease;


    public static void load() {
        Preferences preferences = Gdx.app.getPreferences(PREFS);

        achikapsRelease = preferences.getBoolean("achikaps_release", true);
    }


    public static void save() {
        Preferences preferences = Gdx.app.getPreferences(PREFS);

        preferences.putBoolean("achikaps_release", achikapsRelease);

        preferences.flush();
    }
}
