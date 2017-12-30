package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SingleMessages {

    public static final String PREFS = "antiyoy.messages";
    public static boolean diplomacyWinConditions;


    public static void load() {
        Preferences preferences = Gdx.app.getPreferences(PREFS);

        diplomacyWinConditions = preferences.getBoolean("diplomacy_win_conditions", false);
    }


    public static void save() {
        Preferences preferences = Gdx.app.getPreferences(PREFS);

        preferences.putBoolean("diplomacy_win_conditions", diplomacyWinConditions);

        preferences.flush();
    }
}
