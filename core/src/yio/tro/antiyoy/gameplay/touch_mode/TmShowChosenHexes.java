package yio.tro.antiyoy.gameplay.touch_mode;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

import java.util.ArrayList;

public class TmShowChosenHexes extends TouchMode{

    AbstractScene parentScene;


    public TmShowChosenHexes(GameController gameController) {
        super(gameController);
        parentScene = null;
    }


    @Override
    public void onModeBegin() {
        gameController.highlightManager.clear();
    }


    @Override
    public void onModeEnd() {
        gameController.highlightManager.unStuckAllItems();
    }


    @Override
    public void move() {

    }


    @Override
    public boolean isCameraMovementEnabled() {
        return true;
    }


    @Override
    public void onTouchDown() {

    }


    @Override
    public void onTouchDrag() {

    }


    public void highlightHexList(ArrayList<Hex> hexList) {
        gameController.highlightManager.highlightHexList(hexList, true);
    }


    @Override
    public void onTouchUp() {

    }


    public void setParentScene(AbstractScene parentScene) {
        this.parentScene = parentScene;
    }


    @Override
    public boolean onClick() {
        gameController.resetTouchMode();
        parentScene.create();
        return true;
    }


    @Override
    public String getNameKey() {
        return null;
    }
}
