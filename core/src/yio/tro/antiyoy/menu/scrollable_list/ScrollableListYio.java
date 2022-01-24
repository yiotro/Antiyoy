package yio.tro.antiyoy.menu.scrollable_list;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.scroll_engine.ScrollEngineYio;

import java.util.ArrayList;

public class ScrollableListYio extends InterfaceElement {

    protected MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    public FactorYio appearFactor, textAlphaFactor;
    PointYio currentTouch, lastTouch;
    public ArrayList<ListItemYio> items;
    float hook;
    private float itemHeight;
    protected ScrollEngineYio scrollEngineYio;
    ClickDetector clickDetector;
    public BitmapFont titleFont, descFont;
    public String label;
    public PointYio labelPosition;
    protected float labelWidth;
    ListItemYio clickedItem;
    protected boolean touched, alphaTriggered;
    public RectangleYio topEdge, bottomEdge;
    protected float topLabelOffset;
    ListBehaviorYio listBehaviorYio;
    LongTapDetector longTapDetector;
    boolean readyToProcessLongTap;
    protected boolean scrollLock;
    boolean editable;
    public RenderableTextYio emptySign;
    Reaction longTapReaction;
    public ListItemYio longTappedItem;


    public ScrollableListYio(MenuControllerYio menuControllerYio) {
        super(-1);
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
        label = "default label";
        labelPosition = new PointYio();
        updateLabelWidth();
        items = new ArrayList<>();
        clickedItem = null;
        textAlphaFactor = new FactorYio();
        alphaTriggered = false;
        topEdge = new RectangleYio();
        bottomEdge = new RectangleYio();
        listBehaviorYio = null;
        editable = false;
        emptySign = new RenderableTextYio();
        emptySign.setFont(Fonts.smallerMenuFont);
        setEmptySign("-");
        longTapReaction = null;

        initMetrics();
        initScrollEngine();
        initLongTapDetector();
    }


    private void initLongTapDetector() {
        longTapDetector = new LongTapDetector() {
            @Override
            public void onLongTapDetected() {
                ScrollableListYio.this.onLongTapDetected();
            }
        };
    }


    private void onLongTapDetected() {
        if (!editable) return;

        readyToProcessLongTap = true;
        scrollLock = true;
    }


    private void updateLabelWidth() {
        labelWidth = GraphicsYio.getTextWidth(titleFont, label);
    }


    private void initScrollEngine() {
        scrollEngineYio = new ScrollEngineYio();

        scrollEngineYio.setSlider(0, 0); // will be updated later
        updateScrollEngineLimits();
        scrollEngineYio.setFriction(0.02);
        scrollEngineYio.setSoftLimitOffset(0.05f * GraphicsYio.width);
    }


    public void updateScrollEngineLimits() {
        scrollEngineYio.setLimits(0, getScrollLimit());
    }


    private double getScrollLimit() {
        return items.size() * itemHeight - itemHeight / 2;
    }


    private void initMetrics() {
        itemHeight = getItemHeight();
        topLabelOffset = 0.18f * GraphicsYio.height;
    }


    protected float getItemHeight() {
        return 0.115f * GraphicsYio.height;
    }


    public void setTitle(String label) {
        this.label = label;

        updateLabelWidth();
    }


    public void setTitleFont(BitmapFont titleFont) {
        this.titleFont = titleFont;
    }


    public void addDebugItems() {
        items.clear();

        for (int i = 0; i < 10; i++) {
            addItem("key" + i, "item " + i, "description");
        }
    }


    public void clearItems() {
        items.clear();

        updateMetrics();
        updateScrollEngineLimits();
    }


    public ListItemYio getItem(String key) {
        for (ListItemYio item : items) {
            if (!item.key.equals(key)) continue;
            return item;
        }
        return null;
    }


    public ListItemYio addItem(String key, String title, String description) {
        ListItemYio newItem = new ListItemYio(this);
        newItem.setKey(key);
        newItem.setTitle(title);
        newItem.setDescription(description);
        newItem.setBckViewType(items.size() % 3);

        items.add(newItem);

        updateMetrics();
        updateScrollEngineLimits();

        return newItem;
    }


    public void updateMetrics() {
        float currentY = (float) position.height - topLabelOffset;

        for (ListItemYio item : items) {
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
        updateEmpySignPosition();
    }


    private void updateEmpySignPosition() {
        emptySign.centerHorizontal(viewPosition);
        emptySign.centerVertical(viewPosition);
        emptySign.updateBounds();
    }


    protected void updateEdgeRectangles() {
        if (items.size() == 0) {
            topEdge.setBy(position);
            bottomEdge.setBy(position);
            return;
        }

        ListItemYio firstItem = items.get(0);
        topEdge.setBy(firstItem.position);
        topEdge.y += firstItem.position.height;

        ListItemYio lastItem = items.get(items.size() - 1);
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


    protected void updateLabelPosition() {
        labelPosition.x = (float) (viewPosition.x + viewPosition.width / 2 - labelWidth / 2);
        labelPosition.y = (float) (viewPosition.y + viewPosition.height - 0.02f * GraphicsYio.width) + hook;
    }


    private void updateHook() {
        hook = (float) scrollEngineYio.getSlider().a;
        hook -= (1 - appearFactor.get()) * 0.2f * GraphicsYio.width;
    }


    protected void moveItems() {
        for (ListItemYio item : items) {
            item.move();

            if (!touched) {
                item.moveSelection();
            }
        }
    }


    protected void updateViewPosition() {
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


    protected void onAppear() {
        alphaTriggered = false;
        scrollEngineYio.resetToBottom();
        hook = 0;
        readyToProcessLongTap = false;
        scrollLock = false;
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        if (clickedItem != null) {
            applyItem();

            clickedItem = null;
            return true;
        }

        if (readyToProcessLongTap) {
            readyToProcessLongTap = false;
            processLongTap();
            return true;
        }

        return false;
    }


    private void processLongTap() {
        longTappedItem = null;

        for (ListItemYio item : items) {
            if (!item.isTouched(currentTouch)) continue;
            longTappedItem = item;
            break;
        }

        if (longTappedItem == null) return;

        if (longTapReaction != null) {
            longTapReaction.perform(null);
            return; // override default reaction
        }

        Scenes.sceneContextListMenu.create();
        Scenes.sceneContextListMenu.contextListMenuElement.setEditableItem(longTappedItem);
    }


    private void applyItem() {
        if (listBehaviorYio != null) {
            listBehaviorYio.applyItem(clickedItem);
        }
    }


    public void setListBehavior(ListBehaviorYio listBehaviorYio) {
        this.listBehaviorYio = listBehaviorYio;
    }


    public void setLongTapReaction(Reaction longTapReaction) {
        this.longTapReaction = longTapReaction;
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

        onTouchDown();

        return touched;
    }


    protected void onTouchDown() {
        //
    }


    private void checkToSelectItems() {
        for (ListItemYio item : items) {
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

        for (ListItemYio item : items) {
            if (item.isTouched(currentTouch)) {
                onItemClicked(item);
            }
        }
    }


    private void onItemClicked(ListItemYio item) {
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
        return MenuRender.renderScrollableList;
    }


    public void setEmptySign(String string) {
        emptySign.setString(string);
        emptySign.updateMetrics();
    }


    public void setItemHeight(float itemHeight) {
        this.itemHeight = itemHeight;
    }


    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
