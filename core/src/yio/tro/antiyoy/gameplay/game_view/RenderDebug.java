package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.stuff.CircleYio;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderDebug extends GameRender{


    private TextureRegion redPixel;
    CircleYio tempCircle;


    public RenderDebug(GameRendersList gameRendersList) {
        super(gameRendersList);
        tempCircle = new CircleYio();
    }


    @Override
    public void loadTextures() {
        redPixel = GraphicsYio.loadTextureRegion("pixels/pixel_red.png", false);
    }


    @Override
    public void render() {
        if (!DebugFlags.renderDebug) return;

        renderField();
    }


    private void renderField() {
        GraphicsYio.setBatchAlpha(batchMovable, 0.7);

        tempCircle.setRadius(GraphicsYio.borderThickness);
        for (int i = 0; i < getFieldController().fWidth; i++) {
            for (int j = 0; j < getFieldController().fHeight; j++) {
                Hex hex = getFieldController().field[i][j];
                tempCircle.center.setBy(hex.pos);
                GraphicsYio.drawByCircle(batchMovable, redPixel, tempCircle);
            }
        }

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    private FieldManager getFieldController() {
        return gameController.fieldManager;
    }


    @Override
    public void disposeTextures() {

    }
}
