package yio.tro.antiyoy;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.ai.ArtificialIntelligence;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.*;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.StringTokenizer;

import static yio.tro.antiyoy.GameController.slay_rules;

public class YioGdxGame extends ApplicationAdapter implements InputProcessor {
    public SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    public int w, h;
    public MenuControllerYio menuControllerYio;
    private MenuViewYio menuViewYio;
    public static BitmapFont buttonFont, gameFont, listFont, cityFont;
    private static GlyphLayout glyphLayout = new GlyphLayout();
    public static final String SPECIAL_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<^>";
    public static int FONT_SIZE;
    public static boolean ANDROID = false;
    public static final int INDEX_OF_LAST_LEVEL = 70; // with tutorial
    TextureRegion mainBackground, infoBackground, settingsBackground, pauseBackground;
    TextureRegion currentBackground, lastBackground, splatTexture;
    public static float screenRatio;
    public GameController gameController;
    public GameView gameView;
    boolean gamePaused, readyToUnPause;
    private long timeToUnPause;
    private int frameSkipCount;
    private FrameBuffer frameBuffer;
    private FactorYio transitionFactor, splatTransparencyFactor;
    private ArrayList<Splat> splats;
    private long timeToSpawnNextSplat;
    private float splatSize;
    private int currentSplatIndex;
    public static final Random random = new Random();
    private long lastTimeButtonPressed;
    private boolean alreadyShownErrorMessageOnce;
    private int fps, currentFrameCount;
    long timeToUpdateFpsInfo;
    private int currentBackgroundIndex;
    long timeWhenPauseStarted, timeForFireworkExplosion, timeToHideSplats;
    public int currentBubbleIndex, selectedLevelIndex, splashCount;
    public float defaultBubbleRadius, pressX, pressY, animX, animY, animRadius;
    double bubbleGravity;
    boolean ignoreNextTimeCorrection;
    boolean loadedResources;
    boolean ignoreDrag;
    boolean needToHideSplats;
    public boolean simpleTransitionAnimation, useMenuMasks;
    TextureRegion splash;
    ArrayList<Float> debugValues;
    ArrayList<Integer> backButtonIds;
    static boolean screenVerySmall;
    boolean debugFactorModel;
    int balanceIndicator[];


    @Override
    public void create() {
        loadedResources = false;
        splashCount = 0;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        splash = GameView.loadTextureRegionByName("splash.png", true);
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        pressX = 0.5f * w;
        pressY = 0.5f * h;
        screenRatio = (float) w / (float) h;
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        balanceIndicator = new int[GameController.colorNumber];
        initDebugValues();
        backButtonIds = new ArrayList<Integer>();
        useMenuMasks = true;
    }


    private void initDebugValues() {
        debugFactorModel = false;

        debugValues = new ArrayList<Float>();
        if (debugFactorModel) {
            FactorYio factorYio = new FactorYio();
            factorYio.setValues(0, 0);
            factorYio.beginSpawning(4, 1);
            int c = 100;
            while (factorYio.needsToMove() && c > 0) {
                debugValues.add(Float.valueOf(factorYio.get()));
                factorYio.move();
                c--;
            }
        }
    }


