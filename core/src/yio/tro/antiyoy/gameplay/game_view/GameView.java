package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.stuff.*;

import java.util.ArrayList;


public class GameView {

    private final YioGdxGame yioGdxGame;
    public final GameController gameController;
    private TextureRegion backgroundRegion;
    public final FactorYio factorModel;
    private final FrameBuffer frameBuffer;
    SpriteBatch batchMovable, batchSolid, batchCache;
    float cx, cy, dw, dh, borderLineThickness;
    public TextureRegion blackCircleTexture, exclamationMarkTexture, forefingerTexture;
    TextureRegion animationTextureRegion, blackBorderTexture;
    TextureRegion hexGreen, hexRed, hexBlue, hexYellow, hexCyan, hexColor1, hexColor2, hexColor3;
    public float linkLineThickness, hexViewSize, cacheFrameX1, cacheFrameY1, cacheFrameX2, cacheFrameY2, hexShadowSize;
    public TextureRegion blackPixel, grayPixel, selectionPixel, shadowHexTexture, gradientShadow, transCircle1, transCircle2, selUnitShadow, currentObjectTexture;
    public Storage3xTexture manTextures[], palmTexture, houseTexture, towerTexture, graveTexture, pineTexture;
    public Storage3xTexture castleTexture, strongTowerTexture, farmTexture[];
    int segments, w, h, currentZoomQuality;
    public OrthographicCamera orthoCam, cacheCam;
    TextureRegion cacheLevelTextures[], sideShadow, responseAnimHexTexture, selectionBorder, defenseIcon;
    FrameBuffer frameBufferList[];
    RectangleYio screenRectangle;
    PointYio pos;
    double camBlurSpeed, zoomLevelOne, zoomLevelTwo;
    public GrManager grManager;
    public AtlasLoader atlasLoader;
    private float smFactor;
    private float smDelta;


    public GameView(YioGdxGame yioGdxGame) { //must be called after creation of GameController and MenuView
        this.yioGdxGame = yioGdxGame;
        gameController = yioGdxGame.gameController;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        factorModel = new FactorYio();
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        batchMovable = new SpriteBatch();
        batchSolid = yioGdxGame.batch;
        batchCache = new SpriteBatch();
        createOrthoCam();
        cacheCam = new OrthographicCamera(yioGdxGame.w, yioGdxGame.h);
        cacheCam.position.set(orthoCam.viewportWidth / 2f, orthoCam.viewportHeight / 2f, 0);
        cx = yioGdxGame.w / 2;
        cy = yioGdxGame.h / 2;
        zoomLevelOne = 0.8;
        zoomLevelTwo = 1.3;
        borderLineThickness = 0.006f * w;
        linkLineThickness = 0.01f * Gdx.graphics.getWidth();
        segments = Gdx.graphics.getWidth() / 75;
        if (segments < 12) segments = 12;
        if (segments > 24) segments = 24;
        hexViewSize = 1.04f * gameController.fieldController.hexSize;
        hexShadowSize = 1.00f * hexViewSize;
        initFrameBufferList();
        screenRectangle = new RectangleYio(0, 0, w, h);
        camBlurSpeed = 0.001 * w;
        grManager = new GrManager(this);
        grManager.create();

        loadTextures();
    }


    private void initFrameBufferList() {
        frameBufferList = new FrameBuffer[4];
        for (int i = 0; i < frameBufferList.length; i++) {
            frameBufferList[i] = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        }
    }


    public void createOrthoCam() {
        orthoCam = new OrthographicCamera(yioGdxGame.w, yioGdxGame.h);
        orthoCam.position.set(orthoCam.viewportWidth / 2f, orthoCam.viewportHeight / 2f, 0);
        updateCam();
    }


    public void createLevelCacheTextures() {
        cacheLevelTextures = new TextureRegion[gameController.fieldController.levelSize];
    }


    private void loadTextures() {
        loadBackgroundTexture();
        blackCircleTexture = loadTextureRegion("black_circle.png", true);
        shadowHexTexture = loadTextureRegion("shadow_hex.png", true);
        gradientShadow = loadTextureRegion("gradient_shadow.png", false);
        blackPixel = loadTextureRegion("black_pixel.png", false);
        transCircle1 = loadTextureRegion("transition_circle_1.png", false);
        transCircle2 = loadTextureRegion("transition_circle_2.png", false);
        loadFieldTextures();
        selUnitShadow = loadTextureRegion("sel_shadow.png", true);
        sideShadow = loadTextureRegion("money_shadow.png", true);
        responseAnimHexTexture = loadTextureRegion("response_anim_hex.png", false);
        selectionBorder = loadTextureRegion("selection_border.png", false);
        loadExclamationMark();
        forefingerTexture = loadTextureRegion("forefinger.png", true);
        defenseIcon = loadTextureRegion("defense_icon.png", true);
        blackBorderTexture = loadTextureRegion("pixels/black_border.png", true);
        grayPixel = loadTextureRegion("pixels/gray_pixel.png", false);
        grManager.loadTextures();
    }


