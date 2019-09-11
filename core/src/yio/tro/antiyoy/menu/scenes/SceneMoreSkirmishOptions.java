package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneMoreSkirmishOptions extends AbstractScene{


    private ButtonYio label;
    public SliderYio colorOffsetSlider;
    public CheckButtonYio chkSlayRules;
    public CheckButtonYio chkFogOfWar;
    public CheckButtonYio chkDiplomacy;


    public SceneMoreSkirmishOptions(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        colorOffsetSlider = null;
        chkSlayRules = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);

        menuControllerYio.spawnBackButton(231, Reaction.rbSaveMoreSkirmishOptions);

        createLabel();
        createCheckButtons();
        createColorSlider();

        loadValues();

        menuControllerYio.endMenuCreation();

    }


    private void createColorSlider() {
        if (colorOffsetSlider == null) {
            initColorSlider();
        }

        colorOffsetSlider.setNumberOfSegments(getNumberOfSegmentsForColorOffset());
        colorOffsetSlider.appear();
    }


    private int getNumberOfSegmentsForColorOffset() {
        SliderYio colorsSlider = Scenes.sceneSkirmishMenu.colorsSlider;
        return 5 + colorsSlider.getMinNumber();
    }


    private void loadValues() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");

        int colorOffset = prefs.getInteger("color_offset", 0);
        colorOffsetSlider.setValueIndex(colorOffset);

        chkSlayRules.setChecked(prefs.getBoolean("slay_rules", false));
        chkFogOfWar.setChecked(prefs.getBoolean("fog_of_war", false));
        chkDiplomacy.setChecked(prefs.getBoolean("diplomacy", false));
    }


    public void saveValues() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");

        prefs.putInteger("color_offset", colorOffsetSlider.getValueIndex());
        prefs.putBoolean("slay_rules", chkSlayRules.isChecked());
        prefs.putBoolean("fog_of_war", chkFogOfWar.isChecked());
        prefs.putBoolean("diplomacy", chkDiplomacy.isChecked());

        prefs.flush();
    }


    private void initColorSlider() {
        double sWidth = 0.7;
        RectangleYio pos = generateRectangle((1 - sWidth) / 2, 0, sWidth, 0);

        colorOffsetSlider = new SliderYio(menuControllerYio, -1);
        colorOffsetSlider.setValues(0, 0, 6, Animation.up);
        colorOffsetSlider.setPosition(pos);
        colorOffsetSlider.setParentElement(label, 0.37);
        colorOffsetSlider.setTitle("player_color");
        colorOffsetSlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return ColorsManager.getMenuColorNameByIndex(sliderYio.getValueIndex());
            }
        });

        menuControllerYio.addElementToScene(colorOffsetSlider);
        colorOffsetSlider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        colorOffsetSlider.setTitleOffset(0.125f * GraphicsYio.width);
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
        chkSlayRules.alignTop(0.2);
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
        label = buttonFactory.getButton(generateRectangle(0.05, 0.35, 0.9, 0.5), 232, " ");
        label.setTouchable(false);
        label.setAnimation(Animation.fixed_up);
    }


}