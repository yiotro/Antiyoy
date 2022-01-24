package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.skins.SkinManager;
import yio.tro.antiyoy.stuff.*;


public class GameView {

    public final YioGdxGame yioGdxGame;
    public final GameController gameController;
    public final FactorYio appearFactor;
    private final FrameBuffer frameBuffer;
    SpriteBatch batchMovable, batchSolid;
    float borderLineThickness;
    public float hexViewSize;
    int w, h, currentZoomQuality;
    public OrthographicCamera orthoCam;
    double zoomLevelOne, zoomLevelTwo;
    public GameRendersList rList;
    public GameTexturesManager texturesManager;


    public GameView(YioGdxGame yioGdxGame) { //must be called after creation of GameController and MenuView
        this.yioGdxGame = yioGdxGame;
        gameController = yioGdxGame.gameController;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        appearFactor = new FactorYio();
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        batchMovable = new SpriteBatch();
        batchSolid = yioGdxGame.batch;
        createOrthoCam();
        zoomLevelOne = 0.8;
        zoomLevelTwo = 1.3;
        borderLineThickness = 0.006f * w;
        hexViewSize = 1.04f * gameController.fieldManager.hexSize;
        texturesManager = new GameTexturesManager(this);

        rList = new GameRendersList(this);
        rList.create();

        rList.renderBackgroundCache.performInitialPreparation();
        texturesManager.loadTextures();
    }


    public void createOrthoCam() {
        orthoCam = new OrthographicCamera(yioGdxGame.w, yioGdxGame.h);
        orthoCam.position.set(orthoCam.viewportWidth / 2f, orthoCam.viewportHeight / 2f, 0);
        updateCam();
    }


    public void createLevelCacheTextures() {
        rList.renderBackgroundCache.prepareCacheItems();
    }


    public void onMoreSettingsChanged() {
        texturesManager.onMoreSettingsChanged();
    }


    public SkinManager getSkinManager() {
        return yioGdxGame.skinManager;
    }


    public void updateCacheLevelTextures() {
        rList.renderBackgroundCache.updateFullCache();
    }


    public void updateCacheNearAnimHexes() {
        rList.renderBackgroundCache.updateCacheNearAnimHexes();
    }


    public void updateCam() {
        orthoCam.update();
        batchMovable.setProjectionMatrix(orthoCam.combined);
    }


    public void appear() {
        appearFactor.setValues(0.02, 0);
        appearFactor.appear(2, 1.3);
        updateAnimationTexture();
    }


    public void checkToDestroy() {
        if (yioGdxGame.gamePaused) return;
        if (appearFactor.get() < 1) return;

        destroy();
    }


    public void destroy() {
        appearFactor.destroy(2, 1.5);
        updateAnimationTexture();
    }


    public void updateAnimationTexture() {
        if (!rList.renderBackgroundCache.isCacheAvailable()) return;
        if (DebugFlags.testMode) return;

        frameBuffer.begin();
        batchSolid.begin();
        batchSolid.draw(texturesManager.blackPixel, 0, 0, w, h);
        batchSolid.end();
        renderInternals();
        frameBuffer.end();
        Texture texture = frameBuffer.getColorBufferTexture();
        texturesManager.animationTextureRegion = new TextureRegion(texture);
        texturesManager.animationTextureRegion.flip(false, true);
    }


    public void onAppResume() {
        texturesManager.loadTextures();
    }


    public void onAppPause() {
        texturesManager.disposeTextures();
    }


    private void renderInternals() {
        rList.renderFogOfWar.beginFog();
        batchMovable.begin();
        rList.renderFogOfWar.continueFog();
        rList.renderBackgroundCache.render();
        rList.renderSolidObjects.render();
        rList.renderAnimHexes.render();
        rList.renderSelectedHexes.render();
        rList.renderExclamationMarks.render();
        rList.renderResponseAnimHex.render();
        rList.renderUnits.render();
        rList.renderBlackout.render();
        rList.renderMoveZone.render();
        rList.renderTextHintItems.render();
        rList.renderDiplomaticIndicators.render();
        rList.renderFogOfWar.render();
        rList.renderCityNames.render();
        rList.renderSelectedUnit.render();
        rList.renderDefenseTips.render();
        rList.renderDebug.render();
        rList.renderHighlights.render();
        rList.renderAiData.render();
        renderCurrentTouchMode();

        batchMovable.end();

        rList.renderForefinger.render();
    }


    private void renderCurrentTouchMode() {
        GameRender render = gameController.touchMode.getRender();
        if (render == null) return;

        render.render();
    }


    public void render() {
        if (appearFactor.get() < 0.01) return;

        if (appearFactor.get() < 1) {
            renderTransitionFrame();
        } else {
            if (gameController.backgroundVisible) {
                batchSolid.begin();
                batchSolid.draw(texturesManager.blackPixel, 0, 0, w, h);
                batchSolid.end();
            }
            renderInternals();
        }

        Fonts.gameFont.setColor(Color.WHITE);
        rList.renderSelectionShadows.render();
        rList.renderTip.render();
    }


    private void renderTransitionFrame() {
        batchSolid.begin();
        Color c = batchSolid.getColor();
        float a = c.a;
        float cx = w / 2;
        float cy = h / 2;
        float fw = appearFactor.get() * cx;
        float fh = appearFactor.get() * cy;
        batchSolid.setColor(c.r, c.g, c.b, appearFactor.get());
        batchSolid.draw(texturesManager.getTransitionTexture(), cx - fw, cy - fh, 2 * fw, 2 * fh);
        batchSolid.setColor(c.r, c.g, c.b, a);
        batchSolid.end();
    }


    public void updateCurrentZoomQuality() {
        if (gameController.cameraController.viewZoomLevel < zoomLevelOne) {
            currentZoomQuality = 2;
            return;
        }

        if (gameController.cameraController.viewZoomLevel < zoomLevelTwo) {
            currentZoomQuality = 1;
            return;
        }

        currentZoomQuality = 0;
    }


    public void forceAppearance() {
        appear();
        appearFactor.setValues(1, 0);
    }


    public void moveFactors() {
        appearFactor.move();
    }


    public boolean coversAllScreen() {
        return appearFactor.get() == 1;
    }


    public boolean isInMotion() {
        return appearFactor.get() > 0 && appearFactor.get() < 1;
    }
}
