package yio.tro.antiyoy.menu.replay_selector;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
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
    public FactorYio appearFactor;
    PointYio currentTouch, lastTouch;
    private float animDelta;
    public ArrayList<RsItem> items;
    float hook;
    private float itemHeight;
    ScrollEngineYio scrollEngineYio;
    ClickDetector clickDetector;
    public BitmapFont titleFont, descFont;
    public String label;
    public PointYio labelPosition;
    float labelWidth;
    RsItem clickedItem;
    boolean touched;


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

        initMetrics();
        initScrollEngine();
    }


    private void initScrollEngine() {
        scrollEngineYio = new ScrollEngineYio();

        scrollEngineYio.setSlider(0, GraphicsYio.width);
        updateScrollEngineLimits();
        scrollEngineYio.setFriction(0.02);
        scrollEngineYio.setSoftLimitOffset(0.05f * GraphicsYio.width);
    }


    private void updateScrollEngineLimits() {
        scrollEngineYio.setLimits(0, getLowerLimit());
    }


    private double getLowerLimit() {
        double min = 0;

        for (RsItem item : items) {
            if (item.delta.y < min) {
                min = item.delta.y;
            }
        }

        return position.height - min - itemHeight;
    }


    private void initMetrics() {
        itemHeight = 0.1f * GraphicsYio.height;
    }


    private void updateItems() {
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
        float currentY = (float) position.height - 1.7f * itemHeight;

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
        appearFactor.move();

        updateViewPosition();
        moveItems();
        scrollEngineYio.move();
        updateHook();
        updateLabelPosition();
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
            animDelta = (float) ((1 - appearFactor.get()) * 0.5 * position.width);
            viewPosition.x += animDelta;
            viewPosition.y += animDelta;
            viewPosition.width -= 2 * animDelta;
            viewPosition.height -= 2 * animDelta;
        }
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.beginDestroying(DES_TYPE, DES_SPEED);
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.beginSpawning(SPAWN_TYPE, SPAWN_SPEED);

        onAppear();
    }


    private void onAppear() {
        updateItems();
        updateItemMetrics();
        updateScrollEngineLimits();
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

        return false;
    }


    private void startSelectedReplay() {
        ReplaySaveSystem instance = ReplaySaveSystem.getInstance();
        RepSlot slotByKey = instance.getSlotByKey(clickedItem.key);
        Replay replay = slotByKey.replay;

        replay.loadFromPreferences(slotByKey.key);

        LoadingParameters loadingParameters = new LoadingParameters();
        loadingParameters.mode = LoadingParameters.MODE_LOAD_REPLAY;
        loadingParameters.applyFullLevel(replay.initialLevelString);
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
            clickDetector.touchDown(currentTouch);
            scrollEngineYio.updateCanSoftCorrect(false);

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

            clickDetector.touchDrag(currentTouch);
        }

        return touched;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);
        scrollEngineYio.updateCanSoftCorrect(true);

        if (touched) {
            touched = false;
            clickDetector.touchUp(currentTouch);

            if (clickDetector.isClicked()) {
                onClick();
            }

            return true;
        }

        return false;
    }


    private void onClick() {
        scrollEngineYio.setSpeed(0);

        for (RsItem item : items) {
            if (item.isTouched(currentTouch)) {
                onItemClicked(item);
            }
        }
    }


    private void onItemClicked(RsItem item) {
        clickedItem = item;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public boolean isButton() {
        return false;
    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);

        onPositionChanged();
    }


    private void onPositionChanged() {
        updateItemMetrics();
        updateScrollEngineLimits();
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderReplaySelector;
    }
}
