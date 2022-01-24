package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;

public class RenderForefinger extends GameRender{


    private PointYio pos;


    public RenderForefinger(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        if (!GameRules.tutorialMode) return;

        if (gameController.forefinger.isPointingToHex()) {
            renderInGameState();
            return;
        }

        renderInMenuState();
    }


    private void renderInMenuState() {
        SpriteBatch batch = gameView.batchSolid;
        batch.begin();
        pos = gameController.forefinger.animPos;
        Color c = batch.getColor();
        float a = c.a;
        batch.setColor(c.r, c.g, c.b, gameController.forefinger.getAlpha());

        GraphicsYio.drawFromCenterRotated(
                batch,
                getForefingerTexture(),
                pos.x,
                pos.y,
                hexViewSize * gameController.forefinger.getSize(),
                gameController.forefinger.getRotation()
        );

        batch.setColor(c.r, c.g, c.b, a);
        batch.end();
    }


    private void renderInGameState() {
        batchMovable.begin();
        pos = gameController.forefinger.animPos;
        Color c = batchMovable.getColor();
        batchMovable.setColor(c.r, c.g, c.b, gameController.forefinger.getAlpha());

        GraphicsYio.drawFromCenterRotated(
                batchMovable,
                getForefingerTexture(),
                pos.x,
                pos.y,
                hexViewSize * gameController.forefinger.getSize(),
                gameController.forefinger.getRotation()
        );

        batchMovable.setColor(c.r, c.g, c.b, 1);
        batchMovable.end();
    }


    private TextureRegion getForefingerTexture() {
        return gameView.texturesManager.forefingerTexture;
    }


    @Override
    public void disposeTextures() {

    }
}
