package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.context_list_menu.ClmItem;
import yio.tro.antiyoy.menu.context_list_menu.ContextListMenuElement;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderContextListMenuElement extends MenuRender{


    private ContextListMenuElement contextListMenuElement;
    private float f;
    private TextureRegion background;
    private BitmapFont font;
    private Color color;


    @Override
    public void loadTextures() {
        background = GraphicsYio.loadTextureRegion("pixels/kb_background.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        contextListMenuElement = (ContextListMenuElement) element;
        f = contextListMenuElement.getFactor().get();
        font = contextListMenuElement.font;

        GraphicsYio.setBatchAlpha(batch, 0.15 * f);
        renderBlackout();

        GraphicsYio.setBatchAlpha(batch, 1);
        renderBackground();
        renderItems();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderItems() {
        color = font.getColor();
        font.setColor(Color.BLACK);
        GraphicsYio.setFontAlpha(font, f);

        for (ClmItem item : contextListMenuElement.items) {
            font.draw(
                    batch,
                    item.value,
                    item.textPosition.x,
                    item.textPosition.y
            );

            if (item.isSelected()) {
                renderItemSelection(item);
            }
        }
        font.setColor(color);
    }


    private void renderItemSelection(ClmItem item) {
        GraphicsYio.setBatchAlpha(batch, 0.2 * item.selectionFactor.get());

        GraphicsYio.drawByRectangle(batch, getBlackPixel(), item.position);

        GraphicsYio.setBatchAlpha(batch, f);
    }


    private void renderBlackout() {
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), contextListMenuElement.blackoutPosition);
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(batch, background, contextListMenuElement.viewPosition);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
