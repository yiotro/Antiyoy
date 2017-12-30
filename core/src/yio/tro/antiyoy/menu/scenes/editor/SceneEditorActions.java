package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public class SceneEditorActions extends AbstractScene{


    public SceneEditorActions(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().setGamePaused(true);

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        menuControllerYio.spawnBackButton(189, EditorReactions.rbEditorSlotMenu);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 181, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;

        ButtonYio playButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.1), 182, getString("play"));
        playButton.setReaction(EditorReactions.rbEditorPlay);
        playButton.setShadow(false);

        ButtonYio exportButton = buttonFactory.getButton(generateRectangle(0.1, 0.4, 0.8, 0.1), 183, getString("export"));
        exportButton.setReaction(EditorReactions.rbEditorExport);
        exportButton.setShadow(false);

        ButtonYio importButton = buttonFactory.getButton(generateRectangle(0.1, 0.5, 0.8, 0.1), 184, getString("import"));
        importButton.setReaction(EditorReactions.rbEditorImportConfirmMenu);
        importButton.setShadow(false);

        ButtonYio editButton = buttonFactory.getButton(generateRectangle(0.1, 0.6, 0.8, 0.1), 185, getString("edit"));
        editButton.setReaction(EditorReactions.rbStartEditorMode);
        editButton.setShadow(false);

        for (int i = 181; i <= 185; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            buttonYio.setAnimation(Animation.FROM_CENTER);
            buttonYio.disableTouchAnimation();
        }

        menuControllerYio.endMenuCreation();
    }
}