package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.gameplay.skins.SkinManager;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.income_graph.IgeItem;
import yio.tro.antiyoy.menu.income_graph.IncomeGraphElement;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderIncomeGraphElement extends MenuRender{


    private TextureRegion backgroundTexture;
    private IncomeGraphElement incomeGraphElement;
    private float alpha;
    private TextureRegion borderTexture;
    private TextureRegion pixelGreen;
    private TextureRegion pixelRed;
    private TextureRegion pixelBlue;
    private TextureRegion pixelCyan;
    private TextureRegion pixelYellow;
    private TextureRegion pixelColor1;
    private TextureRegion pixelColor2;
    private TextureRegion pixelColor3;
    private TextureRegion pixelColor4;
    private TextureRegion pixelColor5;
    private TextureRegion pixelColor6;
    private TextureRegion grayPixel;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("diplomacy/background.png", false);
        borderTexture = GraphicsYio.loadTextureRegion("menu/separator.png", true);
        grayPixel = GraphicsYio.loadTextureRegion("pixels/gray_pixel.png", false);
        loadSkinDependentTextures();
    }


    private void loadSkinDependentTextures() {
        pixelGreen = loadColorPixel("green");
        pixelRed = loadColorPixel("red");
        pixelBlue = loadColorPixel("blue");
        pixelCyan = loadColorPixel("cyan");
        pixelYellow = loadColorPixel("yellow");
        pixelColor1 = loadColorPixel("color1");
        pixelColor2 = loadColorPixel("color2");
        pixelColor3 = loadColorPixel("color3");
        pixelColor4 = loadColorPixel("color4");
        pixelColor5 = loadColorPixel("color5");
        pixelColor6 = loadColorPixel("color6");
    }


    private TextureRegion loadColorPixel(String name) {
        SkinManager skinManager = menuViewYio.yioGdxGame.skinManager;
        return GraphicsYio.loadTextureRegion(skinManager.getColorPixelsFolderPath() + "/" + name + ".png", false);
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
            GraphicsYio.renderBorder(batch, borderTexture, item.borderPosition);
        }
        GraphicsYio.setBatchAlpha(batch, alpha);
    }


    private void renderItems() {
        for (IgeItem item : incomeGraphElement.items) {
            renderSingleItem(item);
        }
    }


    private void renderSingleItem(IgeItem item) {
        GraphicsYio.drawByRectangle(
                batch,
                getTextureForItem(item),
                item.viewPosition
        );
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), item.text, alpha);
    }


    public TextureRegion getTextureForItem(IgeItem item) {
        if (!item.scouted) return grayPixel;
        ColorsManager colorsManager = getGameView().gameController.colorsManager;
        int colorByFraction = colorsManager.getColorByFraction(item.fraction);
        return getPixelByColor(colorByFraction);
    }


    public TextureRegion getPixelByColor(int color) {
        switch (color) {
            default:
            case 0:
                return pixelGreen;
            case 1:
                return pixelRed;
            case 2:
                return pixelBlue;
            case 3:
                return pixelCyan;
            case 4:
                return pixelYellow;
            case 5:
                return pixelColor1;
            case 6:
                return pixelColor2;
            case 7:
                return pixelColor3;
            case 8:
                return pixelColor4;
            case 9:
                return pixelColor5;
            case 10:
                return pixelColor6;
        }
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


    public void onSkinChanged() {
        loadSkinDependentTextures();
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(batch, backgroundTexture, incomeGraphElement.viewPosition);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
