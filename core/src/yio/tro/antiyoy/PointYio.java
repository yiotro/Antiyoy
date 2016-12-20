package yio.tro.antiyoy;

/**
 * Created by ivan on 28.08.2014.
 */
public class PointYio {
    public float x, y;


    public PointYio() {
        set(0, 0);
    }


    public PointYio(double x, double y) {
        set(x, y);
    }


    public void set(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }


    void setBy(PointYio p) {
        this.x = p.x;
        this.y = p.y;
    }


    public double distanceTo(PointYio pointYio) {
        return YioGdxGame.distance(x, y, pointYio.x, pointYio.y);
    }


    public double angleTo(PointYio pointYio) {
        return YioGdxGame.angle(x, y, pointYio.x, pointYio.y);
    }
}
