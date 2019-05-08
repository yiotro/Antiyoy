package yio.tro.antiyoy.stuff;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class RenderableTextYio implements ReusableYio{

    public static final double OPTIMIZATION_CUT_OUT = 0.92;
    public BitmapFont font;
    public PointYio position;
    public float width;
    public float height;
    public String string;
    public RectangleYio bounds;
    public PointYio delta;
    public boolean centered; // for external use


    public RenderableTextYio() {
        position = new PointYio();
        bounds = new RectangleYio();
        delta = new PointYio();

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
        bounds.set(
                position.x,
                position.y - height,
                width,
                height
        );
    }


    public void setCentered(boolean centered) {
        this.centered = centered;
    }

}
