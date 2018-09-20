package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.SoundControllerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

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
    public Unit selectedUnit;
    public FactorYio selUnitFactor;
    public FactorYio selMoneyFactor;
    public FactorYio tipFactor;
    public FactorYio blackoutFactor;
    public int tipType;
    public int tipShowType;
    public int defTipDelay;
    long defTipSpawnTime;
    private boolean isSomethingSelected;
    boolean readyToRenameCity;
    private Hex focusedHex;
    boolean areaSelectionMode;
    int asFilterColor; // area selection


    public SelectionController(GameController gameController) {
        this.gameController = gameController;

        defaultBubbleRadius = 0.01f * Gdx.graphics.getWidth();
        defTipDelay = 1000;
        selUnitFactor = new FactorYio();
        selMoneyFactor = new FactorYio();
        blackoutFactor = new FactorYio();
        tipFactor = new FactorYio();
        readyToRenameCity = false;
        areaSelectionMode = false;
        asFilterColor = -1;
    }


    void moveSelections() {
        for (Hex hex : gameController.fieldController.selectedHexes) hex.move();
        if (selectedUnit != null && selUnitFactor.hasToMove()) {
            selUnitFactor.move();
        }
        moveRenameCity();
    }


    private void moveRenameCity() {
        if (!readyToRenameCity) return;

        readyToRenameCity = false;
        if (!isSomethingSelected()) return;

        Province selectedProvince = gameController.fieldController.selectedProvince;
        if (selectedProvince == null) return;

        String name = selectedProvince.getName();

        Scenes.sceneKeyboard.create();
        Scenes.sceneKeyboard.setValue(name);
        Scenes.sceneKeyboard.setReaction(new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                if (input.length() == 0) return;

                Province selectedProvince = gameController.fieldController.selectedProvince;
                if (selectedProvince == null) return;

                Hex capital = selectedProvince.getCapital();
                gameController.namingManager.setHexName(capital, input);

                selectedProvince.updateName();
            }
        });
    }


    void moveDefenseTips() {
        FactorYio defenseTipFactor = gameController.fieldController.defenseTipFactor;
        defenseTipFactor.move();

        if (gameController.getCurrentTime() <= defTipSpawnTime + defTipDelay) return;

        if (defenseTipFactor.getDy() >= 0) {
            defenseTipFactor.destroy(1, 1);
        }

        ArrayList<Hex> defenseTips = gameController.fieldController.defenseTips;
        if (defenseTipFactor.get() == 0 && defenseTips.size() > 0) {
            ListIterator iterator = defenseTips.listIterator();
            while (iterator.hasNext()) {
                Hex hex = (Hex) iterator.next();
                iterator.remove();
            }
        }
    }


    public void awakeTip(int type) {
        tipFactor.setValues(0.01, 0); // should be 0.01 to avoid blinking
        tipFactor.appear(3, 2);
        tipType = type;
        tipShowType = type;
        selectedUnit = null;
        if (isTipTypeSolidObject() && getMoveZone().size() > 0) {
            hideMoveZone();
        }
        gameController.updateCurrentPriceString();
    }


    private ArrayList<Hex> getMoveZone() {
        return gameController.fieldController.moveZoneManager.moveZone;
    }


    private void hideMoveZone() {
        gameController.fieldController.moveZoneManager.hide();
    }


    public boolean checkForCityNameReaction() {
        if (!gameController.areCityNamesEnabled()) return false;
        if (!isSomethingSelected()) return false;

        RectangleYio pos = gameController.yioGdxGame.gameView.grManager.renderCityNames.pos;
        if (pos == null) return false;

        boolean pointInside = pos.isPointInside(gameController.convertedTouchPoint, 0);
        if (!pointInside) return false;

        readyToRenameCity = true;

        return true;
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
        tipFactor.destroy(1, 2);
        resetTipType();
    }


    private void resetTipType() {
        tipType = -1;
    }


    public int getTipType() {
        return tipType;
    }


    public void setAreaSelectionMode(boolean areaSelectionMode) {
        this.areaSelectionMode = areaSelectionMode;
    }


    public boolean isSomethingSelected() {
        return gameController.fieldController.isSomethingSelected();
    }


    public void deselectAll() {
        FieldController fieldController = gameController.fieldController;
        for (int i = 0; i < fieldController.fWidth; i++) {
            for (int j = 0; j < fieldController.fHeight; j++) {
                fieldController.field[i][j].selected = false;
            }
        }
        fieldController.selectedHexes.clear();
        selectedUnit = null;
        selMoneyFactor.destroy(3, 2);
        tipFactor.setValues(0, 0);
        tipFactor.destroy(1, 1);
        hideMoveZone();
        hideBuildOverlay();
        resetTipType();
        areaSelectionMode = false;
    }


    private void hideBuildOverlay() {
        if (Settings.fastConstruction) {
            Scenes.sceneFastConstructionPanel.hide();
        } else {
            Scenes.sceneSelectionOverlay.hide();
        }
    }


    void selectAdjacentHexes(Hex startHex) {
        gameController.fieldController.selectAdjacentHexes(startHex);
    }


    public void updateSelectedProvinceMoney() {
        gameController.fieldController.updateSelectedProvinceMoney();
    }


    void showDefenseTip(Hex hex) {
        FieldController fieldController = gameController.fieldController;
        ArrayList<Hex> defenseTips = fieldController.defenseTips;

        if (fieldController.defenseTipFactor.get() == 1) {
            defenseTips.clear();
        }

        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && adjHex.sameColor(hex)) {
                defenseTips.add(adjHex);
            }
        }
        fieldController.defenseTipFactor.setValues(0, 0);
        fieldController.defenseTipFactor.appear(3, 0.7);
        defTipSpawnTime = System.currentTimeMillis();
        fieldController.defTipHex = hex;
    }


    public Hex getDefSrcHex(Hex hex) {
        for (int i = 0; i < 6; i++) {
            Hex adjacentHex = hex.getAdjacentHex(i);
            if (adjacentHex == null) continue;
            if (!adjacentHex.active) continue;
            if (adjacentHex.colorIndex != hex.colorIndex) continue;
            if (!isHexGoodForDefenseTip(adjacentHex)) continue;

            return adjacentHex;
        }

        return null;
    }


    public void focusedHexActions(Hex focusedHex) {
        this.focusedHex = focusedHex;
        // don't change order in this method

        debug();

        if (focusedHex.ignoreTouch) return;
        if (GameRules.inEditorMode) return;

        if (areaSelectionMode) {
            reactionAreaSelection();
            return;
        }

        if (!focusedHex.active) {
            deselectAll();
            return;
        }

        updateIsSomethingSelected();

        if (isReadyToBuild()) {
            reactionBuildStuff();
            return;
        }

        reactionInsideSelection();

        reactionAttackEnemy();

        reactionSelectProvince();

        reactionSelectOrMovePeacefully();
    }


    private void reactionAreaSelection() {
        MoveZoneManager moveZoneManager = gameController.fieldController.moveZoneManager;

        if (!isHexAllowedForAreaSelection(focusedHex)) return;

        if (moveZoneManager.isHexInMoveZone(focusedHex)) {
            moveZoneManager.removeHexFromMoveZoneManually(focusedHex);
        } else {
            moveZoneManager.addHexToMoveZoneManually(focusedHex);
        }
    }


    private boolean isHexAllowedForAreaSelection(Hex hex) {
        if (hex.isNeutral()) return false;
        if (!hex.active) return false;

        if (asFilterColor == -1) return true;

        if (hex.colorIndex != asFilterColor) return false;
        if (gameController.fieldController.getProvinceByHex(hex) == null) return false;
        if (getMoveZone().size() > 0 && hex.colorIndex != getMoveZone().get(0).colorIndex) return false;

        return true;
    }


    private void debug() {
//        showDebugHexColors(focusedHex);
//        showProvinceHexListInConsole(focusedHex);
    }


    private void showProvinceHexListInConsole(Hex focusedHex) {
        System.out.println();
        System.out.println("Province:");

        Province provinceByHex = gameController.fieldController.getProvinceByHex(focusedHex);
        for (Hex hex : provinceByHex.hexList) {
            System.out.println(" - " + hex);
        }

        System.out.println();
    }


    private void showDebugHexColors(Hex focusedHex) {
        System.out.println();
        System.out.println("focusedHex.colorIndex = " + focusedHex.colorIndex);
        System.out.println("gameController.colorIndexViewOffset = " + gameController.colorIndexViewOffset);
        int colorIndexWithOffset = gameController.ruleset.getColorIndexWithOffset(focusedHex.colorIndex);
        System.out.println("colorIndexWithOffset = " + colorIndexWithOffset);
    }


    private void reactionSelectOrMovePeacefully() {
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
        gameController.fieldController.moveZoneManager.detectAndShowMoveZone(selectedUnit.currentHex, selectedUnit.strength, GameRules.UNIT_MOVE_LIMIT);
        selUnitFactor.setValues(0, 0);
        selUnitFactor.appear(3, 2);
        hideTip();
        return true;
    }


    private void reactionSelectProvince() {
        if (!gameController.isCurrentTurn(focusedHex.colorIndex)) return;
        if (!gameController.fieldController.hexHasNeighbourWithColor(focusedHex, gameController.getTurn())) return;

        gameController.fieldController.selectAdjacentHexes(focusedHex);
        isSomethingSelected = true;
    }


    private void reactionAttackEnemy() {
        if (focusedHex.colorIndex == gameController.getTurn()) return;
        if (!focusedHex.inMoveZone) return;
        if (selectedUnit == null) return;

        gameController.takeSnapshot();
        gameController.moveUnit(selectedUnit, focusedHex, gameController.fieldController.selectedProvince);
        SoundControllerYio.playSound(SoundControllerYio.soundAttack);
        selectedUnit = null;
    }


    private void reactionInsideSelection() {
        if (!isSomethingSelected) return;

        if (!focusedHex.selected && !focusedHex.inMoveZone) deselectAll();

        if (getMoveZone().size() > 0 && !focusedHex.inMoveZone) {
            selectedUnit = null;
            hideMoveZone();
        }

        if (defenseTipConditions()) {
            showAllDefenseTipsInProvince(focusedHex);
        }
    }


    private boolean defenseTipConditions() {
        if (!focusedHex.selected) return false;
        if (getMoveZone().size() != 0) return false;
        if (!isHexGoodForDefenseTip(focusedHex)) return false;

        return true;
    }


    private void showAllDefenseTipsInProvince(Hex srcHex) {
        Province provinceByHex = gameController.fieldController.getProvinceByHex(srcHex);

        for (Hex hex : provinceByHex.hexList) {
            if (!isHexGoodForDefenseTip(hex)) continue;
            if (hex.getPos().distanceTo(srcHex.getPos()) > 0.8f * GraphicsYio.width) continue;

            showDefenseTip(hex);
        }

        gameController.fieldController.defTipHex = provinceByHex.getCapital();
    }


    private boolean isHexGoodForDefenseTip(Hex hex) {
        if (!hex.containsBuilding()) return false;

        if (hex.objectInside == Obj.TOWER) return true;
        if (hex.objectInside == Obj.STRONG_TOWER) return true;
        if (hex.objectInside == Obj.TOWN) return true;

        return false;
    }


    private boolean reactionBuildStuff() {
        FieldController fieldController = gameController.fieldController;

        if (canBuildOnHex(focusedHex, tipType)) {
            buildSomethingOnHex(focusedHex);
            // else attack by building unit
        } else {
            if (unitBuildConditions()) {
                fieldController.buildUnit(fieldController.selectedProvince, focusedHex, tipType);
                fieldController.selectedProvince = fieldController.getProvinceByHex(focusedHex); // when uniting provinces, selected province object may change
                fieldController.selectAdjacentHexes(focusedHex);
                resetTipType();
                SoundControllerYio.playSound(SoundControllerYio.soundBuild);
            } else {
                fieldController.setResponseAnimHex(focusedHex);
            }
        }

        hideTip();
        Scenes.sceneFastConstructionPanel.checkToReappear();
        hideMoveZone();

        return true;
    }


    private boolean unitBuildConditions() {
        if (!focusedHex.isInMoveZone()) return false;
        if (focusedHex.colorIndex == gameController.getTurn()) return false;
        if (!isTipTypeUnit()) return false;
        if (!gameController.fieldController.selectedProvince.canBuildUnit(tipType)) return false;

        return true;
    }


    private boolean isReadyToBuild() {
        return tipFactor.get() > 0 && tipFactor.getDy() >= 0;
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
                return gameController.fieldController.selectedProvince.getCurrentFarmPrice();
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
            return focusedHex.selected && (!focusedHex.containsBuilding() || focusedHex.objectInside == Obj.TOWER);
        }

        return focusedHex.selected && !focusedHex.containsBuilding();
    }


    public void updateFocusedHex(int screenX, int screenY) {
        gameController.fieldController.updateFocusedHex(screenX, screenY);
    }


    public void setSelectedUnit(Unit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }


    public void setAsFilterColor(int asFilterColor) {
        this.asFilterColor = asFilterColor;
    }


    public int getAsFilterColor() {
        return asFilterColor;
    }


    public FactorYio getSelMoneyFactor() {
        return selMoneyFactor;
    }


    public FactorYio getBlackoutFactor() {
        return blackoutFactor;
    }
}