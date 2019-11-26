package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Obj;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.PointYio;

public class RenderSolidObjects extends GameRender{


    private TextureRegion currentObjectTexture;


    public RenderSolidObjects(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        if (!YioGdxGame.isScreenVerySmall()) return;

        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (!activeHex.containsObject()) continue;

            renderSolidObject(batchMovable, activeHex.getPos(), activeHex);
        }
    }


    @Override
    protected void renderSolidObject(SpriteBatch spriteBatch, PointYio pos, Hex hex) {
        spriteBatch.draw(
                getSolidObjectTexture(hex),
                pos.x - 0.7f * hexViewSize,
                pos.y - 0.5f * hexViewSize,
                1.4f * hexViewSize,
                1.6f * hexViewSize
        );
    }


    private TextureRegion getSolidObjectTexture(Hex hex) {
        return getSolidObjectTexture(hex, gameView.currentZoomQuality);
    }


    private TextureRegion getSolidObjectTexture(Hex hex, int quality) {
        switch (hex.objectInside) {
            case Obj.GRAVE:
                return gameView.texturesManager.graveTexture.getTexture(quality);
            case Obj.TOWN:
                if (GameRules.slayRules) {
                    return gameView.texturesManager.houseTexture.getTexture(quality);
                }
                return gameView.texturesManager.castleTexture.getTexture(quality);
            case Obj.PALM:
                return gameView.texturesManager.palmTexture.getTexture(quality);
            case Obj.PINE:
                return gameView.texturesManager.pineTexture.getTexture(quality);
            case Obj.TOWER:
                return gameView.texturesManager.towerTexture.getTexture(quality);
            case Obj.FARM:
                return gameView.texturesManager.farmTexture[hex.visualDiversityIndex].getTexture(quality);
            case Obj.STRONG_TOWER:
                return gameView.texturesManager.strongTowerTexture.getTexture(quality);
            default:
                return gameView.texturesManager.selectionPixel;
        }
    }


    @Override
    public void disposeTextures() {

    }
}
