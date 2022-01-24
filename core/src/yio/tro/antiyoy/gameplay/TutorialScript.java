package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.menu.AbstractRectangularUiElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public abstract class TutorialScript {

    GameController gameController;
    MenuControllerYio menuControllerYio;
    boolean tipIsCurrentlyShown;


    public TutorialScript(GameController gameController) {
        this.gameController = gameController;
    }


    public abstract void createTutorialGame();


    public abstract void move();


    public void setTipIsCurrentlyShown(boolean tipIsCurrentlyShown) {
        this.tipIsCurrentlyShown = tipIsCurrentlyShown;
    }

}
