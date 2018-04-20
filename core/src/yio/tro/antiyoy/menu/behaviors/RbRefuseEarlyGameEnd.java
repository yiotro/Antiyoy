package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.gameplay.RefuseStatistics;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class RbRefuseEarlyGameEnd extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).setGamePaused(false);
        if (getGameController(buttonYio).tutorialScript != null)
            getGameController(buttonYio).tutorialScript.setTipIsCurrentlyShown(false);

        Scenes.sceneTutorialTip.hide();
        RefuseStatistics.getInstance().onEarlyGameEndRefuse();
    }
}