    void disposeTextures() {
        backgroundRegion.getTexture().dispose();

        blackCircleTexture.getTexture().dispose();
        shadowHexTexture.getTexture().dispose();
        gradientShadow.getTexture().dispose();
        blackPixel.getTexture().dispose();
        transCircle1.getTexture().dispose();
        transCircle2.getTexture().dispose();
        selUnitShadow.getTexture().dispose();
        sideShadow.getTexture().dispose();
        responseAnimHexTexture.getTexture().dispose();
        selectionBorder.getTexture().dispose();
        forefingerTexture.getTexture().dispose();
        defenseIcon.getTexture().dispose();
        blackBorderTexture.getTexture().dispose();

        atlasLoader.disposeAtlasRegion();
        grManager.disposeTextures();
    }


    private void loadExclamationMark() {
        if (Settings.isShroomArtsEnabled()) {
            exclamationMarkTexture = loadTextureRegion("skins/ant/exclamation_mark.png", true);
            return;
        }

        exclamationMarkTexture = loadTextureRegion("exclamation_mark.png", true);
    }


    public void loadBackgroundTexture() {
        if (Settings.waterTextureChosen) {
            backgroundRegion = loadTextureRegion("game_background_water.png", true);
        } else {
            backgroundRegion = loadTextureRegion("game_background.png", true);
        }
    }


    public void loadSkin(int skin) {
        switch (skin) {
            default:
            case 0: // original
                loadOriginalSkin();
                break;
            case 1: // points
                loadPointsSkin();
                break;
            case 2: // grid
                loadGridSkin();
                break;
        }

        reloadTextures();
    }


    private void reloadTextures() {
        loadFieldTextures();
        loadExclamationMark();

        ButtonYio coinButton = yioGdxGame.menuControllerYio.getCoinButton();
        if (coinButton != null) {
            coinButton.resetTexture();
        }

        resetButtonTexture(38); // tower (build)
        resetButtonTexture(39); // unit (build)
    }


    private void resetButtonTexture(int id) {
        ButtonYio button = yioGdxGame.menuControllerYio.getButtonById(id);
        if (button != null) {
            button.resetTexture();
        }
    }


    private void loadOriginalSkin() {
        hexGreen = loadTextureRegion("hex_green.png", false);
        hexRed = loadTextureRegion("hex_red.png", false);
        hexBlue = loadTextureRegion("hex_blue.png", false);
        hexCyan = loadTextureRegion("hex_cyan.png", false);
        hexYellow = loadTextureRegion("hex_yellow.png", false);
        hexColor1 = loadTextureRegion("hex_color1.png", false);
        hexColor2 = loadTextureRegion("hex_color2.png", false);
        hexColor3 = loadTextureRegion("hex_color3.png", false);
    }


    private void loadPointsSkin() {
        hexGreen = loadTextureRegion("skins/points_hex_green.png", false);
        hexRed = loadTextureRegion("skins/points_hex_red.png", false);
        hexBlue = loadTextureRegion("skins/points_hex_blue.png", false);
        hexCyan = loadTextureRegion("skins/points_hex_cyan.png", false);
        hexYellow = loadTextureRegion("skins/points_hex_yellow.png", false);
        hexColor1 = loadTextureRegion("skins/points_hex_color1.png", false);
        hexColor2 = loadTextureRegion("skins/points_hex_color2.png", false);
        hexColor3 = loadTextureRegion("skins/points_hex_color3.png", false);
    }


    private void loadGridSkin() {
        hexGreen = loadTextureRegion("skins/hex_green_grid.png", false);
        hexRed = loadTextureRegion("skins/hex_red_grid.png", false);
        hexBlue = loadTextureRegion("skins/hex_blue_grid.png", false);
        hexCyan = loadTextureRegion("skins/hex_cyan_grid.png", false);
        hexYellow = loadTextureRegion("skins/hex_yellow_grid.png", false);
        hexColor1 = loadTextureRegion("skins/hex_color1_grid.png", false);
        hexColor2 = loadTextureRegion("skins/hex_color2_grid.png", false);
        hexColor3 = loadTextureRegion("skins/hex_color3_grid.png", false);
    }


    private void loadFieldTextures() {
        atlasLoader = createAtlasLoader();
        selectionPixel = atlasLoader.getTexture("selection_pixel_lowest.png");
        manTextures = new Storage3xTexture[4];
        for (int i = 0; i < 4; i++) {
            manTextures[i] = new Storage3xTexture(atlasLoader, "man" + i + ".png");
        }
        graveTexture = new Storage3xTexture(atlasLoader, "grave.png");
        houseTexture = new Storage3xTexture(atlasLoader, "house.png");
        palmTexture = new Storage3xTexture(atlasLoader, "palm.png");
        pineTexture = new Storage3xTexture(atlasLoader, "pine.png");
        towerTexture = new Storage3xTexture(atlasLoader, "tower.png");
        castleTexture = new Storage3xTexture(atlasLoader, "castle.png");
        farmTexture = new Storage3xTexture[3];
        farmTexture[0] = new Storage3xTexture(atlasLoader, "farm1.png");
        farmTexture[1] = new Storage3xTexture(atlasLoader, "farm2.png");
        farmTexture[2] = new Storage3xTexture(atlasLoader, "farm3.png");
        strongTowerTexture = new Storage3xTexture(atlasLoader, "strong_tower.png");
    }


