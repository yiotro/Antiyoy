package yio.tro.antiyoy.menu.scenes.exception;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.ExceptionViewElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;

import java.util.ArrayList;

public class SceneExceptionReport extends AbstractScene{

    Exception exception;
    private ExceptionViewElement exceptionViewElement;


    public SceneExceptionReport(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, false, true);

        exceptionViewElement = new ExceptionViewElement(menuControllerYio);
        exceptionViewElement.setPosition(generateRectangle(0, 0.06, 1, 0.94));
        exceptionViewElement.setAnimation(Animation.down);
        exceptionViewElement.setException(exception);
        menuControllerYio.addElementToScene(exceptionViewElement);
        exceptionViewElement.appear();

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0, 0, 1, 0.06), 73612322, "Ok");
        okButton.enableRectangularMask();
        okButton.setShadow(false);
        okButton.setReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Reaction.rbExit.perform(buttonYio);
            }
        });

        menuControllerYio.endMenuCreation();
    }


    public void setException(Exception exception) {
        this.exception = exception;
    }
}
