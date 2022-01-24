package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import yio.tro.antiyoy.KeyboardManager;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RepeatYio;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.ListIterator;

public class SelectionManager {

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
    Hex focusedHex;
    boolean areaSelectionMode;
    int asFilterFraction; // area selection
    public ArrayList<TextHintItem> hintItems;
    ObjectPoolYio<TextHintItem> poolHintItems;
    RepeatYio<SelectionManager> repeatRemoveInactiveHintItems;


    public SelectionManager(GameController gameController) {
        this.gameController = gameController;

        defaultBubbleRadius = 0.01f * Gdx.graphics.getWidth();
        defTipDelay = 1000;
        selUnitFactor = new FactorYio();
        selMoneyFactor = new FactorYio();
        blackoutFactor = new FactorYio();
        tipFactor = new FactorYio();
        readyToRenameCity = false;
        areaSelectionMode = false;
        asFilterFraction = -1;
        hintItems = new ArrayList<>();
        initPools();
        initRepeats();
    }


    private void initRepeats() {
        repeatRemoveInactiveHintItems = new RepeatYio<SelectionManager>(this, 60) {
            @Override
            public void performAction() {
                parent.removeInactiveHintItems();
            }
        };
    }


    private void initPools() {
        poolHintItems = new ObjectPoolYio<TextHintItem>(hintItems) {
            @Override
            public TextHintItem makeNewObject() {
                return new TextHintItem(gameController);
            }
        };
    }


    public void defaultValues() {
        setSelectedUnit(null);
        poolHintItems.clearExternalList();
    }


    void moveSelections() {
        moveHintItems();
        for (Hex hex : gameController.fieldManager.selectedHexes) hex.move();
        if (selectedUnit != null && selUnitFactor.hasToMove()) {
            selUnitFactor.move();
        }
        moveRenameCity();
    }


    private void moveHintItems() {
        repeatRemoveInactiveHintItems.move();
        for (TextHintItem hintItem : hintItems) {
            hintItem.move();
        }
    }


    public void showHexHint(Hex hex, String string) {
        TextHintItem freshObject = poolHintItems.getFreshObject();
        freshObject.set(hex, string);
    }


    void removeInactiveHintItems() {
        for (int i = hintItems.size() - 1; i >= 0; i--) {
            TextHintItem textHintItem = hintItems.get(i);
            if (textHintItem.isActive()) continue;
            poolHintItems.removeFromExternalList(textHintItem);
        }
    }


