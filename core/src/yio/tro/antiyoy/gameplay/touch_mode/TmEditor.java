package yio.tro.antiyoy.gameplay.touch_mode;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.editor.LevelEditorManager;

public class TmEditor extends TouchMode{

    public TmEditor(GameController gameController) {
        super(gameController);
    }


    @Override
    public void onModeBegin() {

    }


    @Override
    public void onModeEnd() {

    }


    @Override
    public void move() {

    }


    @Override
    public boolean isCameraMovementEnabled() {
        return !getLevelEditor().isTouchCaptured();
    }


    @Override
    public void onTouchDown() {
        getLevelEditor().onTouchDown((int) getCurrentTouch().x, (int) getCurrentTouch().y);
    }


    @Override
    public void onTouchDrag() {
        getLevelEditor().onTouchDrag((int) getCurrentTouch().x, (int) getCurrentTouch().y);
    }


    @Override
    public void onTouchUp() {
        getLevelEditor().onTouchUp((int) getCurrentTouch().x, (int) getCurrentTouch().y);
    }


    private LevelEditorManager getLevelEditor() {
        return gameController.levelEditorManager;
    }


    @Override
    public boolean onClick() {
        return false;
    }


    @Override
    public String getNameKey() {
        return null;
    }
}
