package yio.tro.antiyoy.menu.slider;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;

import java.util.ArrayList;

public class SliderYio extends InterfaceElement implements SliderListener {

    MenuControllerYio menuControllerYio;

    boolean isCurrentlyPressed, internalSegmentsHidden, solidWidth, accentVisible, touchable;
    boolean listenersEnabled;
    public int numberOfSegments;
    int minNumber;
    Animation animType;
    public float runnerValue, currentVerticalPos, circleSize, segmentSize, textWidth;
    float viewMagnifier, circleDefaultSize, verticalTouchOffset, linkedDelta, viewX, viewWidth;
    float circleSizeDelta;

    public FactorYio sizeFactor, appearFactor;
    RectangleYio pos;
    String valueString;
    public SliderParentElement parentElement;
    ArrayList<SliderListener> listeners;
    private RectangleYio touchRectangle; // used for debug
    SliderBehavior behavior;
    private float animDistance;
    public BitmapFont valueFont, titleFont;
    public PointYio titlePosition, valueStringPosition;
    public String title;
    float titleOffset, valueOffset;
    ButtonYio visualHook;


    public SliderYio(MenuControllerYio menuControllerYio, int id) {
        super(id);
        this.menuControllerYio = menuControllerYio;
        behavior = new SbDefault();
        appearFactor = new FactorYio();
        sizeFactor = new FactorYio();
        pos = new RectangleYio(0, 0, 0, 0);
        solidWidth = true;
        circleDefaultSize = 0.012f * Gdx.graphics.getHeight();
        circleSize = circleDefaultSize;
        listeners = new ArrayList<>();
        verticalTouchOffset = 0.1f * Gdx.graphics.getHeight();
        circleSizeDelta = 0.005f * Gdx.graphics.getHeight();
        internalSegmentsHidden = false;
        accentVisible = false;
        touchRectangle = new RectangleYio();
        touchable = true;
        listenersEnabled = true;
        valueFont = Fonts.smallerMenuFont;
        titleFont = Fonts.gameFont;
        titlePosition = new PointYio();
        valueStringPosition = new PointYio();
        titleOffset = 0.125f * GraphicsYio.width;
        valueOffset = 0.065f * GraphicsYio.width;
        visualHook = null;
        title = null;
    }


