package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

import java.util.ArrayList;

public class RenderExceptionViewElement extends MenuRender{


    private ExceptionViewElement exceptionViewElement;
    private TextureRegion backgroundTexture;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("diplomacy/background.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        exceptionViewElement = (ExceptionViewElement) element;

        if (exceptionViewElement.getFactor().get() < 0.05) return;

        GraphicsYio.drawByRectangle(batch, backgroundTexture, exceptionViewElement.viewPosition);
        ArrayList<RenderableTextYio> viewList = exceptionViewElement.visualTextContainer.viewList;
        for (RenderableTextYio renderableTextYio : viewList) {
            if (renderableTextYio.bounds.y < exceptionViewElement.viewPosition.y) continue;
            renderBlackText(batch, renderableTextYio);
        }
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
