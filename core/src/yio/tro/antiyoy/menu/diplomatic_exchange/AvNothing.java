package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.stuff.*;

public class AvNothing extends AbstractExchangeArgumentView{

    public RenderableTextYio title;


    public AvNothing() {
        super();
    }


    @Override
    protected void init() {
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        title.setString("[" + LanguagesManager.getInstance().getString("choose") + "]");
        title.updateMetrics();
    }


    @Override
    public ExchangeType getExchangeType() {
        return ExchangeType.nothing;
    }


    @Override
    public float getHeight() {
        if (isInReadMode()) {
            return 0.05f * GraphicsYio.height;
        }
        return 0.15f * GraphicsYio.height;
    }


    @Override
    void move() {
        title.centerVertical(position);
        title.centerHorizontal(position);
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
        return !isInReadMode();
    }
}
