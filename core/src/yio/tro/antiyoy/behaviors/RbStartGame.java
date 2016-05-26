package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonLighty;

/**
 * Created by ivan on 05.08.14.
 */
public class RbStartGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getYioGdxGame(buttonLighty).startGame(true, true);
        getYioGdxGame(buttonLighty).setAnimToStartButtonSpecial();
    }
}
