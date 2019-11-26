package yio.tro.antiyoy.menu.replay_selector;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.data_storage.LegacyImportManager;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.replays.RepSlot;
import yio.tro.antiyoy.gameplay.replays.Replay;
import yio.tro.antiyoy.gameplay.replays.ReplaySaveSystem;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.SceneSkirmishMenu;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.scroll_engine.ScrollEngineYio;

import java.util.ArrayList;

public class ReplaySelector extends InterfaceElement {

    MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    public FactorYio appearFactor, textAlphaFactor;
    PointYio currentTouch, lastTouch;
    public ArrayList<RsItem> items;
    float hook;
    private float itemHeight;
    ScrollEngineYio scrollEngineYio;
    ClickDetector clickDetector;
    public BitmapFont titleFont, descFont;
    public String label;
    public PointYio labelPosition;
    float labelWidth;
    RsItem clickedItem, readyToRemoveItem;
    boolean touched, alphaTriggered;
    private float topLabelOffset;
    LongTapDetector longTapDetector;
    boolean inRemoveMode;


    public ReplaySelector(MenuControllerYio menuControllerYio, int id) {
        super(id);
        this.menuControllerYio = menuControllerYio;

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        currentTouch = new PointYio();
        lastTouch = new PointYio();
        clickDetector = new ClickDetector();
        touched = false;
        titleFont = Fonts.gameFont;
        descFont = Fonts.microFont;
        label = LanguagesManager.getInstance().getString("replays");
        labelPosition = new PointYio();
        labelWidth = GraphicsYio.getTextWidth(titleFont, label);
        items = new ArrayList<>();
        clickedItem = null;
        textAlphaFactor = new FactorYio();
        alphaTriggered = false;
        readyToRemoveItem = null;

        initMetrics();
        initScrollEngine();
        initLongTapDetector();
    }


    private void initLongTapDetector() {
        longTapDetector = new LongTapDetector() {
            @Override
            public void onLongTapDetected() {
                ReplaySelector.this.onLongTapDetected();
            }
        };
    }


    private void initScrollEngine() {
        scrollEngineYio = new ScrollEngineYio();

        scrollEngineYio.setSlider(0, 0);
        updateScrollEngineLimits();
        scrollEngineYio.setFriction(0.02);
        scrollEngineYio.setSoftLimitOffset(0.05f * GraphicsYio.width);
    }


    private void updateScrollEngineLimits() {
        scrollEngineYio.setLimits(0, getLowerLimit());
    }


    private double getLowerLimit() {
        return items.size() * itemHeight - itemHeight / 2;
    }


    private void initMetrics() {
        itemHeight = 0.1f * GraphicsYio.height;
        topLabelOffset = 0.18f * GraphicsYio.height;
    }


    private void loadValues() {
        items.clear();

        ReplaySaveSystem instance = ReplaySaveSystem.getInstance();
        ArrayList<String> keys = instance.getKeys();

        for (String key : keys) {
            RepSlot slotByKey = instance.getSlotByKey(key);

            RsItem rsItem = new RsItem(this);
            rsItem.setTitle(makeItemTitle(slotByKey));
            rsItem.setDescription(makeItemDescription(slotByKey));
            rsItem.setKey(key);

            items.add(rsItem);
        }
    }


    private String makeItemDescription(RepSlot repSlot) {
        return repSlot.date + ", " + SceneSkirmishMenu.getHumansString(repSlot.numberOfHumans);
    }


    private String makeItemTitle(RepSlot repSlot) {
        LanguagesManager instance = LanguagesManager.getInstance();

        if (repSlot.campaignMode) {
            String typeString = instance.getString("choose_game_mode_campaign");
            if (repSlot.levelIndex == -1) {
                return typeString;
            } else {
                return typeString + ", " + repSlot.levelIndex;
            }
        } else {
            return instance.getString("choose_game_mode_skirmish");
        }
    }


    void updateItemMetrics() {
        float currentY = (float) position.height - topLabelOffset;

        for (RsItem item : items) {
            item.position.width = position.width;
            item.position.height = itemHeight;
            item.delta.x = 0;
            item.delta.y = currentY;
            currentY -= itemHeight;
        }
    }


    @Override
    public void move() {
        moveFactors();

        updateViewPosition();
        moveItems();
        scrollEngineYio.move();
        updateHook();
        updateLabelPosition();
        longTapDetector.move();
    }


    private void onLongTapDetected() {
        switchRemoveMode();
    }


    private void switchRemoveMode() {
        inRemoveMode = !inRemoveMode;

        if (inRemoveMode) {
            onRemoveModeEnabled();
        } else {
            onRemoveModeDisabled();
        }
    }


    private void onRemoveModeDisabled() {
        //
    }


    private void onRemoveModeEnabled() {
        //
    }


    private void moveFactors() {
        textAlphaFactor.move();

        if (!appearFactor.hasToMove()) return;

        appearFactor.move();

        if (!alphaTriggered && appearFactor.get() > 0.9) {
            textAlphaFactor.appear(3, 0.7);
            alphaTriggered = true;
        }
    }


    private void updateLabelPosition() {
        labelPosition.x = (float) (viewPosition.x + viewPosition.width / 2 - labelWidth / 2);
        labelPosition.y = (float) (viewPosition.y + viewPosition.height - 0.02f * GraphicsYio.width) + hook;
    }


