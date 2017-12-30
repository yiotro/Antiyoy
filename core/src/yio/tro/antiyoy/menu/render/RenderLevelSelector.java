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

        if (selector.getFactor().get() == 0) return;
        c = batch.getColor();

        renderShadows();

        batch.end();
        Masking.begin();
        drawShapeRendererStuff();

        batch.begin();
        Masking.continueAfterBatchBegin();

        batch.setColor(c.r, c.g, c.b, selector.getFactor().get());

        for (int i = 0; i < selector.textures.length; i++) {
            RectangleYio pos = selector.positions[i];
            batch.draw(selector.textures[i], (float) pos.x, (float) pos.y, (float) pos.width, (float) pos.height);
            checkToRenderSelection(selector, pos, i);
        }


        Masking.end(batch);
        batch.setColor(c.r, c.g, c.b, 1);
    }


    private void drawShapeRendererStuff() {
        menuViewYio.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (selector.getFactor().get() == 1) {
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
        if (selector.getFactor().get() <= 0.95) return;

        for (RectangleYio pos : selector.positions) {
            menuViewYio.renderShadow(pos, selector.getFactor().get(), batch);
        }
    }


    private void checkToRenderSelection(LevelSelector selector, RectangleYio pos, int i) {
        if (selector.selectionFactor.get() <= 0) return;
        if (i != selector.selectedPanelIndex) return;

        GraphicsYio.setBatchAlpha(batch, 0.5f * selector.selectionFactor.get());

        selX = selector.selIndexX * 2 * selector.iconRadius;
        selY = selector.selIndexY * 2 * selector.iconRadius;
        selX *= pos.width / selector.defPos.width;

        batch.draw(
                getGameView().blackPixel,
                (float) pos.x + selector.horOffset + selX,
                (float) pos.y + selector.verOffset + selY,
                2 * selector.iconRadius, 2 * selector.iconRadius
        );

        GraphicsYio.setBatchAlpha(batch, selector.getFactor().get());
    }
}
