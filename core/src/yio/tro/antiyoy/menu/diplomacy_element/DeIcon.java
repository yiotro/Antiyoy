package yio.tro.antiyoy.menu.diplomacy_element;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.PointYio;

public class DeIcon {

    DiplomacyElement diplomacyElement;
    public PointYio position, delta, touchDelta;
    public float radius, touchOffset;
    public FactorYio selectionFactor, appearFactor;
    public DipActionType action;
    boolean visible;


    public DeIcon(DiplomacyElement diplomacyElement) {
        this.diplomacyElement = diplomacyElement;

        position = new PointYio();
        delta = new PointYio();
        touchDelta = new PointYio();
        radius = 0;
        touchOffset = 0;
        selectionFactor = new FactorYio();
        appearFactor = new FactorYio();
        visible = false;
        action = null;
    }


    public boolean isTouched(PointYio touchPoint) {
        if (!isVisible()) return false;

        return diplomacyElement.isTouchInsideRectangle(
                touchPoint.x,
                touchPoint.y,
                position.x - radius + touchDelta.x,
                position.y - radius + touchDelta.y,
                2 * radius,
                2 * radius,
                touchOffset
        );
    }


    public boolean isSelected() {
        return selectionFactor.get() > 0;
    }


    void select() {
        selectionFactor.setValues(1, 0);
        selectionFactor.destroy(1, 2.5);
    }


    void move() {
        selectionFactor.move();
        appearFactor.move();

        position.x = (float) (diplomacyElement.viewPosition.x + delta.x);
        position.y = (float) (diplomacyElement.viewPosition.y + delta.y);
    }


    void appear() {
        appearFactor.setValues(1, 0);
        appearFactor.appear(1, 1);
    }


    public boolean isVisible() {
        return visible;
    }


    public void setDelta(double x, double y) {
        delta.set(x, y);
    }


    public void setAction(DipActionType action) {
        this.action = action;
    }


    public void setTouchOffset(float touchOffset) {
        this.touchOffset = touchOffset;
    }


    public void setRadius(float radius) {
        this.radius = radius;
    }
}
