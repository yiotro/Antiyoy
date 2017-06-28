package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.SliderYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

import java.util.ArrayList;

/**
 * Created by ivan on 05.08.14.
 */
public class RbStartSkirmishGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        MenuControllerYio menuControllerYio = buttonYio.menuControllerYio;
        ArrayList<SliderYio> sliders = menuControllerYio.sliders;

        menuControllerYio.getButtonById(80).setTouchable(false);

        LoadingParameters instance = LoadingParameters.getInstance();
        instance.mode = LoadingParameters.MODE_SKIRMISH;
        instance.levelSize = getLevelSizeBySliderPos(sliders.get(0));
        instance.playersNumber = sliders.get(1).getCurrentRunnerIndex();
        instance.colorNumber = sliders.get(2).getCurrentRunnerIndex() + 2;
        instance.difficulty = sliders.get(3).getCurrentRunnerIndex();
        instance.colorOffset = getGameController(buttonYio).getColorOffsetBySlider(sliders.get(4), instance.colorNumber);
        instance.slayRules = menuControllerYio.getCheckButtonById(16).isChecked();
        LoadingManager.getInstance().startGame(instance);

        getYioGdxGame(buttonYio).setAnimToStartButtonSpecial();
        Scenes.sceneSkirmishMenu.saveSkirmishSettings();
    }


    public int getLevelSizeBySliderPos(SliderYio sliderYio) {
        switch (sliderYio.getCurrentRunnerIndex()) {
            default:
            case 0:
                return FieldController.SIZE_SMALL;
            case 1:
                return FieldController.SIZE_MEDIUM;
            case 2:
                return FieldController.SIZE_BIG;
        }
    }


}
