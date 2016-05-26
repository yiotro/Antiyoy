package yio.tro.antiyoy.behaviors;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.ButtonLighty;

/**
 * Created by ivan on 05.08.14.
 */
public class RbExit extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getYioGdxGame(buttonLighty).close();
        Gdx.app.exit();
    }
}
