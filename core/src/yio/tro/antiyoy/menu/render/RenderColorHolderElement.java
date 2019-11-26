package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.color_picking.ColorHolderElement;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class RenderColorHolderElement extends MenuRender{


    private ColorHolderElement colorHolderElement;
    private float alpha;
    private RectangleYio viewPosition;
    private RenderableTextYio title;
    private BitmapFont font;
    private TextureRegion separatorTexture;
    private TextureRegion randomColorPixel;
    private TextureRegion grayPixel;


    @Override
    public void loadTextures() {
        separatorTexture = GraphicsYio.loadTextureRegion("menu/separator.png", true);
        randomColorPixel = GraphicsYio.loadTextureRegion("pixels/colors/random_color_pixel.png", true);
        grayPixel = GraphicsYio.loadTextureRegion("pixels/gray_pixel.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        colorHolderElement = (ColorHolderElement) element;
        alpha = colorHolderElement.getAlpha();
        viewPosition = colorHolderElement.viewPosition;
        title = colorHolderElement.title;
        font = title.font;

        GraphicsYio.setBatchAlpha(batch, 0.1 * alpha);
        renderBackground();
        GraphicsYio.setBatchAlpha(batch, alpha);
        renderTag();
        renderTitle();
        renderSelection();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderBackground() {
        TextureRegion bck = getPixelByValueIndex(colorHolderElement.getValueIndex());
        if (colorHolderElement.getValueIndex() == 0) {
            bck = grayPixel;
        }

        GraphicsYio.drawByRectangle(
                batch,
                bck,
                viewPosition
        );
    }


    private void renderSelection() {
        if (!colorHolderElement.selectionEngineYio.isSelected()) return;
        GraphicsYio.setBatchAlpha(batch, colorHolderElement.selectionEngineYio.getAlpha() * alpha);
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), colorHolderElement.viewPosition);
    }


    private void renderTitle() {
        GraphicsYio.setFontAlpha(font, alpha);
        Color fontColor = title.font.getColor();
        title.font.setColor(Color.BLACK);
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), title, alpha);
        GraphicsYio.setFontAlpha(font, 1);
        title.font.setColor(fontColor);
    }


    private void renderTag() {
        GraphicsYio.drawByRectangle(
                batch,
                getPixelByValueIndex(colorHolderElement.getValueIndex()),
                colorHolderElement.tagPosition
        );
        GraphicsYio.renderBorder(batch, separatorTexture, colorHolderElement.tagPosition);
    }


    private TextureRegion getPixelByValueIndex(int valueIndex) {
        if (valueIndex == 0) return randomColorPixel;
        return MenuRender.renderIncomeGraphElement.getPixelByColor(valueIndex - 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
