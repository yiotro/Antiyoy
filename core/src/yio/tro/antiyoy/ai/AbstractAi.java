package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.stuff.Yio;

public abstract class AbstractAi {

    public GameController gameController;
    protected int fraction;


    public AbstractAi(GameController gameController, int fraction) {
        this.gameController = gameController;
        this.fraction = fraction;
    }


    public abstract void perform();


    public int getFraction() {
        return fraction;
    }
}
