package yio.tro.antiyoy.stuff;

import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

/**
 * Created by ivan on 22.07.14.
 */
public class RectangleYio implements ReusableYio{
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


    public void setBy(RectangleYio src) {
        set(src.x, src.y, src.width, src.height);
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
