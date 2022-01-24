package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.diplomatic_log.DiplomaticLogPanel;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class RenderDiplomaticLogPanel extends MenuRender{


    protected TextureRegion backgroundTexture;
    private TextureRegion selectionPixel;
    DiplomaticLogPanel panel;
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
        panel = (DiplomaticLogPanel) element;
        viewPosition = panel.viewPosition;
        factor = panel.appearFactor.get();

        if (factor < 0.05) return;

        GraphicsYio.setBatchAlpha(batch, factor);

        renderBackground();
//        renderEdges();
        renderItems();
        renderLabel();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                viewPosition
        );
    }


    private void renderEdges() {
        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                panel.topEdge
        );

        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                panel.bottomEdge
        );
    }


    private void renderLabel() {
        if (factor < 0.5) return;

        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                panel.titleBackground
        );

        BitmapFont titleFont = panel.titleFont;
        GraphicsYio.setFontAlpha(titleFont, panel.textAlphaFactor.get());
        Color backupColor = titleFont.getColor();
        titleFont.setColor(Color.BLACK);

        titleFont.draw(
                batch,
                panel.label,
                panel.labelPosition.x,
                panel.labelPosition.y
        );

        GraphicsYio.setFontAlpha(titleFont, 1);
        titleFont.setColor(backupColor);
    }


    protected void renderItems() {
        panel.descFont.setColor(Color.BLACK);
        Color titleColor = panel.titleFont.getColor();
        panel.titleFont.setColor(Color.BLACK);

        for (ListItemYio item : panel.items) {
            if (!isItemVisible(item)) continue;

            renderItemBackground(item);
            renderItemTitle(item);
//            renderItemDescription(item);
            renderItemSelection(item);
        }

        panel.descFont.setColor(Color.WHITE);
        panel.titleFont.setColor(titleColor);
    }


    private boolean isItemVisible(ListItemYio itemYio) {
        RectangleYio position = itemYio.position;
        if (position.y > panel.titleBackground.y) return false;
        if (position.y + position.height < panel.viewPosition.y) return false;

        return true;
    }


    protected void renderItemBackground(ListItemYio item) {
        if (item.bckViewType == -1) return;

        GraphicsYio.setBatchAlpha(batch, factor);

        GraphicsYio.drawByRectangle(
                batch,
                getItemBackgroundTexture(item),
                item.position
        );
    }


    protected TextureRegion getItemBackgroundTexture(ListItemYio item) {
        return MenuRender.renderDiplomacyElement.getBackgroundPixelByColor(item.bckViewType);
    }


    protected void renderItemDescription(ListItemYio item) {
        if (panel.textAlphaFactor.get() == 0) return;

        GraphicsYio.setFontAlpha(panel.descFont, panel.textAlphaFactor.get());

        panel.descFont.draw(
                batch,
                item.description,
                item.descPosition.x,
                item.descPosition.y
        );

        GraphicsYio.setFontAlpha(panel.descFont, 1);
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
        if (panel.textAlphaFactor.get() == 0) return;

        GraphicsYio.setFontAlpha(panel.titleFont, panel.textAlphaFactor.get());

        panel.titleFont.draw(
                batch,
                item.title,
                item.titlePosition.x,
                item.titlePosition.y
        );

        GraphicsYio.setFontAlpha(panel.titleFont, 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
