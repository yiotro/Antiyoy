package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.LevelSize;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class RenderBackgroundCache extends GameRender {

    ArrayList<CacheItem> items;
    public OrthographicCamera cacheCam;
    SpriteBatch batchCache;
    PointYio pos;
    RectangleYio cacheFrame;
    RectangleYio animBounds;
    ObjectPoolYio<CacheItem> poolItems;
    PointYio tempPoint;
    public boolean updateAllowed;


    public RenderBackgroundCache(GameRendersList gameRendersList) {
        super(gameRendersList);

        cacheCam = new OrthographicCamera(GraphicsYio.width, GraphicsYio.height);
        cacheCam.position.set(gameView.orthoCam.viewportWidth / 2f, gameView.orthoCam.viewportHeight / 2f, 0);
        batchCache = new SpriteBatch();
        cacheFrame = new RectangleYio();
        animBounds = new RectangleYio();
        items = new ArrayList<>();
        tempPoint = new PointYio();
        updateAllowed = true;

        initPools();
    }


    private void initPools() {
        poolItems = new ObjectPoolYio<CacheItem>() {
            @Override
            public CacheItem makeNewObject() {
                return new CacheItem();
            }
        };
    }


    @Override
    public void loadTextures() {

    }


    public void performInitialPreparation() {
        // this may help to avoid bug with frame buffers
        for (int i = 0; i < LevelSize.HUGE; i++) {
            addCacheItem(0, 0);
        }
        clearItems();
    }


    public void prepareCacheItems() {
        clearItems();

        addCacheItem(0, 0);
        if (gameController.levelSizeManager.levelSize < LevelSize.MEDIUM) return;

        addCacheItem(1, 0);
        if (gameController.levelSizeManager.levelSize < LevelSize.BIG) return;

        addCacheItem(0, 1);
        addCacheItem(1, 1);
        if (gameController.levelSizeManager.levelSize < LevelSize.HUGE) return;

        addCacheItem(0, 2);
        addCacheItem(1, 2);
        addCacheItem(2, 2);
        addCacheItem(2, 1);
        addCacheItem(2, 0);
    }


    private void clearItems() {
        for (CacheItem item : items) {
            poolItems.addWithCheck(item);
        }

        items.clear();
    }


    private void addCacheItem(int xIndex, int yIndex) {
        CacheItem next = poolItems.getNext();

        next.position.set(
                xIndex * GraphicsYio.width,
                yIndex * GraphicsYio.height,
                GraphicsYio.width,
                GraphicsYio.height
        );

        items.add(next);
    }


    @Override
    public void render() {
        for (CacheItem item : items) {
            GraphicsYio.drawByRectangle(
                    batchMovable,
                    item.textureRegion,
                    item.position
            );
        }
    }


    void updateCacheNearAnimHexes() {
        if (DebugFlags.testMode) return;
        if (!isThereAtLeastOneAnimHex()) return;
        if (!isUpdateAllowed()) return;

        updateAnimBounds();

        cacheCam.position.set(0.5f * GraphicsYio.width, 0.5f * GraphicsYio.height, 0);
        for (CacheItem item : items) {
            tempPoint.set(item.position.x + item.position.width / 2, item.position.y + item.position.height / 2); // current center
            cacheCam.translate(tempPoint.x - cacheCam.position.x, tempPoint.y - cacheCam.position.y);
            cacheFrame.setBy(item.position);
            if (!cacheFrame.isInCollisionWith(animBounds)) continue;

            FrameBuffer frameBuffer = item.frameBuffer;
            if (frameBuffer == null) continue;
            frameBuffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            cacheCam.update();
            batchCache.setProjectionMatrix(cacheCam.combined);
            renderCache();

            applyFrameBuffer(item, frameBuffer);
            frameBuffer.end();
        }
    }


    private void applyFrameBuffer(CacheItem item, FrameBuffer frameBuffer) {
        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        item.textureRegion = new TextureRegion(texture, (int) GraphicsYio.width, (int) GraphicsYio.height);
        item.textureRegion.flip(false, true);
    }


    private boolean isThereAtLeastOneAnimHex() {
        return gameController.fieldManager.animHexes.size() > 0;
    }


    private void updateAnimBounds() {
        ArrayList<Hex> animHexes = gameController.fieldManager.animHexes;
        float up, right, down, left;
        up = down = animHexes.get(0).getPos().y;
        left = right = animHexes.get(0).getPos().x;

        for (int i = 1; i < animHexes.size(); i++) {
            PointYio tempPos = animHexes.get(i).getPos();
            if (tempPos.x < left) left = tempPos.x;
            if (tempPos.x > right) right = tempPos.x;
            if (tempPos.y < down) down = tempPos.y;
            if (tempPos.y > up) up = tempPos.y;
        }

        right += hexViewSize;
        left -= hexViewSize;
        up += hexViewSize;
        down -= hexViewSize;

        animBounds.set(left, down, right - left, up - down);
    }


    private boolean isPosInCacheFrame(PointYio pos, float offset) {
        return cacheFrame.isPointInside(pos, offset);
    }


    public void updateFullCache() {
        if (DebugFlags.testMode) return;
        if (!isUpdateAllowed()) return;
        gameController.letsUpdateCacheByAnim = false;

        cacheCam.position.set(0.5f * GraphicsYio.width, 0.5f * GraphicsYio.height, 0);
        for (CacheItem item : items) {
            FrameBuffer frameBuffer = item.frameBuffer;
            frameBuffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            tempPoint.set(item.position.x + item.position.width / 2, item.position.y + item.position.height / 2); // current center
            cacheCam.translate(tempPoint.x - cacheCam.position.x, tempPoint.y - cacheCam.position.y);
            cacheFrame.setBy(item.position);

            cacheCam.update();
            batchCache.setProjectionMatrix(cacheCam.combined);
            renderCache();

            applyFrameBuffer(item, frameBuffer);
            frameBuffer.end();
        }
    }


    private void renderCache() {
        batchCache.begin();
        renderBackground();
        int actualZoomQuality = gameView.currentZoomQuality;
        gameView.currentZoomQuality = 2;
        renderHexField();
        gameView.currentZoomQuality = actualZoomQuality;
        batchCache.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }


    private void renderBackground() {
        batchCache.draw(gameView.texturesManager.backgroundRegion, 0, 0, 3 * GraphicsYio.width, 3 * GraphicsYio.height);
    }


    private void renderHexField() {
        renderShadows();
        renderHexes();
        renderLinesBetweenHexes();
        renderSolidObjects();
    }


    private void renderShadows() {
        for (Hex hex : gameController.fieldManager.activeHexes) {
            pos = hex.getPos();
            if (!isPosInCacheFrame(pos, hexViewSize)) continue;

            batchCache.draw(gameView.texturesManager.shadowHexTexture, pos.x - hexViewSize + 0.1f * hexViewSize, pos.y - hexViewSize - 0.15f * hexViewSize, 2 * hexViewSize, 2 * hexViewSize);
        }
    }


    private void renderHexes() {
        TextureRegion currentHexTexture;
        GameTexturesManager texturesManager = gameView.texturesManager;
        for (Hex hex : gameController.fieldManager.activeHexes) {
            pos = hex.getPos();
            if (!isPosInCacheFrame(pos, hexViewSize)) continue;

            currentHexTexture = texturesManager.getHexTextureByFraction(hex.fraction);
            batchCache.draw(currentHexTexture, pos.x - 0.99f * hexViewSize, pos.y - 0.99f * hexViewSize, 2 * 0.99f * hexViewSize, 2 * 0.99f * hexViewSize);
        }
    }


    private void renderLinesBetweenHexes() {
        for (Hex hex : gameController.fieldManager.activeHexes) {
            pos = hex.getPos();
            if (!isPosInCacheFrame(pos, hexViewSize)) continue;

            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (adjacentHex == gameController.fieldManager.nullHex) continue;
                if (adjacentHex == null) continue;
                if (!isLineBetweenHexesNeeded(hex, dir, adjacentHex)) continue;

                if (isDirectionDown(dir)) {
                    renderGradientShadow(batchCache, hex, adjacentHex);
                }
                renderLineBetweenHexes(batchCache, adjacentHex, hex, gameView.borderLineThickness, dir);
            }
        }
    }


    private boolean isLineBetweenHexesNeeded(Hex hex, int dir, Hex adjacentHex) {
        if (!adjacentHex.active) return true;
        if (!adjacentHex.sameFraction(hex) && isDirectionDown(dir)) return true;
        return false;
    }


    private boolean isDirectionDown(int i) {
        return i == 2 || i == 3 || i == 4;
    }


    private void renderSolidObjects() {
        for (Hex hex : gameController.fieldManager.solidObjects) {
            renderSolidObject(batchCache, hex.getPos(), hex);
        }
    }


    public boolean isCacheAvailable() {
        return items.size() > 0;
    }


    @Override
    public void disposeTextures() {

    }


    public boolean isUpdateAllowed() {
        return updateAllowed;
    }


    public void setUpdateAllowed(boolean updateAllowed) {
        this.updateAllowed = updateAllowed;
    }
}
