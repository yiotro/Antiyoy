package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderSelectionShadows extends GameRender{


    private float smFactor;
    private float smDelta;


    public RenderSelectionShadows(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        smFactor = gameController.selectionManager.getSelMoneyFactor().get();
        smDelta = 0.1f * GraphicsYio.height * (1 - smFactor);
        if (smFactor <= 0) return;

        SpriteBatch batch = gameView.batchSolid;
        batch.begin();

        batch.draw(
                gameView.texturesManager.sideShadow,
                GraphicsYio.width,
                GraphicsYio.height + smDelta,
                0, 0,
                GraphicsYio.width,
                0.1f * GraphicsYio.height,
                1, 1, 180
        );

        batch.draw(
                gameView.texturesManager.sideShadow,
                0,
                -smDelta + 0,
                GraphicsYio.width,
                0.1f * GraphicsYio.height
        );

        batch.end();
    }


    @Override
    public void disposeTextures() {

    }
}
