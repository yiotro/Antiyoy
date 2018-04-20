package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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

//        return colorsSlider.getCurrentRunnerIndex() + colorsSlider.getMinNumber();

        return 5 + colorsSlider.getMinNumber();
    }


    private void loadValues() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");

        int colorOffset = prefs.getInteger("color_offset", 0);
        colorOffsetSlider.setCurrentRunnerIndex(colorOffset);

        chkSlayRules.setChecked(prefs.getBoolean("slay_rules", false));
        chkFogOfWar.setChecked(prefs.getBoolean("fog_of_war", false));
        chkDiplomacy.setChecked(prefs.getBoolean("diplomacy", false));
    }


    public void saveValues() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");

        prefs.putInteger("color_offset", colorOffsetSlider.getCurrentRunnerIndex());
        prefs.putBoolean("slay_rules", chkSlayRules.isChecked());
        prefs.putBoolean("fog_of_war", chkFogOfWar.isChecked());
        prefs.putBoolean("diplomacy", chkDiplomacy.isChecked());

        prefs.flush();
    }


    private void initColorSlider() {
        double sWidth = 0.7;
        RectangleYio pos = generateRectangle((1 - sWidth) / 2, 0, sWidth, 0);

        colorOffsetSlider = new SliderYio(menuControllerYio, -1);
        colorOffsetSlider.setValues(0, 0, 6, Animation.UP);
        colorOffsetSlider.setPosition(pos);
        colorOffsetSlider.setLinkedButton(label, 0.37);
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


    private void createCheckButtons() {
        double checkButtonSize = 0.045;
        double hSize = GraphicsYio.convertToHeight(checkButtonSize);
        double chkX = 0.88 - checkButtonSize;
        double chkY = 0.6;
        double delta = hSize + 0.062;

        chkSlayRules = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 16);
        chkSlayRules.setTouchPosition(generateRectangle(0.05, chkY - hSize * 1.5, 0.9, hSize * 3));
        chkSlayRules.setAnimation(Animation.FIXED_UP);
        chkY -= delta;

        chkFogOfWar = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 19);
        chkFogOfWar.setTouchPosition(generateRectangle(0.05, chkY - hSize * 1.5, 0.9, hSize * 3));
        chkFogOfWar.setAnimation(Animation.FIXED_UP);
        chkY -= delta;

        chkDiplomacy = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 20);
        chkDiplomacy.setTouchPosition(generateRectangle(0.05, chkY - hSize * 1.5, 0.9, hSize * 3));
        chkDiplomacy.setAnimation(Animation.FIXED_UP);
        chkY -= delta;
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(0.05, 0.35, 0.9, 0.5), 232, null);
        if (label.notRendered()) {
            label.cleatText();
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(getString("slay_rules"));
            label.addTextLine(" ");
            label.addTextLine(getString("fog_of_war"));
            label.addTextLine(" ");
            label.addTextLine(getString("diplomacy"));
            label.addTextLine(" ");

            label.setTextOffset(0.09f * GraphicsYio.width);
            menuControllerYio.getButtonRenderer().renderButton(label);
        }
        label.setTouchable(false);
        label.setAnimation(Animation.FIXED_UP);
    }


}