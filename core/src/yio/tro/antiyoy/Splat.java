package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.stuff.CircleYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.Yio;

/**
 * Created by yiotro on 13.08.2014.
 */
public class Splat {

    final TextureRegion textureRegion;
    float x, y, dx, dy, wind, r, speedMultiplier, a;
    PointYio screenCenter;


    public Splat(TextureRegion textureRegion, float x, float y) {
        this.textureRegion = textureRegion;
        this.x = x;
        this.y = y;
        screenCenter = new PointYio(GraphicsYio.width / 2, GraphicsYio.height / 2);
        a = (float) Yio.getRandomAngle();
    }


    void move() {
        x += dx;
        y += dy * speedMultiplier;
        dx += wind;
        if (Math.abs(dx) > 0.001f * Gdx.graphics.getWidth()) wind = -wind;
    }


    void applyToCircle(CircleYio circleYio, double factor, boolean newYearMode) {
        circleYio.center.set(x, y);
        circleYio.setRadius(0.5 * r * factor);
        circleYio.setAngle(a);

        if (newYearMode) {
            circleYio.center.y = GraphicsYio.height - circleYio.center.y;
        }
    }


    void set(float x, float y) {
        this.x = x;
        this.y = y;
    }


    void setSpeed(float sdx, float sdy) {
        dx = sdx;
        dy = sdy;
        wind = -0.01f * dx;
    }


    public void setRadius(float r) {
        this.r = r;
        speedMultiplier = (0.05f * Gdx.graphics.getHeight()) / r;
//        speedMultiplier = (float)Math.sqrt(speedMultiplier);
    }


    boolean isVisible() {
        return y < Gdx.graphics.getHeight() + r;
    }
}
