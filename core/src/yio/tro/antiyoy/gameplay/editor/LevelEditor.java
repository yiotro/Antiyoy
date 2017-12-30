package yio.tro.antiyoy.gameplay.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

import java.util.ArrayList;
import java.util.ListIterator;


public class LevelEditor {

    public static final String EDITOR_PREFS = "editor";
    public static final String SLOT_NAME = "slot";
    public static final int TEMPORARY_SLOT_NUMBER = 1993; // to edit campaign levels
    public GameController gameController;
    private final EditorAutomationManager editorAutomationManager;
    private int inputMode, inputColor, inputObject;
    private boolean randomColor, filteredByOnlyLand;
    public static final int MODE_MOVE = 0;
    public static final int MODE_SET_HEX = 1;
    public static final int MODE_SET_OBJECT = 2;
    public static final int MODE_DELETE = 3;
    private int scrX, scrY;
    private long lastTimeTouched;
    private int currentSlotNumber;
    private ArrayList<Hex> tempList;
    private GameSaver gameSaver;
    public boolean showMoney;
    DetectorProvince detectorProvince;


    public LevelEditor(GameController gameController) {
        this.gameController = gameController;
        filteredByOnlyLand = false;
        tempList = new ArrayList<>();
        gameSaver = new GameSaver(gameController);
        detectorProvince = new DetectorProvince();
        editorAutomationManager = new EditorAutomationManager(this);
        showMoney = false;
    }


    private void focusedHexActions(Hex focusedHex) {
        if (focusedHex == null) return;
        if (randomColor) inputColor = gameController.random.nextInt(GameRules.MAX_COLOR_NUMBER);
        switch (inputMode) {
            case MODE_MOVE:
                inputModeMoveActions(focusedHex);
                break;
            case MODE_SET_HEX:
                inputModeHexActions(focusedHex);
                break;
            case MODE_SET_OBJECT:
                inputModeSetObjectActions(focusedHex);
                break;
            case MODE_DELETE:
                inputModeDeleteActions(focusedHex);
                break;
        }
    }


