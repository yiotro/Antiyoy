package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.Yio;

public abstract class AbstractRectangularUiElement extends InterfaceElement {

    public MenuControllerYio menuControllerYio;
    protected FactorYio appearFactor;
    protected RectangleYio position;
    public RectangleYio viewPosition;
    boolean touched;
    protected PointYio currentTouch;
    public boolean factorMoved;
    protected ClickDetector clickDetector;
    protected Animation animation;
    public float touchOffset;


    public AbstractRectangularUiElement(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;
        appearFactor = new FactorYio();
        position = new RectangleYio();
        viewPosition = new RectangleYio();
        touched = false;
        currentTouch = new PointYio();
        factorMoved = false;
        clickDetector = new ClickDetector();
        animation = Animation.def;
        touchOffset = 0;
    }


    @Override
    public void move() {
        moveFactor();
        updateViewPosition();
        onMove();
    }


    protected abstract void onMove();


    protected void updateViewPosition() {
        if (!factorMoved) return;
        switch (animation) {
            default:
            case def:
            case fixed_down:
                animDef();
                break;
            case down:
                animDown();
                break;
            case up:
                animUp();
                break;
            case from_center:
                animFromCenter();
                break;
            case none:
                animNone();
                break;
            case fixed_up:
                animFixedUp();
                break;
            case left:
                System.out.println("AbstractRectangularUiElement.updateViewPosition: " + animation + " shouldn't be used");
                break;
        }
    }


    private void animFixedUp() {
        viewPosition.setBy(position);
        viewPosition.y += (1 - appearFactor.get()) * 0.1f * GraphicsYio.height;
    }


    private void animNone() {
        viewPosition.setBy(position);
    }


    private void animFromCenter() {
        viewPosition.set(GraphicsYio.width / 2, GraphicsYio.height / 2, 0, 0);
        viewPosition.y += appearFactor.get() * (position.y - viewPosition.y);
        viewPosition.x += appearFactor.get() * (position.x - viewPosition.x);
        viewPosition.width += appearFactor.get() * (position.width - viewPosition.width);
        viewPosition.height += appearFactor.get() * (position.height - viewPosition.height);
    }


    private void animUp() {
        viewPosition.setBy(position);
        viewPosition.y += (1 - appearFactor.get()) * (GraphicsYio.height + 5 * GraphicsYio.borderThickness - position.y);
    }


    private void animDown() {
        viewPosition.setBy(position);
        viewPosition.y -= (1 - appearFactor.get()) * (position.y + position.height + 5 * GraphicsYio.borderThickness);
    }


    private void animDef() {
        viewPosition.setBy(position);
        viewPosition.y -= (1 - appearFactor.get()) * 0.1f * GraphicsYio.height;
    }


    private void moveFactor() {
        factorMoved = false;
        if (!appearFactor.hasToMove()) return;

        factorMoved = true;
        appearFactor.move();
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(MenuControllerYio.DESTROY_ANIM, MenuControllerYio.DESTROY_SPEED);
        onDestroy();
    }


    protected abstract void onDestroy();


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
        onAppear();
    }


    protected abstract void onAppear();


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    @Override
    public void setTouchable(boolean touchable) {
        // nothing
    }


    private void updateCurrentTouch(int screenX, int screenY) {
        currentTouch.set(screenX, screenY);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);
        touched = isCurrentlyTouched();
        if (touched) {
            clickDetector.onTouchDown(currentTouch);
            onTouchDown();
        }
        return touched;
    }


    protected boolean isCurrentlyTouched() {
        return viewPosition.isPointInside(currentTouch, touchOffset);
    }


    protected abstract void onTouchDown();


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        if (!touched) return false;
        updateCurrentTouch(screenX, screenY);
        clickDetector.onTouchDrag(currentTouch);
        onTouchDrag();
        return true;
    }


    protected abstract void onTouchDrag();


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!touched) return false;
        updateCurrentTouch(screenX, screenY);
        clickDetector.onTouchUp(currentTouch);
        if (clickDetector.isClicked()) {
            onClick();
        }
        onTouchUp();
        touched = false;
        return true;
    }


    public void forceUpdateViewPosition() {
        boolean fm = factorMoved;
        factorMoved = true;
        updateViewPosition();
        factorMoved = fm;
    }


    protected abstract void onTouchUp();


    protected abstract void onClick();


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);
    }


    public void setAnimation(Animation animation) {
        this.animation = animation;
    }


    public void setTouchOffset(float touchOffset) {
        this.touchOffset = touchOffset * GraphicsYio.width;
    }


    public float getAlpha() {
        if (appearFactor.get() < 0.2) return 0;
        return appearFactor.get();
    }
}
