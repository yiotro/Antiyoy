package yio.tro.antiyoy.stuff;

public abstract class LongTapDetector {

    public static final int LONG_TAP_DELAY = 600;

    boolean performedCheck;
    long touchDownTime;
    PointYio initialTouch, currentTouch;
    boolean touched;
    int delay;


    public LongTapDetector() {
        performedCheck = false;
        initialTouch = new PointYio();
        currentTouch = new PointYio();
        touched = false;
        delay = LONG_TAP_DELAY;
    }


    public void setDelay(int delay) {
        this.delay = delay;
    }


    public void onTouchDown(PointYio touchPoint) {
        initialTouch.setBy(touchPoint);
        currentTouch.setBy(initialTouch);

        performedCheck = false;
        touched = true;
        touchDownTime = System.currentTimeMillis();
    }


    public void onTouchDrag(PointYio touchPoint) {
        currentTouch.setBy(touchPoint);
    }


    public void onTouchUp(PointYio touchPoint) {
        touched = false;
    }


    public void move() {
        if (!touched) return;
        if (performedCheck) return;
        if (System.currentTimeMillis() - touchDownTime <= delay) return;

        performedCheck = true;

        if (initialTouch.distanceTo(currentTouch) > 0.05f * GraphicsYio.width) return;

        onLongTapDetected();
    }


    public abstract void onLongTapDetected();

}
