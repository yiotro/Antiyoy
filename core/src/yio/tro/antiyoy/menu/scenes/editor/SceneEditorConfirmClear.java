package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public class SceneEditorConfirmClear extends AbstractScene {


    public SceneEditorConfirmClear(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.hideAllEditorPanels();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        createInvisibleButton();
        createBasePanel();
        createYesButton();
        createCancelButton();
    }


    private void createInvisibleButton() {
        ButtonYio invisibleButton = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 363, null);
        invisibleButton.setRenderable(false);
    }


    private void createCancelButton() {
        ButtonYio cancelButton = buttonFactory.getButton(generateRectangle(0.025, 0.15, 0.475, 0.06), 362, getString("cancel"));
        cancelButton.setReaction(EditorReactions.rbEditorHideConfirmClearLevelMenu);
        cancelButton.setShadow(false);
        cancelButton.setAnimation(Animation.fixed_down);
    }


    private void createYesButton() {
        ButtonYio yesButton = buttonFactory.getButton(generateRectangle(0.5, 0.15, 0.475, 0.06), 361, getString("editor_clear"));
        yesButton.setReaction(EditorReactions.rbClearEditorLevel);
        yesButton.setShadow(false);
        yesButton.setAnimation(Animation.fixed_down);
    }


    private void createBasePanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.025, 0.15, 0.95, 0.2), 360, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_clear_level"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.fixed_down);
    }


    public void hide() {
        destroyByIndex(360, 369);
    }
}