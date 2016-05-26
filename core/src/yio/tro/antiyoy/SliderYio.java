package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.factor_yio.FactorYio;

import java.util.ArrayList;

/**
 * Created by ivan on 15.08.2015.
 */
class SliderYio {

    private final MenuControllerLighty menuControllerLighty;
    float runnerValue, currentVerticalPos, circleSize, segmentSize, textWidth;
    float viewMagnifier, circleDefaultSize, verticalTouchOffset, viewX, viewWidth;
    FactorYio appearFactor;
    FactorYio sizeFactor;
    boolean fromUp, isCurrentlyPressed;
    int numberOfSegments, configureType, minNumber, index;
    public static final int CONFIGURE_SIZE = 0;
    public static final int CONFIGURE_HUMANS = 1;
    public static final int CONFIGURE_COLORS = 2;
    public static final int CONFIGURE_DIFFICULTY = 3;
    public static final int CONFIGURE_ON_OFF = 4;
    public static final int CONFIGURE_SKIN = 5;
    public static final int CONFIGURE_SLOT_NUMBER = 6;
    public static final int CONFIGURE_ASK_END_TURN = 7;
    public static final int CONFIGURE_ANIM_STYLE = 8;
    Rect pos;
    String valueString;
    ArrayList<SliderYio> listeners;


    public SliderYio(MenuControllerLighty menuControllerLighty) {
        this.menuControllerLighty = menuControllerLighty;
        appearFactor = new FactorYio();
        sizeFactor = new FactorYio();
        pos = new Rect(0, 0, 0, 0);
        fromUp = true;
        circleDefaultSize = 0.015f * Gdx.graphics.getHeight();
        circleSize = circleDefaultSize;
        listeners = new ArrayList<SliderYio>();
        verticalTouchOffset = 0.1f * Gdx.graphics.getHeight();
    }


    void setPos(double kx, double ky, double kw, double kh) {
        pos.x = (int) (kx * Gdx.graphics.getWidth());
        pos.y = (int) (ky * Gdx.graphics.getHeight());
        pos.width = (int) (kw * Gdx.graphics.getWidth());
        pos.height = (int) (kh * Gdx.graphics.getHeight());
    }


    private boolean isCoorInsideSlider(float x, float y) {
        return x > pos.x - 0.05f * Gdx.graphics.getWidth() &&
                x < pos.x + pos.width + 0.05f * Gdx.graphics.getWidth() &&
                y > currentVerticalPos - verticalTouchOffset &&
                y < currentVerticalPos + verticalTouchOffset;
    }


    boolean touchDown(float x, float y) {
        if (isCoorInsideSlider(x, y) && appearFactor.get() == 1) {
            sizeFactor.beginSpawning(3, 2);
            isCurrentlyPressed = true;
            setValueByX(x);
            return true;
        }
        return false;
    }


    boolean touchUp(float x, float y) {
        if (isCurrentlyPressed) {
            sizeFactor.beginDestroying(1, 1);
            isCurrentlyPressed = false;
            updateValueString();
            return true;
        }
        return false;
    }


    void touchDrag(float x, float y) {
        if (isCurrentlyPressed) {
            setValueByX(x);
        }
    }


    boolean isVisible() {
        return appearFactor.get() > 0;
    }


    private void setValueByX(float x) {
        x -= pos.x;
        runnerValue = x / pos.width;
        if (runnerValue < 0) runnerValue = 0;
        if (runnerValue > 1) runnerValue = 1;
        updateValueString();
    }


    private void pullRunnerToCenterOfSegment() {
        double cx = getCurrentRunnerIndex() * segmentSize;
        double delta = cx - runnerValue;
        runnerValue += 0.2 * delta;
    }


