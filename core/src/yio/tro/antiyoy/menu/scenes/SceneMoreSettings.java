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
    public CheckButtonYio chkFastConstruction;
    public CheckButtonYio chkResumeButton;
    public CheckButtonYio chkFullScreen;
    private ButtonYio topLabel;
    ArrayList<SliderYio> sliders;
    public SliderYio sensitivitySlider;
    private Reaction rbBack;
    private Reaction rbStatistics;
    private ButtonYio statisticsButton;
    private ButtonYio checksLabel;
    private CheckButtonYio chkNativeKeyboard;
    public int chosenSkinIndex;
    private Reaction rbChooseSkin;


    public SceneMoreSettings(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        sliders = null;
        chkWaterTexture = null;

        initReactions();
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

        initMetrics();
        createTopLabel();
        createSliders();
        createSkinButton();

        createChecksLabel();
        createCheckButtons();

        createGlobalStatisticsButton();

        loadValues();

        menuControllerYio.endMenuCreation();
    }


    private void createSkinButton() {
        ButtonYio skinButton = buttonFactory.getButton(generateRectangle(0.25, 0.58, 0.5, 0.05), 313, getString("skin"));
        skinButton.setReaction(rbChooseSkin);
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
        chkFastConstruction.setChecked(SettingsManager.fastConstructionEnabled);
        chkLeftHanded.setChecked(SettingsManager.leftHandMode);
        chkResumeButton.setChecked(SettingsManager.resumeButtonEnabled);
        chkFullScreen.setChecked(SettingsManager.fullScreenMode);
        chkNativeKeyboard.setChecked(SettingsManager.nativeKeyboard);
    }


    public void applyValues() {
        boolean needRestart = false;

        SettingsManager.getInstance().setSkin(chosenSkinIndex);
        SettingsManager.getInstance().setSensitivity(sensitivitySlider.getValueIndex());

        SettingsManager.longTapToMove = chkLongTapToMove.isChecked();
        SettingsManager.waterTextureEnabled = chkWaterTexture.isChecked();
        SettingsManager.fastConstructionEnabled = chkFastConstruction.isChecked();
        SettingsManager.leftHandMode = chkLeftHanded.isChecked();
        SettingsManager.resumeButtonEnabled = chkResumeButton.isChecked();
        SettingsManager.nativeKeyboard = chkNativeKeyboard.isChecked();

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
        topLabel = buttonFactory.getButton(generateRectangle(0.1, 0.56, 0.8, 0.25), 312, " ");
        topLabel.setTouchable(false);
        topLabel.setAnimation(Animation.up);
    }


    private void createCheckButtons() {
        initChecks();
        chkWaterTexture.appear();
        chkLongTapToMove.appear();
        chkFastConstruction.appear();
        chkLeftHanded.appear();
        chkResumeButton.appear();
        chkFullScreen.appear();
        chkNativeKeyboard.appear();
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

        chkFastConstruction = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkFastConstruction.setParent(checksLabel);
        chkFastConstruction.setHeight(0.055);
        chkFastConstruction.alignUnderPreviousElement();
        chkFastConstruction.setTitle("fast_construction");

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

        chkNativeKeyboard = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkNativeKeyboard.setParent(checksLabel);
        chkNativeKeyboard.setHeight(0.055);
        chkNativeKeyboard.alignUnderPreviousElement();
        chkNativeKeyboard.setTitle("system_keyboard");
    }


    private void createChecksLabel() {
        panelHeight = 0.4;
        double y = 0.53 - panelHeight;
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


    private void initMetrics() {
        panelHeight = 0.14;
    }


    private void createResetButton() {
        ButtonYio resetButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 311, getString("menu_reset"));
        resetButton.setReaction(Reaction.rbConfirmReset);
        resetButton.setAnimation(Animation.up);
    }
}