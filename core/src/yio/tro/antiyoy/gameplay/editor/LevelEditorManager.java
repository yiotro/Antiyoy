package yio.tro.antiyoy.gameplay.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.data_storage.GameSaver;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.touch_mode.TouchMode;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;
import java.util.ListIterator;


public class LevelEditorManager {

    public static final String SLOT_NAME = "slot";
    public static final int MAX_ACCEPTABLE_DELTA = 22;
    public GameController gameController;
    private final EditorAutomationManager editorAutomationManager;
    private int inputFraction, inputObject;
    private boolean randomizeFraction, filteredByOnlyLand;
    private LeInputMode leInputMode;
    private int scrX, scrY;
    private long lastTimeTouched;
    private ArrayList<Hex> tempList;
    private GameSaver gameSaver;
    DetectorProvince detectorProvince;
    public EditorProvinceManager editorProvinceManager;
    public EditorRelationsManager editorRelationsManager;
    public EditorCoalitionsManager coalitionsManager;


    public LevelEditorManager(GameController gameController) {
        this.gameController = gameController;
        filteredByOnlyLand = false;
        tempList = new ArrayList<>();
        gameSaver = new GameSaver(gameController);
        detectorProvince = new DetectorProvince();
        editorAutomationManager = new EditorAutomationManager(this);
        editorProvinceManager = new EditorProvinceManager(this);
        editorRelationsManager = new EditorRelationsManager(this);
        coalitionsManager = new EditorCoalitionsManager(this);
    }


    private void focusedHexActions(Hex focusedHex) {
        if (focusedHex == null) return;
        if (randomizeFraction) {
            inputFraction = gameController.random.nextInt(GameRules.MAX_FRACTIONS_QUANTITY);
        }
        switch (leInputMode) {
            case move:
                inputModeMoveActions(focusedHex);
                break;
            case set_hex:
                inputModeHexActions(focusedHex);
                break;
            case set_object:
                inputModeSetObjectActions(focusedHex);
                break;
            case delete:
                inputModeDeleteActions(focusedHex);
                break;
        }
    }


    private void inputModeDeleteActions(Hex focusedHex) {
        if (!focusedHex.active) return;
        deactivateHex(focusedHex);
    }


    private void inputModeSetObjectActions(Hex focusedHex) {
        if (!focusedHex.active) return;

        int unitStrength = 0;
        if (focusedHex.containsUnit()) {
            unitStrength = focusedHex.unit.strength;
        }

        int lastObject = focusedHex.objectInside;
        gameController.cleanOutHex(focusedHex);

        if (inputObject == 0) { // delete object
            focusedHex.setObjectInside(0);
            gameController.addAnimHex(focusedHex);
        } else if (inputObject < 5) { // objects
            addSolidObject(focusedHex, lastObject);
            checkToTurnIntoFarm(focusedHex);
            gameController.addAnimHex(focusedHex);
        } else { // units
            tryToAddUnitToFocusedHex(focusedHex, unitStrength);
        }
    }


    private void addSolidObject(Hex focusedHex, int lastObject) {
        if (!canAddObjectToHex(focusedHex)) return;

        if (lastObject == Obj.TOWER && inputObject == Obj.TOWER) {
            gameController.addSolidObject(focusedHex, Obj.STRONG_TOWER);
            return;
        }

        gameController.addSolidObject(focusedHex, inputObject);
    }


    private boolean canAddObjectToHex(Hex hex) {
        if (hex.isNeutral()) {
            switch (inputObject) {
                default:
                    return false;
                case Obj.TOWER:
                case Obj.PALM:
                case Obj.PINE:
                case Obj.STRONG_TOWER:
                case Obj.TOWN:
                    return true;
            }
        }

        return true;
    }


    private void checkToTurnIntoFarm(Hex srcHex) {
        if (inputObject != Obj.TOWN) return;

        ArrayList<Hex> province = detectProvince(srcHex);
        if (province == null) return;

        for (Hex hex : province) {
            if (hex.objectInside == Obj.TOWN && hex != srcHex) {
                srcHex.objectInside = Obj.FARM;
                return;
            }
        }
    }


