package yio.tro.antiyoy.gameplay.tests;

import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.stuff.Yio;

public abstract class AbstractTest {

    GameController gameController;
    int quantity;
    long startTime;


    public AbstractTest() {
        quantity = -1;
    }


    public void perform() {
        DebugFlags.testMode = true;
        DebugFlags.testWinner = -1;
        startTime = System.currentTimeMillis();
        execute();
        disableTestMode();
    }


    protected void disableTestMode() {
        DebugFlags.testMode = false;
        gameController.yioGdxGame.gameView.rList.renderBackgroundCache.updateFullCache();
    }


    public abstract String getName();


    protected abstract void execute();


    protected String getPassedTime() {
        double delta = System.currentTimeMillis() - startTime;
        delta /= 1000;
        delta *= 60;
        return Yio.convertTime((long) delta);
    }


    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
