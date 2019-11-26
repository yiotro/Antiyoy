package yio.tro.antiyoy.gameplay.touch_mode;

import yio.tro.antiyoy.gameplay.GameController;

public class TmIgnore extends TouchMode{

    public TmIgnore(GameController gameController) {
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
        return false;
    }


    @Override
    public void onTouchDown() {

    }


    @Override
    public void onTouchDrag() {

    }


    @Override
    public void onTouchUp() {

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
