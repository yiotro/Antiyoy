package yio.tro.antiyoy.gameplay.game_view;

import yio.tro.antiyoy.gameplay.TextHintItem;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderTextHintItems extends GameRender{

    public RenderTextHintItems(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        for (TextHintItem hintItem : gameController.selectionManager.hintItems) {
            if (hintItem.appearFactor.get() == 0) continue;
            GraphicsYio.setFontAlpha(hintItem.title.font, hintItem.appearFactor.get());
            GraphicsYio.renderText(batchMovable, hintItem.title);
            GraphicsYio.setFontAlpha(hintItem.title.font, 1);
        }
    }


    @Override
    public void disposeTextures() {

    }
}
