package yio.tro.antiyoy.stuff;

import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

/**
 * Created by yiotro on 22.07.14.
 */
public class RectangleYio implements ReusableYio {

    public double x;
    public double y;
    public double width;
    public double height;


    public RectangleYio() {
        this(0, 0, 0, 0);
    }


    @Override
    public void reset() {
        set(0, 0, 0, 0);
    }


    public RectangleYio(double x, double y, double width, double height) {
        set(x, y, width, height);
    }


    public RectangleYio(RectangleYio src) {
        setBy(src);
    }


    public void set(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public void setBy(CircleYio circleYio) {
        x = circleYio.center.x - circleYio.radius;
        y = circleYio.center.y - circleYio.radius;
        width = 2 * circleYio.radius;
        height = 2 * circleYio.radius;
    }


    public void setBy(RectangleYio src) {
        set(src.x, src.y, src.width, src.height);
    }


    public void increase(double delta) {
        x -= delta;
        y -= delta;
        width += 2 * delta;
        height += 2 * delta;
    }


    public boolean isInCollisionWith(RectangleYio rect) {
        if (rect.x > x + width) return false;
        if (rect.x + rect.width < x) return false;
        if (rect.y > y + height) return false;
        if (rect.y + rect.height < y) return false;
        return true;
    }


    public boolean isPointInside(PointYio pointYio) {
        if (pointYio.x < x) return false;
        if (pointYio.y < y) return false;
        if (pointYio.x > x + width) return false;
        if (pointYio.y > y + height) return false;
        return true;
    }


    public boolean isPointInside(PointYio pointYio, float offset) {
        if (pointYio.x < x - offset) return false;
        if (pointYio.y < y - offset) return false;
        if (pointYio.x > x + width + offset) return false;
        if (pointYio.y > y + height + offset) return false;
        return true;
    }


    @Override
    public String toString() {
        return "(" + Yio.roundUp(x, 3) + ", " + Yio.roundUp(y, 3) + ", " + Yio.roundUp(width, 3) + ", " + Yio.roundUp(height, 3) + ")";
    }
}
