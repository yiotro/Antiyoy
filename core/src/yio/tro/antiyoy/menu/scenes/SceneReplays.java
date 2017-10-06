package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.replay_selector.ReplaySelector;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;

public class SceneReplays extends AbstractScene{

    ReplaySelector replaySelector;


    public SceneReplays(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        replaySelector = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        changeBackground();
        createBackButton();
        createReplaySelector();

        menuControllerYio.endMenuCreation();
    }


    private void createReplaySelector() {
        if (replaySelector == null) {
            replaySelector = new ReplaySelector(menuControllerYio, -1);
            replaySelector.setPosition(generateRectangle(0.1, 0.07, 0.8, 0.75));
            menuControllerYio.addElementToScene(replaySelector);
        }

        replaySelector.appear();
    }


    private void createBackButton() {
        menuControllerYio.spawnBackButton(600, ReactBehavior.rbMoreSettings);
    }


    private void changeBackground() {
        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, false, true);
    }
}
