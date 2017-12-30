package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneMainMenu extends AbstractScene{


    public ButtonYio playButton;


    public SceneMainMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(0, false, true);

        ButtonYio exitButton = buttonFactory.getButton(generateSquare(0.8, 0.87, 0.15 * YioGdxGame.screenRatio), 1, null);
        menuControllerYio.loadButtonOnce(exitButton, "shut_down.png");
        exitButton.setShadow(true);
        exitButton.setAnimation(Animation.UP);
        exitButton.setReaction(Reaction.rbExit);
        exitButton.setTouchOffset(0.05f * GraphicsYio.width);
        exitButton.disableTouchAnimation();

        ButtonYio settingsButton = buttonFactory.getButton(generateSquare(0.05, 0.87, 0.15 * YioGdxGame.screenRatio), 2, null);
        menuControllerYio.loadButtonOnce(settingsButton, "settings_icon.png");
        settingsButton.setShadow(true);
        settingsButton.setAnimation(Animation.UP);
        settingsButton.setReaction(Reaction.rbSettingsMenu);
        settingsButton.setTouchOffset(0.05f * GraphicsYio.width);
        settingsButton.disableTouchAnimation();

        playButton = buttonFactory.getButton(generateSquare(0.3, 0.35, 0.4 * YioGdxGame.screenRatio), 3, null);
        menuControllerYio.loadButtonOnce(playButton, "play_button.png");
        playButton.setReaction(Reaction.rbChooseGameModeMenu);
        playButton.disableTouchAnimation();
        playButton.selectionFactor.setValues(1, 0);

        menuControllerYio.endMenuCreation();
    }
}