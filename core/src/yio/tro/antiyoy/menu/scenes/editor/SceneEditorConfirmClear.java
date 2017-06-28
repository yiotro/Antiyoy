package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SceneEditorConfirmClear extends AbstractScene {


    public SceneEditorConfirmClear(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        Scenes.sceneEditorOptionsPanel.hide();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.025, 0.15, 0.95, 0.2), 360, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_clear_level"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);

        ButtonYio clearButton = buttonFactory.getButton(generateRectangle(0.5, 0.15, 0.475, 0.06), 361, getString("editor_clear"));
        clearButton.setReactBehavior(EditorReactions.rbClearEditorLevel);
        clearButton.setShadow(false);
        clearButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);

        ButtonYio cancelButton = buttonFactory.getButton(generateRectangle(0.025, 0.15, 0.475, 0.06), 362, getString("cancel"));
        cancelButton.setReactBehavior(EditorReactions.rbEditorHideConfirmClearLevelMenu);
        cancelButton.setShadow(false);
        cancelButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
    }


    public void hide() {
        for (int i = 360; i <= 362; i++) {
            ButtonYio buttonById = menuControllerYio.getButtonById(i);
            buttonById.destroy();
        }
    }
}