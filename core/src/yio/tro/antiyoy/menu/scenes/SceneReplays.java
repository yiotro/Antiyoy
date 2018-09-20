package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.replay_selector.ReplaySelector;
import yio.tro.antiyoy.menu.behaviors.Reaction;

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
            replaySelector.setPosition(generateRectangle(0.05, 0.05, 0.9, 0.8));
            menuControllerYio.addElementToScene(replaySelector);
        }

        replaySelector.appear();
    }


    private void createBackButton() {
        menuControllerYio.spawnBackButton(600, Reaction.rbLoadGame);
    }


    private void changeBackground() {
        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, false, true);
    }
}
