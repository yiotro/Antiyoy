package yio.tro.antiyoy.menu.speed_panel;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.PointYio;

public class SpItem {

    public static final int ACTION_STOP = 0;
    public static final int ACTION_PLAY_PAUSE = 1;
    public static final int ACTION_FAST_FORWARD = 2;
    public static final int ACTION_SAVE = 3;

    SpeedPanel speedPanel;
    public PointYio position, delta;
    public float radius, touchOffset;
    public FactorYio selectionFactor, appearFactor;
    public int action;


    public SpItem(SpeedPanel speedPanel) {
        this.speedPanel = speedPanel;

        position = new PointYio();
        delta = new PointYio();
        radius = 0;
        touchOffset = 0;
        selectionFactor = new FactorYio();
        appearFactor = new FactorYio();
        defaultAppearFactorState();
        action = -1;
    }


    public void defaultAppearFactorState() {
        appearFactor.setValues(1, 0);
        appearFactor.appear(1, 1);
    }


    public boolean isTouched(PointYio touchPoint) {
        if (!isVisible()) return false;

        return speedPanel.isTouchInsideRectangle(
                touchPoint.x,
                touchPoint.y,
                position.x - radius,
                position.y - radius,
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

        position.x = (float) (speedPanel.viewPosition.x + delta.x);
        position.y = (float) (speedPanel.viewPosition.y + delta.y);
    }


    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    void destroy() {
        appearFactor.destroy(1, 0.5);
    }


    public void setDelta(double x, double y) {
        delta.set(x, y);
    }


    public void setAction(int action) {
        this.action = action;
    }


    public void setTouchOffset(float touchOffset) {
        this.touchOffset = touchOffset;
    }


    public void setRadius(float radius) {
        this.radius = radius;
    }
}
