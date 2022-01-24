package yio.tro.antiyoy.gameplay.game_view;

import yio.tro.antiyoy.ai.master.AiData;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderAiData extends GameRender{


    public RenderAiData(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        if (!DebugFlags.showAiData) return;

        FieldManager fieldManager = gameController.fieldManager;
        for (int i = 0; i < fieldManager.fWidth; i++) {
            for (int j = 0; j < fieldManager.fHeight; j++) {
                Hex hex = fieldManager.field[i][j];
                AiData aiData = hex.aiData;
                if (aiData.renderableTextYio.string.length() == 0) continue;
                GraphicsYio.drawByRectangle(batchMovable, getBlackPixel(), aiData.incBounds);
                GraphicsYio.renderText(batchMovable, aiData.renderableTextYio);
            }
        }
    }


    @Override
    public void disposeTextures() {

    }
}
