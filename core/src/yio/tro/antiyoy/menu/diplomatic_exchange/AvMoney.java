package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.stuff.*;

public class AvMoney extends AbstractExchangeArgumentView{

    public CustomArgViewSlider slider; // for write mode
    public RenderableTextYio title; // for read mode


    public AvMoney() {
        super();
    }


    @Override
    protected void init() {
        slider = new CustomArgViewSlider(exchangeProfitView);
        slider.verticalDelta = 0.02f * GraphicsYio.height;
        slider.setMode(CavsMode.money);
        slider.setValues(new int[]{0, 1, 2, 5, 10, 15, 20, 25, 30, 40, 50, 75, 100, 125, 150, 175, 200, 250, 300, 400, 500, 750, 1000, 2000, 5000, 10000});
        slider.setTitle(LanguagesManager.getInstance().getString("money"));
        slider.setValueIndex(4);

        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
    }


    @Override
    public ExchangeType getExchangeType() {
        return ExchangeType.money;
    }


    public void setTitle(int moneyValue) {
        title.setString(LanguagesManager.getInstance().getString("money") + ": $" + moneyValue);
        title.updateMetrics();
    }


    @Override
    public float getHeight() {
        if (isInReadMode()) {
            return 0.08f * GraphicsYio.height;
        }
        return 0.11f * GraphicsYio.height;
    }


    @Override
    void move() {
        slider.move();
        moveTitle();
    }


    private void moveTitle() {
        title.centerHorizontal(position);
        title.centerVertical(position);
        title.updateBounds();
    }


    @Override
    void onAppear() {

    }


    @Override
    void onDestroy() {

    }


    @Override
    void onTouchDown(PointYio touchPoint) {
        slider.onTouchDown(touchPoint);
    }


    @Override
    void onTouchDrag(PointYio touchPoint) {
        slider.onTouchDrag(touchPoint);
    }


    @Override
    void onTouchUp(PointYio touchPoint) {
        slider.onTouchUp(touchPoint);
    }


    @Override
    void onClick(PointYio touchPoint) {

    }


    @Override
    boolean isSelectionAllowed() {
        return false;
    }


    @Override
    public boolean isExchangeTypeTitleHidden() {
        return true;
    }
}
