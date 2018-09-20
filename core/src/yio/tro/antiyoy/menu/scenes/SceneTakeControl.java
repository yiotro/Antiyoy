package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneTakeControl extends AbstractScene{


    private ButtonYio label;
    private SliderYio colorSlider;
    private RectangleYio pos;
    private ButtonYio okButton;
    private Reaction rbBack;


    public SceneTakeControl(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        pos = new RectangleYio(0.1, 0.35, 0.8, 0.2);

        rbBack = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneCheatsMenu.create();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);
        menuControllerYio.spawnBackButton(820, rbBack);

        createInternals();

        menuControllerYio.endMenuCreation();
    }


    private void createInternals() {
        createLabel();
        createSlider();
        createOkButton();

        loadValues();
    }


    private void loadValues() {
        GameController gameController = getGameController();
        int colorIndexWithOffset = gameController.getColorIndexWithOffset(0);
        colorSlider.setCurrentRunnerIndex(colorIndexWithOffset + 1);
    }


    private void createOkButton() {
        double bw = 0.3;
        okButton = buttonFactory.getButton(generateRectangle(pos.x + pos.width - bw, pos.y, bw, 0.055), 824, getString("Apply"));
        okButton.setAnimation(Animation.FROM_CENTER);
        okButton.disableTouchAnimation();
        okButton.setShadow(false);
        okButton.setVisualHook(label);
        okButton.setReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onOkButtonPressed();
            }
        });
    }


    private void onOkButtonPressed() {
        if (GameRules.diplomacyEnabled) {
            Scenes.sceneNotification.showNotification("Please disable diplomacy");
            return;
        }

        YioGdxGame yioGdxGame = menuControllerYio.yioGdxGame;
        GameController gameController = yioGdxGame.gameController;

        int currentRunnerIndex = colorSlider.getCurrentRunnerIndex();
        int desiredColor = currentRunnerIndex - 1;
        if (currentRunnerIndex == 0) {
            desiredColor = YioGdxGame.random.nextInt(GameRules.MAX_COLOR_NUMBER);
        }

        int invertedDesiredColor = gameController.getInvertedColor(desiredColor);

        shiftColors(-invertedDesiredColor);
        gameController.colorIndexViewOffset = desiredColor;

        yioGdxGame.gameView.updateCacheLevelTextures();
        gameController.replayManager.defaultValues();
        gameController.replayManager.getReplay().updateInitialLevelString();

        Reaction.rbResumeGame.perform(okButton);
    }


    private void shiftColors(int delta) {
        GameController gameController = getGameController();

        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (!GameRules.slayRules && activeHex.isNeutral()) continue;

            activeHex.colorIndex = getShiftedColor(activeHex.colorIndex, delta);
        }
    }


    private int getShiftedColor(int color, int delta) {
        color += delta;

        if (color >= GameRules.colorNumber) {
            color -= GameRules.colorNumber;
        }

        if (color < 0) {
            color += GameRules.colorNumber;
        }

        return color;
    }



    private GameController getGameController() {
        return menuControllerYio.yioGdxGame.gameController;
    }


    private void createSlider() {
        initColorSlider();

        colorSlider.appear();
    }


    private void initColorSlider() {
        if (colorSlider != null) return;

        double sWidth = 0.7;
        RectangleYio pos = generateRectangle((1 - sWidth) / 2, 0, sWidth, 0);

        colorSlider = new SliderYio(menuControllerYio, -1);
        colorSlider.setValues(0, 0, GameRules.MAX_COLOR_NUMBER, Animation.SOLID);
        colorSlider.setPosition(pos);
        colorSlider.setParentElement(label, 0.1);
        colorSlider.setTitle("player_color");
        colorSlider.setVisualHook(label);
        colorSlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return SceneSkirmishMenu.getColorStringBySliderIndex(sliderYio.getCurrentRunnerIndex());
            }
        });

        menuControllerYio.addElementToScene(colorSlider);
        colorSlider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        colorSlider.setTitleOffset(0.125f * GraphicsYio.width);
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(pos.x, pos.y, pos.width, pos.height), 821, " ");
        label.setTouchable(false);
        label.setAnimation(Animation.FROM_CENTER);
    }
}
