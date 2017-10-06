package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.speed_panel.SpeedPanel;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;

public class SceneAiOnlyOverlay extends AbstractScene{


    public SpeedPanel speedPanel;
    public ButtonYio inGameMenuButton;


    public SceneAiOnlyOverlay(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        speedPanel = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        createInGameMenuButton();

        createSpeedPanel();
        speedPanel.appear();

        menuControllerYio.endMenuCreation();
    }


    private void createInGameMenuButton() {
        inGameMenuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07), 530, null);
        menuControllerYio.loadButtonOnce(inGameMenuButton, "menu_icon.png");
        inGameMenuButton.setReactBehavior(ReactBehavior.rbPauseMenu);
        inGameMenuButton.setAnimType(ButtonYio.ANIM_UP);
        inGameMenuButton.enableRectangularMask();
        inGameMenuButton.disableTouchAnimation();
    }


    private void createSpeedPanel() {
        if (speedPanel != null) return;

        speedPanel = new SpeedPanel(menuControllerYio, -1);
        menuControllerYio.addElementToScene(speedPanel);
    }
}
