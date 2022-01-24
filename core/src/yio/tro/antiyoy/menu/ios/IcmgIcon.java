package yio.tro.antiyoy.menu.ios;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.*;

public class IcmgIcon {

    public IosCheckMyGamesElement iosCheckMyGamesElement;
    public PointYio delta;
    public CircleYio viewPosition;
    public IcmgType type;
    public float targetRadius;
    public RenderableTextYio title;
    public SelectionEngineYio selectionEngineYio;
    public RectangleYio touchPosition;
    public String url;


    public IcmgIcon(IosCheckMyGamesElement iosCheckMyGamesElement) {
        this.iosCheckMyGamesElement = iosCheckMyGamesElement;
        delta = new PointYio();
        viewPosition = new CircleYio();
        type = null;
        title = new RenderableTextYio();
        title.setFont(Fonts.microFont);
        selectionEngineYio = new SelectionEngineYio();
        touchPosition = new RectangleYio();
    }


    void move() {
        updateViewPosition();
        moveTitle();
        moveSelection();
        updateTouchPosition();
    }


    private void updateTouchPosition() {
        touchPosition.setBy(viewPosition);
        touchPosition.increase(viewPosition.radius);
    }


    private void moveSelection() {
        if (iosCheckMyGamesElement.touched) return;
        selectionEngineYio.move();
    }


    public boolean isTouchedBy(PointYio touchPoint) {
        return touchPosition.isPointInside(touchPoint);
    }


    private void moveTitle() {
        title.position.x = viewPosition.center.x - title.width / 2;
        title.position.y = viewPosition.center.y - viewPosition.radius - 0.01f * GraphicsYio.width;
        title.updateBounds();
    }


    public void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    public void setUrl(String url) {
        this.url = url;
    }


    private void updateViewPosition() {
        RectangleYio pos = iosCheckMyGamesElement.showRoomPosition;
        viewPosition.center.x = (float) (pos.x + delta.x);
        viewPosition.center.y = (float) (pos.y + delta.y);
        viewPosition.radius = iosCheckMyGamesElement.getFactor().get() * targetRadius;
    }


    public void setType(IcmgType type) {
        this.type = type;
    }
}
