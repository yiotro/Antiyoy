package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GlobalStatistics {


    private static GlobalStatistics instance;
    private static String PREFS = "antiyoy.global_stats";
    public int timeInGame;
    public int wins;
    public int turnsMade;
    public int moneySpent;
    public int unitsDied;
    public int friendshipsBroken;
    private MatchStatistics lastReceivedMatchStatistics;


    public GlobalStatistics() {
        lastReceivedMatchStatistics = new MatchStatistics();
    }


    public static void initialize() {
        instance = null;
    }


    public static GlobalStatistics getInstance() {
        if (instance == null) {
            instance = new GlobalStatistics();
            instance.loadValues();
        }

        return instance;
    }


    private void loadValues() {
        Preferences prefs = getPrefs();

        timeInGame = prefs.getInteger("time_in_game", 0);
        wins = prefs.getInteger("wins", 0);
        turnsMade = prefs.getInteger("turns_made", 0);
        moneySpent = prefs.getInteger("money_spent", 0);
        unitsDied = prefs.getInteger("units_died", 0);
        friendshipsBroken = prefs.getInteger("friendships_broken", 0);
    }


    public void updateByMatchStatistics(MatchStatistics matchStatistics) {
        // this method is called at start of every turn
        int timeDelta = matchStatistics.timeCount - lastReceivedMatchStatistics.timeCount;
        if (timeDelta <= 0) return;

        timeInGame += timeDelta;
        moneySpent += Math.max(0, matchStatistics.firstPlayerMoneySpent - lastReceivedMatchStatistics.firstPlayerMoneySpent);
        turnsMade += Math.max(0, matchStatistics.turnsMade - lastReceivedMatchStatistics.turnsMade);
        unitsDied += Math.max(0, matchStatistics.unitsDied - lastReceivedMatchStatistics.unitsDied);
        friendshipsBroken += Math.max(0, matchStatistics.friendshipsBroken - lastReceivedMatchStatistics.friendshipsBroken);

        lastReceivedMatchStatistics.copyFrom(matchStatistics);
        saveValues();
    }


    public void onGameWon() {
        wins++;
        saveValues();
    }


    public void saveValues() {
        Preferences prefs = getPrefs();

         prefs.putInteger("time_in_game", timeInGame);
         prefs.putInteger("wins", wins);
         prefs.putInteger("turns_made", turnsMade);
         prefs.putInteger("money_spent", moneySpent);
         prefs.putInteger("units_died", unitsDied);
         prefs.putInteger("friendships_broken", friendshipsBroken);

        prefs.flush();
    }


    private Preferences getPrefs() {
        return Gdx.app.getPreferences(PREFS);
    }
}
