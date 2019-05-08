package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.loading_screen.LoadingScreenElement;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class RenderLoadingScreenElement extends MenuRender{


    private LoadingScreenElement loadingScreenElement;
    private TextureRegion backgroundRegion;
    private RenderableTextYio title;


    @Override
    public void loadTextures() {
        // loading screen is not used currently
//        backgroundRegion = GraphicsYio.loadTextureRegion("game_background.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        loadingScreenElement = (LoadingScreenElement) element;
        title = loadingScreenElement.title;

        GraphicsYio.setBatchAlpha(batch, loadingScreenElement.appearFactor.get());
        Color previousColor = title.font.getColor();
        title.font.setColor(Color.WHITE);
        GraphicsYio.setFontAlpha(title.font, loadingScreenElement.appearFactor.get() * loadingScreenElement.appearFactor.get());

        GraphicsYio.drawByRectangle(batch, backgroundRegion, loadingScreenElement.viewPosition);
        GraphicsYio.renderText(batch, title);

        GraphicsYio.setBatchAlpha(batch, 1);
        GraphicsYio.setFontAlpha(title.font, 1);
        title.font.setColor(previousColor);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