    private void loadResourcesAndInitEverything() {
        long time1 = System.currentTimeMillis();
        loadedResources = true;
        screenVerySmall = Gdx.graphics.getDensity() < 1.2;
        mainBackground = GameView.loadTextureRegionByName("main_menu_background.png", true);
        infoBackground = GameView.loadTextureRegionByName("info_background.png", true);
        settingsBackground = GameView.loadTextureRegionByName("settings_background.png", true);
        pauseBackground = GameView.loadTextureRegionByName("pause_background.png", true);
        splatTexture = GameView.loadTextureRegionByName("splat.png", true);
        SoundControllerYio.loadAllSounds();
        Province.decodeCityNameParts();
        transitionFactor = new FactorYio();
        splatTransparencyFactor = new FactorYio();
        initSplats();

        initFonts();
        gamePaused = true;
        alreadyShownErrorMessageOnce = false;
        fps = 0;
        timeToUpdateFpsInfo = System.currentTimeMillis() + 1000;
//        decorations = new ArrayList<BackgroundMenuDecoration>();
//        initDecorations();

        Preferences preferences = Gdx.app.getPreferences("main");
        selectedLevelIndex = preferences.getInteger("progress", 1); // 1 - default value;
        if (selectedLevelIndex > INDEX_OF_LAST_LEVEL) { // completed campaign
            selectedLevelIndex = INDEX_OF_LAST_LEVEL;
        }
        menuControllerYio = new MenuControllerYio(this);
        menuViewYio = new MenuViewYio(this);
        gameController = new GameController(this); // must be called after menu controller is created. because of languages manager and other stuff
        gameView = new GameView(this);
        gameView.factorModel.beginDestroying(1, 1);
        currentBackgroundIndex = -1;
        currentBackground = gameView.blackPixel; // call this after game view is created
        beginBackgroundChange(0, true, false);
        defaultBubbleRadius = 0.02f * w;
        bubbleGravity = 0.00025 * w;
        revealSplats();
        Gdx.input.setInputProcessor(this);
        Gdx.gl.glClearColor(0, 0, 0, 1);

        loadSettings();
        checkTemporaryFlags();

        YioGdxGame.say("full loading time: " + (System.currentTimeMillis() - time1));
    }


    private void checkTemporaryFlags() {
        // check_slay_rules
        Preferences prefs = Gdx.app.getPreferences("temporary_flags");

        // slay rules
        if (!prefs.getBoolean("check_slay_rules", false)) {
            menuControllerYio.loadMoreSkirmishOptions();
            Preferences tempPrefs = Gdx.app.getPreferences("settings");
            slay_rules = tempPrefs.getBoolean("slay_rules", false);
            menuControllerYio.getCheckButtonById(6).setChecked(slay_rules);
            menuControllerYio.saveMoreSkirmishOptions();
            menuControllerYio.saveMoreCampaignOptions();
            prefs.putBoolean("check_slay_rules", true);
        }

        prefs.flush();
    }


    private void initFonts() {
        long time1 = System.currentTimeMillis();
        FileHandle fontFile = Gdx.files.internal("font.otf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FONT_SIZE = (int) (0.041 * Gdx.graphics.getHeight());

        parameter.size = FONT_SIZE;
        parameter.characters = getAllCharacters();
        parameter.flip = true;
        buttonFont = generator.generateFont(parameter);

        parameter.size = (int) (1.5f * FONT_SIZE);
        parameter.flip = true;
        listFont = generator.generateFont(parameter);
        listFont.setColor(Color.BLACK);

        parameter.size = FONT_SIZE;
        parameter.flip = false;
        gameFont = generator.generateFont(parameter);
        gameFont.setColor(Color.BLACK);

        parameter.size = (int)(0.5 * FONT_SIZE);
        parameter.flip = false;
        cityFont = generator.generateFont(parameter);
        cityFont.setColor(Color.WHITE);

        generator.dispose();

        YioGdxGame.say("time to generate fonts: " + (System.currentTimeMillis() - time1));
    }


    private void initSplats() {
        splats = new ArrayList<Splat>();
        splatSize = 0.15f * Gdx.graphics.getWidth();
        ListIterator iterator = splats.listIterator();
        for (int i = 0; i < 100; i++) {
            float sx, sy, sr;
            sx = random.nextFloat() * w;
            sr = 0.03f * random.nextFloat() * h + 0.02f * h;
            sy = random.nextFloat() * h;
            float dx, dy;
            dx = 0.02f * splatSize * random.nextFloat() - 0.01f * splatSize;
            dy = 0.01f * splatSize;
            Splat splat = new Splat(null, sx, sy);
            if (random.nextDouble() < 0.6 || distance(w / 2, h / 2, sx, sy) > 0.6f * w) splat.y = 2 * h; // hide splat
            splat.setSpeed(dx, dy);
            splat.setRadius(sr);
            iterator.add(splat);
        }
    }


    public void saveSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");
        prefs.putInteger("sound", boolToInteger(menuControllerYio.getCheckButtonById(5).isChecked()));
        prefs.putInteger("skin", menuControllerYio.sliders.get(5).getCurrentRunnerIndex());
        prefs.putInteger("interface", boolToInteger(menuControllerYio.getCheckButtonById(2).isChecked())); // slot number
        prefs.putInteger("autosave", boolToInteger(menuControllerYio.getCheckButtonById(1).isChecked()));
        prefs.putInteger("ask_to_end_turn", boolToInteger(menuControllerYio.getCheckButtonById(3).isChecked()));
        prefs.putInteger("sensitivity", menuControllerYio.sliders.get(9).getCurrentRunnerIndex());
        prefs.putInteger("city_names", boolToInteger(menuControllerYio.getCheckButtonById(4).isChecked()));
        prefs.putInteger("camera_offset", menuControllerYio.sliders.get(6).getCurrentRunnerIndex());
        prefs.putBoolean("turns_limit", menuControllerYio.getCheckButtonById(6).isChecked());
        prefs.putBoolean("long_tap_to_move", menuControllerYio.getCheckButtonById(7).isChecked());
        CheckButtonYio chkWaterTexture = menuControllerYio.getCheckButtonById(10);
        if (chkWaterTexture != null) {
            prefs.putBoolean("water_texture", chkWaterTexture.isChecked());
        }
        prefs.flush();
    }