    private void inputModeDeleteActions(Hex focusedHex) {
        if (focusedHex.active) {
            deactivateHex(focusedHex);
        }
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
                if (adjacentHex.active && adjacentHex.sameColor(hex) && !province.contains(adjacentHex)) {
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


    private int countUpColorNumber() {
        int cn = 0;
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (activeHex.colorIndex > cn) {
                cn = activeHex.colorIndex;
            }
        }
        cn++;

        if (cn > FieldController.NEUTRAL_LANDS_INDEX) {
            cn = FieldController.NEUTRAL_LANDS_INDEX;
        }

        return cn;
    }


    public String getFullLevelString() {
        GameRules.colorNumber = countUpColorNumber();
        gameController.fieldController.detectProvinces();
        return gameController.fieldController.getFullLevelString();
    }


    public void saveSlot() {
        String fullLevel = getFullLevelString();
        Preferences prefs = Gdx.app.getPreferences(EDITOR_PREFS);
        prefs.putString(SLOT_NAME + currentSlotNumber, fullLevel);
        prefs.putInteger("chosen_color" + currentSlotNumber, GameRules.editorChosenColor);
        prefs.flush();
    }


    public void loadSlot() {
        Preferences prefs = Gdx.app.getPreferences(EDITOR_PREFS);
        String fullLevel = prefs.getString(SLOT_NAME + currentSlotNumber, "");

        LoadingParameters instance = LoadingParameters.getInstance();

        if (fullLevel.length() < 3) {
            instance.mode = LoadingParameters.MODE_EDITOR_NEW;
            instance.levelSize = FieldController.SIZE_BIG;
            instance.playersNumber = 1;
            instance.colorNumber = 5;
            instance.colorOffset = 0;
            instance.difficulty = 1;
        } else {
            instance.mode = LoadingParameters.MODE_EDITOR_LOAD;
            instance.applyFullLevel(fullLevel);
            instance.colorOffset = 0;
        }

        LoadingManager.getInstance().startGame(instance);
        GameRules.editorChosenColor = prefs.getInteger("chosen_color" + currentSlotNumber);

        defaultValues();
    }


    private void defaultValues() {
        inputMode = MODE_MOVE;
        showMoney = false;
    }


    public void onAllPanelsHide() {
        showMoney = false;
    }


    private boolean isValidLevelString(String fullLevel) {
        if (fullLevel == null) return false;
        if (!fullLevel.contains("/")) return false;
        if (!fullLevel.contains("#")) return false;
        if (fullLevel.length() < 10) return false;
        return true;
    }


    public void importLevel() {
        String fromClipboard = "";

        Clipboard clipboard = Gdx.app.getClipboard();
        fromClipboard = clipboard.getContents();

//        if (YioGdxGame.ANDROID) {
//            GetAndroidClipboardContents getAndroidClipboardContents = new GetAndroidClipboardContents();
//            getAndroidClipboardContents.run();
//            while (!getAndroidClipboardContents.isComplete()) {
//                try {
//                    Thread.holdsLock(Thread.currentThread());
//                    Thread.currentThread().wait(100);
//                    System.out.println("waiting!!!!!");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("--------------------- 2");
//            fromClipboard = getAndroidClipboardContents.getResult();
//        } else {
//            Clipboard clipboard = Gdx.app.getClipboard();
//            fromClipboard = clipboard.getContents();
//        }

        if (isValidLevelString(fromClipboard)) {
            LoadingParameters instance = LoadingParameters.getInstance();
            instance.mode = LoadingParameters.MODE_EDITOR_LOAD;
            instance.applyFullLevel(fromClipboard);
            instance.colorOffset = 0;
            LoadingManager.getInstance().startGame(instance);
        }
    }


    public void exportLevel() {
        // this was not working properly
//        String fullLevel = getFullLevelString();

        Preferences prefs = Gdx.app.getPreferences(EDITOR_PREFS);
        String fullLevel = prefs.getString(SLOT_NAME + currentSlotNumber, "");
        System.out.println("fullLevel = " + fullLevel);
        Clipboard clipboard = Gdx.app.getClipboard();
        clipboard.setContents(fullLevel);
        Scenes.sceneNotification.showNotification("exported");
    }


    public void clearLevel() {
        for (int i = 0; i < gameController.fieldController.fWidth; i++) {
            for (int j = 0; j < gameController.fieldController.fHeight; j++) {
                deactivateHex(gameController.fieldController.field[i][j]);
            }
        }
    }


    private void deactivateHex(Hex hex) {
        if (!hex.active) return;
        gameController.cleanOutHex(hex);
        hex.active = false;
        ListIterator activeIterator = gameController.fieldController.activeHexes.listIterator();
        while (activeIterator.hasNext()) {
            Hex tHex = (Hex) activeIterator.next();
            if (tHex == hex) {
                activeIterator.remove();
                break;
            }
        }
        gameController.addAnimHex(hex);
//        gameController.yioGdxGame.gameView.updateCacheNearAnimHexes();
    }


    public void playLevel() {
        Preferences prefs = Gdx.app.getPreferences(EDITOR_PREFS);
        String fullLevel = prefs.getString(SLOT_NAME + currentSlotNumber, "");

        LoadingParameters instance = LoadingParameters.getInstance();
        instance.mode = LoadingParameters.MODE_EDITOR_PLAY;
        instance.applyFullLevel(fullLevel);
        GameRules.editorChosenColor = prefs.getInteger("chosen_color" + currentSlotNumber);
        instance.colorOffset = gameController.getColorOffsetBySliderIndex(GameRules.editorChosenColor, GameRules.MAX_COLOR_NUMBER);

        LoadingManager.getInstance().startGame(instance);
    }


    public void onEndCreation() {
        for (Unit unit : gameController.unitList) {
            unit.stopJumping();
        }

        gameController.fieldController.provinces.clear();
    }


    private void activateHex(Hex hex, int color) {
        if (hex.active) return;
        hex.active = true;
        hex.setColorIndex(color);
        ListIterator activeIterator = gameController.fieldController.activeHexes.listIterator();
        activeIterator.add(hex);
        gameController.addAnimHex(hex);
    }


    private void inputModeHexActions(Hex focusedHex) {
        if (focusedHex.active) {
            gameController.fieldController.setHexColor(focusedHex, inputColor);
        } else {
            if (filteredByOnlyLand) return;
            activateHex(focusedHex, inputColor);
        }
    }


    private boolean updateFocusedHex() {
        Hex lastFocHex = gameController.fieldController.focusedHex;
        gameController.selectionController.updateFocusedHex(scrX, scrY);
        if (gameController.fieldController.focusedHex == lastFocHex) return false; // focused hex is same
        return true; // focused hex updated
    }


    public boolean isSomethingMoving() {
        if (inputMode == MODE_SET_HEX)
            return gameController.currentTime < lastTimeTouched + 50;
        else return false;
    }


    public boolean touchDown(int x, int y) {
        scrX = x;
        scrY = y;
        lastTimeTouched = gameController.currentTime;
        updateFocusedHex();
        if (inputMode == MODE_SET_OBJECT || inputMode == MODE_SET_HEX || inputMode == MODE_DELETE) {
            focusedHexActions(gameController.fieldController.focusedHex);
        }

        return isTouchCaptured();
    }


    public boolean touchUp(int x, int y) {
        scrX = x;
        scrY = y;
        lastTimeTouched = gameController.currentTime;

        return isTouchCaptured();
    }


    public boolean touchDrag(int x, int y) {
        scrX = x;
        scrY = y;
        lastTimeTouched = gameController.currentTime;
        if (!updateFocusedHex()) return isTouchCaptured();

        if (inputMode == MODE_SET_HEX || inputMode == MODE_DELETE) {
            focusedHexActions(gameController.fieldController.focusedHex);
        }

        return isTouchCaptured();
    }


    private boolean isTouchCaptured() {
        return inputMode != MODE_MOVE;
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
        GameRules.setColorNumber(countUpColorNumber());
        gameController.fieldController.clearField();
        gameController.fieldController.createFieldMatrix();
        if (GameRules.slayRules) {
            gameController.mapGeneratorSlay.generateMap(gameController.random, gameController.fieldController.field);
        } else {
            gameController.mapGeneratorGeneric.generateMap(gameController.random, gameController.fieldController.field);
        }
        gameController.yioGdxGame.gameView.updateCacheLevelTextures();

        resetInputMode();
    }


    public void resetInputMode() {
        setInputMode(LevelEditor.MODE_MOVE);
    }


    public void setInputMode(int inputMode) {
        this.inputMode = inputMode;
//        gameController.cameraControllerOld.camDx = 0;
//        gameController.cameraControllerOld.camDy = 0;
//        gameController.cameraControllerOld.camDZoom = 0;
    }


    public void setInputColor(int inputColor) {
        this.inputColor = inputColor;
        setRandomColor(false);
        if (inputColor >= GameRules.MAX_COLOR_NUMBER && inputColor != FieldController.NEUTRAL_LANDS_INDEX)
            setRandomColor(true);
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


    public void launchEditCampaignLevelMode() {
        if (GameRules.inEditorMode) return;

        int currentLevelIndex = CampaignProgressManager.getInstance().currentLevelIndex;
        System.out.println("opened campaign level in editor: " + currentLevelIndex);

        GameRules.inEditorMode = true;
        currentSlotNumber = TEMPORARY_SLOT_NUMBER;
        saveSlot();
        Scenes.sceneEditorActions.create();
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


    private void setRandomColor(boolean randomColor) {
        this.randomColor = randomColor;
    }


    public void setCurrentSlotNumber(int currentSlotNumber) {
        this.currentSlotNumber = currentSlotNumber;
    }


    public boolean isFilteredByOnlyLand() {
        return filteredByOnlyLand;
    }


    public void setFilteredByOnlyLand(boolean filteredByOnlyLand) {
        this.filteredByOnlyLand = filteredByOnlyLand;

        updateFilterOnlyLandButton();
    }
}
