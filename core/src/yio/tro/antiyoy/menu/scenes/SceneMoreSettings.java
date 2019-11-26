package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.gameplay.skins.SkinType;
import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class SceneMoreSettings extends AbstractScene{


    private double panelHeight;
    public CheckButtonYio chkWaterTexture;
    public CheckButtonYio chkLongTapToMove;
    public CheckButtonYio chkLeftHanded;
    public CheckButtonYio chkResumeButton;
    public CheckButtonYio chkFullScreen;
    private ButtonYio topLabel;
    ArrayList<SliderYio> sliders;
    public SliderYio sensitivitySlider;
    private Reaction rbBack;
    private Reaction rbStatistics;
    private ButtonYio statisticsButton;
    private ButtonYio checksLabel;
    public int chosenSkinIndex;
    private Reaction rbChooseSkin;
    private ButtonYio cityNamesButton;
    private Reaction rbCityNames;
    private double topLabelPos;
    private CheckButtonYio chkAutoTransition;


    public SceneMoreSettings(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        sliders = null;
        chkWaterTexture = null;

        initMetrics();
        initReactions();
    }


    private void initMetrics() {
        topLabelPos = 0.61;
    }


    private void initReactions() {
        rbBack = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onDestroy();
                Scenes.sceneSettings.create();
            }
        };
        rbStatistics = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onDestroy();
                Scenes.sceneGlobalStatistics.create();
            }
        };
        rbChooseSkin = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onDestroy();
                Scenes.sceneChooseSkin.create();
            }
        };
        rbCityNames = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onDestroy();
                Scenes.sceneCityNames.create();
            }
        };
    }


    public void onDestroy() {
        applyValues();
        SettingsManager.getInstance().saveMoreSettings();
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        changeBackground();
        createBackButton();
        createResetButton();

        createTopLabel();
        createSliders();
        createSkinButton();

        createChecksLabel();
        createCheckButtons();

        createGlobalStatisticsButton();
        createCityNamesButton();

        loadValues();

        menuControllerYio.endMenuCreation();
    }


    private void createCityNamesButton() {
        cityNamesButton = buttonFactory.getButton(generateRectangle(0.1, 0.12, 0.8, 0.07), 315, getString("city_names"));
        cityNamesButton.setReaction(rbCityNames);
        cityNamesButton.setAnimation(Animation.down);
    }


    private void createSkinButton() {
        ButtonYio skinButton = buttonFactory.getButton(generateRectangle(0.25, topLabelPos + 0.02, 0.5, 0.05), 313, getString("skin"));
        skinButton.setReaction(rbChooseSkin);
        skinButton.setShadow(false);
        skinButton.setAnimation(Animation.up);
    }


    private void createGlobalStatisticsButton() {
        statisticsButton = buttonFactory.getButton(generateRectangle(0.1, 0.03, 0.8, 0.07), 317, getString("statistics"));
        statisticsButton.setReaction(rbStatistics);
        statisticsButton.setAnimation(Animation.down);
    }


    private void loadValues() {
        chosenSkinIndex = SettingsManager.skinIndex;
        sensitivitySlider.setValueIndex((int) (6f * SettingsManager.sensitivity));

        chkLongTapToMove.setChecked(SettingsManager.longTapToMove);
        chkWaterTexture.setChecked(SettingsManager.waterTextureEnabled);
        chkLeftHanded.setChecked(SettingsManager.leftHandMode);
        chkResumeButton.setChecked(SettingsManager.resumeButtonEnabled);
        chkFullScreen.setChecked(SettingsManager.fullScreenMode);
        chkAutoTransition.setChecked(SettingsManager.automaticTransition);
    }


    public void applyValues() {
        boolean needRestart = false;

        SettingsManager.getInstance().setSkin(chosenSkinIndex);
        SettingsManager.getInstance().setSensitivity(sensitivitySlider.getValueIndex());

        SettingsManager.longTapToMove = chkLongTapToMove.isChecked();
        SettingsManager.waterTextureEnabled = chkWaterTexture.isChecked();
        SettingsManager.leftHandMode = chkLeftHanded.isChecked();
        SettingsManager.resumeButtonEnabled = chkResumeButton.isChecked();
        SettingsManager.automaticTransition = chkAutoTransition.isChecked();

        if (SettingsManager.fullScreenMode != chkFullScreen.isChecked()) {
            SettingsManager.fullScreenMode = chkFullScreen.isChecked();
            needRestart = true;
        }

        menuControllerYio.yioGdxGame.gameView.onMoreSettingsChanged();

        if (needRestart) {
            Scenes.sceneNotification.show("restart_app");
        }
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

        sensitivitySlider = new SliderYio(menuControllerYio, -1);
        sensitivitySlider.setValues(0.5, 0, 9, Animation.up);
        sensitivitySlider.setPosition(pos);
        sensitivitySlider.setParentElement(topLabel, 0.135);
        sensitivitySlider.setTitle("anim_style");
        sensitivitySlider.setBehavior(getSensitivitySliderBehavior());
        sliders.add(sensitivitySlider);

        for (SliderYio slider : sliders) {
            menuControllerYio.addElementToScene(slider);
            slider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        }
    }


    private SliderBehavior getSensitivitySliderBehavior() {
        return new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return "" + (sliderYio.getValueIndex() + 1);
            }
        };
    }


    private void createTopLabel() {
        topLabel = buttonFactory.getButton(generateRectangle(0.1, topLabelPos, 0.8, 0.25), 312, " ");
        topLabel.setTouchable(false);
        topLabel.setAnimation(Animation.up);
    }


    private void createCheckButtons() {
        initChecks();
        chkWaterTexture.appear();
        chkLongTapToMove.appear();
        chkLeftHanded.appear();
        chkResumeButton.appear();
        chkFullScreen.appear();
        chkAutoTransition.appear();
    }


    private void initChecks() {
        if (chkWaterTexture != null) return;

        chkWaterTexture = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkWaterTexture.setParent(checksLabel);
        chkWaterTexture.setHeight(0.055);
        chkWaterTexture.alignTop(0);
        chkWaterTexture.setTitle("water_texture");

        chkLongTapToMove = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkLongTapToMove.setParent(checksLabel);
        chkLongTapToMove.setHeight(0.055);
        chkLongTapToMove.alignUnderPreviousElement();
        chkLongTapToMove.setTitle("hold_to_march");

        chkLeftHanded = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkLeftHanded.setParent(checksLabel);
        chkLeftHanded.setHeight(0.055);
        chkLeftHanded.alignUnderPreviousElement();
        chkLeftHanded.setTitle("left_handed");

        chkResumeButton = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkResumeButton.setParent(checksLabel);
        chkResumeButton.setHeight(0.055);
        chkResumeButton.alignUnderPreviousElement();
        chkResumeButton.setTitle("resume_button");

        chkFullScreen = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkFullScreen.setParent(checksLabel);
        chkFullScreen.setHeight(0.055);
        chkFullScreen.alignUnderPreviousElement();
        chkFullScreen.setTitle("full_screen_mode");

        chkAutoTransition = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkAutoTransition.setParent(checksLabel);
        chkAutoTransition.setHeight(0.055);
        chkAutoTransition.alignUnderPreviousElement();
        chkAutoTransition.setTitle("automatic_transition");
    }


    private void createChecksLabel() {
        panelHeight = 0.34;
        double y = 0.57 - panelHeight;
        checksLabel = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, panelHeight), 316, " ");
        checksLabel.setTouchable(false);
        checksLabel.setAnimation(Animation.down);
    }


    private void changeBackground() {
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);
    }


    private void createBackButton() {
        menuControllerYio.spawnBackButton(310, rbBack);
    }


    private void createResetButton() {
        ButtonYio resetButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 311, getString("menu_reset"));
        resetButton.setReaction(Reaction.rbConfirmReset);
        resetButton.setAnimation(Animation.up);
    }
}