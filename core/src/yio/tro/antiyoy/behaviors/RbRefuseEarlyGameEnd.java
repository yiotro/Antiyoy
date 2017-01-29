package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.gameplay.RefuseStatistics;
import yio.tro.antiyoy.menu.ButtonYio;

public class RbRefuseEarlyGameEnd extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).setGamePaused(false);
        if (getGameController(buttonYio).tutorialScript != null)
            getGameController(buttonYio).tutorialScript.setTipIsCurrentlyShown(false);

        buttonYio.menuControllerYio.closeTutorialTip();
        RefuseStatistics.getInstance().onEarlyGameEndRefuse();
    }
}
