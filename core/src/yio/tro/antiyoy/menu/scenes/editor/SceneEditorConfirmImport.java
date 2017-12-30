package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public class SceneEditorConfirmImport extends AbstractScene{


    public SceneEditorConfirmImport(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        ButtonYio labelButton = buttonFactory.getButton(generateRectangle(0.1, 0.45, 0.8, 0.2), 350, null);
        if (labelButton.notRendered()) {
            labelButton.cleatText();
            menuControllerYio.renderTextAndSomeEmptyLines(labelButton, getString("confirm_import"), 3);
        }
        labelButton.setTouchable(false);
        labelButton.setAnimation(Animation.FROM_CENTER);

        ButtonYio noButton = buttonFactory.getButton(generateRectangle(0.1, 0.45, 0.4, 0.08), 351, getString("no"));
        noButton.setReaction(EditorReactions.rbEditorActionsMenu);
        noButton.setShadow(false);
        noButton.setAnimation(Animation.FROM_CENTER);

        ButtonYio yesButton = buttonFactory.getButton(generateRectangle(0.5, 0.45, 0.4, 0.08), 352, getString("yes"));
        yesButton.setReaction(EditorReactions.rbEditorImport);
        yesButton.setShadow(false);
        yesButton.setAnimation(Animation.FROM_CENTER);

        menuControllerYio.endMenuCreation();
    }
}