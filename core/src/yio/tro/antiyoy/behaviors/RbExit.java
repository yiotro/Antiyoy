package yio.tro.antiyoy.behaviors;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 05.08.14.
 */
public class RbExit extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).close();
        Gdx.app.exit();
    }
}
