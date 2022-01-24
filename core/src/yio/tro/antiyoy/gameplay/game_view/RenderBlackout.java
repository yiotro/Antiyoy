package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.Color;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderBlackout extends GameRender{

    public RenderBlackout(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        if (gameController.fieldManager.moveZoneManager.appearFactor.get() < 0.01) return;

        Color c = batchMovable.getColor();
        float a = c.a;
        batchMovable.setColor(c.r, c.g, c.b, 0.4f * gameController.selectionManager.getBlackoutFactor().get());
        GraphicsYio.drawByRectangle(batchMovable, getBlackPixel(), gameController.cameraController.frame);
        batchMovable.setColor(c.r, c.g, c.b, a);
    }


    @Override
    public void disposeTextures() {

    }
}
