package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomatic_log.DiplomaticLogPanel;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneDiplomaticLog extends AbstractModalScene {

    DiplomaticLogPanel logPanel;


    public SceneDiplomaticLog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        logPanel = null;
    }


    @Override
    public void create() {
        initLogPanelOnce();

        logPanel.appear();
        logPanel.loadValues();

        if (logPanel.isEmpty()) {
            hide();
        }
    }


    private void initLogPanelOnce() {
        if (logPanel != null) return;

        logPanel = new DiplomaticLogPanel(menuControllerYio);

        logPanel.setPosition(generateRectangle(0, 0, 1, 0.55));
        logPanel.setTitle(LanguagesManager.getInstance().getString("log"));

        menuControllerYio.addElementToScene(logPanel);
    }


    @Override
    public void hide() {
        logPanel.destroy();
    }
}
