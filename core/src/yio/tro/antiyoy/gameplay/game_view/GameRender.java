package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;

public abstract class GameRender {


    protected final GameRendersList gameRendersList;
    protected final float hexViewSize;
    protected GameView gameView;
    protected final SpriteBatch batchMovable;
    protected final GameController gameController;


    public GameRender(GameRendersList gameRendersList) {
        this.gameRendersList = gameRendersList;

        gameView = gameRendersList.gameView;
        batchMovable = gameView.batchMovable;
        gameController = gameView.gameController;
        hexViewSize = gameView.hexViewSize;

        gameRendersList.list.add(this);
    }


    public abstract void loadTextures();


    public abstract void render();


    public abstract void disposeTextures();


    protected TextureRegion loadTextureRegion(String name, boolean antialias) {
        return GraphicsYio.loadTextureRegion(name, antialias);
    }


    protected TextureRegion getBlackPixel() {
        return gameView.texturesManager.blackPixel;
    }


    boolean isPosInViewFrame(PointYio pos, float offset) {
        return gameController.cameraController.isPosInViewFrame(pos, offset);
    }


    protected void renderSolidObject(SpriteBatch spriteBatch, PointYio pos, Hex hex) {
        gameView.rList.renderSolidObjects.renderSolidObject(spriteBatch, pos, hex);
    }


    void renderGradientShadow(SpriteBatch spriteBatch, Hex hex1, Hex hex2) {
        gameView.rList.renderHexLines.renderGradientShadow(spriteBatch, hex1, hex2);
    }


    void renderLineBetweenHexesWithOffset(SpriteBatch spriteBatch, TextureRegion textureRegion, Hex hex1, Hex hex2, double thickness, double offset, int rotation, double factor) {
        gameView.rList.renderHexLines.renderLineBetweenHexesWithOffset(spriteBatch, textureRegion, hex1, hex2, thickness, offset, rotation, factor);
    }


    void renderLineBetweenHexes(SpriteBatch spriteBatch, Hex hex1, Hex hex2, double thickness, int rotation) {
        gameView.rList.renderHexLines.renderLineBetweenHexes(spriteBatch, hex1, hex2, thickness, rotation);
    }
}
