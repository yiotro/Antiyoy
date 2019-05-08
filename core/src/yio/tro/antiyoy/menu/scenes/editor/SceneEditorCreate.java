package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.RbStartSkirmishGame;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.SceneSkirmishMenu;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneEditorCreate extends AbstractScene{


    private Reaction rbBack;
    private ButtonYio label;
    SliderYio mapSizeSlider;
    private ButtonYio applyButton;
    private Reaction rbApply;


    public SceneEditorCreate(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        mapSizeSlider = null;
        initReactions();
    }


    private void initReactions() {
        rbBack = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneEditorActions.create();
            }
        };

        rbApply = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onApplyButtonPressed();
            }
        };
    }


    private void onApplyButtonPressed() {
        int levelSize = RbStartSkirmishGame.getLevelSizeBySliderPos(mapSizeSlider);
        LevelEditor levelEditor = menuControllerYio.yioGdxGame.gameController.getLevelEditor();
        levelEditor.createNewLevel(levelSize);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);
        menuControllerYio.spawnBackButton(1300, rbBack);

        createLabel();
        createSlider();
        createApplyButton();

        menuControllerYio.endMenuCreation();
    }


    private void createApplyButton() {
        applyButton = buttonFactory.getButton(generateRectangle(0.5, 0.3, 0.4, 0.05), 1304, LanguagesManager.getInstance().getString("create"));
        applyButton.setShadow(false);
        applyButton.setAnimation(Animation.FIXED_DOWN);
        applyButton.disableTouchAnimation();
        applyButton.setReaction(rbApply);
    }


    private void createSlider() {
        initSlider();
        mapSizeSlider.appear();
    }


    private void initSlider() {
        if (mapSizeSlider != null) return;

        double sWidth = 0.6;
        RectangleYio pos = generateRectangle((1 - sWidth) / 2, 0, sWidth, 0);

        mapSizeSlider = new SliderYio(menuControllerYio, -1);
        mapSizeSlider.setValues(0.5, 1, 4, Animation.NONE);
        mapSizeSlider.setPosition(pos);
        mapSizeSlider.setParentElement(label, 0.1);
        mapSizeSlider.setTitle("map_size");
        mapSizeSlider.setValueIndex(2);
        mapSizeSlider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        mapSizeSlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return SceneSkirmishMenu.getMapSizeStringBySliderIndex(sliderYio.getValueIndex());
            }
        });

        menuControllerYio.addElementToScene(mapSizeSlider);
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.22), 1301, " ");
        label.setTouchable(false);
        label.setAnimation(Animation.FIXED_DOWN);
    }
}
