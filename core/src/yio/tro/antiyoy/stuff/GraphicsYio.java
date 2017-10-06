package yio.tro.antiyoy.stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.Yio;

public class GraphicsYio {

    private static GlyphLayout glyphLayout = new GlyphLayout();
    public static float height = (float) Gdx.graphics.getHeight();
    public static float width = (float) Gdx.graphics.getWidth();
    public static float screenRatio = height / width;
    public static final float borderThickness = 0.003f * Gdx.graphics.getHeight();


    public static TextureRegion loadTextureRegion(String name, boolean antialias) {
        Texture texture = new Texture(Gdx.files.internal(name));
        if (antialias) texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        return region;
    }


    static public float getTextWidth(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }


    static public float getTextHeight(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.height;
    }


    public static void drawFromCenter(SpriteBatch batch, TextureRegion textureRegion, double cx, double cy, double r) {
        batch.draw(textureRegion, (float) (cx - r), (float) (cy - r), (float) (2d * r), (float) (2d * r));
    }


    public static void drawFromCenterRotated(Batch batch, TextureRegion textureRegion, double cx, double cy, double r, double rotationAngle) {
        batch.draw(textureRegion, (float) (cx - r), (float) (cy - r), (float) r, (float) r, (float) (2d * r), (float) (2d * r), 1, 1, 57.29f * (float) rotationAngle);
    }


    public static void renderBorder(RectangleYio viewPos, SpriteBatch batch, TextureRegion pixel) {
        renderBorder(viewPos, borderThickness, batch, pixel);
    }


    public static void renderBorder(RectangleYio viewPos, float thickness, SpriteBatch batch, TextureRegion pixel) {
        drawLine(viewPos.x, viewPos.y, viewPos.x, viewPos.y + viewPos.height, thickness, batch, pixel);
        drawLine(viewPos.x, viewPos.y, viewPos.x + viewPos.width, viewPos.y, thickness, batch, pixel);
        drawLine(viewPos.x, viewPos.y + viewPos.height, viewPos.x + viewPos.width, viewPos.y + viewPos.height, thickness, batch, pixel);
        drawLine(viewPos.x + viewPos.width, viewPos.y, viewPos.x + viewPos.width, viewPos.y + viewPos.height, thickness, batch, pixel);
    }


    public static void drawByRectangle(Batch batch, TextureRegion textureRegion, RectangleYio rectangleYio) {
        batch.draw(textureRegion, (float)rectangleYio.x, (float)rectangleYio.y, (float)rectangleYio.width, (float)rectangleYio.height);
    }


    public static void drawLine(PointYio p1, PointYio p2, double thickness, SpriteBatch spriteBatch, TextureRegion texture) {
        drawLine(p1.x, p1.y, p2.x, p2.y, thickness, spriteBatch, texture);
    }


    public static double convertToHeight(double width) {
        return width / screenRatio;
    }


    public static double convertToWidth(double height) {
        return height * screenRatio;
    }


    public static void setBatchAlpha(SpriteBatch spriteBatch, double alpha) {
        Color color = spriteBatch.getColor();
        spriteBatch.setColor(color.r, color.g, color.b, (float) alpha);
    }


    public static void setFontAlpha(BitmapFont font, double alpha) {
        Color color = font.getColor();
        font.setColor(color.r, color.g, color.b, (float) alpha);
    }


    public static void drawLine(double x1, double y1, double x2, double y2, double thickness, SpriteBatch spriteBatch, TextureRegion texture) {
        spriteBatch.draw(texture, (float) x1, (float) (y1 - thickness * 0.5), 0f, (float) thickness * 0.5f,
                (float) Yio.distance(x1, y1, x2, y2), (float) thickness, 1f, 1f, (float) (180 / Math.PI * Yio.angle(x1, y1, x2, y2)));
    }
}
