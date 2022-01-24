package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.KeyboardManager;
import yio.tro.antiyoy.gameplay.editor.EditorProvinceData;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.TextLabelElement;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneEditorProvincePanel extends AbstractModalScene{


    public static final int NAME_LIMIT = 15;
    private Reaction rbHide;
    private ButtonYio label;
    EditorProvinceData editorProvinceData;
    private TextLabelElement textLabelElement;
    private ButtonYio changeNameButton;
    private SliderYio moneySlider;
    int moneyValues[];


    public SceneEditorProvincePanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        moneySlider = null;
        textLabelElement = null;
        initMoneyValues();
        initReactions();
    }


    private void initMoneyValues() {
        moneyValues = new int[]{0, 5, 10, 25, 50, 75, 100, 125, 150, 200, 250, 500, 1000, 2500, 5000, 10000};
    }


    private void initReactions() {
        rbHide = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
    }


    @Override
    public void create() {
        createInvisibleCloseButton(rbHide);
        invisibleCloseElement.setPosition(generateRectangle(0, 0.3, 1, 0.7));
        createLabel();
        createTitle();
        createChangeNameButton();
        createSlider();
    }


    private void createSlider() {
        initSlider();
        moneySlider.appear();
    }


    private void initSlider() {
        if (moneySlider != null) return;
        moneySlider = new SliderYio(menuControllerYio, -1);
        moneySlider.setValues(0.5, 0, moneyValues.length - 1, Animation.down);
        moneySlider.setPosition(generateRectangle(0.1, 0, 0.8, 0));
        moneySlider.setParentElement(label, 0.1);
        moneySlider.setTitle("money");
        moneySlider.setBehavior(getMoneySliderBehavior());
        moneySlider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        menuControllerYio.addElementToScene(moneySlider);
    }


    private SliderBehavior getMoneySliderBehavior() {
        return new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return "$" + moneyValues[sliderYio.getValueIndex()];
            }


            @Override
            public void onValueChanged(SliderYio sliderYio) {
                editorProvinceData.startingMoney = moneyValues[sliderYio.getValueIndex()];
            }
        };
    }


    private void createChangeNameButton() {
        changeNameButton = buttonFactory.getButton(generateRectangle(0.5, 0.23, 0.45, 0.05), 272, getString("change"));
        changeNameButton.setAnimation(Animation.down);
        changeNameButton.setReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onChangeNameButtonPressed();
            }
        });
    }


    private void onChangeNameButtonPressed() {
        KeyboardManager.getInstance().apply(editorProvinceData.name, new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                System.out.println("input = " + input);
                if (input.length() > NAME_LIMIT) {
                    input = input.substring(0, NAME_LIMIT);
                }
                editorProvinceData.name = input;
                loadValues();
            }
        });
    }


    private void createTitle() {
        initTextLabel();
        textLabelElement.appear();
    }


    private void initTextLabel() {
        if (textLabelElement != null) return;
        textLabelElement = new TextLabelElement(menuControllerYio);
        textLabelElement.setParent(label);
        textLabelElement.alignTitleTop(0.03);
        textLabelElement.alignTitleLeft(0.05);
        menuControllerYio.addElementToScene(textLabelElement);
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(0, 0, 1, 0.3), 271, null);
        menuControllerYio.loadButtonOnce(label, "gray_pixel.png");
        label.setAnimation(Animation.down);
        label.setTouchable(false);
    }


    public void setEditorProvinceData(EditorProvinceData editorProvinceData) {
        this.editorProvinceData = editorProvinceData;
        loadValues();
    }


    private int getSliderIndexByMoneyValue(int moneyValue) {
        for (int i = 0; i < moneyValues.length; i++) {
            if (moneyValue == moneyValues[i]) return i;
        }
        return 2;
    }


    private void loadValues() {
        textLabelElement.setTitle("" + editorProvinceData.name);
        moneySlider.setValueIndex(getSliderIndexByMoneyValue(editorProvinceData.startingMoney));
    }


    @Override
    public void hide() {
        destroyByIndex(270, 279);
        if (textLabelElement != null) {
            textLabelElement.destroy();
        }
    }
}
