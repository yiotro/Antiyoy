package yio.tro.antiyoy;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class RenderLevelSelector extends MenuRender{

    TextureRegion blackPixel;


    @Override
    public void loadTextures() {
        blackPixel = GraphicsYio.loadTextureRegionByName("pixels/black_pixel.png", false);
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


    void renderLevelSelector(LevelSelector levelSelector) {
        if (levelSelector.getFactor().get() == 0) return;
        c = batch.getColor();

        // shadows
        if (levelSelector.getFactor().get() > 0.95) {
            batch.begin();
            for (RectangleYio po : levelSelector.pos) {
                menuViewYio.renderShadow(po, levelSelector.getFactor().get(), batch);
            }
            batch.end();
        }

        // masking
        Masking.begin();
        menuViewYio.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (levelSelector.getFactor().get() == 1) {
            for (RectangleYio po : levelSelector.pos) {
                menuViewYio.drawRoundRect(po);
            }
        } else {
            menuViewYio.drawCircle(levelSelector.getCircleX(), levelSelector.getCircleY(), levelSelector.getCircleR());
        }
        menuViewYio.shapeRenderer.end();

        Masking.continueFromBatch();
        batch.begin();

        batch.setColor(c.r, c.g, c.b, levelSelector.getFactor().get());

        // main part
        RectangleYio pos;
        for (int i = 0; i < levelSelector.textures.length; i++) {
            pos = levelSelector.pos[i];
            batch.draw(levelSelector.textures[i], (float)pos.x, (float)pos.y, (float)pos.width, (float)pos.height);
//            GraphicsYio.renderBorder(pos, batch, blackPixel);
            if (i == levelSelector.selectedPanelIndex && levelSelector.selectionFactor.get() > 0) {
                GraphicsYio.setBatchAlpha(batch, 0.5f * levelSelector.selectionFactor.get());
                batch.draw(getGameView().blackPixel, (float)pos.x + levelSelector.horOffset + 2 * levelSelector.selIndexX * levelSelector.iconRadius, (float)pos.y + levelSelector.verOffset + 2 * levelSelector.selIndexY * levelSelector.iconRadius, 2 * levelSelector.iconRadius, 2 * levelSelector.iconRadius);
                GraphicsYio.setBatchAlpha(batch, levelSelector.getFactor().get());
            }
        }
        batch.end();
        Masking.end();
        batch.setColor(c.r, c.g, c.b, 1);
    }
}
