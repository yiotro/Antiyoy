package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.menu.speed_panel.SpeedPanel;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.OneTimeInfo;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SpecialActionController {

    MenuControllerYio menuControllerYio;
    boolean enabled;
    int countDown;


    public SpecialActionController(MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;

        enabled = false;
    }


    public void move() {
        if (!enabled) return;

        if (countDown > 0) {
            countDown--;
        } else {
            enabled = false;
            finish();
        }
    }


    private void start() {
        pressPlayPauseButton();
    }


    private void finish() {
        pressPlayPauseButton();
    }


    private void pressPlayPauseButton() {
        SpeedPanel speedPanel = Scenes.sceneReplayOverlay.speedPanel;
        if (speedPanel == null) return;
        speedPanel.onPlayPauseButtonPressed(menuControllerYio.getYioGdxGame().gameController.speedManager);
    }


    public void perform() {
        enabled = true;
        countDown = 5;

        start();
    }
}
