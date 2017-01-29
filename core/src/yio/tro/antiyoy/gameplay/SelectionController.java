package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.SoundControllerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;

import java.util.ArrayList;
import java.util.ListIterator;

public class SelectionController {

    private final GameController gameController;
    float defaultBubbleRadius;
    float selectX;
    float selectY;
    Unit selectedUnit;
    public FactorYio selUnitFactor;
    public FactorYio selMoneyFactor;
    public FactorYio tipFactor;
    public FactorYio blackoutFactor;
    public int tipType;
    public int tipShowType;
    public int defTipDelay;
    long defTipSpawnTime;


    public SelectionController(GameController gameController) {
        this.gameController = gameController;

        defaultBubbleRadius = 0.01f * Gdx.graphics.getWidth();
        defTipDelay = 1000;
        selUnitFactor = new FactorYio();
        selMoneyFactor = new FactorYio();
        blackoutFactor = new FactorYio();
        tipFactor = new FactorYio();
    }


    void moveSelections() {
        for (Hex hex : gameController.fieldController.selectedHexes) hex.move();
        if (selectedUnit != null && selUnitFactor.needsToMove()) {
            selUnitFactor.move();
        }
    }


    void moveDefenseTips() {
        gameController.fieldController.defenseTipFactor.move();
        if (gameController.getCurrentTime() > defTipSpawnTime + defTipDelay) {
            if (gameController.fieldController.defenseTipFactor.getDy() >= 0)
                gameController.fieldController.defenseTipFactor.beginDestroying(1, 1);
            if (gameController.fieldController.defenseTipFactor.get() == 0 && gameController.fieldController.defenseTips.size() > 0) {
                ListIterator iterator = gameController.fieldController.defenseTips.listIterator();
                while (iterator.hasNext()) {
                    Hex hex = (Hex) iterator.next();
                    iterator.remove();
                }
            }
        }
    }


    public void awakeTip(int type) {
        tipFactor.setValues(0.01, 0); // should be 0.01 to avoid blinking
        tipFactor.beginSpawning(3, 2);
        tipType = type;
        tipShowType = type;
        selectedUnit = null;
        if ((tipType == 0 || tipType >= 5) && gameController.fieldController.moveZone.size() > 0)
            gameController.fieldController.hideMoveZone();
        gameController.updateCurrentPriceString();
    }


    void hideTip() {
        tipFactor.beginDestroying(1, 2);
        tipType = -1;
    }


    public int getTipType() {
        return tipType;
    }


    public boolean isSomethingSelected() {
        return gameController.fieldController.selectedHexes.size() > 0;
    }


    public void deselectAll() {
        for (int i = 0; i < gameController.fieldController.fWidth; i++)
            for (int j = 0; j < gameController.fieldController.fHeight; j++) {
                gameController.fieldController.field[i][j].selected = false;
            }
        ListIterator listIterator = gameController.fieldController.selectedHexes.listIterator();
        while (listIterator.hasNext()) {
            listIterator.next();
            listIterator.remove();
        }
//        if (selectedUnit != null) selectedUnit.selected = false;
        selectedUnit = null;
        selMoneyFactor.beginDestroying(3, 2);
        tipFactor.setValues(0, 0);
        tipFactor.beginDestroying(1, 1);
        gameController.fieldController.hideMoveZone();
        gameController.getYioGdxGame().menuControllerYio.hideBuildButtons();
        tipType = -1;
    }


    void selectAdjacentHexes(Hex startHex) {
        //        ArrayList<Hex> tempList = new ArrayList<Hex>();
//        Hex tempHex;
//        tempList.add(startHex);
//        while (tempList.size() > 0) {
//            tempHex = tempList.get(0);
//            tempHex.select();
//            if (!selectedHexes.contains(tempHex)) listIterator.add(tempHex);
//            for (int i=0; i<6; i++) {
//                Hex h = tempHex.adjacentHex(i);
//                if (h != null && h.active && !h.selected && h.colorIndex == tempHex.colorIndex && !tempList.contains(h)) {
//                    tempList.add(h);
//                }
//            }
//            tempList.remove(tempHex);
//        }
        gameController.fieldController.selectAdjacentHexes(startHex);
    }


    void updateSelectedProvinceMoney() {
        gameController.fieldController.updateSelectedProvinceMoney();
    }


