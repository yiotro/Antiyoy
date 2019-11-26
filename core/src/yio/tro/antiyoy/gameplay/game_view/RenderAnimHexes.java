package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;

public class RenderAnimHexes extends GameRender {


    private PointYio pos;
    private Color c;


    public RenderAnimHexes(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        c = batchMovable.getColor();
        renderHexesThatChangeColor();
        renderStuffOnAnimHexes();
    }


    private void renderStuffOnAnimHexes() {
        for (Hex hex : gameController.fieldManager.animHexes) {
            pos = hex.getPos();
            if (!isPosInViewFrame(pos, hexViewSize)) continue;

            renderBetweenHexesStuff(hex);
            renderObject(hex);
        }
        batchMovable.setColor(c.r, c.g, c.b, 1);
    }


    private void renderObject(Hex hex) {
        if (!hex.containsObject()) return;

        batchMovable.setColor(c.r, c.g, c.b, 1);
        renderSolidObject(batchMovable, pos, hex);
    }


    private void renderBetweenHexesStuff(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adj = hex.getAdjacentHex(dir);
            if (adj == null || (adj.active && adj.sameFraction(hex))) continue;

            if (isDirectionDown(dir)) {
                renderGradientShadow(batchMovable, hex, adj);
            }

            renderLineBetweenHexes(batchMovable, adj, hex, gameView.borderLineThickness, dir);
        }
    }


    private void renderHexesThatChangeColor() {
        TextureRegion previousTexture, targetTexture;
        GameTexturesManager texturesManager = gameView.texturesManager;

        for (Hex hex : gameController.fieldManager.animHexes) {
            pos = hex.getPos();
            if (!isPosInViewFrame(pos, hexViewSize)) continue;

            if (hex.animFactor.get() < 1) {
                previousTexture = texturesManager.getHexTextureByFraction(hex.previousFraction);
                batchMovable.setColor(c.r, c.g, c.b, 1f - hex.animFactor.get());
                GraphicsYio.drawFromCenter(batchMovable, previousTexture, pos.x, pos.y, hexViewSize);
            }

            targetTexture = texturesManager.getHexTextureByFraction(hex.fraction);
            batchMovable.setColor(c.r, c.g, c.b, hex.animFactor.get());
            GraphicsYio.drawFromCenter(batchMovable, targetTexture, pos.x, pos.y, hexViewSize);
        }

        batchMovable.setColor(c.r, c.g, c.b, 1);
    }


    private boolean isDirectionDown(int i) {
        return i == 2 || i == 3 || i == 4;
    }


    @Override
    public void disposeTextures() {

    }
}
