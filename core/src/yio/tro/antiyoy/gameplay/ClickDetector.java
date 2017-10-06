package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;

public class ClickDetector {

    public static final int CLICK_DELAY = 250;

    PointYio touchDownPoint;
    float maxDistance, clickDistance, currentDistance;
    long touchDownTime;


    public ClickDetector() {
        clickDistance = 0.02f * GraphicsYio.width;

        touchDownPoint = new PointYio();
    }


    public boolean isClicked() {
        if (System.currentTimeMillis() - touchDownTime > CLICK_DELAY) return false;
        if (maxDistance > clickDistance) return false;

        return true;
    }


    public boolean touchDown(PointYio touchPoint) {
        touchDownPoint.setBy(touchPoint);
        touchDownTime = System.currentTimeMillis();
        maxDistance = 0;

        return false;
    }


    public boolean touchDrag(PointYio touchPoint) {
        checkToUpdateMaxDistance(touchPoint);

        return false;
    }


    public boolean touchUp(PointYio touchPoint) {
        checkToUpdateMaxDistance(touchPoint);

        return false;
    }


    private void checkToUpdateMaxDistance(PointYio touchPoint) {
        currentDistance = (float) touchDownPoint.distanceTo(touchPoint);

        if (currentDistance > maxDistance) {
            maxDistance = currentDistance;
        }
    }


}
