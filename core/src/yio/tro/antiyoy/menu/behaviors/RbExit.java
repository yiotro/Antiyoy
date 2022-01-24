package yio.tro.antiyoy.menu.behaviors;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.PlatformType;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by yiotro on 05.08.14.
 */
public class RbExit extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        if (YioGdxGame.platformType == PlatformType.ios) return;

        getYioGdxGame(buttonYio).close();
        buttonYio.menuControllerYio.yioGdxGame.startedExitProcess = true;
        Gdx.app.exit();
    }
}
