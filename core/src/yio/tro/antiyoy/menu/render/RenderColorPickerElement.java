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
    private TextureRegion randomColorPixel;
    private TextureRegion shadowTexture;
    private TextureRegion grayPixel;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("menu/background.png", false);
        borderTexture = GraphicsYio.loadTextureRegion("menu/separator.png", true);
        randomColorPixel = GraphicsYio.loadTextureRegion("pixels/colors/random_color_pixel.png", true);
        shadowTexture = GraphicsYio.loadTextureRegion("side_shadow.png", true);
        grayPixel = GraphicsYio.loadTextureRegion("pixels/gray_pixel.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        colorPickerElement = (ColorPickerElement) element;
        alpha = 1;

        GraphicsYio.setBatchAlpha(batch, alpha);
        renderInternals();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderInternals() {
        renderShadow();
        renderBackground();
        renderItems();
        renderFill();
    }


    private void renderFill() {
        if (colorPickerElement.fillFactor.get() == 0) return;
        TextureRegion tx = getItemTexture(colorPickerElement.chosenColor);
        if (colorPickerElement.chosenColor == -1) {
            tx = grayPixel;
        }
        GraphicsYio.drawByRectangle(batch, tx, colorPickerElement.fillPosition);
    }


    private void renderShadow() {
        GraphicsYio.drawByRectangle(batch, shadowTexture, colorPickerElement.shadowPosition);
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(batch, backgroundTexture, colorPickerElement.viewPosition);
    }


    private void renderItems() {
        for (CpeItem item : colorPickerElement.items) {
            renderSingleItem(item);
        }
    }


    private void renderSingleItem(CpeItem item) {
        GraphicsYio.drawByCircle(batch, getItemTexture(item.color), item.viewPosition);
        GraphicsYio.renderBorder(batch, borderTexture, item.borderPosition);
    }


    private void renderSelection(CpeItem item) {
        if (!item.selectionEngineYio.isSelected()) return;
        GraphicsYio.setBatchAlpha(batch, 3 * alpha * item.selectionEngineYio.getAlpha());
        GraphicsYio.drawByCircle(batch, getBlackPixel(), item.viewPosition);
        GraphicsYio.setBatchAlpha(batch, alpha);
    }


    private TextureRegion getItemTexture(int color) {
        if (color == -1) return randomColorPixel;
        return MenuRender.renderIncomeGraphElement.getPixelByColor(color);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
