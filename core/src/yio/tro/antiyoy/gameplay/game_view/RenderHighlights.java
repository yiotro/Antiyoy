package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.highlight.HighlightItem;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderHighlights extends GameRender{

    private TextureRegion borderTexture;


    public RenderHighlights(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {
        borderTexture = GraphicsYio.loadTextureRegion("selection_border.png", false);
    }


    @Override
    public void render() {
        for (HighlightItem highlightItem : gameController.highlightManager.items) {
            if (!highlightItem.isVisible()) continue;
            GraphicsYio.setBatchAlpha(batchMovable, highlightItem.appearFactor.get());
            GraphicsYio.drawByCircle(batchMovable, borderTexture, highlightItem.viewPosition);
        }
        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    @Override
    public void disposeTextures() {
        borderTexture.getTexture().dispose();
    }
}
