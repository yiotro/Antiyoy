package yio.tro.antiyoy.menu.render;

import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.TextLabelElement;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderTextLabel extends MenuRender{


    private TextLabelElement tlElement;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        tlElement = (TextLabelElement) element;

        renderBlackTextOptimized(batch, getBlackPixel(), tlElement.title, tlElement.getFactor().get());
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