    private int boolToInteger(boolean b) {
        if (b) return 1;
        return 0;
    }


    public void loadSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");

        // sound
        int soundIndex = prefs.getInteger("sound", 0);
        if (soundIndex == 0) Settings.SOUND = false;
        else Settings.SOUND = true;
        menuControllerYio.getCheckButtonById(5).setChecked(Settings.SOUND);

        // skin
        int skin = prefs.getInteger("skin", 0);
        gameView.loadSkin(skin);
        float slSkinValue = (float) skin / 2f;
        menuControllerYio.sliders.get(5).setRunnerValue(slSkinValue);

        // interface. Number of save slots
        Settings.interface_type = prefs.getInteger("interface", 0);
        menuControllerYio.getCheckButtonById(2).setChecked(Settings.interface_type == 1);

        // autosave
        int AS = prefs.getInteger("autosave", 0);
        Settings.autosave = false;
        if (AS == 1) Settings.autosave = true;
        menuControllerYio.getCheckButtonById(1).setChecked(Settings.autosave);

        // sensitivity
        int sensitivity = prefs.getInteger("sensitivity", 6);
        menuControllerYio.sliders.get(9).setRunnerValueByIndex(sensitivity);
        Settings.sensitivity = Math.max(0.1f, menuControllerYio.sliders.get(9).runnerValue);

        // ask to end turn
        int ATET = prefs.getInteger("ask_to_end_turn", 0);
        Settings.ask_to_end_turn = (ATET == 1);
//        menuControllerYio.sliders.get(8).setRunnerValue(ATET);
        menuControllerYio.getCheckButtonById(3).setChecked(Settings.ask_to_end_turn);

        // show city names
        int cityNames = prefs.getInteger("city_names", 0);
        gameController.setShowCityNames(cityNames);
        menuControllerYio.getCheckButtonById(4).setChecked(cityNames == 1);

        // camera offset
        int camOffsetIndex = prefs.getInteger("camera_offset", 2);
        gameController.cameraOffset = 0.05f * w * camOffsetIndex;
        menuControllerYio.sliders.get(6).setRunnerValueByIndex(camOffsetIndex);

        // turns limit
        Settings.turns_limit = prefs.getBoolean("turns_limit", true);
        menuControllerYio.getCheckButtonById(6).setChecked(Settings.turns_limit);

        // long tap to move
        Settings.long_tap_to_move = prefs.getBoolean("long_tap_to_move", true);
        CheckButtonYio checkButtonById = menuControllerYio.getCheckButtonById(7);
        if (checkButtonById != null) {
            checkButtonById.setChecked(Settings.long_tap_to_move);
        }

        // water texture
        Settings.waterTexture = prefs.getBoolean("water_texture", false);
        gameView.loadBackgroundTexture();
        CheckButtonYio chkWaterTexture = menuControllerYio.getCheckButtonById(10);
        if (chkWaterTexture != null) {
            chkWaterTexture.setChecked(Settings.waterTexture);
        }

