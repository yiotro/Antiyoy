package yio.tro.antiyoy.gameplay;

/**
 * Created by ivan on 09.11.2015.
 */
public class Statistics {

    final GameController gameController;
    public int turnsMade, unitsDied, unitsProduced, moneySpent;
    public int timeCount;


    public Statistics(GameController gameController) {
        this.gameController = gameController;
    }


    void defaultValues() {
        turnsMade = 0;
        unitsDied = 0;
        unitsProduced = 0;
        moneySpent = 0;
        timeCount = 0;
    }


    void moneyWereSpent(int amount) {
        moneySpent += amount;
    }


    void unitWasProduced() {
        unitsProduced++;
    }


    void unitWasKilled() {
        unitsDied++;
    }


    void turnWasMade() {
        turnsMade++;
    }


    public String getTimeString() {
        int currentCountDown = timeCount;
        currentCountDown /= 60; // seconds
        int min = 0;
        while (currentCountDown >= 60) {
            min++;
            currentCountDown -= 60;
        }
        String zero = "";
        if (currentCountDown < 10) zero = "0";
        return min + ":" + zero + currentCountDown;
    }


    void increaseTimeCount() {
        timeCount++;
    }
}
