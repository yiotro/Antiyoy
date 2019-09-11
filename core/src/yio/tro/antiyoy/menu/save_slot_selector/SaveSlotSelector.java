package yio.tro.antiyoy.menu.save_slot_selector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.scroll_engine.ScrollEngineYio;

import java.util.ArrayList;

public class SaveSlotSelector extends InterfaceElement {

    public static final int MIN_ITEMS_NUMBER = 9;
    public static final int EMPTY_ITEM_CUT = 9;

    MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    public FactorYio appearFactor, textAlphaFactor;
    PointYio currentTouch, lastTouch;
    public ArrayList<SsItem> items;
    float hook;
    private float itemHeight;
    ScrollEngineYio scrollEngineYio;
    ClickDetector clickDetector;
    public BitmapFont titleFont, descFont;
    public String label;
    public PointYio labelPosition;
    float labelWidth;
    SsItem clickedItem;
    boolean touched, alphaTriggered;
    public RectangleYio topEdge, bottomEdge;
    boolean operationType;
    private String slotPrefsString;
    private float topLabelOffset;
    LongTapDetector longTapDetector;
    boolean readyToProcessLongTap;
    boolean scrollLock;
    boolean readyToDeleteItem;
    SsItem targetItem;


    public SaveSlotSelector(MenuControllerYio menuControllerYio, int id) {
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
        descFont = Fonts.smallerMenuFont;
        label = LanguagesManager.getInstance().getString("slots");
        labelPosition = new PointYio();
        labelWidth = GraphicsYio.getTextWidth(titleFont, label);
        items = new ArrayList<>();
        clickedItem = null;
        textAlphaFactor = new FactorYio();
        alphaTriggered = false;
        topEdge = new RectangleYio();
        bottomEdge = new RectangleYio();
        operationType = false;
        slotPrefsString = SaveSystem.SAVE_SLOT_PREFS;
        readyToDeleteItem = false;

        initMetrics();
        initLongTapDetector();
        initScrollEngine();
    }


    private void initLongTapDetector() {
        longTapDetector = new LongTapDetector() {
            @Override
            public void onLongTapDetected() {
                onLongTap();
            }
        };
    }


    private void onLongTap() {
        readyToProcessLongTap = true;
        scrollLock = true;
    }


    private void initScrollEngine() {
        scrollEngineYio = new ScrollEngineYio();

        scrollEngineYio.setSlider(0, 0); // will be updated later
        updateScrollEngineLimits();
        scrollEngineYio.setFriction(0.02);
        scrollEngineYio.setSoftLimitOffset(0.05f * GraphicsYio.width);
    }


    private void updateScrollEngineLimits() {
        scrollEngineYio.setLimits(0, getScrollLimit());
    }


    private double getScrollLimit() {
        return items.size() * itemHeight - itemHeight / 2;
    }


    private void initMetrics() {
        itemHeight = 0.115f * GraphicsYio.height;
        topLabelOffset = 0.18f * GraphicsYio.height;
    }


    private void loadValues() {
        items.clear();

        SaveSystem saveSystem = menuControllerYio.yioGdxGame.saveSystem;
        ArrayList<String> keys = saveSystem.getKeys(slotPrefsString);

        int index = 0;
        if (!operationType) {
            addEmptyItem(saveSystem.getKeyForNewSlot(slotPrefsString), index);
            index++;
        }

        for (String key : keys) {
            SaveSlotInfo slotInfo = saveSystem.getSlotInfo(key, slotPrefsString);

            if (slotInfo.name.equals("")) {
                addEmptyItem(key, index);
                index++;
                continue;
            }

            addItem(index, key, slotInfo);
            index++;
        }
    }


    private void addItem(int index, String key, SaveSlotInfo slotInfo) {
        SsItem ssItem = new SsItem(this);
        ssItem.setKey(key);
        ssItem.setTitle(slotInfo.name);
        ssItem.setDescription(slotInfo.description);
        ssItem.setBckViewType(index % 3);

        items.add(ssItem);
    }


