package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;

public class CameraController implements EncodeableYio{

    public static final double ZOOM_CATCH_DISTANCE = 0.002;
    YioGdxGame yioGdxGame;
    GameController gameController;
    OrthographicCamera orthoCam;
    boolean blockDragMovement, kineticsEnabled, touched;
    int w, h;
    float boundWidth, boundHeight, zoomMinimum, zoomMaximum;
    public float camDx, camDy, camDz, viewZoomLevel, targetZoomLevel;
    long touchDownTime, lastTapTime;
    public RectangleYio field; // bounds of level
    public RectangleYio frame; // what is visible
    RectangleYio lastMultiTouch, currentMultiTouch;
    double zoomValues[][], kineticsSpeed;
    PointYio touchPos, position, viewPosition, defaultDragBounds, backVisBounds;
    PointYio delta, kinetics, actualDragBounds;
    private PointYio initialTouch;
    private float sensitivityModifier;
    PointYio tempPoint;


    public CameraController(GameController gameController) {
        this.gameController = gameController;
        yioGdxGame = gameController.yioGdxGame;
        w = (int) GraphicsYio.width;
        h = (int) GraphicsYio.height;

        touchPos = new PointYio();
        position = new PointYio();
        initialTouch = new PointYio();
        viewPosition = new PointYio();
        field = new RectangleYio();
        frame = new RectangleYio();
        currentMultiTouch = new RectangleYio();
        lastMultiTouch = new RectangleYio();
        defaultDragBounds = new PointYio();
        backVisBounds = new PointYio();
        delta = new PointYio();
        kinetics = new PointYio();
        actualDragBounds = new PointYio();
        tempPoint = new PointYio();

        zoomMinimum = 0.5f;
        kineticsSpeed = 0.01 * w;
        kineticsEnabled = false;
    }


    public void initLevels(int levelSize) {
        zoomValues = new double[][]{
                {0.8, 1.3, 1.1},
                {0.8, 1.3, 2.0},
                {0.8, 1.3, 2.1},
                {0.8, 1.3, 3.2}
        };

        updateUpperZoomLimit(levelSize);
    }


    public void updateUpperZoomLimit(int levelSize) {
        int zIndex = 0;
        switch (levelSize) {
            case LevelSize.SMALL:
                zIndex = 0;
                break;
            case LevelSize.MEDIUM:
                zIndex = 1;
                break;
            case LevelSize.BIG:
                zIndex = 2;
                break;
            case LevelSize.HUGE:
                zIndex = 3;
                break;
        }

        setZoomMaximum(zoomValues[zIndex][2]);
    }


    public void init(int levelSize) {
        initLevels(levelSize);

        if (orthoCam != null) {
            forceCameraMovementToRealPosition();
        }
    }


    private void forceCameraMovementToRealPosition() {
        for (int i = 0; i < 20; i++) {
            move();
        }
    }


    private void updateCurrentMultiTouch() {
        currentMultiTouch.set(0, 0, Gdx.input.getX(1) - Gdx.input.getX(0), Gdx.input.getY(1) - Gdx.input.getY(0));
    }


    private void updateLastMultiTouch() {
        lastMultiTouch.setBy(currentMultiTouch);
    }


    public void onTouchDown(int x, int y) {
        touched = gameController.currentTouchCount > 0;

        // initial touch with one finger
        if (gameController.currentTouchCount == 1) {
            touchDownTime = gameController.currentTime;
            blockDragMovement = false;
            initialTouch.set(x, y);
            touchPos.set(x, y);
            delta.set(0, 0);
        }

        // multi touch
        if (gameController.currentTouchCount >= 2) {
            blockDragMovement = true;
            updateCurrentMultiTouch();
            updateLastMultiTouch();
        }
    }


    public void onTouchDrag(int x, int y) {
        sensitivityModifier = 1.4f * SettingsManager.sensitivity;
        delta.x = sensitivityModifier * (touchPos.x - x) * viewZoomLevel;
        delta.y = sensitivityModifier * (touchPos.y - y) * viewZoomLevel;

        if (!blockDragMovement) {
            position.x += delta.x;
            position.y += delta.y;

            applyBoundsToPosition();
        }

        touchPos.set(x, y);

        // pinch to zoom
        if (gameController.currentTouchCount == 2) {
            updateCurrentMultiTouch();

            double currentDistance = Yio.distance(0, 0, currentMultiTouch.width, currentMultiTouch.height);
            double lastDistance = Yio.distance(0, 0, lastMultiTouch.width, lastMultiTouch.height);
            double zoomDelta = 0.004 * (lastDistance - currentDistance);

            changeZoomLevel(zoomDelta);

            updateLastMultiTouch();
        }
    }


    public void setTargetZoomLevel(float targetZoomLevel) {
        this.targetZoomLevel = targetZoomLevel;
    }


    public void setTargetZoomToMax() {
        setTargetZoomLevel(0.9f * zoomMaximum);
    }


