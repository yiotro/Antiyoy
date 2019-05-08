package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.SceneSkirmishMenu;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;

import java.util.ArrayList;

public class SceneEditorParams extends AbstractEditorPanel {


    ArrayList<SliderYio> sliders;
    private SliderYio playersSlider;
    private ButtonYio basePanel;
    private double bottom;
    private double pHeight;
    private double yOffset;
    private double bSize;
    private SliderYio difficultySlider;
    private SliderYio colorSlider;


    public SceneEditorParams(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        sliders = null;

        initMetrics();
    }


    private void initMetrics() {
        bottom = 0.07;
        pHeight = 0.6;
        yOffset = 0.02;
        bSize = 0.06;
    }


    @Override
    public void create() {
        createBasePanel();

        createClearLevelButton();
        createRandomizeButton();

        createSliders();

        loadValues();
    }


    private void createRandomizeButton() {
        ButtonYio randomizeButton = buttonFactory.getButton(generateRectangle(0.1, bottom + pHeight - 2 * (yOffset + bSize), 0.8, bSize), 593, getString("randomize"));
        randomizeButton.setReaction(EditorReactions.rbEditorShowConfirmRandomize);
        randomizeButton.setAnimation(Animation.DOWN);
        randomizeButton.enableRectangularMask();
        randomizeButton.setShadow(false);
    }


    private void createClearLevelButton() {
        ButtonYio clearLevelButton = buttonFactory.getButton(generateRectangle(0.1, bottom + pHeight - yOffset - bSize, 0.8, bSize), 591, getString("editor_clear"));
        clearLevelButton.setReaction(EditorReactions.rbEditorConfirmClearLevelMenu);
        clearLevelButton.setAnimation(Animation.DOWN);
        clearLevelButton.enableRectangularMask();
        clearLevelButton.setShadow(false);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0, bottom, 1, pHeight), 590, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.DOWN);
        basePanel.disableTouchAnimation();
        basePanel.enableRectangularMask();
        basePanel.setShadow(true);
    }


    private void createSliders() {
        if (sliders == null) {
            initSliders();
        }

        for (SliderYio slider : sliders) {
            slider.appear();
        }
    }


    private void loadValues() {
        playersSlider.setValueIndex(getGameController().playersNumber);
        difficultySlider.setValueIndex(GameRules.difficulty);
        colorSlider.setValueIndex(GameRules.editorChosenColor);
    }


    private GameController getGameController() {
        return menuControllerYio.yioGdxGame.gameController;
    }


    private void initSliders() {
        sliders = new ArrayList<>();

        double topY = pHeight - 0.27;
        double slDelta = 0.13;
        double curSlY = topY;

        playersSlider = new SliderYio(menuControllerYio, -1);
        playersSlider.setValues(0, 0, GameRules.MAX_COLOR_NUMBER, Animation.DOWN);
        playersSlider.setPosition(generateRectangle(0.1, 0, 0.8, 0));
        playersSlider.setParentElement(basePanel, curSlY);
        playersSlider.setTitle("player_number");
        playersSlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return SceneSkirmishMenu.getHumansString(sliderYio.getValueIndex());
            }


            @Override
            public void onValueChanged(SliderYio sliderYio) {
                onPlayersNumberChanged();
            }
        });
        sliders.add(playersSlider);
        curSlY -= slDelta;

        difficultySlider = new SliderYio(menuControllerYio, -1);
        difficultySlider.setValues(1, 0, 4, Animation.DOWN);
        difficultySlider.setPosition(generateRectangle(0.1, 0, 0.8, 0));
        difficultySlider.setParentElement(basePanel, curSlY);
        difficultySlider.setTitle("difficulty");
        difficultySlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return SceneSkirmishMenu.getDifficultyStringBySliderIndex(sliderYio.getValueIndex());
            }


            @Override
            public void onValueChanged(SliderYio sliderYio) {
                onDifficultyChanged();
            }
        });
        sliders.add(difficultySlider);
        curSlY -= slDelta;

        colorSlider = new SliderYio(menuControllerYio, -1);
        colorSlider.setValues(0, 0, 7, Animation.DOWN);
        colorSlider.setPosition(generateRectangle(0.1, 0, 0.8, 0));
        colorSlider.setParentElement(basePanel, curSlY);
        colorSlider.setTitle("player_color");
        colorSlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return SceneSkirmishMenu.getColorStringBySliderIndex(sliderYio.getValueIndex());
            }


            @Override
            public void onValueChanged(SliderYio sliderYio) {
                onColorOffsetChanged();
            }
        });
        sliders.add(colorSlider);
        curSlY -= slDelta;

        for (SliderYio slider : sliders) {
            menuControllerYio.addElementToScene(slider);
            slider.setTitleOffset(0.11f * GraphicsYio.width);
            slider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        }
    }


    void onPlayersNumberChanged() {
        getGameController().setPlayersNumber(playersSlider.getValueIndex());
    }


    void onDifficultyChanged() {
        GameRules.setDifficulty(difficultySlider.getValueIndex());
    }


    void onColorOffsetChanged() {
        GameRules.setEditorChosenColor(colorSlider.getValueIndex());
    }


    @Override
    public void hide() {
        for (int id = 590; id < 599; id++) {
            menuControllerYio.destroyButton(id);
        }

        for (SliderYio slider : sliders) {
            slider.destroy();
        }
    }


    @Override
    public boolean isCurrentlyOpened() {
        if (sliders == null) return false;

        return basePanel.appearFactor.get() == 1;
    }
}