    private ArrayList<Hex> detectProvince(Hex start) {
        ArrayList<Hex> province = new ArrayList<>();
        tempList.clear();
        tempList.add(start);
        province.add(start);

        while (tempList.size() > 0) {
            Hex hex = tempList.get(0);
            tempList.remove(0);
            for (int i = 0; i < 6; i++) {
                Hex adjacentHex = hex.getAdjacentHex(i);
                if (adjacentHex.active && adjacentHex.sameFraction(hex) && !province.contains(adjacentHex)) {
                    tempList.add(adjacentHex);
                    province.add(adjacentHex);
                }
            }
        }

        return province;
    }


    private boolean canAddUnitToHex(Hex hex) {
        if (hex.isNeutral()) {
            return false;
        }

        return true;
    }


    private void tryToAddUnitToFocusedHex(Hex focusedHex, int unitStrength) {
        if (!canAddUnitToHex(focusedHex)) return;

        int defStr = inputObject - 4;
        int str = unitStrength + defStr;
        while (str > 4) {
            str -= 4;
        }
        gameController.addUnit(focusedHex, str);
        focusedHex.unit.stopJumping();
    }


    private void inputModeMoveActions(Hex focusedHex) {
        // nothing here
    }


    int countUpFractionsQuantity() {
        int maxFraction = 0;
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (activeHex.fraction > maxFraction) {
                maxFraction = activeHex.fraction;
            }
        }

        int quantity = maxFraction + 1;
        if (quantity > GameRules.MAX_FRACTIONS_QUANTITY) {
            quantity = GameRules.MAX_FRACTIONS_QUANTITY;
        }

        // temporary fix
        quantity = GameRules.MAX_FRACTIONS_QUANTITY;