    void move() {
        if (appearFactor.needsToMove()) {
            appearFactor.move();
            viewWidth = pos.width * appearFactor.get();
            viewX = pos.x + 0.5f * pos.width - 0.5f * viewWidth;
        }
        if (sizeFactor.needsToMove()) sizeFactor.move();
        circleSize = circleDefaultSize + 0.01f * Gdx.graphics.getHeight() * sizeFactor.get();
        if (fromUp) {
            currentVerticalPos = (1 - appearFactor.get()) * (1.1f * Gdx.graphics.getHeight() - pos.y) + pos.y;
        } else {
            currentVerticalPos = appearFactor.get() * (pos.y + 0.1f * Gdx.graphics.getHeight()) - 0.1f * Gdx.graphics.getHeight();
        }
        if (!isCurrentlyPressed) pullRunnerToCenterOfSegment();
    }


    public void setValues(double runnerValue, int minNumber, int maxNumber, boolean fromUp, int configureType) {
        setRunnerValue((float) runnerValue);
        setNumberOfSegments(maxNumber - minNumber);
        setFromUp(fromUp);
        this.configureType = configureType;
        this.minNumber = minNumber;
        updateValueString();
    }


    public void setRunnerValue(float runnerValue) {
        this.runnerValue = runnerValue;
    }


    public int getCurrentRunnerIndex() {
        return (int) (runnerValue / segmentSize + 0.5);
    }


    private void setNumberOfSegments(int numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
        segmentSize = 1.01f / numberOfSegments;
        viewMagnifier = (numberOfSegments + 1f) / numberOfSegments;
    }


    public void addListener(SliderYio sliderYio) {
        if (listeners.contains(sliderYio)) return;
        listeners.add(sliderYio);
    }


