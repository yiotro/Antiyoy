package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.Color;
import yio.tro.antiyoy.stuff.PointYio;

public class RenderResponseAnimHex extends GameRender{


    private PointYio pos;


    public RenderResponseAnimHex(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        if (gameController.fieldManager.responseAnimHex == null) return;

        pos = gameController.fieldManager.responseAnimHex.getPos();
        Color c = batchMovable.getColor();
        float a = c.a;
        batchMovable.setColor(c.r, c.g, c.b, 0.5f * Math.min(gameController.fieldManager.responseAnimFactor.get(), 1));
        float s = Math.max(hexViewSize, hexViewSize * gameController.fieldManager.responseAnimFactor.get());
        batchMovable.draw(gameView.texturesManager.responseAnimHexTexture, pos.x - s, pos.y - s, 2 * s, 2 * s);
        batchMovable.setColor(c.r, c.g, c.b, a);
    }


    @Override
    public void disposeTextures() {

    }
}
