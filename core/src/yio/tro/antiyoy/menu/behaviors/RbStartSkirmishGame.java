package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.gameplay.LevelSize;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingMode;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.slider.SliderYio;

/**
 * Created by yiotro on 05.08.14.
 */
public class RbStartSkirmishGame extends Reaction {

    @Override
    public void perform(ButtonYio buttonYio) {
        MenuControllerYio menuControllerYio = buttonYio.menuControllerYio;

        Scenes.sceneSkirmishMenu.saveValues();
        Scenes.sceneMoreSkirmishOptions.create(); // to load more skirmish options

        menuControllerYio.getButtonById(80).setTouchable(false);

        LoadingParameters instance = LoadingParameters.getInstance();

        instance.mode = LoadingMode.SKIRMISH;
        instance.levelSize = getLevelSizeBySliderPos(Scenes.sceneSkirmishMenu.mapSizeSlider);
        instance.playersNumber = Scenes.sceneSkirmishMenu.playersSlider.getValueIndex();
        instance.colorNumber = Scenes.sceneSkirmishMenu.colorsSlider.getValueIndex() + 2;
        instance.difficulty = Scenes.sceneSkirmishMenu.difficultySlider.getValueIndex();
        instance.colorOffset = getGameController(buttonYio).getColorOffsetBySliderIndex(
                Scenes.sceneMoreSkirmishOptions.colorOffsetSlider.getValueIndex(), instance.colorNumber);
        instance.slayRules = Scenes.sceneMoreSkirmishOptions.chkSlayRules.isChecked();
        instance.fogOfWar = Scenes.sceneMoreSkirmishOptions.chkFogOfWar.isChecked();
        instance.diplomacy = Scenes.sceneMoreSkirmishOptions.chkDiplomacy.isChecked();

        LoadingManager.getInstance().startGame(instance);

        getYioGdxGame(buttonYio).setAnimToStartButtonSpecial();
    }


    public static int getLevelSizeBySliderPos(SliderYio sliderYio) {
        switch (sliderYio.getValueIndex()) {
            default:
            case 0:
                return LevelSize.SMALL;
            case 1:
                return LevelSize.MEDIUM;
            case 2:
                return LevelSize.BIG;
            case 3:
                return LevelSize.HUGE;
        }
    }


}