    private void notifyListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).beNotifiedAboutChange(this);
        }
    }


    public void appear() {
        appearFactor.beginSpawning(2, 1.5);
    }


    public boolean textVisible() {
        if (appearFactor.get() > 0.5 && index > 3) return true;
        if (appearFactor.getGravity() >= 0) return true;
        return false;
    }


    public float getViewX() {
        return viewX;
    }


    public float getViewWidth() {
        return viewWidth;
    }


    private void beNotifiedAboutChange(SliderYio sliderYio) {
        int s = 0;
        switch (sliderYio.configureType) {
            case CONFIGURE_COLORS:
                s = sliderYio.getCurrentRunnerIndex() + sliderYio.minNumber;
                break;
            case CONFIGURE_SIZE:
                s = 2;
                if (sliderYio.getCurrentRunnerIndex() == 1) s = GameController.MAX_COLOR_NUMBER - 5;
                if (sliderYio.getCurrentRunnerIndex() == 2) s = GameController.MAX_COLOR_NUMBER - 3;
                break;
        }
        setNumberOfSegments(s);
        updateValueString();
    }


    float getSegmentCenterSize(int index) {
//        float cx = index * segmentSize;
//        float dist = Math.abs(runnerValue - cx);
//        if (dist > 0.5f * segmentSize) dist = 0.5f * segmentSize;
//        dist /= segmentSize;
//        dist *= 2;
//        if (!isCurrentlyPressed) dist = 1.0f;
//        float f = 0.5f + (1.0f - dist);
//        return f * circleDefaultSize;
        return 0.4f * circleSize;
    }


    float getSegmentLeftSidePos(int index) {
        return pos.x + index * segmentSize * pos.width;
    }


    void updateValueString() {
        LanguagesManager languagesManager = menuControllerLighty.languagesManager;
        switch (configureType) {
            default:
            case CONFIGURE_HUMANS:
                configureHumans(languagesManager);
                break;
            case CONFIGURE_COLORS:
                configureColors(languagesManager);
                break;
            case CONFIGURE_SIZE:
                configureSize(languagesManager);
                break;
            case CONFIGURE_DIFFICULTY:
                configureDifficulty(languagesManager);
                break;
            case CONFIGURE_ON_OFF:
                configureOnOff(languagesManager);
                break;
            case CONFIGURE_SKIN:
                configureSkin(languagesManager);
                break;
            case CONFIGURE_SLOT_NUMBER:
                configureSlotNumber(languagesManager);
                break;
            case CONFIGURE_ASK_END_TURN:
                configureAskToEndTurn(languagesManager);
                break;
            case CONFIGURE_ANIM_STYLE:
                configureAnimStyle();
                break;
        }
        textWidth = YioGdxGame.getTextWidth(YioGdxGame.gameFont, valueString);
        notifyListeners();
    }


    private void configureAnimStyle() {
        switch (getCurrentRunnerIndex()) {
            case 0:
                valueString = "simple";
                break;
            case 1:
                valueString = "lighty";
                break;
            case 2:
                valueString = "material";
                break;
            case 3:
                valueString = "playful";
                break;
        }
    }


    private void configureAskToEndTurn(LanguagesManager languagesManager) {
        switch (getCurrentRunnerIndex()) {
            case 0:
                valueString = languagesManager.getString("do_not_ask");
                break;
            case 1:
                valueString = languagesManager.getString("ask");
                break;
        }
    }


    private void configureSlotNumber(LanguagesManager languagesManager) {
        switch (getCurrentRunnerIndex()) {
            case 0:
                valueString = languagesManager.getString("slot_single");
                break;
            case 1:
                valueString = languagesManager.getString("slot_multiple");
                break;
        }
    }


    private void configureSkin(LanguagesManager languagesManager) {
        switch (getCurrentRunnerIndex()) {
            case 0:
                valueString = languagesManager.getString("original");
                break;
            case 1:
                valueString = languagesManager.getString("points");
                break;
            case 2:
                valueString = languagesManager.getString("grid");
                break;
        }
    }


    private void configureOnOff(LanguagesManager languagesManager) {
        if (getCurrentRunnerIndex() == 0)
            valueString = languagesManager.getString("off");
        else
            valueString = languagesManager.getString("on");
    }


    private void configureDifficulty(LanguagesManager languagesManager) {
        switch (getCurrentRunnerIndex()) {
            case 0:
                valueString = languagesManager.getString("easy");
                break;
            case 1:
                valueString = languagesManager.getString("normal");
                break;
            case 2:
                valueString = languagesManager.getString("hard");
                break;
            case 3:
                valueString = languagesManager.getString("expert");
                break;
        }
    }


    private void configureSize(LanguagesManager languagesManager) {
        int size = getCurrentRunnerIndex();
        switch (size) {
            default:
            case 0:
                valueString = languagesManager.getString("small");
                break;
            case 1:
                valueString = languagesManager.getString("medium");
                break;
            case 2:
                valueString = languagesManager.getString("big");
                break;
        }
    }


    private void configureColors(LanguagesManager languagesManager) {
        if (getCurrentRunnerIndex() + minNumber <= 4)
            valueString = (getCurrentRunnerIndex() + minNumber) + " " + languagesManager.getString("color");
        else
            valueString = (getCurrentRunnerIndex() + minNumber) + " " + languagesManager.getString("colors");
    }


    private void configureHumans(LanguagesManager languagesManager) {
        if (getCurrentRunnerIndex() + minNumber <= 1)
            valueString = (getCurrentRunnerIndex() + minNumber) + " " + languagesManager.getString("human1");
        else if (getCurrentRunnerIndex() + minNumber <= 4)
            valueString = (getCurrentRunnerIndex() + minNumber) + " " + languagesManager.getString("human2");
        else
            valueString = (getCurrentRunnerIndex() + minNumber) + " " + languagesManager.getString("human3");
    }


    public void setVerticalTouchOffset(float verticalTouchOffset) {
        this.verticalTouchOffset = verticalTouchOffset;
    }


    String getValueString() {
        return valueString;
    }


    private void setFromUp(boolean fromUp) {
        this.fromUp = fromUp;
    }
}