    private void moveRenameCity() {
        if (!readyToRenameCity) return;

        readyToRenameCity = false;
        if (!isSomethingSelected()) return;

        Province selectedProvince = gameController.fieldManager.selectedProvince;
        if (selectedProvince == null) return;

        String name = selectedProvince.getName();

        KeyboardManager.getInstance().apply(name, new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                if (input.length() == 0) return;

                Province selectedProvince = gameController.fieldManager.selectedProvince;
                if (selectedProvince == null) return;

                Hex capital = selectedProvince.getCapital();
                gameController.namingManager.setHexName(capital, input);

                selectedProvince.updateName();
            }
        });
    }


    void moveDefenseTips() {
        FactorYio defenseTipFactor = gameController.fieldManager.defenseTipFactor;
        defenseTipFactor.move();

        if (gameController.getCurrentTime() <= defTipSpawnTime + defTipDelay) return;

        if (defenseTipFactor.getDy() >= 0) {
            defenseTipFactor.destroy(1, 1);
        }

        ArrayList<Hex> defenseTips = gameController.fieldManager.defenseTips;
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
        return gameController.fieldManager.moveZoneManager.moveZone;
    }


    private void hideMoveZone() {
        gameController.fieldManager.moveZoneManager.hide();
    }


    public boolean checkForCityNameReaction() {
        if (!gameController.areCityNamesEnabled()) return false;
        if (!isSomethingSelected()) return false;

        if (focusedHex == null) return false;
        if (focusedHex.objectInside != Obj.TOWN) return false;
        if (!focusedHex.active) return false;
        if (!gameController.isCurrentTurn(focusedHex.fraction)) return false;
        if (!focusedHex.isSelected()) return false;

        readyToRenameCity = true;

        return true;
    }


    private boolean isTipTypeSolidObject() {
        switch (tipType) {
            default:
                return false;
            case SelectionTipType.TOWER:
            case SelectionTipType.FARM:
            case SelectionTipType.STRONG_TOWER:
            case SelectionTipType.TREE:
                return true;
        }
    }


    private boolean isTipTypeUnit() {
        switch (tipType) {
            default:
                return false;
            case SelectionTipType.UNIT_1:
            case SelectionTipType.UNIT_2:
            case SelectionTipType.UNIT_3:
            case SelectionTipType.UNIT_4:
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
        return gameController.fieldManager.isSomethingSelected();
    }


    public void deselectAll() {
        FieldManager fieldManager = gameController.fieldManager;
        for (int i = 0; i < fieldManager.fWidth; i++) {
            for (int j = 0; j < fieldManager.fHeight; j++) {
                Hex hex = fieldManager.field[i][j];
                if (hex == null) continue;
                hex.selected = false;
            }
        }
        fieldManager.selectedHexes.clear();
        selectedUnit = null;
        selMoneyFactor.setDy(0);
        selMoneyFactor.destroy(1, 1);
        tipFactor.setValues(0, 0);
        tipFactor.destroy(1, 1);
        hideMoveZone();
        hideMenuOverlay();
        resetTipType();
        areaSelectionMode = false;
    }


    private void hideMenuOverlay() {
        Scenes.sceneFastConstructionPanel.hide();
        Scenes.sceneSelectionOverlay.hide();
        Scenes.sceneAreaSelectionUI.hide();
        Scenes.sceneFinances.hide();
    }


    void selectAdjacentHexes(Hex startHex) {
        gameController.fieldManager.selectAdjacentHexes(startHex);
    }


    void showDefenseTip(Hex hex) {
        FieldManager fieldManager = gameController.fieldManager;
        ArrayList<Hex> defenseTips = fieldManager.defenseTips;

        if (fieldManager.defenseTipFactor.get() == 1) {
            defenseTips.clear();
        }

        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && adjHex.sameFraction(hex)) {
                defenseTips.add(adjHex);
            }
        }
        fieldManager.defenseTipFactor.setValues(0, 0);
        fieldManager.defenseTipFactor.appear(3, 0.7);
        defTipSpawnTime = System.currentTimeMillis();
        fieldManager.defTipHex = hex;
    }


    public Hex getDefSrcHex(Hex hex) {
        for (int i = 0; i < 6; i++) {
            Hex adjacentHex = hex.getAdjacentHex(i);
            if (adjacentHex == null) continue;
            if (!adjacentHex.active) continue;
            if (adjacentHex.fraction != hex.fraction) continue;
            if (!isHexGoodForDefenseTip(adjacentHex)) continue;

            return adjacentHex;
        }

        return null;
    }


    public void setFocusedHex(Hex focusedHex) {
        this.focusedHex = focusedHex;
    }


    public void focusedHexActions(Hex focusedHex) {
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

        updateIsSomethingSelectedState();

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
        MoveZoneManager moveZoneManager = gameController.fieldManager.moveZoneManager;

        if (!isHexAllowedForAreaSelection(focusedHex)) return;

        if (moveZoneManager.isHexInMoveZone(focusedHex)) {
            moveZoneManager.removeHexFromMoveZoneManually(focusedHex);
        } else {
            moveZoneManager.addHexToMoveZoneManually(focusedHex);
            showHexPrice(focusedHex);
        }
    }


    private void showHexPrice(Hex hex) {
        showHexHint(hex, "$" + gameController.fieldManager.diplomacyManager.getHexPrice(hex));
    }


    private boolean isHexAllowedForAreaSelection(Hex hex) {
        if (hex.isNeutral()) return false;
        if (!hex.active) return false;

        if (asFilterFraction == -1) return true;

        if (hex.fraction != asFilterFraction) return false;
        if (gameController.fieldManager.getProvinceByHex(hex) == null) return false;

        return true;
    }


    private void debug() {

    }


    private void showProvinceHexListInConsole(Hex focusedHex) {
        System.out.println();
        System.out.println("Province:");

        Province provinceByHex = gameController.fieldManager.getProvinceByHex(focusedHex);
        for (Hex hex : provinceByHex.hexList) {
            System.out.println(" - " + hex);
        }

        System.out.println();
    }


    private void reactionSelectOrMovePeacefully() {
        if (!isSomethingSelected) return;

        if (checkToSelectUnit(focusedHex)) return;

        // unit is selected at this point
        if (!focusedHex.inMoveZone) return;
        if (!gameController.isCurrentTurn(focusedHex.fraction)) return;
        if (!selectedUnit.canMoveToFriendlyHex(focusedHex)) return;

        applyUnitMoveAction(selectedUnit, focusedHex, gameController.fieldManager.selectedProvince, SoundManagerYio.soundWalk);
    }


    private boolean checkToSelectUnit(Hex focusedHex) {
        if (selectedUnit != null) return false;
        if (!focusedHex.containsUnit()) return true;
        if (focusedHex.unit.moveFactor.get() != 1) return true;
        if (!focusedHex.unit.isReadyToMove()) return true;
        if (focusedHex.fraction != gameController.turn) return true;

        SoundManagerYio.playSound(SoundManagerYio.soundSelectUnit);
        applyUnitSelection(focusedHex.unit);
        hideTip();
        return true;
    }


    public void applyUnitSelection(Unit unit) {
        selectedUnit = unit;
        MoveZoneManager moveZoneManager = gameController.fieldManager.moveZoneManager;
        moveZoneManager.detectAndShowMoveZone(selectedUnit.currentHex, selectedUnit.strength, GameRules.UNIT_MOVE_LIMIT);
        selUnitFactor.setValues(0, 0);
        selUnitFactor.appear(3, 2);
    }


    private void reactionSelectProvince() {
        if (!gameController.isCurrentTurn(focusedHex.fraction)) return;
        if (!gameController.fieldManager.hexHasNeighbourWithFraction(focusedHex, gameController.getTurn())) return;

        applyProvinceSelection(focusedHex);
    }


    public void applyProvinceSelection(Hex hex) {
        gameController.fieldManager.selectAdjacentHexes(hex);
        if (gameController.fieldManager.selectedProvince == null) return;
        isSomethingSelected = true;
    }


    private void reactionAttackEnemy() {
        if (gameController.isCurrentTurn(focusedHex.fraction)) return;
        if (!focusedHex.inMoveZone) return;
        if (selectedUnit == null) return;

        applyUnitMoveAction(selectedUnit, focusedHex, gameController.fieldManager.selectedProvince, SoundManagerYio.soundAttack);
    }


    private void applyUnitMoveAction(Unit unit, Hex targetHex, Province province, Sound sound) {
        gameController.takeSnapshot();
        gameController.moveUnit(unit, targetHex, province);
        SoundManagerYio.playSound(sound);
        selectedUnit = null;

        checkToApplyAutomaticTransitionToNextUnit(unit);
    }


    private void checkToApplyAutomaticTransitionToNextUnit(Unit previousUnit) {
        if (!SettingsManager.automaticTransition) return;
        if (GameRules.tutorialMode) return;

        AutomaticTransitionWorker automaticTransitionWorker;
        automaticTransitionWorker = gameController.fieldManager.automaticTransitionWorker;
        Unit nextUnit = automaticTransitionWorker.findNextUnit(previousUnit, gameController.turn);

        automaticTransitionWorker.applyUnitSelection(nextUnit);
    }


    private void reactionInsideSelection() {
        if (!isSomethingSelected) return;

        if (!focusedHex.selected && !focusedHex.inMoveZone) {
            deselectAll();
        }

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
        Province provinceByHex = gameController.fieldManager.getProvinceByHex(srcHex);

        for (Hex hex : provinceByHex.hexList) {
            if (!isHexGoodForDefenseTip(hex)) continue;
            if (hex.getPos().distanceTo(srcHex.getPos()) > 0.8f * GraphicsYio.width) continue;

            showDefenseTip(hex);
        }

        gameController.fieldManager.defTipHex = provinceByHex.getCapital();
    }


    private boolean isHexGoodForDefenseTip(Hex hex) {
        if (!hex.containsBuilding()) return false;

        if (hex.objectInside == Obj.TOWER) return true;
        if (hex.objectInside == Obj.STRONG_TOWER) return true;
        if (hex.objectInside == Obj.TOWN) return true;

        return false;
    }


    private boolean reactionBuildStuff() {
        FieldManager fieldManager = gameController.fieldManager;

        if (canBuildOnHex(focusedHex, tipType)) {
            buildSomethingOnHex(focusedHex);
            // else attack by building unit
        } else {
            if (unitBuildConditions()) {
                fieldManager.buildUnit(fieldManager.selectedProvince, focusedHex, tipType);
                fieldManager.selectedProvince = fieldManager.getProvinceByHex(focusedHex); // when uniting provinces, selected province object may change
                fieldManager.selectAdjacentHexes(focusedHex);
                resetTipType();
                SoundManagerYio.playSound(SoundManagerYio.soundBuild);
            } else {
                fieldManager.setResponseAnimHex(focusedHex);
            }
        }

        hideTip();
        fieldManager.showBuildOverlay();
        hideMoveZone();

        return true;
    }


    private boolean unitBuildConditions() {
        if (!focusedHex.isInMoveZone()) return false;
        if (gameController.isCurrentTurn(focusedHex.fraction)) return false;
        if (!isTipTypeUnit()) return false;
        Province selectedProvince = gameController.fieldManager.selectedProvince;
        if (selectedProvince == null) return false;
        if (!selectedProvince.canBuildUnit(tipType)) return false;

        return true;
    }


    private boolean isReadyToBuild() {
        return tipFactor.get() > 0 && tipFactor.getDy() >= 0;
    }


    private void updateIsSomethingSelectedState() {
        isSomethingSelected = gameController.fieldManager.selectedHexes.size() > 0;
    }


    public int getCurrentTipPrice() {
        switch (tipType) {
            default:
                return -1;
            case SelectionTipType.TOWER:
                return GameRules.PRICE_TOWER;
            case SelectionTipType.UNIT_1:
            case SelectionTipType.UNIT_2:
            case SelectionTipType.UNIT_3:
            case SelectionTipType.UNIT_4:
                return (GameRules.PRICE_UNIT * tipType);
            case SelectionTipType.FARM:
                Province selectedProvince = gameController.fieldManager.selectedProvince;
                if (selectedProvince == null) return 9999;
                return selectedProvince.getCurrentFarmPrice();
            case SelectionTipType.STRONG_TOWER:
                return GameRules.PRICE_STRONG_TOWER;
            case SelectionTipType.TREE:
                return GameRules.PRICE_TREE;
        }
    }


    private void buildSomethingOnHex(Hex focusedHex) {
        Province selectedProvince = gameController.fieldManager.selectedProvince;
        switch (tipType) {
            case SelectionTipType.TOWER:
                if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                    gameController.fieldManager.buildTower(selectedProvince, focusedHex);
                }
                break;
            case SelectionTipType.UNIT_1:
            case SelectionTipType.UNIT_2:
            case SelectionTipType.UNIT_3:
            case SelectionTipType.UNIT_4:
                gameController.fieldManager.buildUnit(selectedProvince, focusedHex, tipType);
                break;
            case SelectionTipType.FARM:
                if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                    gameController.fieldManager.buildFarm(selectedProvince, focusedHex);
                }
                break;
            case SelectionTipType.STRONG_TOWER:
                if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                    gameController.fieldManager.buildStrongTower(selectedProvince, focusedHex);
                }
                break;
            case SelectionTipType.TREE:
                if (focusedHex.isFree()) {
                    gameController.fieldManager.buildTree(selectedProvince, focusedHex);
                }
                break;
        }

        resetTipType();
        gameController.fieldManager.setResponseAnimHex(focusedHex);
        SoundManagerYio.playSound(SoundManagerYio.soundBuild);
    }


    private boolean canBuildOnHex(Hex focusedHex, int tipType) {
        if (tipType == SelectionTipType.STRONG_TOWER) { // strong tower
            return focusedHex.selected && (!focusedHex.containsBuilding() || focusedHex.objectInside == Obj.TOWER);
        }

        return focusedHex.selected && !focusedHex.containsBuilding();
    }


    public void updateFocusedHex(int screenX, int screenY) {
        gameController.fieldManager.updateFocusedHex(screenX, screenY);
    }


    public void setSelectedUnit(Unit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }


    public void setAsFilterFraction(int asFilterFraction) {
        this.asFilterFraction = asFilterFraction;
    }


    public int getAsFilterFraction() {
        return asFilterFraction;
    }


    public FactorYio getSelMoneyFactor() {
        return selMoneyFactor;
    }


    public boolean isInAreaSelectionMode() {
        return areaSelectionMode;
    }


    public FactorYio getBlackoutFactor() {
        return blackoutFactor;
    }
}