package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class OneTimeInfo {

    public static final String PREFS_ONE_TIME = "antiyoy.one_time_info";
    private static OneTimeInfo instance = null;

    public boolean aboutOnlyOneKnight;
    public boolean aboutKnightPenalty;
    public boolean aboutShroomArts;
    public boolean aboutStrongTowerDemote;


    public static OneTimeInfo getInstance() {
        if (instance == null) {
            instance = new OneTimeInfo();
            instance.loadValues();
        }

        return instance;
    }


    void loadValues() {
        Preferences preferences = Gdx.app.getPreferences(PREFS_ONE_TIME);

        aboutOnlyOneKnight = preferences.getBoolean("only_one_knight", false);
        aboutKnightPenalty = preferences.getBoolean("knight_penalty", false);
        aboutShroomArts = preferences.getBoolean("shroom_arts", false);
        aboutStrongTowerDemote = preferences.getBoolean("strong_tower_demote", false);
    }


    public void save() {
        Preferences preferences = Gdx.app.getPreferences(PREFS_ONE_TIME);

        preferences.putBoolean("only_one_knight", aboutOnlyOneKnight);
        preferences.putBoolean("knight_penalty", aboutKnightPenalty);
        preferences.putBoolean("shroom_arts", aboutShroomArts);
        preferences.putBoolean("strong_tower_demote", aboutStrongTowerDemote);

        preferences.flush();
    }
}
