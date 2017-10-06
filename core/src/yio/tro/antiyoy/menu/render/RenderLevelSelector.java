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


    @Override
    public void loadTextures() {
        blackPixel = GraphicsYio.loadTextureRegion("pixels/black_pixel.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        renderLevelSelector((LevelSelector) element);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }


    public void renderLevelSelector(LevelSelector selector) {
        if (selector.getFactor().get() == 0) return;
        c = batch.getColor();

        // shadows
        if (selector.getFactor().get() > 0.95) {
            batch.begin();
            for (RectangleYio po : selector.pos) {
                menuViewYio.renderShadow(po, selector.getFactor().get(), batch);
            }
            batch.end();
        }

        // masking
        Masking.begin();
        menuViewYio.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (selector.getFactor().get() == 1) {
            for (RectangleYio po : selector.pos) {
                menuViewYio.drawRoundRect(po);
            }
        } else {
            menuViewYio.drawCircle(selector.getCircleX(), selector.getCircleY(), selector.getCircleR());
        }
        menuViewYio.shapeRenderer.end();

        Masking.continueFromBatch();
        batch.begin();

        batch.setColor(c.r, c.g, c.b, selector.getFactor().get());

        // main part
        RectangleYio pos;
        for (int i = 0; i < selector.textures.length; i++) {
            pos = selector.pos[i];
            batch.draw(selector.textures[i], (float)pos.x, (float)pos.y, (float)pos.width, (float)pos.height);
//            GraphicsYio.renderBorder(pos, batch, blackPixel);
            checkToRenderSelection(selector, pos, i);
        }
        batch.end();
        Masking.end();
        batch.setColor(c.r, c.g, c.b, 1);
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
                (float)pos.x + selector.horOffset + selX,
                (float)pos.y + selector.verOffset + selY,
                2 * selector.iconRadius, 2 * selector.iconRadius
        );

        GraphicsYio.setBatchAlpha(batch, selector.getFactor().get());
    }
}
