package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.SoundControllerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;
import java.util.ListIterator;

public class SelectionController {

    public static final int TIP_INDEX_TOWER = 0;
    public static final int TIP_INDEX_UNIT_1 = 1;
    public static final int TIP_INDEX_UNIT_2 = 2;
    public static final int TIP_INDEX_UNIT_3 = 3;
    public static final int TIP_INDEX_UNIT_4 = 4;
    public static final int TIP_INDEX_FARM = 5;
    public static final int TIP_INDEX_STRONG_TOWER = 6;
    public static final int TIP_INDEX_TREE = 7;
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
    private boolean isSomethingSelected;


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
        if (isTipTypeSolidObject() && gameController.fieldController.moveZone.size() > 0)
            gameController.fieldController.hideMoveZone();
        gameController.updateCurrentPriceString();
    }


    private boolean isTipTypeSolidObject() {
        switch (tipType) {
            default:
                return false;
            case TIP_INDEX_TOWER:
            case TIP_INDEX_FARM:
            case TIP_INDEX_STRONG_TOWER:
            case TIP_INDEX_TREE:
                return true;
        }
    }


    private boolean isTipTypeUnit() {
        switch (tipType) {
            default:
                return false;
            case TIP_INDEX_UNIT_1:
            case TIP_INDEX_UNIT_2:
            case TIP_INDEX_UNIT_3:
            case TIP_INDEX_UNIT_4:
                return true;
        }
    }


    void hideTip() {
        tipFactor.beginDestroying(1, 2);
        resetTipType();
    }


    private void resetTipType() {
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
        resetTipType();
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


    public void updateSelectedProvinceMoney() {
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


    public void focusedHexActions(Hex focusedHex) {
        // don't change order in this method
//        YioGdxGame.say(focusedHex.index1 + " " + focusedHex.index2);
        if (focusedHex.ignoreTouch) return;
        if (GameRules.inEditorMode) return;

        if (!focusedHex.active) {
            deselectAll();
            return;
        }

        updateIsSomethingSelected();

        if (reactionBuildStuff(focusedHex)) return;

        reactionInsideSelection(focusedHex);

        reactionAttackEnemy(focusedHex);

        reactionSelectProvince(focusedHex);

        reactionSelectOrMovePeacefully(focusedHex);
    }


    private void reactionSelectOrMovePeacefully(Hex focusedHex) {
        if (!isSomethingSelected) return;

        if (checkToSelectUnit(focusedHex)) return;

        // unit is selected at this point
        if (!focusedHex.inMoveZone) return;
        if (!gameController.isCurrentTurn(focusedHex.colorIndex)) return;
        if (!selectedUnit.canMoveToFriendlyHex(focusedHex)) return;

        gameController.takeSnapshot();
        SoundControllerYio.playSound(SoundControllerYio.soundWalk);
        gameController.moveUnit(selectedUnit, focusedHex, gameController.fieldController.selectedProvince);
        selectedUnit = null;
    }


    private boolean checkToSelectUnit(Hex focusedHex) {
        if (selectedUnit != null) return false;
        if (!focusedHex.containsUnit()) return true;
        if (focusedHex.unit.moveFactor.get() != 1) return true;
        if (!focusedHex.unit.isReadyToMove()) return true;

        selectedUnit = focusedHex.unit;
        SoundControllerYio.playSound(SoundControllerYio.soundSelectUnit);
        gameController.fieldController.detectAndShowMoveZone(selectedUnit.currentHex, selectedUnit.strength, GameRules.UNIT_MOVE_LIMIT);
        selUnitFactor.setValues(0, 0);
        selUnitFactor.beginSpawning(3, 2);
        hideTip();
        return true;
    }


    private void reactionSelectProvince(Hex focusedHex) {
        if (!gameController.isCurrentTurn(focusedHex.colorIndex)) return;
        if (!gameController.fieldController.hexHasNeighbourWithColor(focusedHex, gameController.getTurn())) return;

        gameController.fieldController.selectAdjacentHexes(focusedHex);
        isSomethingSelected = true;
    }


    private void reactionAttackEnemy(Hex focusedHex) {
        if (focusedHex.colorIndex == gameController.getTurn()) return;
        if (!focusedHex.inMoveZone) return;
        if (selectedUnit == null) return;

        gameController.takeSnapshot();
        gameController.moveUnit(selectedUnit, focusedHex, gameController.fieldController.selectedProvince);
        SoundControllerYio.playSound(SoundControllerYio.soundAttack);
        selectedUnit = null;
    }


    private void reactionInsideSelection(Hex focusedHex) {
        if (!isSomethingSelected) return;

        if (!focusedHex.selected && !focusedHex.inMoveZone) deselectAll();

        if (gameController.fieldController.moveZone.size() > 0 && !focusedHex.inMoveZone) {
            selectedUnit = null;
            gameController.fieldController.hideMoveZone();
        }

        // check to show defense tip
        if (    focusedHex.selected &&
                gameController.fieldController.moveZone.size() == 0 &&
                focusedHex.containsBuilding() &&
                focusedHex.objectInside != Hex.OBJECT_FARM) {
            showDefenseTip(focusedHex);
        }
    }


    private boolean reactionBuildStuff(Hex focusedHex) {
        if (tipFactor.get() <= 0) return false;
        if (tipFactor.getDy() < 0) return false;

        if (canBuildOnHex(focusedHex, tipType)) {
            buildSomethingOnHex(focusedHex);
            // else attack by building unit
        } else if (focusedHex.isInMoveZone() && focusedHex.colorIndex != gameController.getTurn() && isTipTypeUnit() && gameController.fieldController.selectedProvince.canBuildUnit(tipType)) {
            gameController.fieldController.buildUnit(gameController.fieldController.selectedProvince, focusedHex, tipType);
            gameController.fieldController.selectedProvince = gameController.fieldController.getProvinceByHex(focusedHex); // when uniting provinces, selected province object may change
            gameController.fieldController.selectAdjacentHexes(focusedHex);
            resetTipType();
            SoundControllerYio.playSound(SoundControllerYio.soundBuild);
        } else {
            gameController.fieldController.setResponseAnimHex(focusedHex);
        }

        hideTip();
        gameController.fieldController.hideMoveZone();
        return true;
    }


    private void updateIsSomethingSelected() {
        isSomethingSelected = gameController.fieldController.selectedHexes.size() > 0;
    }


    public int getCurrentTipPrice() {
        switch (tipType) {
            default:
                return -1;
            case TIP_INDEX_TOWER:
                return GameRules.PRICE_TOWER;
            case TIP_INDEX_UNIT_1:
            case TIP_INDEX_UNIT_2:
            case TIP_INDEX_UNIT_3:
            case TIP_INDEX_UNIT_4:
                return (GameRules.PRICE_UNIT * tipType);
            case TIP_INDEX_FARM:
                return (GameRules.PRICE_FARM + gameController.fieldController.selectedProvince.getExtraFarmCost());
            case TIP_INDEX_STRONG_TOWER:
                return GameRules.PRICE_STRONG_TOWER;
            case TIP_INDEX_TREE:
                return GameRules.PRICE_TREE;
        }
    }


    private void buildSomethingOnHex(Hex focusedHex) {
        Province selectedProvince = gameController.fieldController.selectedProvince;
        switch (tipType) {
            case TIP_INDEX_TOWER:
                if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                    gameController.fieldController.buildTower(selectedProvince, focusedHex);
                }
                break;
            case TIP_INDEX_UNIT_1:
            case TIP_INDEX_UNIT_2:
            case TIP_INDEX_UNIT_3:
            case TIP_INDEX_UNIT_4:
                gameController.fieldController.buildUnit(selectedProvince, focusedHex, tipType);
                break;
            case TIP_INDEX_FARM:
                if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                    gameController.fieldController.buildFarm(selectedProvince, focusedHex);
                }
                break;
            case TIP_INDEX_STRONG_TOWER:
                if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                    gameController.fieldController.buildStrongTower(selectedProvince, focusedHex);
                }
                break;
            case TIP_INDEX_TREE:
                if (focusedHex.isFree()) {
                    gameController.fieldController.buildTree(selectedProvince, focusedHex);
                }
                break;
        }

        resetTipType();
        gameController.fieldController.setResponseAnimHex(focusedHex);
        SoundControllerYio.playSound(SoundControllerYio.soundBuild);
    }


    private boolean canBuildOnHex(Hex focusedHex, int tipType) {
        if (tipType == TIP_INDEX_STRONG_TOWER) { // strong tower
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