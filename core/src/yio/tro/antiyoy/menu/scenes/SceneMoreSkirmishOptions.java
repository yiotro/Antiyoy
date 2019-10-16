package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.color_picking.ColorHolderElement;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneMoreSkirmishOptions extends AbstractScene{


    private ButtonYio label;
    ColorHolderElement colorHolderElement;
    public CheckButtonYio chkSlayRules;
    public CheckButtonYio chkFogOfWar;
    public CheckButtonYio chkDiplomacy;
    SliderYio provincesSlider;


    public SceneMoreSkirmishOptions(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        colorHolderElement = null;
        chkSlayRules = null;
        provincesSlider = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);
        menuControllerYio.spawnBackButton(231, Reaction.rbSaveMoreSkirmishOptions);

        createLabel();
        createCheckButtons();
        createColorHolder();
        createProvincesSlider();

        loadValues();

        menuControllerYio.endMenuCreation();

    }


    private void createProvincesSlider() {
        initProvincesSlider();
        provincesSlider.appear();
    }


    private void initProvincesSlider() {
        provincesSlider = new SliderYio(menuControllerYio, -1);
        provincesSlider.setValues(0, 0, 3, Animation.none);
        provincesSlider.setPosition(generateRectangle(0.15, 0, 0.7, 0));
        provincesSlider.setParentElement(label, 0.09);
        provincesSlider.setTitle("provinces");
        provincesSlider.setVerticalTouchOffset(0.04f * GraphicsYio.height);
        provincesSlider.setBehavior(getProvincesSliderBehavior());
        menuControllerYio.addElementToScene(provincesSlider);
    }


    private SliderBehavior getProvincesSliderBehavior() {
        return new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                if (sliderYio.getValueIndex() == 0) {
                    return LanguagesManager.getInstance().getString("default");
                }
                return "" + sliderYio.getValueIndex();
            }
        };
    }


    private void createColorHolder() {
        initColorHolder();
        colorHolderElement.appear();
    }


    private void initColorHolder() {
        if (colorHolderElement != null) return;
        colorHolderElement = new ColorHolderElement(menuControllerYio);
        colorHolderElement.setTitle(LanguagesManager.getInstance().getString("player_color") + ":");
        colorHolderElement.setAnimation(Animation.up);
        colorHolderElement.setPosition(generateRectangle(0.1, 0.73, 0.8, 0.08));
        menuControllerYio.addElementToScene(colorHolderElement);
    }


    private void loadValues() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");

        int valueIndex = prefs.getInteger("color_offset", 0);
        colorHolderElement.setValueIndex(valueIndex);

        chkSlayRules.setChecked(prefs.getBoolean("slay_rules", false));
        chkFogOfWar.setChecked(prefs.getBoolean("fog_of_war", false));
        chkDiplomacy.setChecked(prefs.getBoolean("diplomacy", false));

        provincesSlider.setValueIndex(prefs.getInteger("provinces", 0));
    }


    public void saveValues() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");

        prefs.putInteger("color_offset", colorHolderElement.getValueIndex());
        prefs.putBoolean("slay_rules", chkSlayRules.isChecked());
        prefs.putBoolean("fog_of_war", chkFogOfWar.isChecked());
        prefs.putBoolean("diplomacy", chkDiplomacy.isChecked());
        prefs.putInteger("provinces", provincesSlider.getValueIndex());

        prefs.flush();
    }


    private void createCheckButtons() {
        initChecks();
        chkSlayRules.appear();
        chkFogOfWar.appear();
        chkDiplomacy.appear();
    }


    private void initChecks() {
        if (chkSlayRules != null) return;

        chkSlayRules = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkSlayRules.setParent(label);
        chkSlayRules.alignTop(0.16);
        chkSlayRules.setTitle(getString("slay_rules"));
        chkSlayRules.centerHorizontal(0.05);

        chkFogOfWar = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkFogOfWar.setParent(label);
        chkFogOfWar.alignUnderPreviousElement();
        chkFogOfWar.setTitle(getString("fog_of_war"));
        chkFogOfWar.centerHorizontal(0.05);

        chkDiplomacy = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkDiplomacy.setParent(label);
        chkDiplomacy.alignUnderPreviousElement();
        chkDiplomacy.setTitle(getString("diplomacy"));
        chkDiplomacy.centerHorizontal(0.05);
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(0.05, 0.25, 0.9, 0.6), 232, " ");
        label.setTouchable(false);
        label.setAnimation(Animation.fixed_up);
    }


}