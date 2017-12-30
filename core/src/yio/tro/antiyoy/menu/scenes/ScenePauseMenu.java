package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
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
        basePanel.setAnimation(Animation.FROM_CENTER);

        double y = bottomY;

        ButtonYio mainMenuButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 42, getString("in_game_menu_main_menu"));
        mainMenuButton.setReaction(Reaction.rbMainMenu);
        mainMenuButton.setShadow(false);
        mainMenuButton.setAnimation(Animation.FROM_CENTER);
        mainMenuButton.disableTouchAnimation();
        y += bHeight;

        ButtonYio chooseLevelButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 43, getString("in_game_menu_save"));
        chooseLevelButton.setReaction(Reaction.rbSaveGame);
        chooseLevelButton.setShadow(false);
        chooseLevelButton.setAnimation(Animation.FROM_CENTER);
        chooseLevelButton.disableTouchAnimation();
        y += bHeight;

        ButtonYio restartButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 44, getString("in_game_menu_restart"));
        restartButton.setReaction(Reaction.rbRestartGame);
        restartButton.setShadow(false);
        restartButton.setAnimation(Animation.FROM_CENTER);
        restartButton.disableTouchAnimation();
        y += bHeight;

        resumeButton = buttonFactory.getButton(generateRectangle(x, y, bWidth, bHeight), 45, getString("in_game_menu_resume"));
        resumeButton.setReaction(Reaction.rbResumeGame);
        resumeButton.setShadow(false);
        resumeButton.setAnimation(Animation.FROM_CENTER);
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