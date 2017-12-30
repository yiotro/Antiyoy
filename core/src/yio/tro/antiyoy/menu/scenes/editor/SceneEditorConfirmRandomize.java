package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public class SceneEditorConfirmRandomize extends AbstractScene {

    public SceneEditorConfirmRandomize(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.hideAllEditorPanels();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.025, 0.15, 0.95, 0.16), 520, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_randomize"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.FIXED_DOWN);

        ButtonYio clearButton = buttonFactory.getButton(generateRectangle(0.5, 0.15, 0.475, 0.06), 521, getString("yes"));
        clearButton.setReaction(EditorReactions.rbEditorRandomize);
        clearButton.setShadow(false);
        clearButton.setAnimation(Animation.FIXED_DOWN);

        ButtonYio cancelButton = buttonFactory.getButton(generateRectangle(0.025, 0.15, 0.475, 0.06), 522, getString("cancel"));
        cancelButton.setReaction(EditorReactions.rbEditorHideConfirmRandomize);
        cancelButton.setShadow(false);
        cancelButton.setAnimation(Animation.FIXED_DOWN);
    }


    public void hide() {
        destroyByIndex(520, 529);
    }
}
