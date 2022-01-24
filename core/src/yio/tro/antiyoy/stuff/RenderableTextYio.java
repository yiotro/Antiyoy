package yio.tro.antiyoy.stuff;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class RenderableTextYio implements ReusableYio{


    public BitmapFont font;
    public PointYio position;
    public float width;
    public float height;
    public String string;
    public RectangleYio bounds;
    public PointYio delta;
    public boolean centered; // for external use
    RectangleYio previousBounds; // to detect speed
    float lastTravelDistance;
    private float speedOptiCut;


    public RenderableTextYio() {
        position = new PointYio();
        bounds = new RectangleYio();
        delta = new PointYio();
        previousBounds = new RectangleYio();
        speedOptiCut = 0.02f * GraphicsYio.height;

        reset();
    }


    @Override
    public void reset() {
        font = null;
        width = 0;
        height = 0;
        position.reset();
        string = "";
        delta.reset();
        bounds.reset();
        centered = false;
        previousBounds.reset();
        lastTravelDistance = 0;
    }


    public void setBy(RenderableTextYio src) {
        font = src.font;
        width = src.width;
        height = src.height;
        position.setBy(src.position);
        string = src.string;
        delta.setBy(src.delta);
        bounds.setBy(src.bounds);
        centered = src.centered;
        previousBounds.setBy(src.previousBounds);
        lastTravelDistance = src.lastTravelDistance;
    }


    public void centerHorizontal(RectangleYio parent) {
        position.x = (float) (parent.x + (parent.width - width) / 2);
    }


    public void centerVertical(RectangleYio parent) {
        position.y = (float) (parent.y + (parent.height + height) / 2);
    }


    public void updateWidth() {
        width = GraphicsYio.getTextWidth(font, string);
    }


    public void updateHeight() {
        height = GraphicsYio.getTextHeight(font, string);
    }


    public void updateMetrics() {
        updateWidth();
        updateHeight();
    }


    public void setFont(BitmapFont font) {
        this.font = font;
    }


    public void setString(String string) {
        this.string = string;
    }


    public void updateBounds() {
        previousBounds.setBy(bounds);
        bounds.set(
                position.x,
                position.y - height,
                width,
                height
        );
        updateLastTravelDistance();
    }


    private void updateLastTravelDistance() {
        lastTravelDistance = (float) (Math.abs(previousBounds.x - bounds.x) + Math.abs(previousBounds.y - bounds.y));
    }


    public boolean isMovingFast() {
        return lastTravelDistance > speedOptiCut;
    }


    public float getLastTravelDistance() {
        return lastTravelDistance;
    }


    public void setCentered(boolean centered) {
        this.centered = centered;
    }


    @Override
    public String toString() {
        return "[" +
                string +
                "]";
    }
}
