package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class ScenePauseMenu extends AbstractScene{


    public ButtonYio resumeButton;
    private double bHeight;
    private double bottomY;
    private double x;
    private double bWidth;


    public ScenePauseMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        initMetrics();
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(x, bottomY, bWidth, 4 * bHeight), 40, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        double y = bottomY;

        ButtonYio mainMenuButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 42, getString("in_game_menu_main_menu"));
        mainMenuButton.setReactBehavior(ReactBehavior.rbMainMenu);
        mainMenuButton.setShadow(false);
        mainMenuButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        mainMenuButton.disableTouchAnimation();
        y += bHeight;

        ButtonYio chooseLevelButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 43, getString("in_game_menu_save"));
        chooseLevelButton.setReactBehavior(ReactBehavior.rbSaveGame);
        chooseLevelButton.setShadow(false);
        chooseLevelButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        chooseLevelButton.disableTouchAnimation();
        y += bHeight;

        ButtonYio restartButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 44, getString("in_game_menu_restart"));
        restartButton.setReactBehavior(ReactBehavior.rbRestartGame);
        restartButton.setShadow(false);
        restartButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        restartButton.disableTouchAnimation();
        y += bHeight;

        resumeButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 45, getString("in_game_menu_resume"));
        resumeButton.setReactBehavior(ReactBehavior.rbResumeGame);
        resumeButton.setShadow(false);
        resumeButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        resumeButton.disableTouchAnimation();
        y += bHeight;

        menuControllerYio.endMenuCreation();
    }


    private void initMetrics() {
        bHeight = 0.09;
        bottomY = 0.3;
        bWidth = 0.76;
        x = (1 - bWidth) / 2;
    }
}