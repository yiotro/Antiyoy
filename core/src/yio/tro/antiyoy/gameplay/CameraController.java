package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import yio.tro.antiyoy.GraphicsYio;
import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.Yio;
import yio.tro.antiyoy.gameplay.rules.GameRules;

public class CameraController {

    private final GameController gameController;
    public int touchDownX;
    public int touchDownY;
    public int maxTouchCount, currentTouchCount, lastTouchCount;
    public boolean multiTouchDetected;
    public long lastTimeTouched;
    public long lastTimeDragged;
    public boolean blockMultiInput;
    public float camDx;
    public float camDy;
    public float lastMultiTouchDistance;
    public float camDZoom;
    public float trackerZoom;
    public float fieldX1;
    public float fieldY1;
    public float fieldX2;
    public float fieldY2;
    public float frameX1;
    public float frameY1;
    public float frameX2;
    public float frameY2;
    public long touchDownTime;
    public boolean blockDragToRight;
    public boolean blockDragToLeft;
    public boolean blockDragToUp;
    public boolean blockDragToDown;
    public boolean backgroundVisible;
    int compensationOffsetY;
    public double currentCamSpeed;
    public double zoomUpperLimit;
    public double cameraOffset;
    public OrthographicCamera orthoCam;
    float deltaMovementFactor;


    public CameraController(GameController gameController) {
        this.gameController = gameController;

        cameraOffset = 0.1 * GraphicsYio.width;
        deltaMovementFactor = 48;
    }


    public void move() {
//        if (GameRules.inEditorMode && !gameController.getLevelEditor().isCameraMovementAllowed()) return;

        float k = Settings.sensitivity * deltaMovementFactor * 0.025f;
        gameController.yioGdxGame.gameView.orthoCam.translate(k * camDx, k * camDy);
        gameController.yioGdxGame.gameView.updateCam();
        if ((currentTouchCount == 0 && gameController.currentTime > lastTimeTouched + 10) || (currentTouchCount == 1 && gameController.currentTime > lastTimeDragged + 10)) {
            camDx *= 0.8;
            camDy *= 0.8;
        }
        currentCamSpeed = Yio.distance(0, 0, camDx, camDy);
        if (Math.abs(camDZoom) > 0.01) {
            if (trackerZoom > zoomUpperLimit && camDZoom > 0) {
                camDZoom = -0.1f;
                blockMultiInputForSomeTime(50);
            }
            if (trackerZoom < 0.5 && camDZoom < 0) {
                camDZoom = 0.1f;
                blockMultiInputForSomeTime(50);
            }
            gameController.yioGdxGame.gameView.orthoCam.zoom += 0.2 * camDZoom;
            trackerZoom += 0.2 * camDZoom;
            gameController.yioGdxGame.gameView.updateCam();
            if ((currentTouchCount == 0 && gameController.currentTime > lastTimeTouched + 10) || (currentTouchCount == 1 && gameController.currentTime > lastTimeDragged + 10)) {
                camDZoom *= 0.75;
            } else if (currentTouchCount > 1) {
                camDZoom *= 0.95;
            }
        }
        fieldX1 = 0.5f * GraphicsYio.width - orthoCam.position.x / orthoCam.zoom;
        fieldX2 = fieldX1 + gameController.boundWidth / orthoCam.zoom;
        fieldY1 = 0.5f * GraphicsYio.height - orthoCam.position.y / orthoCam.zoom;
        fieldY2 = fieldY1 + gameController.boundHeight / orthoCam.zoom;
        updateFrame();
        if (blockDragToLeft) blockDragToLeft = false;
        if (blockDragToRight) blockDragToRight = false;
        if (blockDragToUp) blockDragToUp = false;
        if (blockDragToDown) blockDragToDown = false;
        backgroundVisible = false;
        if (fieldX2 - fieldX1 < 1.1f * GraphicsYio.width) { //center
            float deltaX = 0.2f * (0.5f * gameController.boundWidth / orthoCam.zoom - orthoCam.position.x / orthoCam.zoom);
            gameController.yioGdxGame.gameView.orthoCam.translate(deltaX, 0);
            backgroundVisible = true;
        } else {
            if (fieldX1 > 0 || fieldX2 < GraphicsYio.width) {
                backgroundVisible = true;
            }
            if (fieldX1 > 0 + cameraOffset) {
                camDx = boundPower();
            }
            if (fieldX1 > -0.1 * GraphicsYio.width + cameraOffset) blockDragToLeft = true;
            if (fieldX2 < GraphicsYio.width - cameraOffset) {
                camDx = -boundPower();
            }
            if (fieldX2 < 1.1 * GraphicsYio.width - cameraOffset) blockDragToRight = true;
        }
        if (fieldY2 - fieldY1 < 1.1f * GraphicsYio.height) {
            float deltaY = 0.2f * (0.5f * gameController.boundHeight / orthoCam.zoom - orthoCam.position.y / orthoCam.zoom);
            gameController.yioGdxGame.gameView.orthoCam.translate(0, deltaY);
            backgroundVisible = true;
        } else {
            if (fieldY1 > 0 || fieldY2 < GraphicsYio.height) {
                backgroundVisible = true;
            }
            if (fieldY1 > 0 + cameraOffset) {
                camDy = boundPower();
            }
            if (fieldY1 > -0.1 * GraphicsYio.width + cameraOffset) blockDragToDown = true;
            if (fieldY2 < GraphicsYio.height - cameraOffset) {
                camDy = -boundPower();
            }
            if (fieldY2 < 1.1 * GraphicsYio.height - cameraOffset) blockDragToUp = true;
        }
    }


    public void onEndTurnButtonPressed() {
        camDx = 0;
        camDy = 0;
        resetCurrentTouchCount();
    }