    private AtlasLoader createAtlasLoader() {
        String path = "field_elements/";
        if (Settings.isShroomArtsEnabled()) {
            path = "skins/ant/field_elements/";
        }

        return new AtlasLoader(path + "atlas_texture.png", path + "atlas_structure.txt", true);
    }


    public static TextureRegion loadTextureRegion(String name, boolean antialias) {
        Texture texture = new Texture(Gdx.files.internal(name));
        if (antialias) texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return new TextureRegion(texture);
    }


    public void updateCacheLevelTextures() {
        gameController.letsUpdateCacheByAnim = false;

        for (int i = 0; i < cacheLevelTextures.length; i++) {
            FrameBuffer cacheLevelFrameBuffer = frameBufferList[i];
            cacheLevelFrameBuffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            switch (i) {
                case 0:
                    cacheCam.position.set(0.5f * w, 0.5f * h, 0);
                    setCacheFrame(0, 0, w, h);
                    break;
                case 1:
                    cacheCam.translate(w, 0);
                    setCacheFrame(w, 0, 2 * w, h);
                    break;
                case 2:
                    cacheCam.translate(-w, h);
                    setCacheFrame(0, h, w, 2 * h);
                    break;
                case 3:
                    cacheCam.translate(w, 0);
                    setCacheFrame(w, h, 2 * w, 2 * h);
                    break;
            }
            cacheCam.update();
            batchCache.setProjectionMatrix(cacheCam.combined);
            renderCache(batchCache);

            Texture texture = cacheLevelFrameBuffer.getColorBufferTexture();
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            cacheLevelTextures[i] = new TextureRegion(texture, w, h);
            cacheLevelTextures[i].flip(false, true);
            cacheLevelFrameBuffer.end();
        }
    }


    public void updateCacheNearAnimHexes() {
        float up, right, down, left;
        ArrayList<Hex> ah = gameController.fieldController.animHexes;
        if (ah.size() == 0) return;
        up = down = ah.get(0).getPos().y;
        left = right = ah.get(0).getPos().x;
        for (int i = 1; i < ah.size(); i++) {
            PointYio tempPos = ah.get(i).getPos();
            if (tempPos.x < left) left = tempPos.x;
            if (tempPos.x > right) right = tempPos.x;
            if (tempPos.y < down) down = tempPos.y;
            if (tempPos.y > up) up = tempPos.y;
        }
        right += hexViewSize;
        left -= hexViewSize;
        up += hexViewSize;
        down -= hexViewSize;
        for (int i = 0; i < cacheLevelTextures.length; i++) {
            switch (i) {
                case 0:
                    cacheCam.position.set(0.5f * w, 0.5f * h, 0);
                    setCacheFrame(0, 0, w, h);
                    if (left > w || down > h) continue;
                    break;
                case 1:
                    cacheCam.translate(w, 0);
                    setCacheFrame(w, 0, 2 * w, h);
                    if (right < w || down > h) continue;
                    break;
                case 2:
                    cacheCam.translate(-w, h);
                    setCacheFrame(0, h, w, 2 * h);
                    if (left > w || up < h) continue;
                    break;
                case 3:
                    cacheCam.translate(w, 0);
                    setCacheFrame(w, h, 2 * w, 2 * h);
                    if (right < w || up < h) continue;
                    break;
            }
            FrameBuffer frameBuffer = frameBufferList[i];
            frameBuffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            cacheCam.update();
            batchCache.setProjectionMatrix(cacheCam.combined);
            renderCache(batchCache);

            Texture texture = frameBuffer.getColorBufferTexture();
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            cacheLevelTextures[i] = new TextureRegion(texture, w, h);
            cacheLevelTextures[i].flip(false, true);
            frameBuffer.end();
        }
    }


    private void setCacheFrame(float x1, float y1, float x2, float y2) {
        cacheFrameX1 = x1;
        cacheFrameY1 = y1;
        cacheFrameX2 = x2;
        cacheFrameY2 = y2;
    }


    public void updateCam() {
        orthoCam.update();
        batchMovable.setProjectionMatrix(orthoCam.combined);
    }


    private void renderCache(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(backgroundRegion, 0, 0, 2 * yioGdxGame.w, 2 * yioGdxGame.h);
        int actualZoomQuality = currentZoomQuality;
        currentZoomQuality = 2;
        renderHexField(spriteBatch);
        currentZoomQuality = actualZoomQuality;
        spriteBatch.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }


    private void renderHexField(SpriteBatch spriteBatch) {
        TextureRegion currentHexTexture;

        // shadows
        for (Hex hex : gameController.fieldController.activeHexes) {
            pos = hex.getPos();
            if (!isPosInCacheFrame(pos, hexViewSize)) continue;

            spriteBatch.draw(shadowHexTexture, pos.x - hexViewSize + 0.1f * hexViewSize, pos.y - hexViewSize - 0.15f * hexViewSize, 2 * hexViewSize, 2 * hexViewSize);
        }

        // hexes
        for (Hex hex : gameController.fieldController.activeHexes) {
            pos = hex.getPos();
            if (!isPosInCacheFrame(pos, hexViewSize)) continue;

            currentHexTexture = getHexTextureByColor(hex.colorIndex);
            spriteBatch.draw(currentHexTexture, pos.x - 0.99f * hexViewSize, pos.y - 0.99f * hexViewSize, 2 * 0.99f * hexViewSize, 2 * 0.99f * hexViewSize);
        }

        // lines between hexes
        for (Hex hex : gameController.fieldController.activeHexes) {
            pos = hex.getPos();
            if (!isPosInCacheFrame(pos, hexViewSize)) continue;

            for (int i = 0; i < 6; i++) {
                Hex adjacentHex = hex.getAdjacentHex(i);
                if (adjacentHex == gameController.fieldController.nullHex) continue;

                if (adjacentHex != null && ((adjacentHex.active && !adjacentHex.sameColor(hex) && i >= 2 && i <= 4) || !adjacentHex.active)) {
                    if (i >= 2 && i <= 4) {
                        renderGradientShadow(hex, adjacentHex, spriteBatch);
                    }
                    renderLineBetweenHexes(adjacentHex, hex, spriteBatch, borderLineThickness, i);
                }
            }
        }

        // solid objects
        for (Hex hex : gameController.fieldController.solidObjects) {
            renderSolidObject(spriteBatch, hex.getPos(), hex);
        }
    }


    private void renderCertainUnits() {
        for (Unit unit : gameController.unitList) {
            if (isPosInViewFrame(unit.currentPos, hexViewSize)) {
                renderUnit(batchMovable, unit);
            }
        }
    }


    private TextureRegion getUnitTexture(Unit unit) {
        if (!gameController.isPlayerTurn() && unit.moveFactor.get() < 1 && unit.moveFactor.get() > 0.1) {
            return manTextures[unit.strength - 1].getLowest();
        }
        return manTextures[unit.strength - 1].getTexture(currentZoomQuality);
    }


    void renderUnit(SpriteBatch spriteBatch, Unit unit) {
        PointYio pos = unit.currentPos;
        spriteBatch.draw(getUnitTexture(unit), pos.x - 0.7f * hexViewSize, pos.y - 0.5f * hexViewSize + unit.jumpPos * hexViewSize, 1.4f * hexViewSize, 1.6f * hexViewSize);
    }


    private TextureRegion getSolidObjectTexture(Hex hex, int quality) {
        switch (hex.objectInside) {
            case Obj.GRAVE:
                return graveTexture.getTexture(quality);
            case Obj.TOWN:
                if (GameRules.slayRules) return houseTexture.getTexture(quality);
                return castleTexture.getTexture(quality);
            case Obj.PALM:
                return palmTexture.getTexture(quality);
            case Obj.PINE:
                return pineTexture.getTexture(quality);
            case Obj.TOWER:
                return towerTexture.getTexture(quality);
            case Obj.FARM:
                return farmTexture[hex.viewDiversityIndex].getTexture(quality);
            case Obj.STRONG_TOWER:
                return strongTowerTexture.getTexture(quality);
            default:
                return selectionPixel;
        }
    }


    private TextureRegion getSolidObjectTexture(Hex hex) {
        return getSolidObjectTexture(hex, currentZoomQuality);
    }


    void renderSolidObject(SpriteBatch spriteBatch, PointYio pos, Hex hex) {
        currentObjectTexture = getSolidObjectTexture(hex);
        spriteBatch.draw(currentObjectTexture, pos.x - 0.7f * hexViewSize, pos.y - 0.5f * hexViewSize, 1.4f * hexViewSize, 1.6f * hexViewSize);
    }


    private void renderGradientShadow(Hex hex1, Hex hex2, SpriteBatch spriteBatch) {
        double a = Yio.angle(hex1.pos.x, hex1.pos.y, hex2.pos.x, hex2.pos.y);
        double cx = 0.5 * (hex1.pos.x + hex2.pos.x);
        double cy = 0.5 * (hex1.pos.y + hex2.pos.y);
        double s = 0.5 * gameController.fieldController.hexSize;
        cx -= 0.2 * s * Math.cos(a);
        cy -= 0.2 * s * Math.sin(a);
        a += 0.5 * Math.PI;
        drawLine(cx + s * Math.cos(a), cy + s * Math.sin(a), cx - s * Math.cos(a), cy - s * Math.sin(a), 0.01 * w, spriteBatch, gradientShadow);
    }