    public void changeZoomLevel(double zoomDelta) {
        targetZoomLevel += zoomDelta;
        if (targetZoomLevel < zoomMinimum) targetZoomLevel = zoomMinimum;
        if (targetZoomLevel > zoomMaximum) targetZoomLevel = zoomMaximum;
    }


    private void applyBoundsToPosition() {
        if (position.x > actualDragBounds.x) position.x = actualDragBounds.x;
        if (position.x < -actualDragBounds.x) position.x = -actualDragBounds.x;
        if (position.y > actualDragBounds.y) position.y = actualDragBounds.y;
        if (position.y < -actualDragBounds.y) position.y = -actualDragBounds.y;
    }


    public void onTouchUp(int x, int y) {
        if (!touched) return;
        touched = gameController.currentTouchCount > 0;

        double speed = Yio.distance(0, 0, delta.x, delta.y);

        if (!blockDragMovement && (speed > kineticsSpeed || touchWasQuick())) {
            kineticsEnabled = true;
            kinetics.x = delta.x;
            kinetics.y = delta.y;
        }

        touchPos.set(x, y);
    }


    private boolean touchWasQuick() {
        return System.currentTimeMillis() - touchDownTime < 200;
    }


    void move() {
        updateDragBounds();
        updateField();

        moveKinetics();
        moveDrag();
        moveZoom();

        updateFrame();
        updateBackgroundVisibility();
    }


    private void updateDragBounds() {
        actualDragBounds.setBy(defaultDragBounds);
        actualDragBounds.x -= 0.4 * w * viewZoomLevel;
        actualDragBounds.y -= 0.45 * h * viewZoomLevel;

        if (actualDragBounds.x < 0) {
            actualDragBounds.x = 0;
        }

        if (actualDragBounds.y < 0) {
            actualDragBounds.y = 0;
        }
    }


    private void moveKinetics() {
        if (!kineticsEnabled) return;

        if (Yio.distance(0, 0, kinetics.x, kinetics.y) < 0.5 * kineticsSpeed) {
            kineticsEnabled = false;
        }

        position.x += kinetics.x;
        position.y += kinetics.y;

        applyBoundsToPosition();

        kinetics.x *= 0.85;
        kinetics.y *= 0.85;
    }


    private void updateBackgroundVisibility() {
        backVisBounds.setBy(defaultDragBounds);
        backVisBounds.x -= 0.5 * w * viewZoomLevel;
        backVisBounds.y -= 0.5 * h * viewZoomLevel;

        if (Math.abs(position.x) > backVisBounds.x || Math.abs(position.y) > backVisBounds.y) {
            gameController.setBackgroundVisible(true);
        } else {
            gameController.setBackgroundVisible(false);
        }
    }


    private void updateField() {
        field.x = 0.5f * w - orthoCam.position.x / orthoCam.zoom;
        field.y = 0.5f * h - orthoCam.position.y / orthoCam.zoom;
        field.width = boundWidth / orthoCam.zoom;
        field.height = boundHeight / orthoCam.zoom;
    }


    private void moveZoom() {
        camDz = 0.2f * (targetZoomLevel - viewZoomLevel);
        if (Math.abs(targetZoomLevel - viewZoomLevel) < ZOOM_CATCH_DISTANCE) return;

        yioGdxGame.gameView.orthoCam.zoom += camDz;
        viewZoomLevel += camDz;
        yioGdxGame.gameView.updateCam();
        applyBoundsToPosition(); // bounds may change on zoom
    }


    public boolean isPosInViewFrame(PointYio pos, float offset) {
        if (pos.x < frame.x - offset) return false;
        if (pos.x > frame.x + frame.width + offset) return false;
        if (pos.y < frame.y - offset) return false;
        if (pos.y > frame.y + frame.height + offset) return false;
        return true;
    }


    public boolean isRectangleInViewFrame(RectangleYio pos, float offset) {
        if (pos.x + pos.width < frame.x - offset) return false;
        if (pos.x > frame.x + frame.width + offset) return false;
        if (pos.y + pos.height < frame.y - offset) return false;
        if (pos.y > frame.y + frame.height + offset) return false;

        return true;
    }


    public void stop() {
        position.setBy(viewPosition);

        camDx = 0;
        camDy = 0;

        kinetics.set(0, 0);
    }


    void moveDrag() {
        camDx = 0.5f * (position.x - viewPosition.x);
        camDy = 0.5f * (position.y - viewPosition.y);

        viewPosition.x += camDx;
        viewPosition.y += camDy;

        yioGdxGame.gameView.orthoCam.translate(camDx, camDy);
        yioGdxGame.gameView.updateCam();
    }


    public void setBounds(float width, float height) {
        boundWidth = width;
        boundHeight = height;
        defaultDragBounds.set(boundWidth / 2, boundHeight / 2);
    }


    public void setZoomMaximum(double zoomMaximum) {
        this.zoomMaximum = (float) zoomMaximum;
    }


