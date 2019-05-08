package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class RenderShadow extends MenuRender{

    public static final int SRC_SIZE = 400;
    public static final int BLUR_RADIUS = 13;
    public static final int OFFSET = 50 - BLUR_RADIUS;
    public static final int CORNER_SIZE = 2 * BLUR_RADIUS;

    private Texture srcShadow;
    private TextureRegion textureCorner, textureSide;
    float cornerRadius;
    private RectangleYio pos;
    float incOffset, slideOffset;
    RectangleYio internalFill;
    boolean internalFillEnabled;


    public RenderShadow() {
        incOffset = 0.025f * GraphicsYio.width;
        slideOffset = incOffset / 2;
        pos = new RectangleYio();
        internalFill = new RectangleYio();
        internalFillEnabled = true;
    }


    @Override
    public void loadTextures() {
        srcShadow = new Texture(Gdx.files.internal("menu/shadow.png"));
        srcShadow.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        textureCorner = new TextureRegion(srcShadow, OFFSET, OFFSET, CORNER_SIZE, CORNER_SIZE);
        textureCorner.flip(false, true);
        textureSide = new TextureRegion(srcShadow, SRC_SIZE / 2, OFFSET, 1, CORNER_SIZE);
        textureSide.flip(false, true);
    }


    public void renderShadow(RectangleYio position, double f) {
        renderShadow(position, (float) f, 0.07f * GraphicsYio.width);
    }


    public void renderShadow(RectangleYio position, float f, float cornerRadius) {
        this.cornerRadius = cornerRadius;
        updatePos(position);

        GraphicsYio.setBatchAlpha(batch, f * f);

        renderSides();
        renderCorners();
        renderInternalFill();

        GraphicsYio.setBatchAlpha(batch, 1);
        internalFillEnabled = true;
    }


    public void disableInternalFillForOneDraw() {
        internalFillEnabled = false;
    }


    private void renderInternalFill() {
        if (!internalFillEnabled) return;

        internalFill.x = pos.x + cornerRadius;
        internalFill.y = pos.y + cornerRadius;
        internalFill.width = pos.width - 2 * cornerRadius;
        internalFill.height = pos.height - 2 * cornerRadius;

        GraphicsYio.drawByRectangle(
                batch,
                getBlackPixel(),
                internalFill
        );
    }


    private void updatePos(RectangleYio position) {
        pos.setBy(position);

        pos.x -= incOffset;
        pos.y -= incOffset;
        pos.width += 2 * incOffset;
        pos.height += 2 * incOffset;

        pos.y -= slideOffset;
    }


    private void renderCorners() {
        GraphicsYio.drawRectangleRotatedSimple(
                batch,
                textureCorner,
                pos.x,
                pos.y,
                cornerRadius,
                cornerRadius,
                0
        );

        GraphicsYio.drawRectangleRotatedSimple(
                batch,
                textureCorner,
                pos.x + pos.width,
                pos.y,
                cornerRadius,
                cornerRadius,
                Math.PI / 2
        );

        GraphicsYio.drawRectangleRotatedSimple(
                batch,
                textureCorner,
                pos.x + pos.width,
                pos.y + pos.height,
                cornerRadius,
                cornerRadius,
                Math.PI
        );

        GraphicsYio.drawRectangleRotatedSimple(
                batch,
                textureCorner,
                pos.x,
                pos.y + pos.height,
                cornerRadius,
                cornerRadius,
                1.5 * Math.PI
        );
    }


    private void renderSides() {
        GraphicsYio.drawRectangleRotatedSimple(
                batch,
                textureSide,
                pos.x + cornerRadius,
                pos.y,
                pos.width - 2 * cornerRadius,
                cornerRadius,
                0);

        GraphicsYio.drawRectangleRotatedSimple(
                batch,
                textureSide,
                pos.x + pos.width,
                pos.y + cornerRadius,
                pos.height - 2 * cornerRadius,
                cornerRadius,
                Math.PI / 2
        );

        GraphicsYio.drawRectangleRotatedSimple(
                batch,
                textureSide,
                pos.x + pos.width - cornerRadius,
                pos.y + pos.height,
                pos.width - 2 * cornerRadius,
                cornerRadius,
                Math.PI
        );

        GraphicsYio.drawRectangleRotatedSimple(
                batch,
                textureSide,
                pos.x,
                pos.y + pos.height - cornerRadius,
                pos.height - 2 * cornerRadius,
                cornerRadius,
                1.5 * Math.PI
        );
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {

    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
