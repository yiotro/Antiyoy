package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;
import yio.tro.antiyoy.stuff.SelectionEngineYio;

public class RenderSimpleButtonElement extends MenuRender{


    private SimpleButtonElement simpleButtonElement;
    private float alpha;
    private RenderableTextYio title;
    private BitmapFont font;
    private SelectionEngineYio selectionEngineYio;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        simpleButtonElement = (SimpleButtonElement) element;
        alpha = simpleButtonElement.getAlpha();

        if (alpha < 0.01) return;

        renderTitle();
        renderSelection();
    }


    private void renderSelection() {
        selectionEngineYio = simpleButtonElement.selectionEngineYio;
        if (!selectionEngineYio.isSelected()) return;

        GraphicsYio.setBatchAlpha(batch, selectionEngineYio.getAlpha() * alpha);
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), simpleButtonElement.viewPosition);
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderTitle() {
        title = simpleButtonElement.title;
        font = title.font;

        Color backupColor = font.getColor();
        font.setColor(Color.BLACK);
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), title, alpha);
        font.setColor(backupColor);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
