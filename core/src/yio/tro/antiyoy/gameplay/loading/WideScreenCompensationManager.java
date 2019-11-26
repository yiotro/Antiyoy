package yio.tro.antiyoy.gameplay.loading;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.PointYio;

public class WideScreenCompensationManager {

    GameController gameController;
    private FieldManager fieldManager;
    private float top;
    private float bottom;
    private float deltaTop;
    private float deltaBottom;
    private float fixDelta;


    public WideScreenCompensationManager() {

    }


    public void perform() {
        fieldManager = gameController.fieldManager;
        if (fieldManager.activeHexes.size() == 0) return;

        updateTopAndBottom();
        updateDeltas();

        if (needVerticalFieldPosFix()) {
            doFix();
        }
    }


    private void doFix() {
        updateFixDelta();
        applyFixDelta();
    }


    private void applyFixDelta() {
        gameController.fieldManager.compensatoryOffset = fixDelta;
        gameController.fieldManager.updateHexPositions();
    }


    private void updateFixDelta() {
        float hexMedium = (top + bottom) / 2;
        fixDelta = (gameController.levelSizeManager.boundHeight / 2 - hexMedium);

        // to keep water texture aligned with hexes
        int rv = (int) (fixDelta / fieldManager.hexStep1);
        fixDelta = rv * fieldManager.hexStep1;
    }


    private boolean needVerticalFieldPosFix() {
        if (deltaTop < 0) return true;
        if (deltaBottom < 0) return true;
        if (GameRules.inEditorMode) return false;

        return true;
    }


    private void updateDeltas() {
        deltaTop = gameController.levelSizeManager.boundHeight - 2 * fieldManager.hexSize - top;
        deltaBottom = bottom - 2 * fieldManager.hexSize;
    }


    private void updateTopAndBottom() {
        top = 0;
        bottom = 0;
        boolean gotTop = false;
        boolean gotBottom = false;
        for (Hex activeHex : fieldManager.activeHexes) {
            PointYio pos = activeHex.getPos();

            if (!gotTop || pos.y > top) {
                top = pos.y;
                gotTop = true;
            }

            if (!gotBottom || pos.y < bottom) {
                bottom = pos.y;
                gotBottom = true;
            }
        }
    }


    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}

