package yio.tro.antiyoy.menu.fast_construction;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.PointYio;

public class FcpItem {

    FastConstructionPanel fastConstructionPanel;
    public PointYio position, delta, touchDelta, animDelta;
    public float radius, touchOffset;
    public FactorYio selectionFactor;
    public FcpActionType actionType;
    boolean visible;


    public FcpItem(FastConstructionPanel fastConstructionPanel) {
        this.fastConstructionPanel = fastConstructionPanel;

        position = new PointYio();
        delta = new PointYio();
        touchDelta = new PointYio();
        animDelta = new PointYio();
        radius = 0;
        touchOffset = 0;
        selectionFactor = new FactorYio();
        visible = false;
        actionType = null;
    }


    public boolean isTouched(PointYio touchPoint) {
        if (!isVisible()) return false;

        return fastConstructionPanel.isTouchInsideRectangle(
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
        moveSelection();

        position.x = (float) (fastConstructionPanel.viewPosition.x + delta.x);
        position.y = (float) (fastConstructionPanel.viewPosition.y + delta.y);

        if (fastConstructionPanel.appearFactor.get() < 1) {
            position.x += (1 - fastConstructionPanel.appearFactor.get()) * animDelta.x;
            position.y += (1 - fastConstructionPanel.appearFactor.get()) * animDelta.y;
        }
    }


    private void moveSelection() {
        if (fastConstructionPanel.touched) return;
        selectionFactor.move();
    }


    public boolean isVisible() {
        return visible;
    }


    public void setDelta(double x, double y) {
        delta.set(x, y);
    }


    public void setActionType(FcpActionType actionType) {
        this.actionType = actionType;
    }


    public void setTouchOffset(float touchOffset) {
        this.touchOffset = touchOffset;
    }


    public void setRadius(float radius) {
        this.radius = radius;
    }
}
