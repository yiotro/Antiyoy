package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.menu.scrollable_list.ScrollableListYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.Masking;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class RenderScrollableList extends MenuRender{

    protected TextureRegion backgroundTexture;
    private TextureRegion selectionPixel;
    ScrollableListYio scrollableList;
    private RectangleYio viewPosition;
    private float alpha;
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
        scrollableList = (ScrollableListYio) element;
        viewPosition = scrollableList.viewPosition;
        alpha = scrollableList.getFactor().get();

        if (alpha < 0.25) return;

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
        if (alpha <= 0.6) return;

        MenuRender.renderShadow.disableInternalFillForOneDraw();
        MenuRender.renderShadow.renderShadow(viewPosition, 1);
    }


    private void renderInternals() {
        GraphicsYio.setBatchAlpha(batch, alpha);

        renderBackground();
        renderEdges();
        renderItems();
        renderLabel();
        renderEmptySign();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderEmptySign() {
        if (scrollableList.items.size() > 0) return;

        RenderableTextYio emptySign = scrollableList.emptySign;
        BitmapFont font = emptySign.font;
        Color color = font.getColor();
        font.setColor(Color.DARK_GRAY);
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), emptySign, alpha);
        font.setColor(color);
    }


    private void renderBackground() {
        if (scrollableList.bottomEdge.y < scrollableList.position.y) return; // no need to render background

        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                scrollableList.viewPosition
        );
    }


    private void renderEdges() {
        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                scrollableList.topEdge
        );

        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                scrollableList.bottomEdge
        );
    }


    private void renderLabel() {
        if (alpha < 0.5) return;

        GraphicsYio.setFontAlpha(scrollableList.titleFont, scrollableList.textAlphaFactor.get());

        scrollableList.titleFont.draw(
                batch,
                scrollableList.label,
                scrollableList.labelPosition.x,
                scrollableList.labelPosition.y
        );

        GraphicsYio.setFontAlpha(scrollableList.titleFont, 1);
    }


    protected void renderItems() {
        Color descBackupColor = scrollableList.descFont.getColor();
        scrollableList.descFont.setColor(Color.BLACK);
        Color titleColor = scrollableList.titleFont.getColor();
        scrollableList.titleFont.setColor(Color.BLACK);

        for (ListItemYio item : scrollableList.items) {
            if (!item.isVisible()) continue;

            renderItemBackground(item);
            renderItemTitle(item);
            renderItemDescription(item);
            renderItemSelection(item);
        }

        scrollableList.descFont.setColor(descBackupColor);
        scrollableList.titleFont.setColor(titleColor);
    }


    protected void renderItemBackground(ListItemYio item) {
        GraphicsYio.setBatchAlpha(batch, alpha);

        GraphicsYio.drawByRectangle(
                batch,
                getItemBackgroundTexture(item),
                item.position
        );
    }


    protected TextureRegion getItemBackgroundTexture(ListItemYio item) {
        switch (item.bckViewType) {
            default: return null;
            case 0: return bck1;
            case 1: return bck2;
            case 2: return bck3;
        }
    }


    protected void renderItemDescription(ListItemYio item) {
        if (scrollableList.textAlphaFactor.get() == 0) return;

        GraphicsYio.setFontAlpha(scrollableList.descFont, scrollableList.textAlphaFactor.get());

        scrollableList.descFont.draw(
                batch,
                item.description,
                item.descPosition.x,
                item.descPosition.y
        );

        GraphicsYio.setFontAlpha(scrollableList.descFont, 1);
    }


    protected void renderItemSelection(ListItemYio item) {
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


    private void renderItemTitle(ListItemYio item) {
        if (scrollableList.textAlphaFactor.get() == 0) return;

        GraphicsYio.setFontAlpha(scrollableList.titleFont, scrollableList.textAlphaFactor.get());

        scrollableList.titleFont.draw(
                batch,
                item.title,
                item.titlePosition.x,
                item.titlePosition.y
        );

        GraphicsYio.setFontAlpha(scrollableList.titleFont, 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
