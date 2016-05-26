package yio.tro.antiyoy.behaviors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.ButtonLighty;

/**
 * Created by ivan on 06.10.2014.
 */
public class RbReset extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        Preferences preferences = Gdx.app.getPreferences("main");
        preferences.putInteger("progress", 0);
        preferences.flush();
        getYioGdxGame(buttonLighty).setSelectedLevelIndex(0);
        getGameController(buttonLighty).setProgress(0);
        buttonLighty.menuControllerLighty.scrollerYio.setSelectionIndex(0);
//        buttonLighty.menuControllerLighty.updateScrollerCache();
    }
}
