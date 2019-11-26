package yio.tro.antiyoy.menu.customizable_list;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LongTapDetector;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.scroll_engine.ScrollEngineYio;

import java.util.ArrayList;

public class CustomizableListYio extends InterfaceElement {


    public MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    public FactorYio appearFactor;
    public boolean factorMoved;
    protected PointYio initialTouch, currentTouch, lastTouch;
    ClickDetector clickDetector;
    public ArrayList<AbstractCustomListItem> items;
    boolean touched;
    float hook;
    ScrollEngineYio scrollEngineYio;
    LongTapDetector longTapDetector;
    AbstractCustomListItem targetItem;
    boolean readyToProcessItemClick;
    boolean readyToProcessItemLongTap;
    public RectangleYio maskPosition;
    public TextureRegion customBackgroundTexture;
    public boolean scrollingEnabled;
    boolean embeddedMode;
    Animation animation;
    int destroyType;
    double destroySpeed;


    public CustomizableListYio(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;

        appearFactor = new FactorYio();
        position = new RectangleYio();
        viewPosition = new RectangleYio(-1, -1, -1, -1); // it has to be not zero
        initialTouch = new PointYio();
        currentTouch = new PointYio();
        lastTouch = new PointYio();
        factorMoved = false;
        clickDetector = new ClickDetector();
        items = new ArrayList<>();
        touched = false;
        hook = 0;
        scrollEngineYio = new ScrollEngineYio();
        scrollEngineYio.setFriction(0.05);
        targetItem = null;
        readyToProcessItemClick = false;
        readyToProcessItemLongTap = false;
        maskPosition = new RectangleYio();
        customBackgroundTexture = null;
        scrollingEnabled = true;
        animation = Animation.def;
        embeddedMode = false;
        destroyType = 2;
        destroySpeed = 2;

        initLongTapDetector();
    }


    private void initLongTapDetector() {
        longTapDetector = new LongTapDetector() {
            @Override
            public void onLongTapDetected() {
                CustomizableListYio.this.onLongTapDetected();
            }
        };
    }


    public void addItem(AbstractCustomListItem newItem) {
        newItem.setCustomizableListYio(this);
        items.add(newItem);
        updateItemDeltas();
        updateScrollLimit();
    }


    public void updateItemDeltas() {
        boolean fm = factorMoved;
        factorMoved = true;
        updateViewPosition();
        maskPosition.setBy(position);
        factorMoved = fm;

        float currentY = (float) maskPosition.height;
        for (AbstractCustomListItem item : items) {
            item.viewPosition.width = (float) item.getWidth();
            item.viewPosition.height = (float) item.getHeight();
            item.positionDelta.set(
                    (maskPosition.width - item.getWidth()) / 2,
                    currentY - item.getHeight()
            );
            item.onPositionChanged();
            currentY -= item.getHeight();
        }

        updateScrollLimit();
    }


    public RectangleYio getPosition() {
        return position;
    }


    public void clearItems() {
        items.clear();
    }


    private void onLongTapDetected() {
        for (AbstractCustomListItem item : items) {
            if (!item.isTouched(currentTouch)) continue;

            targetItem = item;
            readyToProcessItemLongTap = true;
            break;
        }
    }


    @Override
    public void move() {
        moveFactor();
        updateViewPosition();
        scrollEngineYio.move();
        updateHook();
        longTapDetector.move();
        updateMaskPosition();
        moveItems();
    }


    private void updateHook() {
        hook = (float) scrollEngineYio.getSlider().a;
    }


    private void updateScrollLimit() {
        double sum = 0;
        for (AbstractCustomListItem item : items) {
            sum += item.getHeight();
        }
        sum = Math.max(sum, maskPosition.height);

        scrollEngineYio.setLimits(0, sum);
    }


    private void updateViewPosition() {
        if (!factorMoved) return;

        switch (animation) {
            default:
            case def:
                animDef();
                break;
            case none:
                animNone();
                break;
            case from_center:
                animFromCenter();
                break;
            case down:
                animDown();
                break;
        }
    }


    private void animDown() {
        viewPosition.setBy(position);
        viewPosition.y -= (1 - appearFactor.get()) * (position.y + position.height + 0.02f * GraphicsYio.width);
    }


    private void animFromCenter() {
        viewPosition.width = appearFactor.get() * position.width;
        viewPosition.height = appearFactor.get() * position.height;
        viewPosition.x = position.x + position.width / 2 - viewPosition.width / 2;
        viewPosition.y = position.y + position.height / 2 - viewPosition.height / 2;
    }


