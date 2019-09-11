package yio.tro.antiyoy.menu.render;

import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.TextLabelElement;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderTextLabel extends MenuRender{


    private TextLabelElement textLabelElement;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        textLabelElement = (TextLabelElement) element;

        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), textLabelElement.title, textLabelElement.getFactor().get());
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
