package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.stuff.*;

public class ExUiButton {

    ExchangeUiElement exchangeUiElement;
    public RectangleYio position;
    public RenderableTextYio title;
    public SelectionEngineYio selectionEngineYio;
    boolean ready;
    public ExUiActionType actionType;


    public ExUiButton(ExchangeUiElement exchangeUiElement) {
        this.exchangeUiElement = exchangeUiElement;
        position = new RectangleYio();
        position.width = 0.4f * GraphicsYio.width;
        position.height = 0.05f * GraphicsYio.height;
        title = new RenderableTextYio();
        title.setString("-");
        title.setFont(Fonts.smallerMenuFont);
        title.updateMetrics();
        selectionEngineYio = new SelectionEngineYio();
        ready = false;
        actionType = null;
    }


    public void setActionType(ExUiActionType actionType) {
        this.actionType = actionType;
    }


    public void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    void move() {
        updatePosition();
        updateTitle();
        selectionEngineYio.move();
    }


    boolean checkToPerformAction() {
        if (!ready) return false;
        ready = false;
        exchangeUiElement.onExUiButtonPressed(this);
        return true;
    }


    private void updateTitle() {
        title.centerHorizontal(position);
        title.centerVertical(position);
        title.updateBounds();
    }


    public void onTouchDown(PointYio touchPoint) {
        if (!isTouchedBy(touchPoint)) return;
        selectionEngineYio.select();
    }


    public void onClick(PointYio touchPoint) {
        if (!isTouchedBy(touchPoint)) return;
        if (ready) return;
        if (!exchangeUiElement.readMode && actionType == ExUiActionType.refuse) return;
        ready = true;
        SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
    }


    public boolean isVisible() {
        if (!exchangeUiElement.readMode && actionType == ExUiActionType.refuse) return false;
        return true;
    }


    public boolean isTouchedBy(PointYio touchPoint) {
        return position.isPointInside(touchPoint);
    }


    private void updatePosition() {
        RectangleYio src = exchangeUiElement.viewPosition;

        switch (actionType) {
            default:
                break;
            case apply:
                position.x = src.x + src.width - position.width;
                position.y = src.y;
                break;
            case refuse:
                position.x = src.x;
                position.y = src.y;
                break;
        }
    }
}