    boolean isCoorInsideSlider(float x, float y) {
        return x > pos.x - 0.05f * Gdx.graphics.getWidth() &&
                x < pos.x + pos.width + 0.05f * Gdx.graphics.getWidth() &&
                y > currentVerticalPos - verticalTouchOffset &&
                y < currentVerticalPos + verticalTouchOffset;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!touchable) return false;
        if (isCoorInsideSlider(screenX, screenY) && appearFactor.get() == 1) {
            sizeFactor.appear(3, 2);
            isCurrentlyPressed = true;
            setValueByX(screenX);
            return true;
        }
        return false;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        if (isCurrentlyPressed) {
            setValueByX(screenX);
            return true;
        }
        return false;
    }


    @Override
    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (isCurrentlyPressed) {
            sizeFactor.destroy(1, 1);
            isCurrentlyPressed = false;
            updateValueString();
            return true;
        }
        return false;
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderSlider;
    }


    void setValueByX(float x) {
        int lastIndex = getValueIndex();

        x -= pos.x;
        runnerValue = (float) (x / pos.width);

        if (runnerValue < 0) {
            runnerValue = 0;
        }
        if (runnerValue > 1) {
            runnerValue = 1;
        }

        if (lastIndex != getValueIndex()) {
            behavior.onValueChanged(this);
            updateValueString();
        }
    }


    void pullRunnerToCenterOfSegment() {
        double cx = getValueIndex() * segmentSize;
        double delta = cx - runnerValue;
        runnerValue += 0.2 * delta;
        limitRunnerValue();
    }


    private void limitRunnerValue() {
        if (runnerValue > 1) {
            runnerValue = 1;
        }
        if (runnerValue < 0) {
            runnerValue = 0;
        }
    }


    private void updateVerticalPos() {
        if (parentElement != null) {
            updateVerticalPosByLinkedButton();
            return;
        }
        switch (animType) {
            case up:
                animUp();
                break;
            case down:
                animDown();
                break;
            case from_center:
                animFromCenter();
                break;
            default:
            case def:
            case none:
                animNone();
                break;
        }
    }


    private void animFromCenter() {
        currentVerticalPos = (float) (GraphicsYio.height / 2 + (pos.y - GraphicsYio.height / 2) * appearFactor.get());
    }


    private void animDown() {
        currentVerticalPos = (float) (appearFactor.get() * (pos.y + 0.1f * Gdx.graphics.getHeight()) - 0.1f * Gdx.graphics.getHeight());
    }


    private void animUp() {
        currentVerticalPos = (float) ((1 - appearFactor.get()) * (1.1f * Gdx.graphics.getHeight() - pos.y) + pos.y);
    }


    private void animNone() {
        currentVerticalPos = (float) pos.y;
    }


    private void updateVerticalPosByLinkedButton() {
        currentVerticalPos = (float) (parentElement.getViewPosition().y + linkedDelta);
    }


    public float getAlpha() {
        if (appearFactor.get() < 0.5) return 0;

        return Math.min(1, 2 * (appearFactor.get() - 0.5f));
    }


    @Override
    public void move() {
        if (appearFactor.hasToMove()) {
            appearFactor.move();
            updateViewValues();
        }

        if (sizeFactor.hasToMove()) {
            sizeFactor.move();
        }

        if (appearFactor.get() == 0) return;

        updateCircleSize();
        updateVerticalPos();
        updateTitlePosition();
        updateValueStringPosition();

        if (!isCurrentlyPressed) {
            pullRunnerToCenterOfSegment();
        }
    }


    private void updateValueStringPosition() {
        valueStringPosition.x = viewX + viewWidth - textWidth;
        valueStringPosition.y = currentVerticalPos + valueOffset;
    }


    private void updateTitlePosition() {
        titlePosition.x = viewX - 0.015f * GraphicsYio.width;
        titlePosition.y = currentVerticalPos + titleOffset;
    }


    private void updateCircleSize() {
        circleSize = circleDefaultSize + circleSizeDelta * sizeFactor.get();
    }


    private void updateViewValues() {
        if (solidWidth) {
            viewWidth = (float) pos.width;
            viewX = (float) pos.x;
        } else {
            viewWidth = (float) (pos.width * appearFactor.get());
            viewX = (float) (pos.x + 0.5f * pos.width - 0.5f * viewWidth);
        }
    }


    public float getViewX() {
        return viewX;
    }


    public float getViewWidth() {
        return viewWidth;
    }


    public float getRunnerValueViewX() {
        return getViewX() + runnerValue * getViewWidth();
    }


    public float getRunnerValue() {
        return runnerValue;
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void setPosition(RectangleYio position) {
        pos.setBy(position);
    }


    public RectangleYio getPosition() {
        return pos;
    }


    @Override
    public void appear() {
        appearFactor.appear(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
        appearFactor.setValues(0.001, 0.001);
    }


    @Override
    public void destroy() {
        appearFactor.destroy(2, 2);
        appearFactor.setDy(0);
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public boolean isTouchable() {
        return touchable;
    }


    public void setValues(double runnerValue, int minNumber, int maxNumber, Animation animType) {
        setRunnerValue((float) runnerValue);
        setNumberOfSegments(maxNumber - minNumber);
        this.animType = animType;
        this.minNumber = minNumber;
        animDistance = -1;
        updateValueString();
    }


    public void setTitle(String titleKey) {
        title = LanguagesManager.getInstance().getString(titleKey);
    }


    public void setRunnerValue(float runnerValue) {
        if (runnerValue > 1) {
            runnerValue = 1;
        }

        this.runnerValue = runnerValue;
    }


    public void setValueIndex(int index) {
        setRunnerValue((float) index / numberOfSegments);
        updateValueString();
    }


    public int getValueIndex() {
        return (int) (runnerValue / segmentSize + 0.5);
    }


    public void setNumberOfSegments(int numberOfSegments) {
        if (this.numberOfSegments == numberOfSegments) return;

        this.numberOfSegments = numberOfSegments;
        segmentSize = 1.0f / numberOfSegments;
        viewMagnifier = (numberOfSegments + 1f) / numberOfSegments;
    }


    public void addListener(SliderListener listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }


    void notifyListeners() {
        if (!listenersEnabled) return;

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onSliderChange(this);
        }
    }


    public void setListenersEnabled(boolean listenersEnabled) {
        this.listenersEnabled = listenersEnabled;
    }


    @Override
    public void onSliderChange(SliderYio sliderYio) {
        behavior.onAnotherSliderValueChanged(this, sliderYio);
        updateValueString();
    }


    public float getSegmentCircleSize() {
        return 0.4f * circleSize;
    }


    public void setParentElement(SliderParentElement parentElement, double linkedDelta) {
        this.parentElement = parentElement;
        this.linkedDelta = (float) linkedDelta * GraphicsYio.height;
    }


    public float getSegmentLeftSidePos(int index) {
        return (float) (pos.x + index * segmentSize * pos.width);
    }


    public boolean isInternalSegmentsHidden() {
        return internalSegmentsHidden;
    }


    public void setInternalSegmentsHidden(boolean internalSegmentsHidden) {
        this.internalSegmentsHidden = internalSegmentsHidden;
    }


    public RectangleYio getTouchRectangle() {
        touchRectangle.x = pos.x - 0.05f * Gdx.graphics.getWidth();
        touchRectangle.y = currentVerticalPos - verticalTouchOffset;
        touchRectangle.width = pos.width + 0.1f * Gdx.graphics.getWidth();
        touchRectangle.height = 2 * verticalTouchOffset;
        ;
        return touchRectangle;
    }


    public void updateValueString() {
        valueString = behavior.getValueString(this);
        textWidth = GraphicsYio.getTextWidth(valueFont, valueString);
        notifyListeners();
    }


    public void setVerticalTouchOffset(float verticalTouchOffset) {
        this.verticalTouchOffset = verticalTouchOffset;
    }


    public String getValueString() {
        return valueString;
    }


    public boolean isAccentVisible() {
        return accentVisible;
    }


    public void setAccentVisible(boolean accentVisible) {
        this.accentVisible = accentVisible;
    }


    public void setSolidWidth(boolean solidWidth) {
        this.solidWidth = solidWidth;
    }


    public void setTitleOffset(float titleOffset) {
        this.titleOffset = titleOffset;
    }


    public int getMinNumber() {
        return minNumber;
    }


    public void setValueOffset(float valueOffset) {
        this.valueOffset = valueOffset;
    }


    public void setBehavior(SliderBehavior behavior) {
        this.behavior = behavior;
        updateValueString();
    }


    public float getLinkedDelta() {
        return linkedDelta;
    }


    public boolean hasVisualHook() {
        return visualHook != null;
    }


    public ButtonYio getVisualHook() {
        return visualHook;
    }


    public void setVisualHook(ButtonYio visualHook) {
        this.visualHook = visualHook;
    }


    @Override
    public String toString() {
        return "[Slider: " +
                title +
                "]";
    }
}
