package yio.tro.antiyoy.stuff;

import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

/**
 * Created by yiotro on 28.08.2014.
 */
public class PointYio implements ReusableYio{

    public float x, y;


    public PointYio() {
        reset();
    }


    @Override
    public void reset() {
        set(0, 0);
    }


    public PointYio(double x, double y) {
        set(x, y);
    }


    public void set(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }


    public void add(PointYio point) {
        x += point.x;
        y += point.y;
    }


    public void setBy(PointYio p) {
        this.x = p.x;
        this.y = p.y;
    }


    public double distanceTo(PointYio pointYio) {
        return Yio.distance(x, y, pointYio.x, pointYio.y);
    }


    public double fastDistanceTo(PointYio pointYio) {
        return Yio.fastDistance(x, y, pointYio.x, pointYio.y);
    }


    public double angleTo(PointYio pointYio) {
        return Yio.angle(x, y, pointYio.x, pointYio.y);
    }


    public void relocateRadial(double distance, double angle) {
        x += distance * Math.cos(angle);
        y += distance * Math.sin(angle);
    }


    @Override
    public String toString() {
        return "[Point: " + Yio.roundUp(x, 3) + ", " + Yio.roundUp(y, 3) + "]";
    }
}
