package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.Yio;

public class RenderHexLines extends GameRender{

    public RenderHexLines(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {

    }


    void renderGradientShadow(SpriteBatch spriteBatch, Hex hex1, Hex hex2) {
        double a = Yio.angle(hex1.pos.x, hex1.pos.y, hex2.pos.x, hex2.pos.y);
        double cx = 0.5 * (hex1.pos.x + hex2.pos.x);
        double cy = 0.5 * (hex1.pos.y + hex2.pos.y);
        double s = 0.5 * gameController.fieldManager.hexSize;
        cx -= 0.2 * s * Math.cos(a);
        cy -= 0.2 * s * Math.sin(a);
        a += 0.5 * Math.PI;

        GraphicsYio.drawLine(
                spriteBatch,
                gameView.texturesManager.gradientShadow,
                cx + s * Math.cos(a),
                cy + s * Math.sin(a),
                cx - s * Math.cos(a),
                cy - s * Math.sin(a),
                0.01 * GraphicsYio.width
        );
    }


    void renderLineBetweenHexesWithOffset(SpriteBatch spriteBatch, TextureRegion textureRegion, Hex hex1, Hex hex2, double thickness, double offset, int rotation, double factor) {
        double a = Yio.angle(hex1.pos.x, hex1.pos.y, hex2.pos.x, hex2.pos.y);
        double a2 = a + 0.5 * Math.PI;
        double cx = 0.5 * (hex1.pos.x + hex2.pos.x);
        double cy = 0.5 * (hex1.pos.y + hex2.pos.y);
        double s = 0.5 * gameController.fieldManager.hexSize * (0.7 + 0.37 * factor);

        drawSpecialHexedLine(
                spriteBatch, textureRegion, cx + offset * Math.cos(a) + s * Math.cos(a2),
                cy + offset * Math.sin(a) + s * Math.sin(a2),
                cx + offset * Math.cos(a) - s * Math.cos(a2),
                cy + offset * Math.sin(a) - s * Math.sin(a2),
                thickness,
                rotation
        );
    }


    void renderLineBetweenHexes(SpriteBatch spriteBatch, Hex hex1, Hex hex2, double thickness, int rotation) {
        double a = Yio.angle(hex1.pos.x, hex1.pos.y, hex2.pos.x, hex2.pos.y);
        a += 0.5 * Math.PI;
        double cx = 0.5 * (hex1.pos.x + hex2.pos.x);
        double cy = 0.5 * (hex1.pos.y + hex2.pos.y);
        double s = 0.5 * gameController.fieldManager.hexSize;

        drawSpecialHexedLine(
                spriteBatch, gameView.texturesManager.blackBorderTexture, cx + s * Math.cos(a),
                cy + s * Math.sin(a),
                cx - s * Math.cos(a),
                cy - s * Math.sin(a),
                thickness,
                rotation + 3
        );
    }


    private void drawSpecialHexedLine(SpriteBatch spriteBatch, TextureRegion blackPixel, double x1, double y1, double x2, double y2, double thickness, int rotation) {
        spriteBatch.draw(
                blackPixel,
                (float) x1,
                (float) (y1 - thickness * 0.5),
                0f, (float) thickness * 0.5f,
                (float) Yio.distance(x1, y1, x2, y2),
                (float) thickness,
                1f, 1f,
                (float) (180 * (-rotation / 3d))
        );
    }


    @Override
    public void disposeTextures() {

    }
}
