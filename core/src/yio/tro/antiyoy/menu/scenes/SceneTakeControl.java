package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.color_picking.ColorHolderElement;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneTakeControl extends AbstractScene {


    private ButtonYio label;
    ColorHolderElement colorHolderElement;
    private RectangleYio pos;
    private ButtonYio okButton;
    private Reaction rbBack;


    public SceneTakeControl(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        pos = new RectangleYio(0.1, 0.35, 0.8, 0.2);
        colorHolderElement = null;

        initReactions();
    }


    private void initReactions() {
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
        createColorHolder();
        createOkButton();

        loadValues();
    }


    private void loadValues() {
        GameController gameController = getGameController();
        int color = gameController.getColorByFraction(0);
        colorHolderElement.setValueIndex(color + 1);
    }


    private void createOkButton() {
        double bw = 0.3;
        okButton = buttonFactory.getButton(generateRectangle(pos.x + pos.width - bw, pos.y, bw, 0.055), 824, getString("Apply"));
        okButton.setAnimation(Animation.from_center);
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
            Scenes.sceneNotification.show("Please disable diplomacy");
            return;
        }

        YioGdxGame yioGdxGame = menuControllerYio.yioGdxGame;
        GameController gameController = yioGdxGame.gameController;

        int currentRunnerIndex = colorHolderElement.getValueIndex();
        int desiredColor = currentRunnerIndex - 1;
        if (currentRunnerIndex == 0) {
            desiredColor = YioGdxGame.random.nextInt(GameRules.MAX_FRACTIONS_QUANTITY);
        }

        gameController.colorsManager.takeControlOverColor(desiredColor);

        yioGdxGame.gameView.updateCacheLevelTextures();
        gameController.replayManager.defaultValues();
        gameController.replayManager.getReplay().updateInitialLevelString();

        Reaction.rbResumeGame.perform(okButton);
    }


    private void createColorHolder() {
        initColorHolder();
        colorHolderElement.appear();
    }


    private void initColorHolder() {
        if (colorHolderElement != null) return;
        colorHolderElement = new ColorHolderElement(menuControllerYio);
        colorHolderElement.setTitle(LanguagesManager.getInstance().getString("player_color") + ":");
        colorHolderElement.setAnimation(Animation.from_center);
        colorHolderElement.setPosition(generateRectangle(0.1, 0.45, 0.8, 0.08));
        menuControllerYio.addElementToScene(colorHolderElement);
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(pos.x, pos.y, pos.width, pos.height), 821, " ");
        label.setTouchable(false);
        label.setAnimation(Animation.from_center);
    }
}