    void createCamera() {
        gameController.yioGdxGame.gameView.createOrthoCam();
        orthoCam = gameController.yioGdxGame.gameView.orthoCam;

        orthoCam.translate(
                (gameController.levelSizeManager.boundWidth - GraphicsYio.width) / 2,
                (gameController.levelSizeManager.boundHeight - GraphicsYio.height) / 2
        ); // focus camera of center

        gameController.yioGdxGame.gameView.updateCam();
        updateFrame();


//        orthoCam = yioGdxGame.gameView.orthoCam;
//        orthoCam.translate((boundWidth - w) / 2, (boundHeight - h) / 2); // focus camera of center
//        targetZoomLevel = orthoCam.zoom;
//        updateFrame();

        forceCameraMovementToRealPosition();
    }


    void defaultValues() {
        viewZoomLevel = 1;
        targetZoomLevel = 1;
        position.set(0, 0);
        viewPosition.setBy(position);
    }


    public void focusOnTheMiddleOfTheLand() {
        FieldManager fieldManager = gameController.fieldManager;
        if (fieldManager == null) return;

        ArrayList<Hex> activeHexes = fieldManager.activeHexes;
        if (activeHexes.size() == 0) return;

        Hex firstHex = activeHexes.get(0);
        float leftX = firstHex.pos.x;
        float rightX = firstHex.pos.x;
        float bottomY = firstHex.pos.y;
        float topY = firstHex.pos.y;

        for (Hex hex : activeHexes) {
            if (GameRules.fogOfWarEnabled && fieldManager.fogOfWarManager.isHexCoveredByFog(hex)) continue;
            if (hex.pos.x < leftX) {
                leftX = hex.pos.x;
            }
            if (hex.pos.x > rightX) {
                rightX = hex.pos.x;
            }
            if (hex.pos.y < bottomY) {
                bottomY = hex.pos.y;
            }
            if (hex.pos.y > topY) {
                topY = hex.pos.y;
            }
        }

        focusOnPoint(
                (leftX + rightX) / 2,
                (bottomY + topY) / 2
        );
        for (int i = 0; i < 100; i++) {
            move();
        }
    }


    void updateFrame() {
        frame.x = (0 - 0.5f * w) * orthoCam.zoom + orthoCam.position.x;
        frame.y = (0 - 0.5f * h) * orthoCam.zoom + orthoCam.position.y;
        frame.width = w * orthoCam.zoom;
        frame.height = h * orthoCam.zoom;
    }


    public double[][] getZoomValues() {
        return zoomValues;
    }


    public void forgetAboutLastTap() {
        lastTapTime = 0;
    }


    public float getTargetZoomLevel() {
        return targetZoomLevel;
    }


    public void focusOnPoint(PointYio position) {
        focusOnPoint(position.x, position.y);
    }


    boolean checkConditionsToEndTurn() {
        if (DebugFlags.testMode) return true;
        if (camDx > 0.01) return false;
        if (camDy > 0.01) return false;

        if (Math.abs(targetZoomLevel - viewZoomLevel) > ZOOM_CATCH_DISTANCE) {
            return false;
        }

        return true;
    }


    public void onEndTurnButtonPressed() {
        stop();
        gameController.resetCurrentTouchCount();
    }


    public boolean touchedAsClick() {
        return gameController.touchPoint.distanceTo(initialTouch) < 0.03 * GraphicsYio.width &&
                Math.abs(camDx) < 0.01 * GraphicsYio.width &&
                Math.abs(camDy) < 0.01 * GraphicsYio.width;
    }


    public void focusOnHexList(ArrayList<Hex> hexList) {
        tempPoint.reset();
        for (Hex hex : hexList) {
            tempPoint.add(hex.pos);
        }
        tempPoint.x /= hexList.size();
        tempPoint.y /= hexList.size();
        focusOnPoint(tempPoint);
    }


    public void focusOnPoint(double x, double y) {
        position.x = (float) (x - gameController.levelSizeManager.boundWidth / 2);
        position.y = (float) (y - gameController.levelSizeManager.boundHeight / 2);
    }


    public String getValuesAsString() {
        return Yio.roundUp((position.x + boundWidth / 2) / GraphicsYio.width, 2) + " " +
                Yio.roundUp((position.y + boundHeight / 2) / GraphicsYio.width, 2) + " " +
                Yio.roundUp(viewZoomLevel, 2);
    }


    @Override
    public String encode() {
        return getValuesAsString();
    }


    @Override
    public void decode(String source) {
        String[] split = source.split(" ");
        float x = Float.valueOf(split[0]);
        float y = Float.valueOf(split[1]);
        float z = Float.valueOf(split[2]);
        x *= GraphicsYio.width;
        x -= boundWidth / 2;
        y *= GraphicsYio.width;
        y -= boundHeight / 2;

        applyImmediately(x, y, z);
    }


    private void applyImmediately(double x, double y, double z) {
        setTargetZoomLevel((float) z);
        for (int i = 0; i < 100; i++) {
            move();
        }
        position.set(x, y);
        for (int i = 0; i < 100; i++) {
            move();
        }
    }
}
