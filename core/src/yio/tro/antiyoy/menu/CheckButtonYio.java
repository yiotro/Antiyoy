package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;

public class CheckButtonYio extends InterfaceElement{

    public MenuControllerYio menuControllerYio;
    public RectangleYio viewPosition;
    PointYio delta;
    UiChildrenHolder parent;
    FactorYio appearFactor;
    boolean factorMoved;
    public RenderableTextYio title;
    public SelectionEngineYio selectionEngineYio;
    boolean touched;
    PointYio currentTouch;
    public RectangleYio iconPosition;
    boolean checked;
    public FactorYio iconFactor;
    ClickDetector clickDetector;
    ICheckButtonListener listener;
    public boolean alternativeVisualMode;


    public CheckButtonYio(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;
        viewPosition = new RectangleYio();
        delta = new PointYio();
        parent = null;
        appearFactor = new FactorYio();
        factorMoved = false;
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        selectionEngineYio = new SelectionEngineYio();
        touched = false;
        currentTouch = new PointYio();
        iconPosition = new RectangleYio();
        checked = false;
        iconFactor = new FactorYio();
        clickDetector = new ClickDetector();
        listener = null;
        alternativeVisualMode = false;
    }


    @Override
    public void move() {
        moveFactor();
        updateViewPosition();
        moveTitle();
        updateIconPosition();
        moveSelection();
        iconFactor.move();
    }


    private void updateIconPosition() {
        if (!factorMoved) return;
        iconPosition.x = viewPosition.x + viewPosition.width - viewPosition.height / 3 - iconPosition.width;
        iconPosition.y = viewPosition.y + viewPosition.height / 2 - iconPosition.height / 2;
    }


    private void moveSelection() {
        if (touched) return;
        selectionEngineYio.move();
    }


    private void moveTitle() {
        if (!factorMoved) return;
        title.centerVertical(viewPosition);
        title.position.x = (float) (viewPosition.x + viewPosition.height / 3);
        title.updateBounds();
    }


    public static CheckButtonYio getFreshCheckButton(MenuControllerYio menuControllerYio) {
        CheckButtonYio checkButtonYio = new CheckButtonYio(menuControllerYio);
        menuControllerYio.addElementToScene(checkButtonYio);
        return checkButtonYio;
    }


    private void updateViewPosition() {
        if (!factorMoved) return;
        viewPosition.x = parent.getTargetPosition().x + delta.x;
        viewPosition.y = parent.getHookPosition().y + delta.y;
    }


    private void moveFactor() {
        factorMoved = false;
        if (parent != null && parent.getFactor().get() < 1) {
            factorMoved = true;
        }
        if (!appearFactor.hasToMove()) return;

        appearFactor.move();
        factorMoved = true;
    }


    public void setParent(UiChildrenHolder parent) {
        this.parent = parent;
        onParentSet();
    }


    public void setTitle(String key) {
        title.setString(LanguagesManager.getInstance().getString(key));
        title.updateMetrics();
    }


    private void onParentSet() {
        centerHorizontal(-GraphicsYio.borderThickness / GraphicsYio.width);
        setHeight(0.07);
    }


    public void setHeight(double h) {
        viewPosition.height = h * GraphicsYio.height;
        iconPosition.height = 0.4 * viewPosition.height;
        iconPosition.width = 0.4 * viewPosition.height;
    }


    public void alignUnderPreviousElement() {
        InterfaceElement lastAddedElement = menuControllerYio.getPreviouslyAddedElement();
        if (!(lastAddedElement instanceof CheckButtonYio)) return;
        alignUnder((CheckButtonYio) lastAddedElement);
    }


    public void alignUnder(CheckButtonYio checkButtonYio) {
        alignUnder(checkButtonYio, 0);
    }


    public void alignUnder(CheckButtonYio checkButtonYio, double offset) {
        delta.y = (float) (checkButtonYio.delta.y - viewPosition.height - offset * GraphicsYio.height);
    }


    public void alignTop(double offset) {
        delta.y = (float) (parent.getTargetPosition().height - offset * GraphicsYio.height - viewPosition.height);
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(2, 2);
        onDestroy();
    }


    private void onDestroy() {
        touched = false;
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(2, 2);
        onAppear();
    }


    private void onAppear() {
        touched = false;
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    private void updateCurrentTouch(int screenX, int screenY) {
        currentTouch.set(screenX, screenY);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);
        touched = viewPosition.isPointInside(currentTouch, 0);
        if (!touched) return false;

        selectionEngineYio.select();
        clickDetector.onTouchDown(currentTouch);

        return true;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        if (!touched) return false;
        updateCurrentTouch(screenX, screenY);
        clickDetector.onTouchDrag(currentTouch);

        return true;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!touched) return false;
        updateCurrentTouch(screenX, screenY);
        clickDetector.onTouchUp(currentTouch);
        touched = false;

        if (clickDetector.isClicked()) {
            onClick();
        }

        return true;
    }


    private void onClick() {
        setChecked(!checked);
        SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
    }


    @Override
    public void setTouchable(boolean touchable) {
        System.out.println("CheckButtonYio.setTouchable: check button is always touchable");
    }


    @Override
    public void setPosition(RectangleYio position) {
        System.out.println("CheckButtonYio.setPosition: shouldn't be used");
    }


    public UiChildrenHolder getParent() {
        return parent;
    }


    public boolean isChecked() {
        return checked;
    }


    public void setChecked(boolean checked) {
        if (this.checked == checked) return;

        this.checked = checked;

        onCheckedStateChanged();
    }


    public void centerHorizontal(double offset) {
        delta.x = (float) (offset * GraphicsYio.width);
        viewPosition.width = parent.getTargetPosition().width - 2 * delta.x;
    }


    private void onCheckedStateChanged() {
        if (listener != null) {
            listener.onStateChanged(checked);
        }

        launchIconFactor();
    }


    private void launchIconFactor() {
        if (checkToLaunchIconFactorInAlternativeMode()) return;
        if (checked) {
            iconFactor.setValues(0, 0);
            iconFactor.appear(3, 0.5);
        } else {
            iconFactor.setValues(1, -0.1);
            iconFactor.destroy(1, 3);
        }
    }


    private boolean checkToLaunchIconFactorInAlternativeMode() {
        if (!alternativeVisualMode) return false;
        if (checked) {
            iconFactor.setValues(0, 0.1);
            iconFactor.appear(1, 2.5);
        } else {
            iconFactor.setValues(1, -0.1);
            iconFactor.destroy(1, 2.5);
        }
        return true;
    }


    public void setListener(ICheckButtonListener listener) {
        this.listener = listener;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderCheckButton;
    }


    public void setAlternativeVisualMode(boolean alternativeVisualMode) {
        this.alternativeVisualMode = alternativeVisualMode;
    }


    @Override
    public String toString() {
        return "[CheckButton: " +
                title.string +
                "]";
    }
}
