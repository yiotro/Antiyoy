package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.fast_construction.FastConstructionPanel;

public class SceneFastConstructionPanel extends AbstractModalScene {

    public FastConstructionPanel fastConstructionPanel;


    public SceneFastConstructionPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        fastConstructionPanel = null;
    }


    @Override
    public void create() {
        if (isAlreadyShown()) return;

        checkToCreateFastConstructionPanel();

        fastConstructionPanel.appear();
    }


    private boolean isAlreadyShown() {
        if (fastConstructionPanel == null) return false;
        if (fastConstructionPanel.getFactor().get() != 1) return false;
        if (fastConstructionPanel.getFactor().getGravity() <= 0) return false;
        return true;
    }


    private void checkToCreateFastConstructionPanel() {
        if (fastConstructionPanel != null) return;

        fastConstructionPanel = new FastConstructionPanel(menuControllerYio, -1);
        menuControllerYio.addElementToScene(fastConstructionPanel);
    }


    @Override
    public void hide() {
        menuControllerYio.destroyButton(610);

        if (fastConstructionPanel != null) {
            fastConstructionPanel.destroy();
        }
    }
}
