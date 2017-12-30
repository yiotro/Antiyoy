package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.slider.SliderYio;

/**
 * Created by ivan on 05.08.14.
 */
public class RbStartSkirmishGame extends Reaction {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        MenuControllerYio menuControllerYio = buttonYio.menuControllerYio;

        Scenes.sceneSkirmishMenu.saveValues();
        Scenes.sceneMoreSkirmishOptions.create(); // to load more skirmish options

        menuControllerYio.getButtonById(80).setTouchable(false);

        LoadingParameters instance = LoadingParameters.getInstance();

        instance.mode = LoadingParameters.MODE_SKIRMISH;
        instance.levelSize = getLevelSizeBySliderPos(Scenes.sceneSkirmishMenu.mapSizeSlider);
        instance.playersNumber = Scenes.sceneSkirmishMenu.playersSlider.getCurrentRunnerIndex();
        instance.colorNumber = Scenes.sceneSkirmishMenu.colorsSlider.getCurrentRunnerIndex() + 2;
        instance.difficulty = Scenes.sceneSkirmishMenu.difficultySlider.getCurrentRunnerIndex();
        instance.colorOffset = getGameController(buttonYio).getColorOffsetBySliderIndex(
                Scenes.sceneMoreSkirmishOptions.colorOffsetSlider.getCurrentRunnerIndex(), instance.colorNumber);
        instance.slayRules = Scenes.sceneMoreSkirmishOptions.chkSlayRules.isChecked();
        instance.fogOfWar = Scenes.sceneMoreSkirmishOptions.chkFogOfWar.isChecked();
        instance.diplomacy = Scenes.sceneMoreSkirmishOptions.chkDiplomacy.isChecked();

        LoadingManager.getInstance().startGame(instance);

        getYioGdxGame(buttonYio).setAnimToStartButtonSpecial();
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
