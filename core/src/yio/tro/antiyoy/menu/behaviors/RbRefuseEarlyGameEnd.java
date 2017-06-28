package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.gameplay.RefuseStatistics;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbRefuseEarlyGameEnd extends ReactBehavior{

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).setGamePaused(false);
        if (getGameController(buttonYio).tutorialScript != null)
            getGameController(buttonYio).tutorialScript.setTipIsCurrentlyShown(false);

        Scenes.sceneTutorialTip.closeTutorialTip();
        RefuseStatistics.getInstance().onEarlyGameEndRefuse();
    }
}
