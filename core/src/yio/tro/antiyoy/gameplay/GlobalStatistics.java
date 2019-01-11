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
    }


    public void updateByMatchStatistics(MatchStatistics matchStatistics) {
        int timeDelta = matchStatistics.timeCount - lastReceivedMatchStatistics.timeCount;
        if (timeDelta <= 0) return;

        timeInGame += timeDelta;
        moneySpent += Math.max(0, matchStatistics.firstPlayerMoneySpent - lastReceivedMatchStatistics.firstPlayerMoneySpent);
        turnsMade += Math.max(0, matchStatistics.turnsMade - lastReceivedMatchStatistics.turnsMade);

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

        prefs.flush();
    }


    private Preferences getPrefs() {
        return Gdx.app.getPreferences(PREFS);
    }
}