    void showDefenseTip(Hex hex) {
        gameController.fieldController.defenseTips = new ArrayList<Hex>();
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.adjacentHex(i);
            if (adjHex.active && adjHex.sameColor(hex)) {
                gameController.fieldController.defenseTips.add(adjHex);
            }
        }
        gameController.fieldController.defenseTipFactor.setValues(0, 0);
        gameController.fieldController.defenseTipFactor.beginSpawning(3, 1);
        defTipSpawnTime = System.currentTimeMillis();
        gameController.fieldController.defTipHex = hex;
    }


    // this is probably the worst method in whole project :)
    public void focusedHexActions(Hex focusedHex) {
        // don't change order in this method
//        YioGdxGame.say(focusedHex.index1 + " " + focusedHex.index2);
        if (focusedHex.ignoreTouch) return;
        if (GameRules.inEditorMode) return;

        if (!focusedHex.active) {
            deselectAll();
            return;
        }

        boolean isSomethingSelected = false;
        if (gameController.fieldController.selectedHexes.size() > 0) isSomethingSelected = true;

        // building stuff
        if (tipFactor.get() > 0 && tipFactor.getDy() >= 0) {
            // build peacefully inside province
            if (canBuildOnHex(focusedHex, tipType)) {
                if (tipType == 0) {
                    // build tower
                    if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                        gameController.fieldController.buildTower(gameController.fieldController.selectedProvince, focusedHex);
                    }
                } else if (!GameRules.slay_rules && tipType == 5) {
                    // build farm
                    if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                        gameController.fieldController.buildFarm(gameController.fieldController.selectedProvince, focusedHex);
                    }
                } else if (!GameRules.slay_rules && tipType == 6) {
                    // build strong tower
                    if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                        gameController.fieldController.buildStrongTower(gameController.fieldController.selectedProvince, focusedHex);
                    }
                } else {
                    // build unit
                    gameController.fieldController.buildUnit(gameController.fieldController.selectedProvince, focusedHex, tipType);
                    tipType = -1;
                }
                gameController.fieldController.setResponseAnimHex(focusedHex);
                SoundControllerYio.playSound(SoundControllerYio.soundBuild);
                // else attack by building unit
            } else if (focusedHex.isInMoveZone() && focusedHex.colorIndex != gameController.getTurn() && tipType > 0 && gameController.fieldController.selectedProvince.canBuildUnit(tipType)) {
                gameController.fieldController.buildUnit(gameController.fieldController.selectedProvince, focusedHex, tipType);
                gameController.fieldController.selectedProvince = gameController.fieldController.getProvinceByHex(focusedHex); // when uniting provinces, selected province object may change
                gameController.fieldController.selectAdjacentHexes(focusedHex);
                tipType = -1;
                SoundControllerYio.playSound(SoundControllerYio.soundBuild);
            } else gameController.fieldController.setResponseAnimHex(focusedHex);
            hideTip();
            gameController.fieldController.hideMoveZone();
            return;
        }

        // deselect
        if (isSomethingSelected) {
            if (!focusedHex.selected && !focusedHex.inMoveZone) deselectAll();
            if (gameController.fieldController.moveZone.size() > 0 && !focusedHex.inMoveZone) {
                selectedUnit = null;
                gameController.fieldController.hideMoveZone();
            }
            if (focusedHex.selected && gameController.fieldController.moveZone.size() == 0 && focusedHex.containsBuilding() && focusedHex.objectInside != Hex.OBJECT_FARM) { // check to show defense tip
                showDefenseTip(focusedHex);
            }
        }

        // attack enemy province
        if (focusedHex.colorIndex != gameController.getTurn() && focusedHex.inMoveZone && selectedUnit != null) {
            gameController.takeSnapshot();
            gameController.moveUnit(selectedUnit, focusedHex, gameController.fieldController.selectedProvince);
            SoundControllerYio.playSound(SoundControllerYio.soundAttack);
            selectedUnit = null;
        }

        // select province
        if (gameController.isCurrentTurn(focusedHex.colorIndex) && gameController.fieldController.hexHasNeighbourWithColor(focusedHex, gameController.getTurn())) {
            gameController.fieldController.selectAdjacentHexes(focusedHex);
            isSomethingSelected = true;
        }

        // select and move unit peacefully
        if (isSomethingSelected) {
            if (selectedUnit == null) { // check to select unit
                if (focusedHex.containsUnit() && focusedHex.unit.isReadyToMove() && focusedHex.unit.moveFactor.get() == 1) {
                    selectedUnit = focusedHex.unit;
                    SoundControllerYio.playSound(SoundControllerYio.soundSelectUnit);
                    gameController.fieldController.detectAndShowMoveZone(selectedUnit.currentHex, selectedUnit.strength, GameRules.UNIT_MOVE_LIMIT);
                    selUnitFactor.setValues(0, 0);
                    selUnitFactor.beginSpawning(3, 2);
                    hideTip();
                }
            } else { // move unit peacefully
                if (focusedHex.inMoveZone && gameController.isCurrentTurn(focusedHex.colorIndex) && selectedUnit.canMoveToFriendlyHex(focusedHex)) {
                    gameController.takeSnapshot();
                    SoundControllerYio.playSound(SoundControllerYio.soundWalk);
                    gameController.moveUnit(selectedUnit, focusedHex, gameController.fieldController.selectedProvince);
                    selectedUnit = null;
                }
            }
        }
    }


    private boolean canBuildOnHex(Hex focusedHex, int tipType) {
        if (tipType == 6) { // strong tower
            return focusedHex.selected && (!focusedHex.containsBuilding() || focusedHex.objectInside == Hex.OBJECT_TOWER);
        }

        return focusedHex.selected && !focusedHex.containsBuilding();
    }


    void updateFocusedHex(int screenX, int screenY) {
        gameController.fieldController.updateFocusedHex(screenX, screenY);
    }


    public void setSelectedUnit(Unit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }


    public FactorYio getSelMoneyFactor() {
        return selMoneyFactor;
    }


    public FactorYio getBlackoutFactor() {
        return blackoutFactor;
    }
}