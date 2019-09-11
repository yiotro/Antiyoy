package yio.tro.antiyoy.gameplay.replays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.Yio;

public class RepSlot {


    GameController gameController;
    public boolean campaignMode;
    public int numberOfHumans;
    public int levelIndex;
    public String date;
    public String key;
    public Replay replay;


    public RepSlot(GameController gameController, String key) {
        this.gameController = gameController;
        this.key = key;
        replay = new Replay(gameController);
        levelIndex = -1;

        date = Yio.getDate();
    }


    public void save() {
        Preferences preferences = Gdx.app.getPreferences(key);

        preferences.putBoolean("campaign", campaignMode);
        preferences.putInteger("players", numberOfHumans);
        preferences.putString("date", date);
        preferences.putInteger("level_index", levelIndex);

        replay.setTempSlayRules(GameRules.slayRules);
        replay.setTempColorOffset(gameController.colorsManager.colorOffset);
        preferences.flush();

        replay.saveToPreferences(key);
    }


    public void load() {
        Preferences preferences = Gdx.app.getPreferences(key);

        campaignMode = preferences.getBoolean("campaign");
        numberOfHumans = preferences.getInteger("players");
        date = preferences.getString("date", "-");
        levelIndex = preferences.getInteger("level_index", -1);
    }
}