    private void animNone() {
        viewPosition.setBy(position);
    }


    private void animDef() {
        viewPosition.setBy(position);
        viewPosition.y -= (1 - appearFactor.get()) * 0.1f * GraphicsYio.height;
    }


    public void setPosition(RectangleYio src) {
        position.setBy(src);
        onPositionChanged();
    }


    private void onPositionChanged() {
        updateItemDeltas();

        scrollEngineYio.setSlider(0, maskPosition.height);
        scrollEngineYio.setSoftLimitOffset(0.05 * maskPosition.height);

        updateScrollLimit();
    }


    private void updateMaskPosition() {
        maskPosition.setBy(viewPosition);
    }


    private void moveItems() {
        for (AbstractCustomListItem item : items) {
            item.moveItem();

            if (!touched) {
                item.selectionEngineYio.move();
            }
        }
    }


    private void moveFactor() {
        factorMoved = false;
        if (appearFactor.hasToMove()) {
            factorMoved = true;
            appearFactor.move();
        }
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.setDy(0);
        appearFactor.destroy(destroyType, destroySpeed);
        onDestroy();
    }


    private void onDestroy() {
        //
    }


    public void setDestroyParameters(int type, double speed) {
        destroyType = type;
        destroySpeed = speed;
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(2, 2);
        onAppear();
    }


    protected void onAppear() {
        readyToProcessItemClick = false;
        readyToProcessItemLongTap = false;
        scrollEngineYio.resetToBottom();
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        if (readyToProcessItemClick) {
            readyToProcessItemClick = false;

            targetItem.onClicked();

            return true;
        }

        if (readyToProcessItemLongTap) {
            readyToProcessItemLongTap = false;

            targetItem.onLongTapped();

            return true;
        }

        return false;
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    private void updateCurrentTouch(int screenX, int screenY) {
        currentTouch.x = screenX;
        currentTouch.y = screenY;
    }


    protected boolean isClicked() {
        return clickDetector.isClicked();
    }


    public boolean isTouched(PointYio touchPoint) {
        return viewPosition.isPointInside(touchPoint);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);
        touched = isTouched(currentTouch);
        if (!touched) return false;

        lastTouch.setBy(currentTouch);
        initialTouch.setBy(currentTouch);
        clickDetector.onTouchDown(currentTouch);
        scrollEngineYio.onTouchDown();
        checkToSelectItem();
        longTapDetector.onTouchDown(currentTouch);

        return true;
    }


    private void checkToSelectItem() {
        for (AbstractCustomListItem item : items) {
            if (!item.isTouched(currentTouch)) continue;

            item.selectionEngineYio.select();
            break;
        }
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        updateCurrentTouch(screenX, screenY);

        if (!touched) return false;

        if (scrollingEnabled) {
            scrollEngineYio.setSpeed(1.5 * (currentTouch.y - lastTouch.y));
        }
        longTapDetector.onTouchDrag(currentTouch);

        lastTouch.setBy(currentTouch);
        clickDetector.onTouchDrag(currentTouch);
        return true;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);
        clickDetector.onTouchUp(currentTouch);

        if (!touched) return false;

        scrollEngineYio.onTouchUp();
        longTapDetector.onTouchUp(currentTouch);

        if (isClicked()) {
            onClick();
        }

        touched = false;
        return true;
    }


    private void onClick() {
        for (AbstractCustomListItem item : items) {
            if (!item.isTouched(currentTouch)) continue;

            targetItem = item;
            readyToProcessItemClick = true;
            SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
            break;
        }
    }


    @Override
    public boolean onMouseWheelScrolled(int amount) {
        if (!scrollingEnabled) return true;
        if (amount == 1) {
            scrollEngineYio.giveImpulse(0.03 * GraphicsYio.width);
        } else if (amount == -1) {
            scrollEngineYio.giveImpulse(-0.03 * GraphicsYio.width);
        }
        scrollEngineYio.hardCorrection();
        return true;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    public void setCustomBackgroundTexture(TextureRegion customBackgroundTexture) {
        this.customBackgroundTexture = customBackgroundTexture;
    }


    public boolean hasCustomBackground() {
        return customBackgroundTexture != null;
    }


    public void setScrollingEnabled(boolean scrollingEnabled) {
        this.scrollingEnabled = scrollingEnabled;
    }


    public void setAnimation(Animation animation) {
        this.animation = animation;
    }


    public boolean isInEmbeddedMode() {
        return embeddedMode;
    }


    public void setEmbeddedMode(boolean embeddedMode) {
        this.embeddedMode = embeddedMode;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderCustomizableList;
    }

}