    void renderLineBetweenHexesWithOffset(Hex hex1, Hex hex2, SpriteBatch spriteBatch, double thickness, TextureRegion textureRegion, double offset, int rotation, double factor) {
        double a = Yio.angle(hex1.pos.x, hex1.pos.y, hex2.pos.x, hex2.pos.y);
        double a2 = a + 0.5 * Math.PI;
        double cx = 0.5 * (hex1.pos.x + hex2.pos.x);
        double cy = 0.5 * (hex1.pos.y + hex2.pos.y);
        double s = 0.5 * gameController.fieldController.hexSize * (0.7 + 0.37 * factor);
        drawSpecialHexedLine(cx + offset * Math.cos(a) + s * Math.cos(a2), cy + offset * Math.sin(a) + s * Math.sin(a2), cx + offset * Math.cos(a) - s * Math.cos(a2), cy + offset * Math.sin(a) - s * Math.sin(a2), thickness, spriteBatch, textureRegion, rotation);
    }


    void renderLineBetweenHexes(Hex hex1, Hex hex2, SpriteBatch spriteBatch, double thickness, int rotation) {
        double a = Yio.angle(hex1.pos.x, hex1.pos.y, hex2.pos.x, hex2.pos.y);
        a += 0.5 * Math.PI;
        double cx = 0.5 * (hex1.pos.x + hex2.pos.x);
        double cy = 0.5 * (hex1.pos.y + hex2.pos.y);
        double s = 0.5 * gameController.fieldController.hexSize;
        drawSpecialHexedLine(cx + s * Math.cos(a), cy + s * Math.sin(a), cx - s * Math.cos(a), cy - s * Math.sin(a), thickness, spriteBatch, blackBorderTexture, rotation + 3);
    }


    private void drawSpecialHexedLine(double x1, double y1, double x2, double y2, double thickness, SpriteBatch spriteBatch, TextureRegion blackPixel, int rotation) {
        spriteBatch.draw(blackPixel, (float) x1, (float) (y1 - thickness * 0.5), 0f, (float) thickness * 0.5f, (float) Yio.distance(x1, y1, x2, y2), (float) thickness, 1f, 1f, (float) (180 * (-rotation / 3d)));
    }


    public void beginSpawnProcess() {
        factorModel.setValues(0.02, 0);
        factorModel.appear(2, 1.3); // 3, 0.8
        updateAnimationTexture();
    }


    public void beginDestroyProcess() {
        if (yioGdxGame.gamePaused) return;
        if (factorModel.get() >= 1) {
            factorModel.setValues(1, 0);
            factorModel.destroy(2, 1.5); // 1, 5
        }
        updateAnimationTexture();
    }


    public void updateAnimationTexture() {
        frameBuffer.begin();
        batchSolid.begin();
        batchSolid.draw(blackPixel, 0, 0, w, h);
        batchSolid.end();
        renderInternals();
        frameBuffer.end();
        Texture texture = frameBuffer.getColorBufferTexture();
        animationTextureRegion = new TextureRegion(texture);
        animationTextureRegion.flip(false, true);
    }


    private boolean isPosInCacheFrame(PointYio pos, float offset) {
        if (pos.x < cacheFrameX1 - offset) return false;
        if (pos.x > cacheFrameX2 + offset) return false;
        if (pos.y < cacheFrameY1 - offset) return false;
        if (pos.y > cacheFrameY2 + offset) return false;
        return true;
    }


    boolean isPosInViewFrame(PointYio pos, float offset) {
        return gameController.cameraController.isPosInViewFrame(pos, offset);
    }


    TextureRegion getHexTextureByColor(int colorIndex) {
        if (gameController.colorIndexViewOffset > 0 && colorIndex != FieldController.NEUTRAL_LANDS_INDEX) {
            colorIndex = gameController.getColorIndexWithOffset(colorIndex);
        }
        switch (colorIndex) {
            default:
            case 0:
                return hexGreen;
            case 1:
                return hexRed;
            case 2:
                return hexBlue;
            case 3:
                return hexCyan;
            case 4:
                return hexYellow;
            case 5:
                return hexColor1;
            case 6:
                return hexColor2;
            case 7:
                return hexColor3;
        }
    }