    boolean checkConditionsToMarch() {
        if (currentTouchCount != 1) return false;
        if (gameController.currentTime - touchDownTime <= gameController.marchDelay) return false;
        if (!touchedAsClick()) return false;

        return true;
    }


    boolean checkConditionsToEndTurn() {
        if (currentCamSpeed > 0.01) return false;
        if (Math.abs(camDZoom) > 0.01) return false;

        return true;
    }


    public void resetCurrentTouchCount() {
        currentTouchCount = 0;
    }


    public float boundPower() {
        return 0.002f * GraphicsYio.width * trackerZoom * (1 + trackerZoom);
    }


    public void updateFrame() {
        frameX1 = (0 - 0.5f * GraphicsYio.width) * orthoCam.zoom + orthoCam.position.x;
        frameX2 = (GraphicsYio.width - 0.5f * GraphicsYio.width) * orthoCam.zoom + orthoCam.position.x;
        frameY1 = (0 - 0.5f * GraphicsYio.height) * orthoCam.zoom + orthoCam.position.y;
        frameY2 = (GraphicsYio.height - 0.5f * GraphicsYio.height) * orthoCam.zoom + orthoCam.position.y;
    }


    public void defaultCameraValues() {
        maxTouchCount = 0;
        resetCurrentTouchCount();
        compensationOffsetY = 0;
        trackerZoom = 1;
    }


    void updateZoomUpperLimit(int levelSize) {
        switch (levelSize) {
            case FieldController.SIZE_SMALL:
                zoomUpperLimit = 1.1;
                break;
            case FieldController.SIZE_MEDIUM:
                zoomUpperLimit = 1.7;
                break;
            case FieldController.SIZE_BIG:
                zoomUpperLimit = 2.1;
                break;
        }
    }


    public void createCamera() {
        gameController.yioGdxGame.gameView.createOrthoCam();
        this.orthoCam = gameController.yioGdxGame.gameView.orthoCam;
        orthoCam.translate((gameController.boundWidth - GraphicsYio.width) / 2, (gameController.boundHeight - GraphicsYio.height) / 2); // focus camera of center
        gameController.yioGdxGame.gameView.updateCam();
        updateFrame();
    }


    public void touchDown(int screenX, int screenY) {
        currentTouchCount++;
        touchDownX = screenX;
        touchDownY = screenY;
        if (blockMultiInput) blockMultiInput = false;
        if (blockMultiInput) return;
        if (currentTouchCount == 1) { // initial touch
            maxTouchCount = 1;
            multiTouchDetected = false;
            touchDownTime = System.currentTimeMillis();
            gameController.setCheckToMarch(true);
        } else { // second finger or more
            multiTouchDetected = true;
            lastMultiTouchDistance = (float) Yio.distance(Gdx.input.getX(0), Gdx.input.getY(0), Gdx.input.getX(1), Gdx.input.getY(1));
        }

        if (currentTouchCount > maxTouchCount) maxTouchCount = currentTouchCount;
        lastTouchCount = currentTouchCount;
    }


    public void touchUp() {
        lastTimeTouched = System.currentTimeMillis();
        currentTouchCount--;
        if (currentTouchCount < 0) currentTouchCount = 0;
        if (blockMultiInput) return;
        if (currentTouchCount == maxTouchCount - 1) {

        }
        if (currentTouchCount == 0) {
            if (touchedAsClick()) {
                gameController.onClick();
            }
            multiTouchDetected = false;
        }
        lastTouchCount = currentTouchCount;
    }


    public boolean touchedAsClick() {
        return !multiTouchDetected &&
                Yio.distance(gameController.screenX, gameController.screenY, touchDownX, touchDownY) < 0.03 * GraphicsYio.width &&
                Math.abs(camDx) < 0.01 * GraphicsYio.width &&
                Math.abs(camDy) < 0.01 * GraphicsYio.width;
    }


    public void touchDrag(int screenX, int screenY) {
        lastTimeDragged = System.currentTimeMillis();
        if (multiTouchDetected) {
            if (blockMultiInput) return;
            float currentMultiTouchDistance = (float) Yio.distance(Gdx.input.getX(0), Gdx.input.getY(0), Gdx.input.getX(1), Gdx.input.getY(1));
            camDZoom = lastMultiTouchDistance / currentMultiTouchDistance - 1;
            if (camDZoom < 0) camDZoom *= 0.3;
        } else {
            if (GameRules.inEditorMode && !gameController.getLevelEditor().isCameraMovementAllowed()) return;
            float currX, currY;
            currX = (gameController.screenX - screenX) * trackerZoom;
            currY = (gameController.screenY - screenY) * trackerZoom;
            gameController.screenX = screenX;
            gameController.screenY = screenY;
            if (blockDragToLeft && currX < 0) currX = 0;
            if (blockDragToRight && currX > 0) currX = 0;
            if (blockDragToUp && currY > 0) currY = 0;
            if (blockDragToDown && currY < 0) currY = 0;
            if (notTooSlow(currX, camDx)) {
                camDx = currX;
            }
            if (notTooSlow(currY, camDy)) {
                camDy = currY;
            }
        }
    }


    private void blockMultiInputForSomeTime(int time) {
        blockMultiInput = true;
//        timeToUnblockMultiInput = System.currentTimeMillis() + time;
    }


    public boolean notTooSlow(float curr, float cam) {
        return Math.abs(curr) > 0.5 * Math.abs(cam);
    }


    public void scrolled(int amount) {
        if (amount == 1) {
            camDZoom += 0.15f;
        } else if (amount == -1) {
            camDZoom -= 0.2f;
        }
    }
}