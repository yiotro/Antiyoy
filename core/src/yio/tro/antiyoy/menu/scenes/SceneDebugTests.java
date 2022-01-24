package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.tests.*;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.Yio;

public class SceneDebugTests extends AbstractScene{


    private double curY;
    AbstractTest tests[];
    private SliderYio quantitySlider;
    int quantities[];


    public SceneDebugTests(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        initTests();
        quantitySlider = null;
        initQuantities();
    }


    private void initQuantities() {
        quantities = new int[]{1, 5, 10, 50, 100, 500, 1000, 2000, 5000, 10000, 25000};
    }


    private void initTests() {
        tests = new AbstractTest[]{
                new TestLaunchFireworks(),
                new TestAiComparison(),
                new TestLaunchComparisonMatch(),
                new TestRestoreLevelState(),
                new TestCheckMasterAi(),
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, true, true);

        createQuantitySlider();
        createTestButtons();
        createBackButton();

        menuControllerYio.endMenuCreation();
    }


    private void createQuantitySlider() {
        ButtonYio label = buttonFactory.getButton(generateRectangle(0.1, 0.63, 0.8, 0.15), 731, " ");
        label.setTouchable(false);
        label.setAnimation(Animation.up);

        initQuantitySlider(label);
        quantitySlider.appear();
    }


    private void initQuantitySlider(ButtonYio label) {
        if (quantitySlider != null) return;

        double sWidth = 0.7;
        RectangleYio pos = generateRectangle((1 - sWidth) / 2, 0, sWidth, 0);

        quantitySlider = new SliderYio(menuControllerYio, -1);
        quantitySlider.setValues(0, 0, quantities.length - 1, Animation.none);
        quantitySlider.setPosition(pos);
        quantitySlider.setParentElement(label, 0.05);
        quantitySlider.setTitle("Quantity");
        quantitySlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                int quantity = quantities[sliderYio.getValueIndex()];
                return "" + Yio.getCompactMoneyString(quantity);
            }
        });

        menuControllerYio.addElementToScene(quantitySlider);
        quantitySlider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        quantitySlider.setTitleOffset(0.125f * GraphicsYio.width);
    }


    private void createTestButtons() {
        curY = 0.52;
        int id = 732;

        for (final AbstractTest test : tests) {
            createTestButton(id, test.getName(), new Reaction() {
                @Override
                public void perform(ButtonYio buttonYio) {
                    int quantity = quantities[quantitySlider.getValueIndex()];
                    test.setQuantity(quantity);
                    test.setGameController(menuControllerYio.yioGdxGame.gameController);
                    test.perform();
                }
            });
            id++;
        }
    }


    private ButtonYio createTestButton(int id, String key, Reaction reaction) {
        ButtonYio button = buttonFactory.getButton(generateRectangle(0.1, curY, 0.8, 0.07), id, getString(key));
        button.setReaction(reaction);
        button.setAnimation(Animation.fixed_down);

        curY -= 0.09;

        return button;
    }


    private void createBackButton() {
        menuControllerYio.spawnBackButton(730, new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneSecretScreen.create();
            }
        });
    }
}
