package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.editor_elements.color_picker.ColorPickerElement;
import yio.tro.antiyoy.menu.editor_elements.color_picker.CpeItem;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderColorPickerElement extends MenuRender{


    private ColorPickerElement colorPickerElement;
    private float alpha;
    private TextureRegion backgroundTexture;
    private TextureRegion borderTexture;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("menu/background.png", false);
        borderTexture = GraphicsYio.loadTextureRegion("pixels/pixel_dark_gray.png", true);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        colorPickerElement = (ColorPickerElement) element;
        alpha = colorPickerElement.getFactor().get();

        MenuRender.renderShadow.renderShadow(colorPickerElement.viewPosition, alpha);
        GraphicsYio.setBatchAlpha(batch, alpha);
        renderInternals();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderInternals() {
        GraphicsYio.drawByRectangle(batch, backgroundTexture, colorPickerElement.viewPosition);
        renderItems();
    }


    private void renderItems() {
        for (CpeItem item : colorPickerElement.items) {
            renderSingleItem(item);
        }
    }


    private void renderSingleItem(CpeItem item) {
        GraphicsYio.drawByRectangle(batch, borderTexture, item.borderPosition);
        GraphicsYio.drawByCircle(batch, getItemTexture(item.color), item.viewPosition);
        if (item.selectionEngineYio.isSelected()) {
            GraphicsYio.setBatchAlpha(batch, alpha * item.selectionEngineYio.getAlpha());
            GraphicsYio.drawByCircle(batch, getBlackPixel(), item.viewPosition);
            GraphicsYio.setBatchAlpha(batch, alpha);
        }
    }


    private TextureRegion getItemTexture(int color) {
        return MenuRender.renderDiplomacyElement.getBackgroundPixelByColor(color);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
