package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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

        colorOffsetSlider.setNumberOfSegments(GameRules.MAX_COLOR_NUMBER);
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
        colorOffsetSlider.setCurrentRunnerIndex(colorOffset);

        chkSlayRules.setChecked(prefs.getBoolean("slay_rules", false));
    }


    public void saveValues() {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");

        prefs.putInteger("color_offset", colorOffsetSlider.getCurrentRunnerIndex());
        prefs.putBoolean("slay_rules", chkSlayRules.isChecked());

        prefs.flush();
    }


    private void initColorSlider() {
        double sWidth = 0.7;
        RectangleYio pos = generateRectangle((1 - sWidth) / 2, 0, sWidth, 0);

        colorOffsetSlider = new SliderYio(menuControllerYio, -1);
        colorOffsetSlider.setValues(0, 0, 6, Animation.UP);
        colorOffsetSlider.setPosition(pos);
        colorOffsetSlider.setLinkedButton(label, 0.27);
        colorOffsetSlider.setTitle("player_color");
        colorOffsetSlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return SceneSkirmishMenu.getColorStringBySliderIndex(sliderYio.getCurrentRunnerIndex());
            }
        });

        menuControllerYio.addElementToScene(colorOffsetSlider);
        colorOffsetSlider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        colorOffsetSlider.setTitleOffset(0.125f * GraphicsYio.width);
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(0.05, 0.45, 0.9, 0.4), 552, null);
        if (label.notRendered()) {
            label.cleatText();
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(getString("slay_rules"));
            label.addTextLine(" ");

            label.setTextOffset(0.09f * GraphicsYio.width);
            menuControllerYio.getButtonRenderer().renderButton(label);
        }
        label.setTouchable(false);
        label.setAnimation(Animation.UP);
    }


    private void createCheckButtons() {
        double checkButtonSize = 0.05;
        double hSize = GraphicsYio.convertToHeight(checkButtonSize);
        double chkX = 0.88 - checkButtonSize;
        double chkY = 0.53;

        chkSlayRules = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 17);
        chkSlayRules.setTouchPosition(generateRectangle(0.05, chkY - hSize * 1.5, 0.9, hSize * 3));
        chkSlayRules.setAnimation(Animation.UP);
    }
}
