package yio.tro.antiyoy.gameplay.touch_mode;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.SelectionManager;

public class TmDefault extends TouchMode{

    public TmDefault(GameController gameController) {
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
        return true;
    }


    @Override
    public void onTouchDown() {
        if (gameController.currentTouchCount == 1) {
            gameController.setCheckToMarch(true);
        }
    }


    @Override
    public void onTouchDrag() {

    }


    @Override
    public void onTouchUp() {

    }


    @Override
    public boolean onClick() {
        FieldController fieldController = gameController.fieldController;
        fieldController.updateFocusedHex();
        SelectionManager selectionManager = gameController.selectionManager;
        selectionManager.setFocusedHex(fieldController.focusedHex);
        gameController.showFocusedHexInConsole();

        if (selectionManager.checkForCityNameReaction()) return true;

        if (fieldController.focusedHex != null && gameController.isPlayerTurn()) {
            selectionManager.focusedHexActions(fieldController.focusedHex);
        }
        return true;
    }


    @Override
    public String getNameKey() {
        return null;
    }
}