    private void updateHook() {
        hook = + (float) scrollEngineYio.getSlider().a;

        hook -= (1 - appearFactor.get()) * 0.2f * GraphicsYio.width;
    }


    private void moveItems() {
        for (RsItem item : items) {
            item.move();

            if (!touched) {
                item.moveSelection();
            }
        }
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);

        if (appearFactor.get() < 1) {
            viewPosition.x += (float) ((1 - appearFactor.get()) * 0.5 * position.width);
            viewPosition.y += (float) ((1 - appearFactor.get()) * 0.5 * position.height);
            viewPosition.width -= 2 * (float) ((1 - appearFactor.get()) * 0.5 * position.width);
            viewPosition.height -= 2 * (float) ((1 - appearFactor.get()) * 0.5 * position.height);
        }
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(DES_TYPE, DES_SPEED);
        textAlphaFactor.destroy(3, 4);

        onDestroy();
    }


    private void onDestroy() {
        inRemoveMode = false;
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(SPAWN_TYPE, SPAWN_SPEED);

        onAppear();
    }


    private void onAppear() {
        loadValues();
        updateItemMetrics();
        updateScrollEngineLimits();
        alphaTriggered = false;
        scrollEngineYio.resetToBottom();
        inRemoveMode = false;
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        if (clickedItem != null) {
            startSelectedReplay();

            clickedItem = null;
            return true;
        }

        if (readyToRemoveItem != null) {
            removeClickedReplay();

            readyToRemoveItem = null;
            return true;
        }

        return false;
    }


    private void removeClickedReplay() {
        ReplaySaveSystem.getInstance().removeReplay(readyToRemoveItem.key);

        loadValues();
        updateItemMetrics();
        updateScrollEngineLimits();
        scrollEngineYio.resetToBottom();
    }


    private void startSelectedReplay() {
        ReplaySaveSystem instance = ReplaySaveSystem.getInstance();
        RepSlot slotByKey = instance.getSlotByKey(clickedItem.key);
        Replay replay = slotByKey.replay;

        replay.loadFromPreferences(slotByKey.key);

        LoadingParameters loadingParameters = new LoadingParameters();
        loadingParameters.loadingType = LoadingType.load_replay;
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        LegacyImportManager legacyImportManager = gameController.gameSaver.legacyImportManager;
        legacyImportManager.applyFullLevel(loadingParameters, replay.initialLevelString);
        loadingParameters.replay = replay;
        loadingParameters.playersNumber = 0;
        loadingParameters.colorOffset = replay.tempColorOffset;
        loadingParameters.slayRules = replay.tempSlayRules;

        LoadingManager.getInstance().startGame(loadingParameters);
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    private void updateCurrentTouch(int screenX, int screenY) {
        lastTouch.setBy(currentTouch);
        currentTouch.set(screenX, screenY);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!isVisible()) return false;

        updateCurrentTouch(screenX, screenY);
        touched = (screenY < position.y + position.height);

        if (touched) {
            clickDetector.onTouchDown(currentTouch);
            scrollEngineYio.onTouchDown();

            longTapDetector.onTouchDown(currentTouch);

            checkToSelectItems();
        }

        return touched;
    }


    private void checkToSelectItems() {
        for (RsItem item : items) {
            if (item.isTouched(currentTouch)) {
                item.select();
            }
        }
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        if (touched) {
            updateCurrentTouch(screenX, screenY);

            scrollEngineYio.setSpeed(currentTouch.y - lastTouch.y);

            clickDetector.onTouchDrag(currentTouch);
            longTapDetector.onTouchDrag(currentTouch);
        }

        return touched;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);
        scrollEngineYio.onTouchUp();

        if (touched) {
            touched = false;
            clickDetector.onTouchUp(currentTouch);

            if (clickDetector.isClicked()) {
                onClick();
            }

            longTapDetector.onTouchUp(currentTouch);

            return true;
        }

        return false;
    }


    private void onClick() {
        scrollEngineYio.setSpeed(0);

        if (inRemoveMode) {
            onClickInRemoveMode();
            return;
        }

        for (RsItem item : items) {
            if (item.isTouched(currentTouch)) {
                onItemClicked(item);
            }
        }
    }


    private void onClickInRemoveMode() {
        for (RsItem item : items) {
            if (currentTouch.distanceTo(item.removeIconPosition) < 0.07f * GraphicsYio.width) {
                readyToRemoveItem = item;
                return;
            }
        }
    }


    private void onItemClicked(RsItem item) {
        clickedItem = item;
    }


    @Override
    public boolean onMouseWheelScrolled(int amount) {
        if (amount == 1) {
            scrollEngineYio.giveImpulse(0.02 * GraphicsYio.width);
        } else if (amount == -1) {
            scrollEngineYio.giveImpulse(-0.02 * GraphicsYio.width);
        }

        return true;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);

        onPositionChanged();
    }


    private void onPositionChanged() {
        updateItemMetrics();

        scrollEngineYio.setSlider(0, position.height - topLabelOffset);
        updateScrollEngineLimits();
    }


    public boolean isInRemoveMode() {
        return inRemoveMode;
    }


    @Override
    public MenuRender getRenderSystem() {
        return null;
    }
}
