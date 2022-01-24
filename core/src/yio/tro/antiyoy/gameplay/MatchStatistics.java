package yio.tro.antiyoy.gameplay;


import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.stuff.Yio;

public class MatchStatistics {

    public int turnsMade, unitsDied, unitsProduced, moneySpent;
    public int firstPlayerMoneySpent;
    public int timeCount;
    public int friendshipsBroken;


    public MatchStatistics() {

    }


    void defaultValues() {
        turnsMade = 0;
        unitsDied = 0;
        unitsProduced = 0;
        moneySpent = 0;
        timeCount = 0;
        firstPlayerMoneySpent = 0;
        friendshipsBroken = 0;
    }


    public void copyFrom(MatchStatistics source) {
        turnsMade = source.turnsMade;
        unitsDied = source.unitsDied;
        unitsProduced = source.unitsProduced;
        moneySpent = source.moneySpent;
        timeCount = source.timeCount;
        firstPlayerMoneySpent = source.firstPlayerMoneySpent;
        friendshipsBroken = source.friendshipsBroken;
    }


    void onMoneySpent(int who, int amount) {
        moneySpent += amount;

        if (who == 0) {
            onFirstPlayerSpentMoney(amount);
        }
    }


    void onUnitProduced() {
        unitsProduced++;
    }


    void onUnitKilled() {
        unitsDied++;
    }


    void onTurnMade() {
        turnsMade++;
    }


    public void onFriendshipBroken() {
        friendshipsBroken++;
    }


    void onFirstPlayerSpentMoney(int amount) {
        firstPlayerMoneySpent += amount;
    }


    public String getTimeString() {
        return Yio.convertTime(timeCount);
    }


    public void showInConsole() {
        System.out.println();
        System.out.println("Statistics:");
        System.out.println("turnsMade: " + turnsMade);
        System.out.println("unitsDied: " + unitsDied);
        System.out.println("unitsProduced: " + unitsProduced);
        System.out.println("moneySpent: " + moneySpent);
        System.out.println("friendshipsBroken: " + friendshipsBroken);
        System.out.println();
    }


    void increaseTimeCount() {
        timeCount++;
    }


    public void onUnitsMerged() {
        // merging is done by killing 2 units and making new one
        // it should be considered as production of new unit

        unitsDied -= 2;
        unitsProduced -= 1;
    }
}
