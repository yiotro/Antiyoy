package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.stuff.*;

public class AvDotations extends AbstractExchangeArgumentView{

    public CustomArgViewSlider moneySlider; // for write mode
    public CustomArgViewSlider durationSlider; // for write mode
    public RenderableTextYio title; // for read mode


    public AvDotations() {
        super();
    }


    @Override
    protected void init() {
        moneySlider = new CustomArgViewSlider(exchangeProfitView);
        moneySlider.verticalDelta = 0.11f * GraphicsYio.height;
        moneySlider.setMode(CavsMode.money);
        moneySlider.setValues(new int[]{0, 1, 2, 5, 10, 15, 20, 25, 30, 40, 50, 75, 100, 125, 150, 175, 200, 250});
        moneySlider.setTitle(LanguagesManager.getInstance().getString("money"));
        moneySlider.setValueIndex(4);

        durationSlider = new CustomArgViewSlider(exchangeProfitView);
        durationSlider.verticalDelta = 0.02f * GraphicsYio.height;
        durationSlider.setMode(CavsMode.duration);
        durationSlider.setValues(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20});
        durationSlider.setTitle(LanguagesManager.getInstance().getString("duration"));
        durationSlider.setValueIndex(9);

        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
    }


    public void setOptimalValues() {

    }


    @Override
    public ExchangeType getExchangeType() {
        return ExchangeType.dotations;
    }


    public void setTitle(int moneyValue, int duration) {
        title.setString("[$" + moneyValue + ", " + duration + "x]");
        title.updateMetrics();
    }


    @Override
    public float getHeight() {
        if (isInReadMode()) {
            return 0.11f * GraphicsYio.height;
        }
        return 0.22f * GraphicsYio.height;
    }


    @Override
    void move() {
        moneySlider.move();
        durationSlider.move();
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
        moneySlider.onTouchDown(touchPoint);
        durationSlider.onTouchDown(touchPoint);
    }


    @Override
    void onTouchDrag(PointYio touchPoint) {
        moneySlider.onTouchDrag(touchPoint);
        durationSlider.onTouchDrag(touchPoint);
    }


    @Override
    void onTouchUp(PointYio touchPoint) {
        moneySlider.onTouchUp(touchPoint);
        durationSlider.onTouchUp(touchPoint);
    }


    @Override
    void onClick(PointYio touchPoint) {

    }


    @Override
    boolean isSelectionAllowed() {
        return false;
    }
}
