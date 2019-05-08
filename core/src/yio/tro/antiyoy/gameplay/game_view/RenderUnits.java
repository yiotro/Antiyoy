package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import yio.tro.antiyoy.gameplay.Unit;
import yio.tro.antiyoy.stuff.PointYio;

public class RenderUnits extends GameRender{

    public RenderUnits(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        for (Unit unit : gameController.unitList) {
            if (!isPosInViewFrame(unit.currentPos, hexViewSize)) continue;

            renderUnit(batchMovable, unit);
        }
    }


    void renderUnit(SpriteBatch spriteBatch, Unit unit) {
        PointYio pos = unit.currentPos;
        spriteBatch.draw(gameView.texturesManager.getUnitTexture(unit), pos.x - 0.7f * hexViewSize, pos.y - 0.5f * hexViewSize + unit.jumpPos * hexViewSize, 1.4f * hexViewSize, 1.6f * hexViewSize);
    }


    @Override
    public void disposeTextures() {

    }
}
