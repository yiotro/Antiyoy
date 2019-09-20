package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.income_graph.IgeItem;
import yio.tro.antiyoy.menu.income_graph.IncomeGraphElement;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderIncomeGraphElement extends MenuRender{


    private TextureRegion backgroundTexture;
    private IncomeGraphElement incomeGraphElement;
    private float alpha;
    private TextureRegion borderTexture;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("diplomacy/background.png", false);
        borderTexture = GraphicsYio.loadTextureRegion("pixels/pixel_dark_gray.png", true);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        incomeGraphElement = (IncomeGraphElement) element;
        alpha = incomeGraphElement.getFactor().get();

        MenuRender.renderShadow.renderShadow(incomeGraphElement.viewPosition, alpha);
        GraphicsYio.setBatchAlpha(batch, alpha);
        renderInternals();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderInternals() {
        BitmapFont font = incomeGraphElement.getFont();
        font.setColor(Color.BLACK);

        renderBackground();
        renderTitle();
        renderSeparator();
        renderBorders();
        renderItems();

        font.setColor(Color.WHITE);
    }


    private void renderBorders() {
        for (IgeItem item : incomeGraphElement.items) {
            if (item.borderFactor.get() == 0) continue;
            if (item.text.string.equals("0")) continue;
            GraphicsYio.setBatchAlpha(batch, alpha * item.borderFactor.get());
            GraphicsYio.drawByRectangle(batch, borderTexture, item.borderPosition);
        }
        GraphicsYio.setBatchAlpha(batch, alpha);
    }


    private void renderItems() {
        for (IgeItem item : incomeGraphElement.items) {
            renderSingleItem(item);
        }
    }


    private void renderSingleItem(IgeItem item) {
        int colorByFraction = getGameView().gameController.colorsManager.getColorByFraction(item.fraction);
        GraphicsYio.drawByRectangle(
                batch,
                MenuRender.renderDiplomacyElement.getBackgroundPixelByColor(colorByFraction),
                item.viewPosition
        );
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), item.text, alpha);
    }


    private void renderSeparator() {
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), incomeGraphElement.separatorPosition);
    }


    private void renderInnerAreaBorder() {
        GraphicsYio.renderBorder(batch, getBlackPixel(), incomeGraphElement.columnsArea);
    }


    private void renderTitle() {
        BitmapFont titleFont = incomeGraphElement.title.font;
        Color previousColor = titleFont.getColor();
        titleFont.setColor(Color.BLACK);
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), incomeGraphElement.title, alpha);
        titleFont.setColor(previousColor);
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(batch, backgroundTexture, incomeGraphElement.viewPosition);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
