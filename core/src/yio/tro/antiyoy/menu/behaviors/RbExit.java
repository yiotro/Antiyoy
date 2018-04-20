package yio.tro.antiyoy.menu.behaviors;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 05.08.14.
 */
public class RbExit extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).close();
        buttonYio.menuControllerYio.yioGdxGame.startedExitProcess = true;
        Gdx.app.exit();
    }
}
