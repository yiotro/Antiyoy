package yio.tro.antiyoy.gameplay.game_view;

import yio.tro.antiyoy.gameplay.touch_mode.TmEditProvinces;
import yio.tro.antiyoy.gameplay.touch_mode.TmepCityName;
import yio.tro.antiyoy.gameplay.touch_mode.TouchMode;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class RenderTmEditProvinces extends GameRender{


    private TmEditProvinces tm;


    public RenderTmEditProvinces(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        tm = TouchMode.tmEditProvinces;

        for (TmepCityName cityName : tm.cityNames) {
            if (!cityName.isCurrentlyVisible()) continue;
            RenderableTextYio renderableTextYio = cityName.renderableTextYio;
            if (!gameController.cameraController.isRectangleInViewFrame(cityName.biggerBounds, 0)) continue;
            float alpha = cityName.appearFactor.get();
            GraphicsYio.setBatchAlpha(batchMovable, alpha);
            GraphicsYio.drawByRectangle(batchMovable, getBlackPixel(), cityName.biggerBounds);
            GraphicsYio.setFontAlpha(renderableTextYio.font, alpha);
            GraphicsYio.renderTextOptimized(batchMovable, getBlackPixel(), renderableTextYio, alpha);
            GraphicsYio.setFontAlpha(renderableTextYio.font, 1);
        }

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    @Override
    public void disposeTextures() {

    }
}
