package yio.tro.antiyoy;

/**
 * Created by ivan on 22.07.14.
 */
public class RectangleYio {
    public double x;
    public double y;
    public double width;
    public double height;


    public RectangleYio() {
        this(0, 0, 0, 0);
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
}
