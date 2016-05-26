package yio.tro.antiyoy;

/**
 * Created by ivan on 09.11.2015.
 */
public class Statistics {

    final GameController gameController;
    int turnsMade, unitsDied, unitsProduced, moneySpent;


    public Statistics(GameController gameController) {
        this.gameController = gameController;
    }


    void defaultValues() {
        turnsMade = 0;
        unitsDied = 0;
        unitsProduced = 0;
        moneySpent = 0;
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
}
