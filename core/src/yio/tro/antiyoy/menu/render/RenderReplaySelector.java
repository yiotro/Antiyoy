package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.replay_selector.ReplaySelector;
import yio.tro.antiyoy.menu.replay_selector.RsItem;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.Masking;
import yio.tro.antiyoy.stuff.RectangleYio;

public class RenderReplaySelector extends MenuRender{


    private TextureRegion backgroundTexture;
    private TextureRegion selectionPixel;
    private ReplaySelector rs;
    private RectangleYio viewPosition;
    private float factor;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("menu/background.png", false);
        selectionPixel = GraphicsYio.loadTextureRegion("pixels/black_pixel.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        rs = (ReplaySelector) element;
        viewPosition = rs.viewPosition;
        factor = rs.getFactor().get();

        renderShadow();

        batch.end();
        Masking.begin();
        menuViewYio.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        menuViewYio.drawRoundRect(viewPosition);
        menuViewYio.shapeRenderer.end();

        batch.begin();
        Masking.continueAfterBatchBegin();

        renderInternals();

        Masking.end(batch);
    }


    private void renderShadow() {
        if (factor <= 0.5) return;

//        batch.begin();
        menuViewYio.renderShadow(viewPosition, 1, batch);
//        batch.end();
    }


    private void renderInternals() {
        GraphicsYio.setBatchAlpha(batch, Math.sqrt(factor));

        renderBackground();
        renderItems();
        renderLabel();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderLabel() {
        if (factor < 0.5) return;

        GraphicsYio.setFontAlpha(rs.titleFont, rs.textAlphaFactor.get());

        rs.titleFont.draw(
                batch,
                rs.label,
                rs.labelPosition.x,
                rs.labelPosition.y
        );

        GraphicsYio.setFontAlpha(rs.titleFont, 1);
    }


    private void renderItems() {
        if (rs.textAlphaFactor.get() == 0) return;

        rs.descFont.setColor(Color.BLACK);
        Color titleColor = rs.titleFont.getColor();
        rs.titleFont.setColor(Color.BLACK);

        for (RsItem item : rs.items) {
            if (!item.isVisible()) continue;
            RectangleYio pos = item.position;

            renderItemTitle(item);
            renderItemDescription(item);
            renderItemSelection(item, pos);
        }

        rs.descFont.setColor(Color.WHITE);
        rs.titleFont.setColor(titleColor);
    }


    private void renderItemDescription(RsItem item) {
        GraphicsYio.setFontAlpha(rs.descFont, rs.textAlphaFactor.get());

        rs.descFont.draw(
                batch,
                item.description,
                item.descPosition.x,
                item.descPosition.y
        );

        GraphicsYio.setFontAlpha(rs.descFont, 1);
    }


    private void renderItemSelection(RsItem item, RectangleYio pos) {
        if (!item.isSelected()) return;

        GraphicsYio.setBatchAlpha(batch, 0.5 * item.getSelectionFactor().get());

        GraphicsYio.drawByRectangle(
                batch,
                selectionPixel,
                pos
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderItemTitle(RsItem item) {
        GraphicsYio.setFontAlpha(rs.titleFont, rs.textAlphaFactor.get());

        rs.titleFont.draw(
                batch,
                item.title,
                item.titlePosition.x,
                item.titlePosition.y
        );

        GraphicsYio.setFontAlpha(rs.titleFont, 1);
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                viewPosition
        );
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
