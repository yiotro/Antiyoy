package yio.tro.antiyoy;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.ai.ArtificialIntelligence;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.menu.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import static yio.tro.antiyoy.gameplay.GameRules.slay_rules;

public class YioGdxGame extends ApplicationAdapter implements InputProcessor {

    final SplatController splatController;
    public SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    public int w, h;
    public MenuControllerYio menuControllerYio;
    private MenuViewYio menuViewYio;
    private static GlyphLayout glyphLayout = new GlyphLayout();
    public static boolean ANDROID = false;
    public static final int INDEX_OF_LAST_LEVEL = 70; // with tutorial
    TextureRegion mainBackground, infoBackground, settingsBackground, pauseBackground;
    TextureRegion currentBackground;
    TextureRegion lastBackground;
    public static float screenRatio;
    public GameController gameController;
    public GameView gameView;
    public boolean gamePaused, readyToUnPause;
    private long timeToUnPause;
    private int frameSkipCount;
    private FrameBuffer frameBuffer;
    private FactorYio transitionFactor;
    public static final Random random = new Random();
    private long lastTimeButtonPressed;
    private boolean alreadyShownErrorMessageOnce;
    private int fps, currentFrameCount;
    long timeToUpdateFpsInfo;
    private int currentBackgroundIndex;
    public int currentBubbleIndex, selectedLevelIndex, splashCount;
    public float defaultBubbleRadius, pressX, pressY, animX, animY, animRadius;
    double bubbleGravity;
    boolean ignoreNextTimeCorrection;
    boolean loadedResources;
    boolean ignoreDrag;
    public boolean simpleTransitionAnimation, useMenuMasks;
    TextureRegion splash;
    ArrayList<Float> debugValues;
    ArrayList<Integer> backButtonIds;
    static boolean screenVerySmall;
    boolean debugFactorModel;
    public int balanceIndicator[];


    public YioGdxGame() {
        splatController = new SplatController(this);
    }


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
        balanceIndicator = new int[GameRules.colorNumber];
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
        splatController.splatTexture = GameView.loadTextureRegionByName("splat.png", true);
        SoundControllerYio.loadAllSounds();
        Province.decodeCityNameParts();
        transitionFactor = new FactorYio();
        splatController.splatTransparencyFactor = new FactorYio();
        splatController.initSplats();

        Fonts.initFonts();
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
        splatController.revealSplats();
        Gdx.input.setInputProcessor(this);
        Gdx.gl.glClearColor(0, 0, 0, 1);

        Settings.getInstance().setYioGdxGame(this);
        Settings.getInstance().loadSettings();
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


    private void initSplats() {
        splatController.initSplats();
    }


    public void setGamePaused(boolean gamePaused) {
        if (gamePaused && !this.gamePaused) { // actions when paused
            this.gamePaused = true;
            gameController.selectionController.deselectAll();
            splatController.revealSplats();
            Fonts.gameFont.setColor(Color.BLACK);
            menuControllerYio.forceDyingButtonsToEnd();
        } else if (!gamePaused && this.gamePaused) { // actions when unpaused
            unPauseAfterSomeTime();
            beginBackgroundChange(4, true, true);
            splatController.hideSplats();
            Fonts.gameFont.setColor(Color.WHITE);
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
            r1 = (float) Yio.distance(animX, animY, 0, 0);
            r2 = (float) Yio.distance(animX, animY, w, 0);
            r3 = (float) Yio.distance(animX, animY, 0, h);
            r4 = (float) Yio.distance(animX, animY, w, h);
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
        gameController.selectionController.getSelMoneyFactor().move();
        if (readyToUnPause && System.currentTimeMillis() > timeToUnPause && gameView.coversAllScreen()) {
            gamePaused = false;
            readyToUnPause = false;
            gameController.cameraController.resetCurrentTouchCount();
        }
        if (!gamePaused) {
            gameView.moveInsideStuff();
            gameController.move();
            if (gameView.factorModel.get() < 0.95) say("game not paused but game view is not visible");
        }
        gameView.moveFactors();
        menuControllerYio.move();
        if (!loadedResources) return; // if exit button was pressed
        checkToUseMenuMasks();

        splatController.moveSplats();
    }


    private void moveSplats() {

        splatController.moveSplats();
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
        splatController.renderSplats(c);
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
        splatController.renderSplats(c);
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
            Fonts.gameFont.draw(batch, "" + fps, 0.2f * w, Gdx.graphics.getHeight() - 10);
            batch.end();
        }
    }


    private void renderSplats(Color c) {
        splatController.renderSplats(c);
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
        if (gameController != null) return CampaignController.getInstance().progress < index;
        return selectedLevelIndex < index;
    }


    public boolean isLevelComplete(int index) {
        if (gameController != null) return CampaignController.getInstance().progress > index;
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
        animRadius = (float) Yio.distance(0, 0, w, h);
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
        if (GameRules.campaignMode) {
            CampaignController.getInstance().loadCampaignLevel(CampaignController.getInstance().currentLevelIndex);
            return;
        }
        gameController.restartGame();
    }


    public void startInEditorMode() {
        GameRules.inEditorMode = true;
        if (GameRules.colorNumber == 0) { // default
            gameController.setLevelSize(FieldController.SIZE_BIG);
            gameController.setPlayersNumber(1);
            GameRules.setColorNumber(5);
            startGame(false, false);
            gameController.fieldController.createFieldMatrix();
            gameController.fieldController.clearField();
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
    }


    void increaseLevelSelection() {
//        menuControllerYio.scrollerYio.increaseSelection();
        setSelectedLevelIndex(selectedLevelIndex + 1);
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
            if (integer == id) return;
        }
        backButtonIds.add(id);
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
                    pressButtonIfVisible(integer);
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
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("[");
        for (int i = 0; i < GameRules.colorNumber; i++) {
            stringBuffer.append(" ").append(array[i]);
        }
        stringBuffer.append(" ]");
        return stringBuffer.toString();
    }


    public String getBalanceIndicatorString() {
        double D = 0;
        int max = balanceIndicator[0], min = balanceIndicator[0];
        for (int i = 0; i < GameRules.colorNumber; i++) {
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
