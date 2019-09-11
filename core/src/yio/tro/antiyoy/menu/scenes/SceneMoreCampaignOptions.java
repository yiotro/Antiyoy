package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneMoreCampaignOptions extends AbstractScene{


    private CheckButtonYio chkSlayRules;
    private ButtonYio label;
    public SliderYio colorOffsetSlider;


    public SceneMoreCampaignOptions(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        colorOffsetSlider = null;
        chkSlayRules = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);

        menuControllerYio.spawnBackButton(551, Reaction.rbExitToCampaign);

        createInternals();

        menuControllerYio.endMenuCreation();
    }


    public void createInternals() {
        createLabel();
        createCheckButtons();
        createColorSlider();

        loadValues();
    }


    public void prepare() {
        if (colorOffsetSlider != null) return;

        createInternals();
    }


    private void createColorSlider() {
        checkToInitSlider();

        colorOffsetSlider.setNumberOfSegments(GameRules.MAX_FRACTIONS_QUANTITY);
        colorOffsetSlider.appear();
    }


    private void checkToInitSlider() {
        if (colorOffsetSlider == null) {
            initColorSlider();
        }
    }


    private void loadValues() {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");

        int colorOffset = prefs.getInteger("color_offset", 0);
        colorOffsetSlider.setValueIndex(colorOffset);

        chkSlayRules.setChecked(prefs.getBoolean("slay_rules", false));
    }


    public void saveValues() {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");

        prefs.putInteger("color_offset", colorOffsetSlider.getValueIndex());
        prefs.putBoolean("slay_rules", chkSlayRules.isChecked());

        prefs.flush();
    }


    private void initColorSlider() {
        double sWidth = 0.7;
        RectangleYio pos = generateRectangle((1 - sWidth) / 2, 0, sWidth, 0);

        colorOffsetSlider = new SliderYio(menuControllerYio, -1);
        colorOffsetSlider.setValues(0, 0, 6, Animation.up);
        colorOffsetSlider.setPosition(pos);
        colorOffsetSlider.setParentElement(label, 0.27);
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


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(0.05, 0.45, 0.9, 0.4), 552, " ");
        label.setTouchable(false);
        label.setAnimation(Animation.up);
    }


    private void createCheckButtons() {
        initChecks();
        chkSlayRules.appear();
    }


    private void initChecks() {
        if (chkSlayRules != null) return;

        chkSlayRules = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkSlayRules.setParent(label);
        chkSlayRules.alignTop(0.2);
        chkSlayRules.setTitle(getString("slay_rules"));
        chkSlayRules.centerHorizontal(0.05);
    }
}
