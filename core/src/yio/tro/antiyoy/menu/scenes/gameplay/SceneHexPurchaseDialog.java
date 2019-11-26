package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomatic_dialogs.HexPurchaseDialog;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class SceneHexPurchaseDialog extends AbstractModalScene {

    public HexPurchaseDialog dialog;
    public SliderYio moneySlider;
    public int moneyValues[];


    public SceneHexPurchaseDialog(MenuControllerYio menuControllerYio) {
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
        initDialog();
        dialog.appear();
        initSlider();
        moneySlider.appear();
    }


    public void setData(DiplomaticEntity sender, ArrayList<Hex> hexesToBuy) {
        dialog.setData(sender, hexesToBuy);
        loadValues();
    }


    private void loadValues() {
        DiplomacyManager diplomacyManager = getGameController().fieldManager.diplomacyManager;
        int price = diplomacyManager.calculatePriceForHexes(dialog.getHexesToBuy());
        int indexByPrice = getIndexByPrice(price);
        moneySlider.setValueIndex(indexByPrice);
    }


    private int getIndexByPrice(int price) {
        for (int i = 1; i < moneyValues.length; i++) {
            if (moneyValues[i] < price) continue;
            return i;
        }
        return moneyValues.length - 1;
    }


    private void initSlider() {
        if (moneySlider != null) return;

        double sWidth = 0.8;
        RectangleYio pos = generateRectangle((1 - sWidth) / 2, 0, sWidth, 0);

        moneySlider = new SliderYio(menuControllerYio, -1);
        moneySlider.setValues(0, 0, moneyValues.length - 1, Animation.down);
        moneySlider.setPosition(pos);
        moneySlider.setParentElement(dialog, 0.15);
        moneySlider.setTitle("price");
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


    public int convertSliderIndexIntoMoneyValue(int sliderIndex) {
        return moneyValues[sliderIndex];
    }


    private void initDialog() {
        if (dialog != null) return;

        dialog = new HexPurchaseDialog(menuControllerYio);
        dialog.setPosition(generateRectangle(0, 0.15, 1, GraphicsYio.convertToHeight(0.8)));
        menuControllerYio.addElementToScene(dialog);
    }


    @Override
    public void hide() {
        dialog.destroy();
        moneySlider.destroy();
    }
}
