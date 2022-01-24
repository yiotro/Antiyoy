package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.diplomatic_exchange.QuickExchangeTutorialElement;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class RenderQuickExchangeTutorialElement extends MenuRender{


    private TextureRegion redPixel;
    private QuickExchangeTutorialElement qetElement;
    private float f;
    private RenderableTextYio title;
    private BitmapFont font;
    private TextureRegion whitePixel;
    RectangleYio increasedBounds;


    public RenderQuickExchangeTutorialElement() {
        increasedBounds = new RectangleYio();
    }


    @Override
    public void loadTextures() {
        redPixel = GraphicsYio.loadTextureRegion("pixels/pixel_red.png", false);
        whitePixel = GraphicsYio.loadTextureRegion("pixels/white_pixel.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        qetElement = (QuickExchangeTutorialElement) element;
        f = Math.min(element.getFactor().get(), qetElement.realFactor.get());

        renderBlackouts();
        renderRedBorder();
        renderTitle();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderTitle() {
        title = qetElement.title;
        updateIncreasedBounds();
        GraphicsYio.setBatchAlpha(batch, 0.8 * f);
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), increasedBounds);
        font = title.font;
        Color fontBackupColor = font.getColor();
        font.setColor(Color.WHITE);
        GraphicsYio.renderTextOptimized(batch, whitePixel, title, f);
        font.setColor(fontBackupColor);
    }


    private void updateIncreasedBounds() {
        increasedBounds.setBy(title.bounds);
        increasedBounds.increase(0.01f * GraphicsYio.width);
    }


    private void renderBlackouts() {
        GraphicsYio.setBatchAlpha(batch, 0.33 * f);
        for (RectangleYio blackout : qetElement.blackouts) {
            GraphicsYio.drawByRectangle(batch, getBlackPixel(), blackout);
        }
    }


    private void renderRedBorder() {
        GraphicsYio.setBatchAlpha(batch, f);
        GraphicsYio.renderBorder(batch, redPixel, qetElement.currentProfitView.position);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
