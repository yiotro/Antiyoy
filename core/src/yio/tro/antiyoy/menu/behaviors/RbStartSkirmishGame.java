package yio.tro.antiyoy.menu.behaviors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.LevelSize;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.color_picking.ColorHolderElement;
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

        instance.loadingType = LoadingType.skirmish;
        instance.levelSize = getLevelSizeBySliderPos(Scenes.sceneSkirmishMenu.mapSizeSlider);
        instance.playersNumber = getPlayersNumber();
        instance.fractionsQuantity = getFractionsQuantity();
        instance.difficulty = Scenes.sceneSkirmishMenu.difficultySlider.getValueIndex();
        instance.colorOffset = getColorOffset(buttonYio, instance);
        instance.slayRules = Scenes.sceneMoreSkirmishOptions.chkSlayRules.isChecked();
        instance.fogOfWar = Scenes.sceneMoreSkirmishOptions.chkFogOfWar.isChecked();
        instance.diplomacy = Scenes.sceneMoreSkirmishOptions.chkDiplomacy.isChecked();
        instance.genProvinces = getGenProvinces();
        instance.treesPercentageIndex = getTreesPercentageIndex();

        LoadingManager.getInstance().startGame(instance);

        getYioGdxGame(buttonYio).setAnimToStartButtonSpecial();
    }


    private int getTreesPercentageIndex() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");
        return prefs.getInteger("trees", 2);
    }


    private int getGenProvinces() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");
        return prefs.getInteger("provinces", 0);
    }


    private int getColorOffset(ButtonYio buttonYio, LoadingParameters instance) {
        Preferences prefs = Gdx.app.getPreferences("skirmish");
        int valueIndex = prefs.getInteger("color_offset", 0);
        return ColorHolderElement.getColor(valueIndex, instance.fractionsQuantity);
    }


    private int getPlayersNumber() {
        int valueIndex = Scenes.sceneSkirmishMenu.playersSlider.getValueIndex();
        if (valueIndex > GameRules.NEUTRAL_FRACTION) {
            valueIndex++;
        }
        return valueIndex;
    }


    private int getFractionsQuantity() {
        int fractionsQuantity = Scenes.sceneSkirmishMenu.colorsSlider.getValueIndex() + 2;
        if (fractionsQuantity >= GameRules.NEUTRAL_FRACTION) {
            fractionsQuantity++;
        }
        return fractionsQuantity;
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