        return quantity;
    }


    private Preferences getPreferences() {
        return Gdx.app.getPreferences("dsajdha");
    }


    public void onAllPanelsHide() {
        gameController.resetTouchMode();
    }


    public void importLevelFromClipboard() {
        gameSaver.legacyImportManager.importLevelFromClipboard();
    }


    public void exportLevel(int slotNumber) {
        Preferences prefs = getPreferences();
        String fullLevel = prefs.getString(SLOT_NAME + slotNumber, "");
        System.out.println("Level exported to clipboard.");
        Clipboard clipboard = Gdx.app.getClipboard();
        clipboard.setContents(fullLevel);

        if (!isLevelAcceptableForUserLevels(fullLevel)) {
            Scenes.sceneMapTooBig.create();
        }

        Scenes.sceneNotification.show("exported");
    }


    public boolean isLevelAcceptableForUserLevels(String fullLevel) {
        if (getLevelSize(fullLevel) >= LevelSize.HUGE) return false;

        int beginIndex = fullLevel.indexOf("/") + 1;
        if (beginIndex >= fullLevel.length()) return false;
        String innerString = fullLevel.substring(beginIndex);
        float min = -1;
        float max = -1;
        FieldManager fieldManager = gameController.fieldManager;
        for (String token : innerString.split("#")) {
            String[] split = token.split(" ");
            int index1 = Integer.valueOf(split[0]);
            int index2 = Integer.valueOf(split[1]);
            float y = fieldManager.hexStep1 * index1 + fieldManager.hexStep2 * index2 * (float) Math.cos(Math.PI / 3d);
            if (min == -1 || y < min) {
                min = y;
            }
            if (max == -1 || y > max) {
                max = y;
            }
        }

        float delta = max - min;
        delta /= fieldManager.hexStep1;
        delta += 1;

        return delta <= MAX_ACCEPTABLE_DELTA;
    }


    private int getLevelSize(String fullLevel) {
        int endIndex = fullLevel.indexOf("/");
        if (endIndex >= fullLevel.length()) return LevelSize.BIG;
        String basicInfoString = fullLevel.substring(0, endIndex);
        String[] split = basicInfoString.split(" ");
        return Integer.valueOf(split[1]);
    }


    public void clearLevel() {
        for (int i = 0; i < gameController.fieldManager.fWidth; i++) {
            for (int j = 0; j < gameController.fieldManager.fHeight; j++) {
                deactivateHex(gameController.fieldManager.field[i][j]);
            }
        }
    }


    private void deactivateHex(Hex hex) {
        if (!hex.active) return;
        gameController.cleanOutHex(hex);
        hex.active = false;
        ListIterator activeIterator = gameController.fieldManager.activeHexes.listIterator();
        while (activeIterator.hasNext()) {
            Hex tHex = (Hex) activeIterator.next();
            if (tHex == hex) {
                activeIterator.remove();
                break;
            }
        }
        gameController.addAnimHex(hex);
    }


    public void move() {
        editorProvinceManager.move();
    }


    public void onEndCreation() {
        for (Unit unit : gameController.unitList) {
            unit.stopJumping();
        }

        gameController.fieldManager.clearProvincesList();
        gameController.setTouchMode(TouchMode.tmEditor);
        editorProvinceManager.onEndCreation();
        editorRelationsManager.onEndCreation();
        coalitionsManager.onEndCreation();

        if (gameController.isInEditorMode()) {
            resetInputMode();
        }
    }


    private void activateHex(Hex hex, int fraction) {
        if (hex.active) return;
        hex.active = true;
        hex.setFraction(fraction);
        ListIterator activeIterator = gameController.fieldManager.activeHexes.listIterator();
        activeIterator.add(hex);
        gameController.addAnimHex(hex);
    }


    private void inputModeHexActions(Hex focusedHex) {
        if (focusedHex.active) {
            applySetHex(focusedHex);
        } else {
            if (filteredByOnlyLand) return;
            activateHex(focusedHex, inputFraction);
        }
    }


    private void applySetHex(Hex focusedHex) {
        if (focusedHex.fraction == inputFraction) return;
        int objectInside = focusedHex.objectInside;
        gameController.fieldManager.setHexFraction(focusedHex, inputFraction);
        if (inputFraction != GameRules.NEUTRAL_FRACTION) {
            gameController.fieldManager.addSolidObject(focusedHex, objectInside);
        }
    }


    private boolean updateFocusedHex() {
        Hex lastFocHex = gameController.fieldManager.focusedHex;
        gameController.selectionManager.updateFocusedHex(scrX, scrY);
        if (gameController.fieldManager.focusedHex == lastFocHex) return false; // focused hex is same
        return true; // focused hex updated
    }


    public boolean isSomethingMoving() {
        if (leInputMode == LeInputMode.set_hex) {
            return gameController.currentTime < lastTimeTouched + 50;
        } else {
            return false;
        }
    }


    public boolean onTouchDown(int x, int y) {
        scrX = x;
        scrY = y;
        lastTimeTouched = gameController.currentTime;
        updateFocusedHex();
        if (leInputMode == LeInputMode.set_object || leInputMode == LeInputMode.set_hex || leInputMode == LeInputMode.delete) {
            focusedHexActions(gameController.fieldManager.focusedHex);
        }

        return isTouchCaptured();
    }


    public boolean onTouchUp(int x, int y) {
        scrX = x;
        scrY = y;
        lastTimeTouched = gameController.currentTime;

        return isTouchCaptured();
    }


    public boolean onTouchDrag(int x, int y) {
        scrX = x;
        scrY = y;
        lastTimeTouched = gameController.currentTime;
        if (!updateFocusedHex()) {
            return isTouchCaptured();
        }

        if (leInputMode == LeInputMode.set_hex || leInputMode == LeInputMode.delete) {
            focusedHexActions(gameController.fieldManager.focusedHex);
        }

        return isTouchCaptured();
    }


    public void onEditDiplomacyButtonPressed() {
        gameController.yioGdxGame.menuControllerYio.hideAllEditorPanels();
        Scenes.sceneEditorDiplomacy.create();
    }


    public void defaultValues() {
        editorProvinceManager.defaultValues();
        editorRelationsManager.defaultValues();
        coalitionsManager.defaultValues();
    }


    public void onEditProvincesButtonPressed() {
        gameController.yioGdxGame.menuControllerYio.hideAllEditorPanels();
        editorProvinceManager.performUpdate();
        gameController.setTouchMode(TouchMode.tmEditProvinces);
    }


    public void onExitedToPauseMenu() {
        gameController.yioGdxGame.menuControllerYio.hideAllEditorPanels();
        editorProvinceManager.onExitedToPauseMenu();
    }


    public void onAppPause() {
        if (gameController.yioGdxGame.gamePaused) return;
        if (!gameController.isInEditorMode()) return;

        EditorSaveSystem editorSaveSystem = gameController.editorSaveSystem;
        gameController.levelEditorManager.onExitedToPauseMenu();
        editorSaveSystem.saveSlot(GameRules.editorSlotNumber);
    }


    public boolean isTouchCaptured() {
        return leInputMode != LeInputMode.move;
    }


    public void placeObject(Hex hex, int type) {
        gameController.cleanOutHex(hex);
        gameController.addSolidObject(hex, type);
    }


    public void expandProvinces() {
        editorAutomationManager.expandProvinces();
    }


    public void expandTrees() {
        editorAutomationManager.expandTrees();
    }


    public void placeCapitalsOrFarms() {
        editorAutomationManager.placeCapitalsOrFarms();
    }


    public void placeRandomTowers() {
        editorAutomationManager.placeRandomTowers();
    }


    public void cutExcessStuff() {
        editorAutomationManager.cutExcessStuff();
    }


    private LanguagesManager getLangManager() {
        return LanguagesManager.getInstance();
    }


    public void randomize() {
        gameSaver.detectRules();
        GameRules.setFractionsQuantity(countUpFractionsQuantity());
        checkToFixRandomizationRulesForEmptyMap();
        gameController.fieldManager.clearField();
        gameController.fieldManager.createFieldMatrix();
        if (GameRules.slayRules) {
            gameController.mapGeneratorSlay.generateMap(gameController.random, gameController.fieldManager.field);
        } else {
            gameController.mapGeneratorGeneric.generateMap(gameController.random, gameController.fieldManager.field);
        }
        gameController.yioGdxGame.gameView.updateCacheLevelTextures();
        editorProvinceManager.onLevelRandomlyCreated();

        resetInputMode();
    }


    private void checkToFixRandomizationRulesForEmptyMap() {
        if (GameRules.fractionsQuantity != 1) return;
        if (gameController.fieldManager.activeHexes.size() > 0) return;

        GameRules.setFractionsQuantity(GameRules.MAX_FRACTIONS_QUANTITY);
        GameRules.slayRules = false;
    }


    public void resetInputMode() {
        setInputMode(LeInputMode.move);
    }


    public void setInputMode(LeInputMode inputMode) {
        this.leInputMode = inputMode;
    }


    public void setInputFraction(int inputFraction) {
        this.inputFraction = inputFraction;
        setRandomizeFraction(false);
    }


    public void switchFilterOnlyLand() {
        setFilteredByOnlyLand(!filteredByOnlyLand);
    }


    private void updateTextOnFilterOnlyLandButton(ButtonYio filterButton) {
        if (filteredByOnlyLand) {
            filterButton.setTextLine(getLangManager().getString("filter_only_land"));
        } else {
            filterButton.setTextLine(getLangManager().getString("filter_no"));
        }
    }


    public void launchEditLevelMode() {
        if (GameRules.inEditorMode) return;

        int currentLevelIndex = CampaignProgressManager.getInstance().currentLevelIndex;
        if (currentLevelIndex > 0) {
            System.out.println("opened campaign level in editor: " + currentLevelIndex);
        }

        GameRules.inEditorMode = true;
        String levelCode = gameController.encodeManager.perform();
        gameController.importManager.launchGame(LoadingType.editor_import, levelCode);
    }


    public void importFromClipboardToExtraSlot() {
        importLevelFromClipboard();
    }


    public void updateFilterOnlyLandButton() {
        MenuControllerYio menuControllerYio = gameController.yioGdxGame.menuControllerYio;
        ButtonYio filterButton = menuControllerYio.getButtonById(12353);
        if (filterButton == null) return;

        updateTextOnFilterOnlyLandButton(filterButton);
        menuControllerYio.buttonRenderer.renderButton(filterButton);
    }


    public void setInputObject(int inputObject) {
        this.inputObject = inputObject;
    }


    public void setRandomizeFraction(boolean randomizeFraction) {
        this.randomizeFraction = randomizeFraction;
    }


    public void onLevelImported(String levelCode) {
        editorProvinceManager.onLevelImported(levelCode);
        editorRelationsManager.onLevelImported(levelCode);
        gameController.messagesManager.onLevelImported(levelCode);
        coalitionsManager.onLevelImported(levelCode);
    }


    public void checkToApplyAdditionalData() {
        editorProvinceManager.checkToApplyData();
        editorRelationsManager.checkToApplyData();
        coalitionsManager.checkToApplyData();
    }


    public boolean isFilteredByOnlyLand() {
        return filteredByOnlyLand;
    }


    public void setFilteredByOnlyLand(boolean filteredByOnlyLand) {
        this.filteredByOnlyLand = filteredByOnlyLand;

        updateFilterOnlyLandButton();
    }
}
