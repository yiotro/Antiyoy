package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.save_slot_selector.SaveSlotSelector;
import yio.tro.antiyoy.menu.save_slot_selector.SsItem;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.Masking;
import yio.tro.antiyoy.stuff.RectangleYio;

public class RenderSaveSlotSelector extends MenuRender{

    private TextureRegion backgroundTexture;
    private TextureRegion selectionPixel;
    private SaveSlotSelector selector;
    private RectangleYio viewPosition;
    private float factor;
    private TextureRegion bck1;
    private TextureRegion bck2;
    private TextureRegion bck3;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("menu/background.png", false);
        selectionPixel = GraphicsYio.loadTextureRegion("pixels/black_pixel.png", false);
        bck1 = GraphicsYio.loadTextureRegion("button_background_1.png", false);
        bck2 = GraphicsYio.loadTextureRegion("button_background_2.png", false);
        bck3 = GraphicsYio.loadTextureRegion("button_background_3.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        selector = (SaveSlotSelector) element;
        viewPosition = selector.viewPosition;
        factor = selector.getFactor().get();

        if (factor < 0.25) return;

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
        if (factor <= 0.6) return;

        MenuRender.renderShadow.disableInternalFillForOneDraw();
        MenuRender.renderShadow.renderShadow(viewPosition, 1);
    }


    private void renderInternals() {
        GraphicsYio.setBatchAlpha(batch, factor);

        renderBackground();
        renderEdges();
        renderItems();
        renderLabel();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderBackground() {
        if (selector.bottomEdge.y < selector.position.y) return; // no need to render background

        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                selector.viewPosition
        );
    }


    private void renderEdges() {
        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                selector.topEdge
        );

        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                selector.bottomEdge
        );
    }


    private void renderLabel() {
        if (factor < 0.5) return;

        GraphicsYio.setFontAlpha(selector.titleFont, selector.textAlphaFactor.get());

        selector.titleFont.draw(
                batch,
                selector.label,
                selector.labelPosition.x,
                selector.labelPosition.y
        );

        GraphicsYio.setFontAlpha(selector.titleFont, 1);
    }


    private void renderItems() {
        selector.descFont.setColor(Color.BLACK);
        Color titleColor = selector.titleFont.getColor();
        selector.titleFont.setColor(Color.BLACK);

        for (SsItem item : selector.items) {
            if (!item.isVisible()) continue;

            renderItemBackground(item);
            renderItemTitle(item);
            renderItemDescription(item);
            renderItemSelection(item);
        }

        selector.descFont.setColor(Color.WHITE);
        selector.titleFont.setColor(titleColor);
    }


    private void renderItemBackground(SsItem item) {
        GraphicsYio.setBatchAlpha(batch, factor);

        GraphicsYio.drawByRectangle(
                batch,
                getItemBackgroundTexture(item),
                item.position
        );
    }


    private TextureRegion getItemBackgroundTexture(SsItem item) {
        switch (item.bckViewType) {
            default: return null;
            case 0: return bck1;
            case 1: return bck2;
            case 2: return bck3;
        }
    }


    private void renderItemDescription(SsItem item) {
        if (selector.textAlphaFactor.get() == 0) return;

        GraphicsYio.setFontAlpha(selector.descFont, selector.textAlphaFactor.get());

        selector.descFont.draw(
                batch,
                item.description,
                item.descPosition.x,
                item.descPosition.y
        );

        GraphicsYio.setFontAlpha(selector.descFont, 1);
    }


    private void renderItemSelection(SsItem item) {
        if (!item.isSelected()) return;

        RectangleYio pos = item.position;

        GraphicsYio.setBatchAlpha(batch, 0.5 * item.getSelectionFactor().get());

        GraphicsYio.drawByRectangle(
                batch,
                selectionPixel,
                pos
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderItemTitle(SsItem item) {
        if (selector.textAlphaFactor.get() == 0) return;

        GraphicsYio.setFontAlpha(selector.titleFont, selector.textAlphaFactor.get());

        selector.titleFont.draw(
                batch,
                item.title,
                item.titlePosition.x,
                item.titlePosition.y
        );

        GraphicsYio.setFontAlpha(selector.titleFont, 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
