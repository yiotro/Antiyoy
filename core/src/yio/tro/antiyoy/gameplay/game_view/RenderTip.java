package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.gameplay.SelectionTipType;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderTip extends GameRender{

    public RenderTip(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        if (gameController.selectionManager.tipFactor.get() <= 0.01) return;

        SpriteBatch batch = gameView.batchSolid;
        batch.begin();
        float s = 0.2f * GraphicsYio.width;

        batch.draw(
                getTipTypeTexture(gameController.selectionManager.tipShowType),
                0.5f * GraphicsYio.width - 0.5f * s,
                getTipVerticalPos(s), s, s
        );

        Fonts.gameFont.draw(batch,
                gameController.currentPriceString,
                0.5f * GraphicsYio.width - 0.5f * gameController.priceStringWidth,
                getTipVerticalPos(s)
        );

        batch.end();
    }


    private float getTipVerticalPos(float s) {
        if (SettingsManager.fastConstructionEnabled) {
            return s * (gameController.selectionManager.tipFactor.get() - 1) + 0.12f * GraphicsYio.height;
        }

        return s * (gameController.selectionManager.tipFactor.get() - 1) + 0.04f * GraphicsYio.height;
    }


    private TextureRegion getTipTypeTexture(int tipShowType) {
        switch (tipShowType) {
            default:
            case SelectionTipType.TOWER:
                return gameView.texturesManager.towerTexture.getNormal();
            case SelectionTipType.FARM:
                return gameView.texturesManager.farmTexture[0].getNormal();
            case SelectionTipType.STRONG_TOWER:
                return gameView.texturesManager.strongTowerTexture.getNormal();
            case SelectionTipType.TREE:
                return gameView.texturesManager.pineTexture.getNormal();
            case SelectionTipType.UNIT_1:
                return gameView.texturesManager.manTextures[0].getNormal();
            case SelectionTipType.UNIT_2:
                return gameView.texturesManager.manTextures[1].getNormal();
            case SelectionTipType.UNIT_3:
                return gameView.texturesManager.manTextures[2].getNormal();
            case SelectionTipType.UNIT_4:
                return gameView.texturesManager.manTextures[3].getNormal();
        }
    }


    @Override
    public void disposeTextures() {

    }
}
