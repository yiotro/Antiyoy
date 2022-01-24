package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.OneTimeInfo;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneAoButton extends AbstractModalScene {

    ButtonYio buttonYio;


    public SceneAoButton(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        buttonYio = null;
    }


    @Override
    public void create() {
        buttonYio = buttonFactory.getButton(generateRectangle(0.2, 0.025, 0.6, 0.054), 931827314, "Antiyoy Online");
        buttonYio.setAnimation(Animation.down);
        buttonYio.setTouchOffset(0.1f * GraphicsYio.width);
        buttonYio.setReaction(getReaction());
        buttonYio.appear();
    }


    private Reaction getReaction() {
        return new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneAoArticle.create();
                OneTimeInfo.getInstance().antiyoyOnline = true;
                OneTimeInfo.getInstance().save();
            }
        };
    }


    @Override
    public void hide() {
        buttonYio.destroy();
    }
}