    private void updateSingleItem(String key) {
        SaveSystem saveSystem = menuControllerYio.yioGdxGame.saveSystem;
        SaveSlotInfo slotInfo = saveSystem.getSlotInfo(key, slotPrefsString);

        for (SsItem item : items) {
            if (item.key.equals(key)) {
                item.setTitle(slotInfo.name);
                item.setDescription(slotInfo.description);
                break;
            }
        }
    }


    private void addEmptyItem(String key, int index) {
        if (items.size() > EMPTY_ITEM_CUT) return;

        SsItem ssItem = new SsItem(this);
        ssItem.setTitle(LanguagesManager.getInstance().getString("empty"));
        ssItem.setDescription("");
        ssItem.setKey(key);
        ssItem.setBckViewType(index % 3);

        items.add(ssItem);
    }


    void updateMetrics() {
        float currentY = (float) position.height - topLabelOffset;

        for (SsItem item : items) {
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
        updateEdgeRectangles();
        longTapDetector.move();
    }


    private void updateEdgeRectangles() {
        if (items.size() == 0) {
            topEdge.setBy(position);
            bottomEdge.setBy(position);
            return;
        }

        SsItem firstItem = items.get(0);
        topEdge.setBy(firstItem.position);
        topEdge.y += firstItem.position.height;

        SsItem lastItem = items.get(items.size() - 1);
        bottomEdge.setBy(lastItem.position);
        bottomEdge.y -= lastItem.position.height;
    }


    private void moveFactors() {
        textAlphaFactor.move();

        if (!appearFactor.hasToMove()) return;

        appearFactor.move();

        if (!alphaTriggered && appearFactor.get() > 0.95) {
            textAlphaFactor.appear(3, 0.7);
            alphaTriggered = true;
        }
    }


    private void updateLabelPosition() {
        labelPosition.x = (float) (viewPosition.x + viewPosition.width / 2 - labelWidth / 2);
        labelPosition.y = (float) (viewPosition.y + viewPosition.height - 0.02f * GraphicsYio.width) + hook;
    }


    private void updateHook() {
        hook = +(float) scrollEngineYio.getSlider().a;

        hook -= (1 - appearFactor.get()) * 0.2f * GraphicsYio.width;
    }


    private void moveItems() {
        for (SsItem item : items) {
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
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(SPAWN_TYPE, SPAWN_SPEED);

        onAppear();
    }


    private void onAppear() {
        alphaTriggered = false;
        scrollEngineYio.resetToBottom();
        readyToProcessLongTap = false;
        readyToDeleteItem = false;
        targetItem = null;
        scrollLock = false;
    }


    public void updateAll() {
        loadValues();
        updateMetrics();
        updateScrollEngineLimits();
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        if (clickedItem != null) {
            onSlotSelected();

            clickedItem = null;
            return true;
        }

        if (readyToProcessLongTap) {
            readyToProcessLongTap = false;
            processLongTap();
            return true;
        }

        if (readyToDeleteItem) {
            readyToDeleteItem = false;
            showConfirmDeleteDialog();
            return true;
        }

        return false;
    }


    private void showConfirmDeleteDialog() {
        Scenes.sceneConfirmDeleteSlot.create();
        Scenes.sceneConfirmDeleteSlot.setCurrentYesReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                deleteTargetItem();
                Scenes.sceneSaveLoad.create();
                Scenes.sceneSaveLoad.setOperationType(Scenes.sceneSaveLoad.getOperationType());
                updateAll();
            }
        });
    }


    public void deleteTargetItem() {
        SaveSystem saveSystem = menuControllerYio.yioGdxGame.saveSystem;
        saveSystem.deleteSlot(targetItem.key, SaveSystem.SAVE_SLOT_PREFS);
    }


    private void processLongTap() {
        SsItem longTappedItem = null;

        for (SsItem item : items) {
            if (!item.isTouched(currentTouch)) continue;
            longTappedItem = item;
            break;
        }

        if (longTappedItem == null) return;
        if (longTappedItem.key.equals(SaveSystem.AUTOSAVE_KEY)) return; // don't edit autosave slot

        Scenes.sceneContextListMenu.create();
        Scenes.sceneContextListMenu.contextListMenuElement.setEditableItem(longTappedItem);
    }


    private void onSlotSelected() {
        String key = clickedItem.key;
        if (operationType) {
            loadSlot(key);
        } else {
            saveSlot(key);
        }
    }


    void saveSlot(String key) {
        SaveSystem saveSystem = menuControllerYio.yioGdxGame.saveSystem;
        if (key.equals(SaveSystem.AUTOSAVE_KEY)) return; // don't overwrite autosave in selector

        if (!saveSystem.containsKey(key, slotPrefsString)) {
            saveSystem.addKey(key, slotPrefsString);
        }

        saveSystem.saveGame(key);

        SaveSlotInfo saveSlotInfo = new SaveSlotInfo();

        Preferences slotPrefs = Gdx.app.getPreferences(key);
        saveSlotInfo.name = SaveSystem.getNameString(slotPrefs);
        saveSlotInfo.description = SaveSystem.getDescriptionString(slotPrefs);
        saveSlotInfo.key = key;

        saveSystem.editSlot(key, saveSlotInfo, slotPrefsString);
        updateSingleItem(saveSlotInfo.key);
    }


    void onSlotRenamed(SsItem renamedItem) {
        String key = renamedItem.key;
        if (key.equals(SaveSystem.AUTOSAVE_KEY)) return;

        SaveSlotInfo saveSlotInfo = new SaveSlotInfo();

        Preferences slotPrefs = Gdx.app.getPreferences(key);
        saveSlotInfo.name = renamedItem.getEditableName();
        saveSlotInfo.description = SaveSystem.getDescriptionString(slotPrefs);
        saveSlotInfo.key = key;

        SaveSystem saveSystem = menuControllerYio.yioGdxGame.saveSystem;
        saveSystem.editSlot(key, saveSlotInfo, slotPrefsString);
    }


    private void loadSlot(String key) {
        SaveSystem saveSystem = menuControllerYio.yioGdxGame.saveSystem;
        SaveSlotInfo slotInfo = saveSystem.getSlotInfo(key, slotPrefsString);
        if (slotInfo.name.length() < 3) {
            System.out.println("clicked on empty slot");
            return;
        }

        saveSystem.loadGame(slotInfo.key);
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
            readyToProcessLongTap = false;
            scrollLock = false;
            clickDetector.onTouchDown(currentTouch);
            scrollEngineYio.onTouchDown();
            longTapDetector.onTouchDown(currentTouch);

            checkToSelectItems();
        }

        return touched;
    }


    private void checkToSelectItems() {
        for (SsItem item : items) {
            if (item.isTouched(currentTouch)) {
                item.select();
            }
        }
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        if (touched) {
            updateCurrentTouch(screenX, screenY);

            if (!scrollLock) {
                scrollEngineYio.setSpeed(currentTouch.y - lastTouch.y);
            }

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
            longTapDetector.onTouchUp(currentTouch);

            if (clickDetector.isClicked()) {
                onClick();
            }

            return true;
        }

        return false;
    }


    private void onClick() {
        scrollEngineYio.setSpeed(0);

        for (SsItem item : items) {
            if (item.isTouched(currentTouch)) {
                onItemClicked(item);
            }
        }
    }


    private void onItemClicked(SsItem item) {
        clickedItem = item;

        SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
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


    public void setOperationType(boolean operationType) {
        this.operationType = operationType;

        updateAll();
    }


    public boolean getOperationType() {
        return operationType;
    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);

        onPositionChanged();
    }


    private void onPositionChanged() {
        updateMetrics();
        scrollEngineYio.setSlider(0, position.height - topLabelOffset);
        updateScrollEngineLimits();
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderSaveSlotSelector;
    }
}
