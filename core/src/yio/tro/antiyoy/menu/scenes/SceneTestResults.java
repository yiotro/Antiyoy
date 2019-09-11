package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

import java.util.ArrayList;

public class SceneTestResults extends AbstractScene{


    private ButtonYio label;


    public SceneTestResults(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, true, true);
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, true, true);

        createLabel();
        createBackButton();

        menuControllerYio.endMenuCreation();
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(0.1, 0.15, 0.8, 0.6), 741, " ");
        label.setTouchable(false);
        label.setAnimation(Animation.from_center);
    }


    public void renderResults(ArrayList<String> src) {
        label.cleatText();
        for (String s : src) {
            label.addTextLine(s);
        }
        label.applyNumberOfLines(12);
        menuControllerYio.buttonRenderer.renderButton(label);
    }


    private void createBackButton() {
        menuControllerYio.spawnBackButton(740, new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneDebugTests.create();
            }
        });
    }
}