        menuControllerYio.sliders.get(5).updateValueString();
        menuControllerYio.sliders.get(6).updateValueString();
        menuControllerYio.sliders.get(9).updateValueString();
    }


    public void setGamePaused(boolean gamePaused) {
        if (gamePaused && !this.gamePaused) { // actions when paused
            this.gamePaused = true;
            timeWhenPauseStarted = System.currentTimeMillis();
            gameController.deselectAll();
            revealSplats();
            gameFont.setColor(Color.BLACK);
            menuControllerYio.forceDyingButtonsToEnd();
        } else if (!gamePaused && this.gamePaused) { // actions when unpaused
            unPauseAfterSomeTime();
            beginBackgroundChange(4, true, true);
            hideSplats();
            gameFont.setColor(Color.WHITE);
        }
    }


    public void beginBackgroundChange(int index, boolean updateAnimPos, boolean simpleTransition) {
        if (currentBackgroundIndex == index && index == 4) return;
        this.simpleTransitionAnimation = simpleTransition;
        currentBackgroundIndex = index;
        lastBackground = currentBackground;
        if (updateAnimPos) {
            animX = pressX;
            animY = pressY;
            float r1, r2, r3, r4;
            r1 = (float) distance(animX, animY, 0, 0);
            r2 = (float) distance(animX, animY, w, 0);
            r3 = (float) distance(animX, animY, 0, h);
            r4 = (float) distance(animX, animY, w, h);
            animRadius = r1;
            if (r2 > animRadius) animRadius = r2;
            if (r3 > animRadius) animRadius = r3;
            if (r4 > animRadius) animRadius = r4;
        }
        switch (index) {
            case 0:
                currentBackground = mainBackground;
                break;
            case 1:
                currentBackground = infoBackground;
                break;
            case 2:
                currentBackground = settingsBackground;
                break;
            case 3:
                currentBackground = pauseBackground;
                break;
            case 4:
                currentBackground = gameView.blackPixel;
                break;
        }
        transitionFactor.setValues(0.02, 0.01);
        transitionFactor.beginSpawning(0, 0.8);
    }


    private void timeCorrection(long correction) {
        if (ignoreNextTimeCorrection) {
            ignoreNextTimeCorrection = false;
            return;
        }
        gameController.timeCorrection(correction);
    }


    private void letsIgnoreNextTimeCorrection() {
        ignoreNextTimeCorrection = true;
    }


    private void checkToUseMenuMasks() {
        if (!useMenuMasks) { // check to switch on masks
            if (gameView.factorModel.get() < 1) {
                useMenuMasks = true;
                return;
            }
            ButtonYio buttonYio = menuControllerYio.getButtonById(30);
            if (buttonYio != null && buttonYio.isCurrentlyTouched()) {
                useMenuMasks = true;
                return;
            }
        } else { // check to switch off masks
            if (gameView.factorModel.get() == 1 && gameView.factorModel.getDy() == 0) {
                useMenuMasks = false;
                return;
            }
        }
    }


    private void move() {
        if (!loadedResources) return;
//        if (random.nextInt(100) == 0) {
//            say("memory: " + (Gdx.app.getJavaHeap() + Gdx.app.getNativeHeap()));
//        }
        transitionFactor.move();
        splatTransparencyFactor.move();
        gameController.selMoneyFactor.move();
        if (readyToUnPause && System.currentTimeMillis() > timeToUnPause && gameView.coversAllScreen()) {
            gamePaused = false;
            readyToUnPause = false;
            gameController.currentTouchCount = 0;
            timeCorrection(System.currentTimeMillis() - timeWhenPauseStarted);
        }
        if (needToHideSplats && System.currentTimeMillis() > timeToHideSplats) {
            needToHideSplats = false;
        }
        gameView.moveFactors();
        menuControllerYio.move();
        if (!loadedResources) return; // if exit button was pressed
        checkToUseMenuMasks();
        if (!gamePaused) {
            gameView.moveInsideStuff();
            gameController.move();
            if (gameView.factorModel.get() < 0.95) say("game not paused but game view is not visible");
        }
        if (!gameView.coversAllScreen()) {
            if (System.currentTimeMillis() > timeToSpawnNextSplat) {
                timeToSpawnNextSplat = System.currentTimeMillis() + 300 + random.nextInt(100);
                float sx, sy, sr;
                sx = random.nextFloat() * w;
                sr = 0.03f * random.nextFloat() * h + 0.02f * h;
                sy = -sr;
                int c = 0, size = splats.size();
                Splat splat = null;
                while (c < size) {
                    c++;
                    splat = splats.get(currentSplatIndex);
                    currentSplatIndex++;
                    if (currentSplatIndex >= size) currentSplatIndex = 0;
                    if (!splat.isVisible()) {
                        float dx, dy;
                        dx = 0.02f * splatSize * random.nextFloat() - 0.01f * splatSize;
                        dy = 0.01f * splatSize;
                        splat.set(sx, sy);
                        splat.setSpeed(dx, dy);
                        splat.setRadius(sr);
                        break;
                    }
                }
            }
            for (Splat splat : splats) {
                splat.move();
            }
        }
    }


    private void renderDebugValues() {
//        batch.begin();
//        batch.draw(gameView.grayPixel, 0, 0, w, h);
//        int graphWidth = w;
//        int graphHeight = (int)(0.45 * h);
//        int graphPos = h / 2;
//        float max = maxElement(debugValues);
//        float x, y, s;
//        s = 0.01f * w;
//        batch.draw(gameView.blackPixel, 0, graphPos - s, w, 2 * s);
//        batch.draw(gameView.blackPixel, 0, graphPos + graphHeight - s, w, 2 * s);
//        batch.draw(gameView.blackPixel, 0, graphPos - graphHeight - s, w, 2 * s);
//        for (int i=0; i<debugValues.size(); i++) {
//            x = ((float)i / (float)debugValues.size()) * graphWidth;
//            y = graphPos + ((float)graphHeight / max) * debugValues.get(i);
//            batch.draw(gameView.redUnit, x - s, y - s, 2 * s, 2 * s);
//        }
//        batch.end();
    }


    public static String getAllCharacters() {
        String langChars = MenuControllerYio.languagesManager.getString("lang_characters");
        return langChars + SPECIAL_CHARACTERS;
    }


    private void drawBackground(TextureRegion textureRegion) {
        batch.begin();
        batch.draw(textureRegion, 0, 0, w, h);
        batch.end();
    }


    private void renderMenuLayersWhenNothingIsMoving() { // when transitionFactor.get() == 1
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 1);
        batch.begin();
        batch.draw(currentBackground, 0, 0, w, h);
        renderSplats(c);
        batch.end();
    }

