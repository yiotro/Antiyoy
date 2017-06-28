package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public class SceneEditorOptionsPanel extends AbstractScene{


    public SceneEditorOptionsPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.21), 172, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);

        ButtonYio hideButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.21, 0.07), 171, null);
        menuControllerYio.loadButtonOnce(hideButton, "hide_panel.png");
        hideButton.setReactBehavior(EditorReactions.rbHideOptionsPanel);

        ButtonYio clearLevelButton = buttonFactory.getButton(generateRectangle(0, 0.21, 0.8, 0.07), 173, getString("editor_clear"));
        clearLevelButton.setReactBehavior(EditorReactions.rbEditorConfirmClearLevelMenu);

        ButtonYio changePlayerNumberButton = buttonFactory.getButton(generateRectangle(0, 0.14, 0.8, 0.07), 174, getString("player_number"));
        changePlayerNumberButton.setReactBehavior(EditorReactions.rbEditorChangePlayersNumber);

        ButtonYio changeDifficultyButton = buttonFactory.getButton(generateRectangle(0, 0.07, 0.8, 0.07), 175, getString("difficulty"));
        changeDifficultyButton.setReactBehavior(EditorReactions.rbEditorChangeDifficulty);

        ButtonYio randomButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.07, 0.07), 176, "G");
        randomButton.setReactBehavior(EditorReactions.rbEditorShowConfirmRandomize);

        for (int i = 171; i <= 176; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            buttonYio.factorModel.beginSpawning(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimType(ButtonYio.ANIM_DOWN);
        }
    }


    public void hide() {
        for (int i = 171; i <= 176; i++) {
            menuControllerYio.destroyButton(i);
        }
    }
}