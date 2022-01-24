package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;

public class AvBlank extends AbstractExchangeArgumentView{


    public AvBlank() {
        super();
    }


    @Override
    protected void init() {

    }


    @Override
    public ExchangeType getExchangeType() {
        return null;
    }


    @Override
    public float getHeight() {
        return 0.05f * GraphicsYio.height;
    }


    @Override
    void move() {

    }


    @Override
    void onAppear() {

    }


    @Override
    void onDestroy() {

    }


    @Override
    void onTouchDown(PointYio touchPoint) {

    }


    @Override
    void onTouchDrag(PointYio touchPoint) {

    }


    @Override
    void onTouchUp(PointYio touchPoint) {

    }


    @Override
    void onClick(PointYio touchPoint) {

    }


    @Override
    boolean isSelectionAllowed() {
        return false;
    }
}
