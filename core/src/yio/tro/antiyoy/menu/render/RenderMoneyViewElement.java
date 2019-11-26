package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.income_view.MoneyViewElement;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;
import yio.tro.antiyoy.stuff.SelectionEngineYio;

public class RenderMoneyViewElement extends MenuRender{


    private MoneyViewElement moneyViewElement;
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
        moneyViewElement = (MoneyViewElement) element;
        title = moneyViewElement.title;
        font = title.font;
        selectionEngineYio = moneyViewElement.selectionEngineYio;

        renderTitle();
        renderSelection();
    }


    private void renderSelection() {
        if (!selectionEngineYio.isSelected()) return;
        GraphicsYio.setBatchAlpha(batch, selectionEngineYio.getAlpha() * moneyViewElement.getAlpha());
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), moneyViewElement.outerBounds);
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderTitle() {
        GraphicsYio.setFontAlpha(font, moneyViewElement.getAlpha());
        Color fontColor = font.getColor();
        font.setColor(Color.WHITE);

        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), title, moneyViewElement.getAlpha());

        GraphicsYio.setFontAlpha(font, 1);
        font.setColor(fontColor);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
