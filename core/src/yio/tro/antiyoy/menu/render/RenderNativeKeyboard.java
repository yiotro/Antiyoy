package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.keyboard.NativeKeyboardElement;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderNativeKeyboard extends MenuRender{

    private NativeKeyboardElement nativeKeyboardElement;
    private float alpha;
    private TextureRegion tfBackground;


    @Override
    public void loadTextures() {
        tfBackground = GraphicsYio.loadTextureRegion("pixels/white_pixel.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        nativeKeyboardElement = (NativeKeyboardElement) element;
        alpha = nativeKeyboardElement.getFactor().get();

        renderBlackout();
        renderFrame();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderFrame() {
        if (nativeKeyboardElement.tfFactor.get() < 0.1) return;

        GraphicsYio.setBatchAlpha(batch, 1);
        GraphicsYio.drawByRectangle(batch, tfBackground, nativeKeyboardElement.tfFrame);
        GraphicsYio.renderBorder(batch, getBlackPixel(), nativeKeyboardElement.tfFrame, GraphicsYio.borderThickness);
    }


    private void renderBlackout() {
        GraphicsYio.setBatchAlpha(batch, 0.15 * alpha);
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), nativeKeyboardElement.blackoutPosition);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
