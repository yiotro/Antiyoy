package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.LevelSelector;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.Masking;
import yio.tro.antiyoy.stuff.RectangleYio;

public class RenderLevelSelector extends MenuRender {

    TextureRegion blackPixel;
    private float selX;
    private float selY;
    private LevelSelector selector;
    private float f;


    @Override
    public void loadTextures() {
        blackPixel = GraphicsYio.loadTextureRegion("pixels/black_pixel.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        selector = (LevelSelector) element;
        f = selector.getFactor().get();

        if (f == 0) return;
        c = batch.getColor();

        renderShadows();

        batch.end();
        Masking.begin();
        drawShapeRendererStuff();

        batch.begin();
        Masking.continueAfterBatchBegin();

        batch.setColor(c.r, c.g, c.b, f);

        for (int i = 0; i < selector.textures.length; i++) {
            RectangleYio pos = selector.positions[i];
            GraphicsYio.drawByRectangle(batch, selector.textures[i], pos);
            checkToRenderSelection(selector, pos, i);
        }

        Masking.end(batch);
        batch.setColor(c.r, c.g, c.b, 1);
    }


    private void drawShapeRendererStuff() {
        menuViewYio.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (f > 0.98) {
            for (RectangleYio pos : selector.positions) {
                menuViewYio.drawRoundRect(pos);
            }
        } else {
            menuViewYio.drawCircle(selector.getCircleX(), selector.getCircleY(), selector.getCircleR());
        }
        menuViewYio.shapeRenderer.end();
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }


    public void renderLevelSelector(LevelSelector selector) {

    }


    private void renderShadows() {
        if (f <= 0.05) return;

        if (f < 1) {
            renderShadowsInAnimationPhase();
            return;
        }

        for (RectangleYio pos : selector.positions) {
            MenuRender.renderShadow.renderShadow(pos, f);
        }
    }


    private void renderShadowsInAnimationPhase() {
        batch.end();
        Masking.begin();

        menuViewYio.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        menuViewYio.drawCircle(selector.getCircleX(), selector.getCircleY(), selector.getCircleR() + 0.03f * GraphicsYio.width);
        menuViewYio.shapeRenderer.end();

        batch.begin();
        Masking.continueAfterBatchBegin();

        for (RectangleYio pos : selector.positions) {
            MenuRender.renderShadow.renderShadow(pos, Math.max(0.25 * f, Math.pow(f, 10)));
        }

        Masking.end(batch);
    }


    private void checkToRenderSelection(LevelSelector selector, RectangleYio pos, int i) {
        if (selector.selectionFactor.get() <= 0) return;
        if (i != selector.selectedPanelIndex) return;

        GraphicsYio.setBatchAlpha(batch, 0.5f * selector.selectionFactor.get());

        selX = selector.selIndexX * 2 * selector.iconRadius;
        selY = selector.selIndexY * 2 * selector.iconRadius;
        selX *= pos.width / selector.defPos.width;

        batch.draw(
                getBlackPixel(),
                (float) pos.x + selector.horOffset + selX,
                (float) pos.y + selector.verOffset + selY,
                2 * selector.iconRadius, 2 * selector.iconRadius
        );

        GraphicsYio.setBatchAlpha(batch, f);
    }
}
