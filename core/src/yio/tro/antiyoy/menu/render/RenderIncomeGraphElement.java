package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
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


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("diplomacy/background.png", false);
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
        Color previousColor = Fonts.smallerMenuFont.getColor();
        Fonts.smallerMenuFont.setColor(Color.BLACK);

        renderBackground();
        renderTitle();
        renderSeparator();
        renderItems();

        Fonts.smallerMenuFont.setColor(previousColor);
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
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), incomeGraphElement.title, alpha);
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(batch, backgroundTexture, incomeGraphElement.viewPosition);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