    private void renderAllSolidObjects() {
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (activeHex.containsObject())
                renderSolidObject(batchMovable, activeHex.getPos(), activeHex);
        }
    }


    private void renderAnimHexes() {
        PointYio pos;
        TextureRegion currentHexLastTexture, currentHexTexture;
        Color c = batchMovable.getColor();

        for (Hex hex : gameController.fieldController.animHexes) {
            pos = hex.getPos();
            if (!isPosInViewFrame(pos, hexViewSize)) continue;

            if (hex.animFactor.get() < 1) {
                currentHexLastTexture = getHexTextureByColor(hex.lastColorIndex);
                batchMovable.setColor(c.r, c.g, c.b, 1f - hex.animFactor.get());
                batchMovable.draw(currentHexLastTexture, pos.x - hexViewSize, pos.y - hexViewSize, 2 * hexViewSize, 2 * hexViewSize);
            }
            currentHexTexture = getHexTextureByColor(hex.colorIndex);
            batchMovable.setColor(c.r, c.g, c.b, hex.animFactor.get());
            batchMovable.draw(currentHexTexture, pos.x - hexViewSize, pos.y - hexViewSize, 2 * hexViewSize, 2 * hexViewSize);
        }

        batchMovable.setColor(c.r, c.g, c.b, 1);
        for (Hex hex : gameController.fieldController.animHexes) {
            pos = hex.getPos();
            if (!isPosInViewFrame(pos, hexViewSize)) continue;
            for (int i = 0; i < 6; i++) {
                Hex adjacentHex = hex.getAdjacentHex(i);
                if (adjacentHex != null && (!adjacentHex.active || !adjacentHex.sameColor(hex))) {
                    if (i >= 2 && i <= 4) renderGradientShadow(hex, adjacentHex, batchMovable);
                    renderLineBetweenHexes(adjacentHex, hex, batchMovable, borderLineThickness, i);
                }
            }
            if (hex.containsObject()) {
                batchMovable.setColor(c.r, c.g, c.b, 1);
                renderSolidObject(batchMovable, pos, hex);
            }
        }
        batchMovable.setColor(c.r, c.g, c.b, 1);
    }


    public static void drawFromCenter(SpriteBatch batch, TextureRegion textureRegion, double cx, double cy, double r) {
        batch.draw(textureRegion, (float) (cx - r), (float) (cy - r), (float) (2d * r), (float) (2d * r));
    }


    private static void drawFromCenterRotated(Batch batch, TextureRegion textureRegion, double cx, double cy, double r, double rotationAngle) {
        batch.draw(textureRegion, (float) (cx - r), (float) (cy - r), (float) r, (float) r, (float) (2d * r), (float) (2d * r), 1, 1, 57.29f * (float) rotationAngle);
    }


    void renderResponseAnimHex() {
        if (gameController.fieldController.responseAnimHex != null) {
            pos = gameController.fieldController.responseAnimHex.getPos();
            Color c = batchMovable.getColor();
            batchMovable.setColor(c.r, c.g, c.b, 0.5f * Math.min(gameController.fieldController.responseAnimFactor.get(), 1));
            float s = Math.max(hexViewSize, hexViewSize * gameController.fieldController.responseAnimFactor.get());
            batchMovable.draw(responseAnimHexTexture, pos.x - s, pos.y - s, 2 * s, 2 * s);
            batchMovable.setColor(c.r, c.g, c.b, c.a);
        }
    }


    private void renderExclamationMarks() {
        if (!gameController.isPlayerTurn()) return;

        for (Province province : gameController.fieldController.provinces) {
            if (gameController.isCurrentTurn(province.getColor()) && province.money >= GameRules.PRICE_UNIT) {
                Hex capitalHex = province.getCapital();
                PointYio pos = capitalHex.getPos();
                if (!isPosInViewFrame(pos, hexViewSize)) continue;
                batchMovable.draw(exclamationMarkTexture, pos.x - 0.5f * hexViewSize, pos.y + 0.3f * hexViewSize + gameController.jumperUnit.jumpPos * hexViewSize, 0.35f * hexViewSize, 0.6f * hexViewSize);
            }
        }
    }


    public void onResume() {
        loadTextures();
    }


    public void onPause() {
        disposeTextures();
    }


    private void renderSelectedHexes() {
        for (Hex hex : gameController.fieldController.selectedHexes) {
            if (hex.selectionFactor.get() < 0.01) continue;
            for (int i = 0; i < 6; i++) {
                Hex h = hex.getAdjacentHex(i);
                if (h != null && !h.isNullHex() && (!h.active || !h.sameColor(hex)))
                    renderLineBetweenHexesWithOffset(hex, h, batchMovable, hex.selectionFactor.get() * 0.01 * w, selectionBorder, -(1d - hex.selectionFactor.get()) * 0.01 * w, i, hex.selectionFactor.get());
            }
        }

        for (Hex hex : gameController.fieldController.selectedHexes) {
            if (hex.containsObject()) {
                renderSolidObject(batchMovable, hex.getPos(), hex);
            }
        }
    }


    private void renderTextOnHex(Hex hex, String text) {
        Fonts.gameFont.draw(batchMovable, text, hex.pos.x - 0.02f * w, hex.pos.y + 0.02f * w);
    }


    private void renderForefinger() {
        if (!GameRules.tutorialMode) return;

        if (gameController.forefinger.isPointingToHex()) {
            batchMovable.begin();
            pos = gameController.forefinger.animPos;
            Color c = batchMovable.getColor();
            batchMovable.setColor(c.r, c.g, c.b, gameController.forefinger.getAlpha());
            drawFromCenterRotated(batchMovable, forefingerTexture, pos.x, pos.y, hexViewSize * gameController.forefinger.getSize(), gameController.forefinger.getRotation());
            batchMovable.setColor(c.r, c.g, c.b, c.a);
            batchMovable.end();
        } else {
            batchSolid.begin();
            pos = gameController.forefinger.animPos;
            Color c = batchSolid.getColor();
            batchSolid.setColor(c.r, c.g, c.b, gameController.forefinger.getAlpha());
            drawFromCenterRotated(batchSolid, forefingerTexture, pos.x, pos.y, hexViewSize * gameController.forefinger.getSize(), gameController.forefinger.getRotation());
            batchSolid.setColor(c.r, c.g, c.b, c.a);
            batchSolid.end();
        }
    }


    private void renderSelectedUnit() {
        PointYio pos;
        if (gameController.selectionController.selectedUnit != null) {
            pos = gameController.selectionController.selectedUnit.currentPos;
            float ar = 0.35f * hexViewSize * gameController.selectionController.selUnitFactor.get();
            batchMovable.draw(selUnitShadow, pos.x - 0.7f * hexViewSize - 2 * ar, pos.y - 0.6f * hexViewSize - 2 * ar, 1.4f * hexViewSize + 4 * ar, 1.6f * hexViewSize + 4 * ar);
            batchMovable.draw(manTextures[gameController.selectionController.selectedUnit.strength - 1].getNormal(), pos.x - 0.7f * hexViewSize - ar, pos.y - 0.6f * hexViewSize - ar, 1.4f * hexViewSize + 2 * ar, 1.6f * hexViewSize + 2 * ar);
        }
    }


    private void renderBlackout() {
        if (gameController.fieldController.moveZoneManager.appearFactor.get() < 0.01) return;

        Color c = batchMovable.getColor();
        batchMovable.setColor(c.r, c.g, c.b, 0.5f * gameController.selectionController.getBlackoutFactor().get());
        GraphicsYio.drawByRectangle(batchMovable, blackPixel, gameController.cameraController.frame);
        batchMovable.setColor(c.r, c.g, c.b, c.a);
    }


    private void renderCacheLevelTextures() {
        batchMovable.draw(cacheLevelTextures[0], 0, 0);
        if (gameController.fieldController.levelSize >= FieldController.SIZE_MEDIUM) {
            batchMovable.draw(cacheLevelTextures[1], w, 0);
            if (gameController.fieldController.levelSize >= FieldController.SIZE_BIG) {
                batchMovable.draw(cacheLevelTextures[2], 0, h);
                batchMovable.draw(cacheLevelTextures[3], w, h);
            }
        }
    }


    private void renderDefenseTips() {
        float f = gameController.fieldController.defenseTipFactor.get();
        if (f == 0) return;

        ArrayList<Hex> defenseTips = gameController.fieldController.defenseTips;
        if (defenseTips.size() == 0) return;

        Color c = batchMovable.getColor();
        batchMovable.setColor(c.r, c.g, c.b, f);
        float x, y, size;
        for (Hex defenseTip : defenseTips) {
            PointYio tipPos = defenseTip.getPos();
            PointYio cPos;

            Hex defSrcHex = gameController.selectionController.getDefSrcHex(defenseTip);
            if (defSrcHex != null) {
                cPos = defSrcHex.getPos();
            } else {
                cPos = gameController.fieldController.defTipHex.getPos();
            }

            if (gameController.fieldController.defenseTipFactor.getDy() >= 0) {
                x = cPos.x + f * (tipPos.x - cPos.x);
                y = cPos.y + f * (tipPos.y - cPos.y);
                size = (0.5f + 0.1f * f) * hexViewSize;
            } else {
                x = tipPos.x;
                y = tipPos.y;
                size = (0.7f - 0.1f * f) * hexViewSize;
            }
            drawFromCenter(batchMovable, defenseIcon, x, y, size);
        }
        batchMovable.setColor(c.r, c.g, c.b, c.a);
    }


    private void renderInternals() {
        grManager.renderFogOfWar.beginFog();
        batchMovable.begin();
        grManager.renderFogOfWar.continueFog();
        renderCacheLevelTextures();
        if (YioGdxGame.isScreenVerySmall()) {
            renderAllSolidObjects();
        }

        renderAnimHexes();
        renderSelectedHexes();
        renderExclamationMarks();
        renderResponseAnimHex();
        renderCertainUnits();
        renderBlackout();
        grManager.renderMoveZone.render();

        grManager.render(); // disable fog

        grManager.renderCityNames.render();
        renderSelectedUnit();
        renderDefenseTips();

        batchMovable.end();

        renderForefinger();
    }


    public void render() {
        if (factorModel.get() < 0.01) {
            return;
        } else if (factorModel.get() < 1) {
            renderTransitionFrame();
        } else {
            if (gameController.backgroundVisible) {
                batchSolid.begin();
                batchSolid.draw(blackPixel, 0, 0, w, h);
                batchSolid.end();
            }
            renderInternals();
        }

        Fonts.gameFont.setColor(Color.WHITE);
        renderMoney();
        renderTip();
    }


    private void renderMoney() {
        smFactor = gameController.selectionController.getSelMoneyFactor().get();
        smDelta = 0.1f * h * (1 - smFactor);
        if (smFactor > 0) {
            batchSolid.begin();
            batchSolid.draw(sideShadow, w, h + smDelta, 0, 0, w, 0.1f * h, 1, 1, 180);
            batchSolid.draw(sideShadow, 0, -smDelta + 0, w, 0.1f * h);
            Fonts.gameFont.draw(batchSolid, "" + gameController.fieldController.selectedProvinceMoney, 0.12f * w, (1.08f - 0.1f * smFactor) * h);
            Fonts.gameFont.draw(batchSolid, gameController.balanceString, 0.47f * w, (1.08f - 0.1f * smFactor) * h);
            batchSolid.end();
        }
    }


    private void renderTransitionFrame() {
        batchSolid.begin();
        Color c = batchSolid.getColor();
        float cx = w / 2;
        float cy = h / 2;
        float fw = factorModel.get() * cx;
        float fh = factorModel.get() * cy;
        batchSolid.setColor(c.r, c.g, c.b, factorModel.get());
        batchSolid.draw(getTransitionTexture(), cx - fw, cy - fh, 2 * fw, 2 * fh);
        batchSolid.setColor(c.r, c.g, c.b, c.a);
        batchSolid.end();
    }


    private TextureRegion getTransitionTexture() {
        if (animationTextureRegion != null) {
            return animationTextureRegion;
        } else {
            return backgroundRegion;
        }
    }


    private void renderTip() {
        if (gameController.selectionController.tipFactor.get() <= 0.01) return;

        batchSolid.begin();
        float s = 0.2f * w;

        batchSolid.draw(
                getTipTypeTexture(gameController.selectionController.tipShowType),
                0.5f * w - 0.5f * s,
                getTipVerticalPos(s), s, s
        );

        Fonts.gameFont.draw(batchSolid,
                gameController.currentPriceString,
                0.5f * w - 0.5f * gameController.priceStringWidth,
                getTipVerticalPos(s)
        );

        batchSolid.end();
    }


    private float getTipVerticalPos(float s) {
        if (Settings.fastConstructionEnabled) {
            return s * (gameController.selectionController.tipFactor.get() - 1) + 0.12f * h;
        } else {
            return s * (gameController.selectionController.tipFactor.get() - 1) + 0.04f * h;
        }
    }


    private TextureRegion getTipTypeTexture(int tipShowType) {
        switch (tipShowType) {
            default:
            case SelectionController.TIP_INDEX_TOWER:
                return towerTexture.getNormal();
            case SelectionController.TIP_INDEX_FARM:
                return farmTexture[0].getNormal();
            case SelectionController.TIP_INDEX_STRONG_TOWER:
                return strongTowerTexture.getNormal();
            case SelectionController.TIP_INDEX_TREE:
                return pineTexture.getNormal();
            case SelectionController.TIP_INDEX_UNIT_1:
                return manTextures[0].getNormal();
            case SelectionController.TIP_INDEX_UNIT_2:
                return manTextures[1].getNormal();
            case SelectionController.TIP_INDEX_UNIT_3:
                return manTextures[2].getNormal();
            case SelectionController.TIP_INDEX_UNIT_4:
                return manTextures[3].getNormal();
        }
    }


    public void moveInsideStuff() {
        if (gameController.cameraController.viewZoomLevel < zoomLevelOne) {
            currentZoomQuality = 2;
        } else if (gameController.cameraController.viewZoomLevel < zoomLevelTwo) {
            currentZoomQuality = 1;
        } else {
            currentZoomQuality = 0;
        }
    }


    public void moveFactors() {
        factorModel.move();
    }


    private static void drawLine(double x1, double y1, double x2, double y2, double thickness, SpriteBatch spriteBatch, TextureRegion blackPixel) {
        spriteBatch.draw(blackPixel, (float) x1, (float) (y1 - thickness * 0.5), 0f, (float) thickness * 0.5f, (float) Yio.distance(x1, y1, x2, y2), (float) thickness, 1f, 1f, (float) (180 / Math.PI * Yio.angle(x1, y1, x2, y2)));
    }


    public boolean coversAllScreen() {
        return factorModel.get() == 1;
    }


    public boolean isInMotion() {
        return factorModel.get() > 0 && factorModel.get() < 1;
    }
}
