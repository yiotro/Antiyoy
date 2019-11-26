package yio.tro.antiyoy.gameplay.touch_mode;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.game_view.GameRender;
import yio.tro.antiyoy.stuff.PointYio;

import java.util.ArrayList;

public abstract class TouchMode {


    protected GameController gameController;
    public static ArrayList<TouchMode> touchModes;
    protected boolean alive;

    public static TmDefault tmDefault;
    public static TmEditor tmEditor;
    public static TmEditProvinces tmEditProvinces;
    public static TmShowChosenHexes tmShowChosenHexes;
    public static TmIgnore tmIgnore;
    // create tm lower


    public TouchMode(GameController gameController) {
        this.gameController = gameController;
        alive = true;
        touchModes.add(this);
    }


    public static void createModes(GameController gameController) {
        touchModes = new ArrayList<>();

        tmDefault = new TmDefault(gameController);
        tmEditor = new TmEditor(gameController);
        tmEditProvinces = new TmEditProvinces(gameController);
        tmShowChosenHexes = new TmShowChosenHexes(gameController);
        tmIgnore = new TmIgnore(gameController);
    }


    public abstract void onModeBegin();


    public abstract void onModeEnd();


    public abstract void move();


    public abstract boolean isCameraMovementEnabled();


    public void touchDownReaction() {
        if (isCameraMovementEnabled()) {
            gameController.cameraController.onTouchDown((int) getCurrentTouch().x, (int) getCurrentTouch().y);
        }

        onTouchDown();
    }


    public abstract void onTouchDown();


    public void touchDragReaction() {
        if (isCameraMovementEnabled()) {
            gameController.cameraController.onTouchDrag((int) getCurrentTouch().x, (int) getCurrentTouch().y);
        }

        onTouchDrag();
    }


    public abstract void onTouchDrag();


    public void touchUpReaction() {
        if (isCameraMovementEnabled()) {
            gameController.cameraController.onTouchUp((int) getCurrentTouch().x, (int) getCurrentTouch().y);
        }

        onTouchUp();
    }


    public abstract void onTouchUp();


    protected PointYio getCurrentTouch() {
        return gameController.touchPoint;
    }


    public abstract boolean onClick();


    public abstract String getNameKey();


    public boolean onMouseWheelScrolled(int amount) {
        return false;
    }


    public void resetTouchMode() {
        gameController.resetTouchMode();
    }


    public GameRender getRender() {
        return null;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName();
    }


    public boolean isReadyToBeRemoved() {
        return !alive;
    }


    public boolean isAlive() {
        return alive;
    }


    public void kill() {
        setAlive(false);
    }


    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
