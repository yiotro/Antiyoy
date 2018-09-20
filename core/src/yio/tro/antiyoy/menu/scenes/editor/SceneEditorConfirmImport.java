package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SceneEditorConfirmImport extends AbstractScene{


    private Reaction noReaction;


    public SceneEditorConfirmImport(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        noReaction = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneEditorActions.create();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        ButtonYio labelButton = buttonFactory.getButton(generateRectangle(0.1, 0.45, 0.8, 0.15), 350, null);
        if (labelButton.notRendered()) {
            labelButton.cleatText();
            menuControllerYio.renderTextAndSomeEmptyLines(labelButton, getString("confirm_import"), 2);
        }
        labelButton.setTouchable(false);
        labelButton.setAnimation(Animation.FROM_CENTER);

        ButtonYio noButton = buttonFactory.getButton(generateRectangle(0.1, 0.45, 0.4, 0.06), 351, getString("no"));
        noButton.setReaction(noReaction);
        noButton.setShadow(false);
        noButton.setAnimation(Animation.FROM_CENTER);

        ButtonYio yesButton = buttonFactory.getButton(generateRectangle(0.5, 0.45, 0.4, 0.06), 352, getString("yes"));
        yesButton.setReaction(EditorReactions.rbEditorImport);
        yesButton.setShadow(false);
        yesButton.setAnimation(Animation.FROM_CENTER);

        menuControllerYio.endMenuCreation();
    }
}