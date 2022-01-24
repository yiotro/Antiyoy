package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public abstract class AbstractExchangeArgumentView implements ReusableYio{

    ExchangeProfitView exchangeProfitView;
    protected RectangleYio position;


    public AbstractExchangeArgumentView() {
        position = new RectangleYio();
    }


    public void setExchangeProfitView(ExchangeProfitView exchangeProfitView) {
        this.exchangeProfitView = exchangeProfitView;
        init();
    }


    protected abstract void init();


    @Override
    public void reset() {

    }


    public abstract ExchangeType getExchangeType();


    public abstract float getHeight();


    void moveView() {
        updatePosition();
        move();
    }


    private void updatePosition() {
        position.setBy(exchangeProfitView.position);
        if (isExchangeTypeTitleHidden()) return;

        position.height -= 2 * exchangeProfitView.arrowPosition.radius;
    }


    abstract void move();


    abstract void onAppear();


    abstract void onDestroy();


    abstract void onTouchDown(PointYio touchPoint);


    abstract void onTouchDrag(PointYio touchPoint);


    abstract void onTouchUp(PointYio touchPoint);


    abstract void onClick(PointYio touchPoint);


    abstract boolean isSelectionAllowed();


    protected boolean isInReadMode() {
        ExchangeUiElement exchangeUiElement = exchangeProfitView.exchangeUiElement;
        return exchangeUiElement.readMode;
    }


    protected GameController getGameController() {
        ExchangeUiElement exchangeUiElement = exchangeProfitView.exchangeUiElement;
        MenuControllerYio menuControllerYio = exchangeUiElement.menuControllerYio;
        YioGdxGame yioGdxGame = menuControllerYio.yioGdxGame;
        return yioGdxGame.gameController;
    }


    public boolean isApplyAllowed() {
        return true;
    }


    protected DiplomacyManager getDiplomacyManager() {
        return getGameController().fieldManager.diplomacyManager;
    }


    public boolean isExchangeTypeTitleHidden() {
        return false;
    }
}