//    private void renderMenuLayersWhenBackAnimation() { // when backAnimation == true
//        Color c = batch.getColor();
//        batch.setColor(c.r, c.g, c.b, 1);
//        drawBackground(currentBackground);
//
//        menuViewLighty.render(true, false);
//
//        if (simpleTransitionAnimation) {
//            float f = (1 - transitionFactor.get());
//            batch.setColor(c.r, c.g, c.b, f);
//            drawBackground(lastBackground);
//        } else {
//            float f = (1 - transitionFactor.get());
//            maskingBegin();
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            shapeRenderer.circle(animX, animY, f * animRadius, 32);
//            shapeRenderer.end();
//            maskingContinue();
//            drawBackground(lastBackground);
//            maskingEnd();
//        }
//
//        batch.begin();
//        renderSplats(c);
//        batch.end();
//    }


    private void renderMenuLayersWhenUsualAnimation() {
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 1);
        drawBackground(lastBackground);

        if (simpleTransitionAnimation) {
            float f = (0 + transitionFactor.get());
            batch.setColor(c.r, c.g, c.b, f);
            drawBackground(currentBackground);
        } else {
            float f = (0 + transitionFactor.get());
            maskingBegin();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(animX, animY, f * animRadius, 32);
            shapeRenderer.end();
            maskingContinue();
            drawBackground(currentBackground);
            maskingEnd();
        }

        batch.begin();
        renderSplats(c);
        batch.end();

        menuViewYio.render(false, true);
    }


    private void renderMenuWhenGameViewNotVisible() {
        if (transitionFactor.get() == 1 && !menuControllerYio.notificationIsDestroying()) {
            renderMenuLayersWhenNothingIsMoving();
            return;
        }

        renderMenuLayersWhenUsualAnimation();
    }


    private void renderInternals() {
        currentFrameCount++;
        if (Debug.showFpsInfo && System.currentTimeMillis() > timeToUpdateFpsInfo) {
            timeToUpdateFpsInfo = System.currentTimeMillis() + 1000;
            fps = currentFrameCount;
            currentFrameCount = 0;
        }
        if (debugFactorModel) {
            renderDebugValues();
            return;
        }
        if (!gameView.coversAllScreen()) {
            renderMenuWhenGameViewNotVisible();
        }
//        menuViewYio.renderScroller();
        MenuRender.renderLevelSelector.renderLevelSelector(menuControllerYio.levelSelector);
        gameView.render();
        if (gamePaused) {
            menuViewYio.render(true, false);
        } else {
            menuViewYio.render(true, true);
        }
        if (Debug.showFpsInfo) {
            batch.begin();
            gameFont.draw(batch, "" + fps, 0.2f * w, Gdx.graphics.getHeight() - 10);
            batch.end();
        }
    }


    private void renderSplats(Color c) {
        if (splatTransparencyFactor.get() == 1) {
            batch.setColor(c.r, c.g, c.b, splatTransparencyFactor.get());
            for (Splat splat : splats) {
                batch.draw(splatTexture, splat.x - splat.r / 2, splat.y - splat.r / 2, splat.r, splat.r);
            }
        } else if (splatTransparencyFactor.get() > 0) {
            batch.setColor(c.r, c.g, c.b, splatTransparencyFactor.get());
            float a, d;
            for (Splat splat : splats) {
                a = (float) angle(w / 2, h / 2, splat.x, splat.y);
                d = (float) distance(w / 2, h / 2, splat.x, splat.y);
                d = 0.5f * h - d;
                d *= 1 - splatTransparencyFactor.get();
                batch.draw(splatTexture, splat.x - splat.r / 2 + d * (float) Math.cos(a), splat.y - splat.r / 2 + d * (float) Math.sin(a), splat.r, splat.r);
            }
        }
    }


    public static void maskingBegin() {
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);
    }


    public static void maskingContinue() {
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
    }


    public static void maskingEnd() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }


    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!loadedResources) {
            batch.begin();
            batch.draw(splash, 0, 0, w, h);
            batch.end();
            if (splashCount == 2) loadResourcesAndInitEverything();
            splashCount++;
            return;
        }

        try {
            move();
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerYio.createExceptionReport(exception);
            }
        }

        if (gamePaused) {
            renderInternals();
        } else {
            if (Gdx.graphics.getDeltaTime() < 0.025 || frameSkipCount >= 2) {
                frameSkipCount = 0;
                frameBuffer.begin();
                renderInternals();
                frameBuffer.end();
            } else {
                frameSkipCount++;
            }
            batch.begin();
            batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
            batch.end();
        }
    }


    public boolean isLevelLocked(int index) {
        if (index == CampaignLevelFactory.NORMAL_LEVELS_START) return false;
        if (index == CampaignLevelFactory.HARD_LEVELS_START) return false;
        if (index == CampaignLevelFactory.EXPERT_LEVELS_START) return false;
        if (gameController != null) return gameController.progress < index;
        return selectedLevelIndex < index;
    }


    public boolean isLevelComplete(int index) {
        if (gameController != null) return gameController.progress > index;
        return selectedLevelIndex > index;
    }


    public static String getDifficultyNameByPower(LanguagesManager languagesManager, int difficulty) {
        String diffString = null;
        switch (difficulty) {
            case ArtificialIntelligence.DIFFICULTY_EASY:
                diffString = languagesManager.getString("easy");
                break;
            case ArtificialIntelligence.DIFFICULTY_NORMAL:
                diffString = languagesManager.getString("normal");
                break;
            case ArtificialIntelligence.DIFFICULTY_HARD:
                diffString = languagesManager.getString("hard");
                break;
            case ArtificialIntelligence.DIFFICULTY_EXPERT:
                diffString = languagesManager.getString("expert");
                break;
            case ArtificialIntelligence.DIFFICULTY_BALANCER:
                diffString = languagesManager.getString("balancer");
                break;
        }
        return diffString;
    }


    private void unPauseAfterSomeTime() {
        readyToUnPause = true;
        timeToUnPause = System.currentTimeMillis() + 450; // время анимации - около 420мс
    }


    public void setAnimToPlayButtonSpecial() {
        ButtonYio buttonYio = menuControllerYio.getButtonById(3);
        if (buttonYio != null) {
            animX = buttonYio.cx;
            animY = buttonYio.cy;
        } else {
            animX = w/2;
            animY = h/2;
        }
        transitionFactor.setValues(0.15, 0);
    }


    public void setAnimToResumeButtonSpecial() {
        animX = w;
        animY = h;
        animRadius = (float) distance(0, 0, w, h);
    }


    public void setAnimToStartButtonSpecial() {
        animX = 0.5f * w;
        animY = 0.65f * h;
        animRadius = animY;
    }


    public static boolean isScreenVerySmall() {
        return screenVerySmall;
    }


    public static boolean isScreenVeryWide() {
        float ratio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        return ratio < 1.51;
    }


    public void forceBackgroundChange() {
        transitionFactor.setValues(1, 0);
        simpleTransitionAnimation = true;
    }


    public static void say(String text) {
        System.out.println(text);
    }


    public void restartGame() {
        if (gameController.campaignMode) {
            gameController.loadCampaignLevel(gameController.currentLevelIndex);
            return;
        }
        gameController.restartGame();
    }


    public void startInEditorMode() {
        gameController.editorMode = true;
        if (GameController.colorNumber == 0) { // default
            gameController.setLevelSize(GameController.SIZE_BIG);
            gameController.setPlayersNumber(1);
            GameController.setColorNumber(5);
            startGame(false, false);
            gameController.createFieldMatrix();
            gameController.clearField();
        } else {
            startGame(false, false);
        }
    }


    public void startGame(boolean generateMap, boolean readParametersFromSliders) {
        startGame(random.nextInt(), generateMap, readParametersFromSliders);
    }


    public void startGame(int index, boolean generateMap, boolean readParametersFromSliders) {
//        if (selectedLevelIndex > gameController.progress) return;
        if (selectedLevelIndex < 0 || selectedLevelIndex > INDEX_OF_LAST_LEVEL) return;
        gameController.prepareForNewGame(index, generateMap, readParametersFromSliders);
        gameView.beginSpawnProcess();
        menuControllerYio.createGameOverlay();
//        menuControllerLighty.scrollerYio.factorModel.setValues(0, 0);
        setGamePaused(false);
        letsIgnoreNextTimeCorrection();
    }


    void increaseLevelSelection() {
//        menuControllerYio.scrollerYio.increaseSelection();
        setSelectedLevelIndex(selectedLevelIndex + 1);
    }


    static double angle(double x1, double y1, double x2, double y2) {
        if (x1 == x2) {
            if (y2 > y1) return 0.5 * Math.PI;
            if (y2 < y1) return 1.5 * Math.PI;
            return 0;
        }
        if (x2 >= x1) return Math.atan((y2 - y1) / (x2 - x1));
        else return Math.PI + Math.atan((y2 - y1) / (x2 - x1));
    }


    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }


    public static ArrayList<String> decodeStringToArrayList(String string, String delimiters) {
        ArrayList<String> res = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, delimiters);
        while (tokenizer.hasMoreTokens()) {
            res.add(tokenizer.nextToken());
        }
        return res;
    }


    public int getSelectedLevelIndex() {
        return selectedLevelIndex;
    }


    public void setSelectedLevelIndex(int selectedLevelIndex) {
        if (selectedLevelIndex >= 0 && selectedLevelIndex <= INDEX_OF_LAST_LEVEL)
            this.selectedLevelIndex = selectedLevelIndex;
    }


    private void pressButtonIfVisible(int id) {
        ButtonYio button = menuControllerYio.getButtonById(id);
        if (button != null && button.isVisible() && button.factorModel.get() == 1) button.press();
    }


    public void registerBackButtonId(int id) {
        for (Integer integer : backButtonIds) {
            if (integer.intValue() == id) return;
        }
        backButtonIds.add(Integer.valueOf(id));
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            if (!gamePaused) {
                ButtonYio pauseButton = menuControllerYio.getButtonById(30);
                if (pauseButton != null && pauseButton.isVisible()) pauseButton.press();
                else menuControllerYio.getButtonById(140).press();
            } else {
                pressButtonIfVisible(42);
                pressButtonIfVisible(1);

                // back buttons
                for (Integer integer : backButtonIds) {
                    pressButtonIfVisible(integer.intValue());
                }
            }
        }
        if (keycode == Input.Keys.Q) {
            if (!gamePaused) {
                menuControllerYio.getButtonById(32).press(); // debug
                pressButtonIfVisible(53); // skip tutorial tip
            }
        }
        if (keycode == Input.Keys.SPACE) {
            if (!gamePaused) {
                menuControllerYio.getButtonById(31).press();
            }
        }
        if (keycode == Input.Keys.NUM_1) {
            if (!gamePaused) pressButtonIfVisible(39);
        }
        if (keycode == Input.Keys.NUM_2) {
            if (!gamePaused) pressButtonIfVisible(38);
        }
        if (keycode == Input.Keys.D) {
            if (!gamePaused) gameController.debugActions();
        }
        return false;
    }


    @Override
    public boolean keyUp(int keycode) {
        return false;
    }


    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ignoreDrag = true;
        pressX = screenX;
        pressY = h - screenY;
        try {
            if (!gameView.isInMotion() && transitionFactor.get() > 0.99 && menuControllerYio.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button)) {
                lastTimeButtonPressed = System.currentTimeMillis();
                return false;
            } else {
                ignoreDrag = false;
            }
            if (!gamePaused) gameController.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerYio.createExceptionReport(exception);
            }
        }
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        try {
            menuControllerYio.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
            // System.currentTimeMillis() > lastTimeButtonPressed + 300
            if (!gamePaused && gameView.coversAllScreen())
                gameController.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerYio.createExceptionReport(exception);
            }
        }
        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        menuControllerYio.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
        if (!ignoreDrag && !gamePaused && gameView.coversAllScreen())
            gameController.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
        return false;
    }


    public int gamesPlayed() {
        int s = 0;
        for (int i = 0; i < balanceIndicator.length; i++) {
            s += balanceIndicator[i];
        }
        return s;
    }


    private String getBalanceIndicatorAsString(int array[]) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        for (int i = 0; i < GameController.colorNumber; i++) {
            stringBuffer.append(" " + array[i]);
        }
        stringBuffer.append(" ]");
        return stringBuffer.toString();
    }


    public String getBalanceIndicatorString() {
        double D = 0;
        int max = balanceIndicator[0], min = balanceIndicator[0];
        for (int i = 0; i < GameController.colorNumber; i++) {
            if (balanceIndicator[i] > max) max = balanceIndicator[i];
            if (balanceIndicator[i] < min) min = balanceIndicator[i];
        }
        if (max > 0) {
            D = 1d - (double) min / (double) max;
        }
        String dStr = Double.toString(D);
        if (dStr.length() > 4) dStr = dStr.substring(0, 4);

        return getBalanceIndicatorAsString(balanceIndicator) + " = " + dStr;
    }


    static public float getTextWidth(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }


    private void hideSplats() {
        needToHideSplats = true;
        timeToHideSplats = System.currentTimeMillis() + 350;
        splatTransparencyFactor.setDy(0);
        splatTransparencyFactor.beginDestroying(0, 1);
    }


    private void revealSplats() {
        needToHideSplats = false;
        splatTransparencyFactor.setDy(0);
        splatTransparencyFactor.beginSpawning(0, 0.5);
    }


    static float maxElement(ArrayList<Float> list) {
        if (list.size() == 0) return 0;
        float max = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) > max) max = list.get(i);
        }
        return max;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }


    @Override
    public boolean scrolled(int amount) {
        if (gameView.factorModel.get() > 0.1) gameController.scrolled(amount);
        return true;
    }


    @Override
    public void resume() {
        super.resume();
        menuControllerYio.onResume();
        gameView.onResume();
        System.out.println("Yio -> On resume.");
    }


    public void close() {
        if (true) return;
        loadedResources = false;
        gameController.close();
        menuControllerYio.close();

        gameController = null;
        menuControllerYio = null;
        menuViewYio = null;
        gameView = null;
    }
}
