package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonYio;

/**
 * Created by ivan on 05.08.14.
 */
public class RbStartGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        getYioGdxGame(buttonYio).menuControllerYio.getButtonById(80).setTouchable(false);
        getYioGdxGame(buttonYio).startGame(true, true);
        getYioGdxGame(buttonYio).setAnimToStartButtonSpecial();
        getYioGdxGame(buttonYio).menuControllerYio.saveSkirmishSettings();
    }
}
