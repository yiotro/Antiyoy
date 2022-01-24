package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderDownsidePanelElement extends MenuRender{


    private TextureRegion backgroundTexture;
    private DownsidePanelElement downsidePanelElement;
    private float alpha;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("pixels/kb_background.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        downsidePanelElement = (DownsidePanelElement) element;
        alpha = downsidePanelElement.getAlpha();

        if (alpha < 0.01) return;

        GraphicsYio.setBatchAlpha(batch, 0.15 * alpha);
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), downsidePanelElement.blackoutPosition);

        GraphicsYio.setBatchAlpha(batch, 1);
        GraphicsYio.drawByRectangle(batch, backgroundTexture, downsidePanelElement.renderPosition);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
