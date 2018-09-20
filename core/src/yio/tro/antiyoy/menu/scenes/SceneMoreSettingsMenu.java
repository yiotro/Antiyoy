package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class SceneMoreSettingsMenu extends AbstractScene{


    private double panelHeight;
    private double offset;
    private double checkButtonSize;
    private double hSize;
    private double chkX;
    private double chkY;
    private ButtonYio replaysButton;
    private double chkVerticalDelta;
    public CheckButtonYio chkWaterTexture;
    public CheckButtonYio chkLongTapToMove;
    public CheckButtonYio chkReplays;
    public CheckButtonYio chkLeftHanded;
    private double hTouchSize;
    public CheckButtonYio chkFastConstruction;
    private ButtonYio topLabel;
    ArrayList<SliderYio> sliders;
    public SliderYio skinSlider;
    public SliderYio sensitivitySlider;
    private Reaction backReaction;
    public CheckButtonYio chkResumeButton;


    public SceneMoreSettingsMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        sliders = null;

        backReaction = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Settings.getInstance().saveMoreSettings();
                Scenes.sceneSettingsMenu.create();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        changeBackground();
        createBackButton();
        createResetButton();

        initMetrics();
        createTopLabel();
        createSliders();

        createCheckPanel();
        createCheckButtons();

        createChooseLanguageButton();

        loadValues();

        menuControllerYio.endMenuCreation();
    }


    private void loadValues() {
        skinSlider.setCurrentRunnerIndex(Settings.skinIndex);
        sensitivitySlider.setCurrentRunnerIndex((int) (6f * Settings.sensitivity));
    }


    private void createSliders() {
        if (sliders == null) {
            initSliders();
        }

        for (SliderYio slider : sliders) {
            slider.appear();
        }
    }


    private void initSliders() {
        sliders = new ArrayList<>();
        double sWidth = 0.6;
        RectangleYio pos = generateRectangle((1 - sWidth) / 2, 0, sWidth, 0);

        skinSlider = new SliderYio(menuControllerYio, -1);
        skinSlider.setValues(0, 0, 3, Animation.UP);
        skinSlider.setPosition(pos);
        skinSlider.setParentElement(topLabel, 0.05);
        skinSlider.setTitle("skin");
        skinSlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return getSkinStringBySliderIndex(sliderYio.getCurrentRunnerIndex());
            }
        });
        sliders.add(skinSlider);

        sensitivitySlider = new SliderYio(menuControllerYio, -1);
        sensitivitySlider.setValues(0.5, 0, 9, Animation.UP);
        sensitivitySlider.setPosition(pos);
        sensitivitySlider.setParentElement(topLabel, 0.2);
        sensitivitySlider.setTitle("anim_style");
        sensitivitySlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return "" + (sliderYio.getCurrentRunnerIndex() + 1);
            }
        });
        sliders.add(sensitivitySlider);

        for (SliderYio slider : sliders) {
            menuControllerYio.addElementToScene(slider);
            slider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        }
    }


    private String getSkinStringBySliderIndex(int sliderIndex) {
        switch (sliderIndex) {
            default:
            case 0:
                return LanguagesManager.getInstance().getString("original");
            case 1:
                return LanguagesManager.getInstance().getString("points");
            case 2:
                return LanguagesManager.getInstance().getString("grid");
            case 3:
                return LanguagesManager.getInstance().getString("skin_shroomarts");
        }
    }


    private void createTopLabel() {
        topLabel = buttonFactory.getButton(generateRectangle(0.1, 0.56, 0.8, 0.3), 312, " ");
        topLabel.setTouchable(false);
        topLabel.setAnimation(Animation.UP);
    }


    private void createChooseLanguageButton() {
        ButtonYio chooseLanguageButton = buttonFactory.getButton(generateRectangle(0.1, 0.03, 0.8, 0.07), 315, getString("language"));
        chooseLanguageButton.setReaction(Reaction.rbLanguageMenu);
        chooseLanguageButton.setAnimation(Animation.DOWN);
    }


    private void createCheckButtons() {
        initCheckMetrics();

        chkWaterTexture = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 10);
        chkWaterTexture.setTouchPosition(generateRectangle(0.1, chkY - hTouchSize / 2, 0.8, hTouchSize));
        chkWaterTexture.setAnimation(Animation.DOWN);

        chkY -= chkVerticalDelta;
        chkLongTapToMove = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 7);
        chkLongTapToMove.setTouchPosition(generateRectangle(0.1, chkY - hTouchSize / 2, 0.8, hTouchSize));
        chkLongTapToMove.setAnimation(Animation.DOWN);

        chkY -= chkVerticalDelta;
        chkReplays = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 8);
        chkReplays.setTouchPosition(generateRectangle(0.1, chkY - hTouchSize / 2, 0.8, hTouchSize));
        chkReplays.setAnimation(Animation.DOWN);

        chkY -= chkVerticalDelta;
        chkFastConstruction = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 9);
        chkFastConstruction.setTouchPosition(generateRectangle(0.1, chkY - hTouchSize / 2, 0.8, hTouchSize));
        chkFastConstruction.setAnimation(Animation.DOWN);

        chkY -= chkVerticalDelta;
        chkLeftHanded = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 12);
        chkLeftHanded.setTouchPosition(generateRectangle(0.1, chkY - hTouchSize / 2, 0.8, hTouchSize));
        chkLeftHanded.setAnimation(Animation.DOWN);

        chkY -= chkVerticalDelta;
        chkResumeButton = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 13);
        chkResumeButton.setTouchPosition(generateRectangle(0.1, chkY - hTouchSize / 2, 0.8, hTouchSize));
        chkResumeButton.setAnimation(Animation.DOWN);
    }


    private void initCheckMetrics() {
        checkButtonSize = 0.045;
        hSize = GraphicsYio.convertToHeight(checkButtonSize);
        hTouchSize = hSize * 1.5;
        chkX = 0.87 - checkButtonSize;
        chkY = 0.506;
        chkVerticalDelta = 0.04;
    }


    private void createCheckPanel() {
        panelHeight = 0.04 * 7 + 0.01;
        double y = 0.53 - panelHeight;
        ButtonYio chkPanel = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, panelHeight), 316, null);

        if (chkPanel.notRendered()) {
            chkPanel.cleatText();
            chkPanel.addTextLine(LanguagesManager.getInstance().getString("water_texture"));
            chkPanel.addTextLine(LanguagesManager.getInstance().getString("hold_to_march"));
            chkPanel.addTextLine(LanguagesManager.getInstance().getString("replays"));
            chkPanel.addTextLine(LanguagesManager.getInstance().getString("fast_construction"));
            chkPanel.addTextLine(LanguagesManager.getInstance().getString("left_handed"));
            chkPanel.addTextLine(LanguagesManager.getInstance().getString("resume_button"));
            chkPanel.addTextLine(LanguagesManager.getInstance().getString(" "));
            menuControllerYio.getButtonRenderer().renderButton(chkPanel);
        }

        chkPanel.setTouchable(false);
        chkPanel.setAnimation(Animation.DOWN);
    }


    private void changeBackground() {
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);
    }


    private void createBackButton() {
        menuControllerYio.spawnBackButton(310, backReaction);
    }


    private void initMetrics() {
        panelHeight = 0.14;
        offset = 0.03;
    }


    private void createResetButton() {
        ButtonYio resetButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 311, getString("menu_reset"));
        resetButton.setReaction(Reaction.rbConfirmReset);
        resetButton.setAnimation(Animation.UP);
        resetButton.disableTouchAnimation();
    }
}