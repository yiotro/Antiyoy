package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomatic_dialogs.TransferMoneyDialog;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneTransferMoneyDialog extends AbstractModalScene {

    public TransferMoneyDialog dialog;
    public SliderYio moneySlider;
    private int moneyValues[];


    public SceneTransferMoneyDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        dialog = null;
        moneySlider = null;

        initMoneyValues();
    }


    private void initMoneyValues() {
        moneyValues = new int[]{0, 1, 2, 5, 10, 15, 20, 25, 30, 40, 50, 75, 100, 125, 150, 175, 200, 250, 300, 400, 500, 750, 1000};
    }


    @Override
    public void create() {
        if (dialog == null) {
            initDialog();
            initSlider();
        }

        dialog.appear();
        moneySlider.appear();

        moneySlider.setValueIndex(4);
    }


    public int convertSliderIndexIntoMoneyValue(int sliderIndex) {
        return moneyValues[sliderIndex];
    }


    private void initSlider() {
        if (moneySlider != null) return;

        double sWidth = 0.8;
        RectangleYio pos = generateRectangle((1 - sWidth) / 2, 0, sWidth, 0);

        moneySlider = new SliderYio(menuControllerYio, -1);
        moneySlider.setValues(0, 0, moneyValues.length - 1, Animation.down);
        moneySlider.setPosition(pos);
        moneySlider.setParentElement(dialog, 0.15);
        moneySlider.setTitle("money");
        moneySlider.setInternalSegmentsHidden(true);
        menuControllerYio.addElementToScene(moneySlider);
        moneySlider.setVerticalTouchOffset(0.05f * GraphicsYio.height);
        moneySlider.setTitleOffset(0.125f * GraphicsYio.width);

        moneySlider.setBehavior(new SliderBehavior() {
            @Override
            public String getValueString(SliderYio sliderYio) {
                return "$" + convertSliderIndexIntoMoneyValue(sliderYio.getValueIndex());
            }
        });
    }


    private void initDialog() {
        dialog = new TransferMoneyDialog(menuControllerYio);

        dialog.setPosition(generateRectangle(0, 0.15, 1, GraphicsYio.convertToHeight(0.72)));

        menuControllerYio.addElementToScene(dialog);
    }


    @Override
    public void hide() {
        dialog.destroy();
        moneySlider.destroy();
    }
}
