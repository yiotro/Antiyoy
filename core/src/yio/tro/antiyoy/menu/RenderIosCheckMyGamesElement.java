package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.menu.ios.IcmgIcon;
import yio.tro.antiyoy.menu.ios.IcmgType;
import yio.tro.antiyoy.menu.ios.IosCheckMyGamesElement;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.Masking;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

import java.util.HashMap;

public class RenderIosCheckMyGamesElement extends MenuRender{


    private TextureRegion backgroundTexture;
    private IosCheckMyGamesElement icmgElement;
    private float alpha;
    private RectangleYio viewPosition;
    private HashMap<IcmgType, TextureRegion> mapTextures;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("diplomacy/background.png", false);

        mapTextures = new HashMap<>();
        for (IcmgType icmgType : IcmgType.values()) {
            mapTextures.put(icmgType, GraphicsYio.loadTextureRegion("menu/ios/" + icmgType + ".png", true));
        }
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        icmgElement = (IosCheckMyGamesElement) element;
        alpha = icmgElement.getAlpha();
        viewPosition = icmgElement.viewPosition;

        MenuRender.renderShadow.renderShadow(viewPosition, alpha);

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


    private void renderInternals() {
        GraphicsYio.setBatchAlpha(batch, alpha);
        renderBackground();
        renderTextContainer();
        renderShowRoom();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderShowRoom() {
        for (IcmgIcon icon : icmgElement.icons) {
            GraphicsYio.drawByCircle(batch, mapTextures.get(icon.type), icon.viewPosition);
            if (alpha > 0.25) {
                renderBlackText(batch, icon.title);
            }
            if (icon.selectionEngineYio.isSelected()) {
                GraphicsYio.setBatchAlpha(batch, alpha * icon.selectionEngineYio.getAlpha());
                GraphicsYio.drawByRectangle(batch, getBlackPixel(), icon.touchPosition);
                GraphicsYio.setBatchAlpha(batch, alpha);
            }
        }
    }


    private void renderTextContainer() {
        if (alpha < 0.25) return;
        for (RenderableTextYio renderableTextYio : icmgElement.visualTextContainer.viewList) {
            renderBlackText(batch, renderableTextYio);
        }
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(batch, backgroundTexture, viewPosition);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
