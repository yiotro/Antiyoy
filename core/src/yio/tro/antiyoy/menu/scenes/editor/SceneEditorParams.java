package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.ai.Difficulty;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.color_picking.ColorHolderElement;
import yio.tro.antiyoy.menu.scenes.SceneSkirmishMenu;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

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
    ColorHolderElement colorHolderElement;
    private Reaction rbMessages;


    public SceneEditorParams(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        sliders = null;
        colorHolderElement = null;

        initMetrics();
        initReactions();
    }


    private void initReactions() {
        rbMessages = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onMessagesButtonClicked();
            }
        };
    }


    private void onMessagesButtonClicked() {
        hide();
        Scenes.sceneEditorMessages.create();
    }


    private void initMetrics() {
        bottom = SceneEditorOverlay.PANEL_HEIGHT;
        pHeight = 0.7;
        yOffset = 0.02;
        bSize = 0.06;
    }


    @Override
    public void create() {
        createBasePanel();

        createClearLevelButton();
        createRandomizeButton();
        createMessagesButton();
        createSliders();
        createColorHolder();

        loadValues();
    }


    private void createColorHolder() {
        initColorHolder();
        colorHolderElement.appear();
    }


    private void initColorHolder() {
        if (colorHolderElement != null) return;
        colorHolderElement = new ColorHolderElement(menuControllerYio);
        colorHolderElement.setTitle(LanguagesManager.getInstance().getString("player_color") + ":");
        colorHolderElement.setAnimation(Animation.down);
        colorHolderElement.setPosition(generateRectangle(0.1, 0.1, 0.8, 0.08));
        colorHolderElement.setChangeReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onChosenColorChanged();
            }
        });
        menuControllerYio.addElementToScene(colorHolderElement);
    }


    private void createMessagesButton() {
        ButtonYio messagesButton = buttonFactory.getButton(generateRectangle(0.1, bottom + pHeight - 3 * (yOffset + bSize), 0.8, bSize), 594, getString("messages"));
        messagesButton.setReaction(rbMessages);
        messagesButton.setAnimation(Animation.down);
        messagesButton.enableRectangularMask();
        messagesButton.setShadow(false);
    }


    private void createRandomizeButton() {
        ButtonYio randomizeButton = buttonFactory.getButton(generateRectangle(0.1, bottom + pHeight - 2 * (yOffset + bSize), 0.8, bSize), 593, getString("randomize"));
        randomizeButton.setReaction(EditorReactions.rbEditorShowConfirmRandomize);
        randomizeButton.setAnimation(Animation.down);
        randomizeButton.enableRectangularMask();
        randomizeButton.setShadow(false);
    }


    private void createClearLevelButton() {
        ButtonYio clearLevelButton = buttonFactory.getButton(generateRectangle(0.1, bottom + pHeight - yOffset - bSize, 0.8, bSize), 591, getString("editor_clear"));
        clearLevelButton.setReaction(EditorReactions.rbEditorConfirmClearLevelMenu);
        clearLevelButton.setAnimation(Animation.down);
        clearLevelButton.enableRectangularMask();
        clearLevelButton.setShadow(false);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0, bottom, 1, pHeight), 590, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.down);
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
        colorHolderElement.setValueIndex(GameRules.editorChosenColor);
    }


    private void initSliders() {
        sliders = new ArrayList<>();

        double topY = pHeight - 0.37;
        double slDelta = 0.13;
        double curSlY = topY;

        playersSlider = new SliderYio(menuControllerYio, -1);
        playersSlider.setValues(0, 0, GameRules.MAX_FRACTIONS_QUANTITY - 1, Animation.down);
        playersSlider.setPosition(generateRectangle(0.1, 0, 0.8, 0));
        playersSlider.setParentElement(basePanel, curSlY);
        playersSlider.setTitle("player_number");
        playersSlider.setBehavior(getPlayersSliderBehavior());
        sliders.add(playersSlider);
        curSlY -= slDelta;

        difficultySlider = new SliderYio(menuControllerYio, -1);
        difficultySlider.setValues(1, 0, 5, Animation.down);
        difficultySlider.setPosition(generateRectangle(0.1, 0, 0.8, 0));
        difficultySlider.setParentElement(basePanel, curSlY);
        difficultySlider.setTitle("difficulty");
        difficultySlider.setBehavior(getDifficultySliderBehavior());
        sliders.add(difficultySlider);
        curSlY -= slDelta;

        for (SliderYio slider : sliders) {
            menuControllerYio.addElementToScene(slider);
            slider.setTitleOffset(0.11f * GraphicsYio.width);
            slider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        }
    }


    private SliderBehavior getDifficultySliderBehavior() {
        return new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return Difficulty.convertToString(sliderYio.getValueIndex());
            }


            @Override
            public void onValueChanged(SliderYio sliderYio) {
                onDifficultyChanged();
            }
        };
    }


    private SliderBehavior getPlayersSliderBehavior() {
        return new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return SceneSkirmishMenu.getHumansString(sliderYio.getValueIndex());
            }


            @Override
            public void onValueChanged(SliderYio sliderYio) {
                onPlayersNumberChanged();
            }
        };
    }


    void onPlayersNumberChanged() {
        getGameController().setPlayersNumber(playersSlider.getValueIndex());
    }


    void onDifficultyChanged() {
        GameRules.setDifficulty(difficultySlider.getValueIndex());
    }


    void onChosenColorChanged() {
        GameRules.setEditorChosenColor(colorHolderElement.getValueIndex());
    }


    @Override
    public void hide() {
        for (int id = 590; id < 599; id++) {
            menuControllerYio.destroyButton(id);
        }

        for (SliderYio slider : sliders) {
            slider.destroy();
        }

        if (colorHolderElement != null) {
            colorHolderElement.destroy();
        }
    }


    @Override
    public boolean isCurrentlyOpened() {
        if (sliders == null) return false;

        return basePanel.appearFactor.get() == 1;
    }
}
