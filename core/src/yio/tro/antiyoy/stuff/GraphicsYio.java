package yio.tro.antiyoy.stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;

public class GraphicsYio {

    private static GlyphLayout glyphLayout = new GlyphLayout();
    public static float height = (float) Gdx.graphics.getHeight();
    public static float width = (float) Gdx.graphics.getWidth();
    public static float screenRatio = height / width;
    public static final float borderThickness = 0.003f * Gdx.graphics.getHeight();


    public static TextureRegion loadTextureRegion(String name, boolean antialias) {
        Texture texture = new Texture(Gdx.files.internal(name));
        if (antialias) texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return new TextureRegion(texture);
    }


    static public float getTextWidth(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }


    static public float getTextHeight(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.height;
    }


    public static void renderText(SpriteBatch spriteBatch, RenderableTextYio rText) {
        renderText(spriteBatch, rText.font, rText.string, rText.position);
    }


    public static void renderText(SpriteBatch spriteBatch, BitmapFont font, String text, PointYio position) {
        font.draw(spriteBatch, text, position.x, position.y);
    }


    public static void renderTextOptimized(SpriteBatch spriteBatch, TextureRegion pixel, RenderableTextYio renderableTextYio, float alpha) {
        if (renderableTextYio.isMovingFast()) {
            GraphicsYio.setBatchAlpha(spriteBatch, 0.15 * alpha);
            GraphicsYio.drawByRectangle(spriteBatch, pixel, renderableTextYio.bounds);
            GraphicsYio.setBatchAlpha(spriteBatch, 1);
            return;
        }

        GraphicsYio.setFontAlpha(renderableTextYio.font, alpha);
        GraphicsYio.renderText(spriteBatch, renderableTextYio);
        GraphicsYio.setFontAlpha(renderableTextYio.font, 1);
    }


    public static void drawFromCenter(SpriteBatch batch, TextureRegion textureRegion, double cx, double cy, double r) {
        batch.draw(textureRegion, (float) (cx - r), (float) (cy - r), (float) (2d * r), (float) (2d * r));
    }


    public static void drawFromCenterRotated(Batch batch, TextureRegion textureRegion, double cx, double cy, double r, double rotationAngle) {
        batch.draw(textureRegion, (float) (cx - r), (float) (cy - r), (float) r, (float) r, (float) (2d * r), (float) (2d * r), 1, 1, 57.29f * (float) rotationAngle);
    }


    public static void renderBorder(SpriteBatch batch, TextureRegion pixel, RectangleYio viewPos) {
        renderBorder(batch, pixel, viewPos, borderThickness);
    }


    public static void renderBorder(SpriteBatch batch, TextureRegion pixel, RectangleYio viewPos, float thickness) {
        drawLine(batch, pixel, viewPos.x, viewPos.y, viewPos.x, viewPos.y + viewPos.height, thickness);
        drawLine(batch, pixel, viewPos.x, viewPos.y, viewPos.x + viewPos.width, viewPos.y, thickness);
        drawLine(batch, pixel, viewPos.x, viewPos.y + viewPos.height, viewPos.x + viewPos.width, viewPos.y + viewPos.height, thickness);
        drawLine(batch, pixel, viewPos.x + viewPos.width, viewPos.y, viewPos.x + viewPos.width, viewPos.y + viewPos.height, thickness);
    }


    public static void drawByCircle(Batch batch, TextureRegion textureRegion, CircleYio circleYio) {
        drawFromCenterRotated(batch, textureRegion, circleYio.center.x, circleYio.center.y, circleYio.radius, circleYio.angle);
    }


    public static void drawByRectangle(Batch batch, TextureRegion textureRegion, RectangleYio rectangleYio) {
        batch.draw(textureRegion, (float)rectangleYio.x, (float)rectangleYio.y, (float)rectangleYio.width, (float)rectangleYio.height);
    }


    public static void drawLine(SpriteBatch spriteBatch, TextureRegion texture, PointYio p2, PointYio p1, double thickness) {
        drawLine(spriteBatch, texture, p1.x, p1.y, p2.x, p2.y, thickness);
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


    public static void renderText(BitmapFont font, SpriteBatch spriteBatch, String text, PointYio position) {
        font.draw(spriteBatch, text, position.x, position.y);
    }


    public static void drawRectangleRotatedSimple(Batch batch, TextureRegion textureRegion, double x, double y, double width, double height, double angle) {
        batch.draw(textureRegion, (float) x, (float) y, 0, 0, (float) width, (float) height, 1, 1, 57.29f * (float) angle);
    }


    public static void drawLine(SpriteBatch spriteBatch, TextureRegion texture, double x1, double y1, double x2, double y2, double thickness) {
        spriteBatch.draw(texture, (float) x1, (float) (y1 - thickness * 0.5), 0f, (float) thickness * 0.5f,
                (float) Yio.distance(x1, y1, x2, y2), (float) thickness, 1f, 1f, (float) (180 / Math.PI * Yio.angle(x1, y1, x2, y2)));
    }
}
