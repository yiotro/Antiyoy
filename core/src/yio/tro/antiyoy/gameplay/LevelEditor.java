package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;
import yio.tro.antiyoy.LanguagesManager;
import yio.tro.antiyoy.ai.ArtificialIntelligence;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by ivan on 27.11.2015.
 */
public class LevelEditor {

    public static final String EDITOR_PREFS = "editor";
    public static final String SLOT_NAME = "slot";
    public static final int TEMPORARY_SLOT_NUMBER = 1993; // to edit campaign levels
    private final GameController gameController;
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


    public LevelEditor(GameController gameController) {
        this.gameController = gameController;
        filteredByOnlyLand = false;
        tempList = new ArrayList<>();
        gameSaver = new GameSaver(gameController);
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
        if (lastObject == Hex.OBJECT_TOWER && inputObject == Hex.OBJECT_TOWER) {
            gameController.addSolidObject(focusedHex, Hex.OBJECT_STRONG_TOWER);
            return;
        }

        gameController.addSolidObject(focusedHex, inputObject);
    }


    private void checkToTurnIntoFarm(Hex srcHex) {
        if (inputObject != Hex.OBJECT_TOWN) return;

        ArrayList<Hex> province = detectProvince(srcHex);
        for (Hex hex : province) {
            if (hex.objectInside == Hex.OBJECT_TOWN && hex != srcHex) {
                srcHex.objectInside = Hex.OBJECT_FARM;
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


    private void tryToAddUnitToFocusedHex(Hex focusedHex, int unitStrength) {
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


    private String getBasicInfoString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(GameRules.difficulty + " ");
        stringBuffer.append(gameController.fieldController.levelSize + " ");
        stringBuffer.append(gameController.playersNumber + " ");
        stringBuffer.append(countUpColorNumber() + "");
        return stringBuffer.toString();
    }


    private int countUpColorNumber() {
        int cn = 0;
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (activeHex.colorIndex > cn) {
                cn = activeHex.colorIndex;
            }
        }
        cn++;
        return cn;
    }


    private String getFullLevelString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getBasicInfoString());
        stringBuffer.append("/");
        stringBuffer.append(gameController.gameSaver.getActiveHexesString());
        return stringBuffer.toString();
    }


    public void saveSlot() {
        String fullLevel = getFullLevelString();
        Preferences prefs = Gdx.app.getPreferences(EDITOR_PREFS);
        prefs.putString(SLOT_NAME + currentSlotNumber, fullLevel);
        prefs.flush();
    }


    public void loadSlot() {
        Preferences prefs = Gdx.app.getPreferences(EDITOR_PREFS);
        String fullLevel = prefs.getString(SLOT_NAME + currentSlotNumber, "");
        gameSaver.recreateLevelFromString(fullLevel, true, true);
        defaultValues();
    }


    private void defaultValues() {
        inputMode = MODE_MOVE;
    }


    private boolean isValidLevelString(String fullLevel) {
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
            gameSaver.recreateLevelFromString(fromClipboard, true, true);
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
        gameController.yioGdxGame.menuControllerYio.showNotification(gameController.yioGdxGame.menuControllerYio.languagesManager.getString("exported"), true);
    }


    void clearLevel() {
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
        gameSaver.recreateLevelFromString(fullLevel, false, true);
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


    boolean isSomethingMoving() {
        if (inputMode == MODE_SET_HEX)
            return gameController.currentTime < lastTimeTouched + 50;
        else return false;
    }


    void touchDown(int x, int y) {
        scrX = x;
        scrY = y;
        lastTimeTouched = gameController.currentTime;
        updateFocusedHex();
        if (inputMode == MODE_SET_OBJECT || inputMode == MODE_SET_HEX || inputMode == MODE_DELETE)
            focusedHexActions(gameController.fieldController.focusedHex);
    }


    void touchUp(int x, int y) {
        scrX = x;
        scrY = y;
        lastTimeTouched = gameController.currentTime;

    }


    void touchDrag(int x, int y) {
        scrX = x;
        scrY = y;
        lastTimeTouched = gameController.currentTime;
        if (!updateFocusedHex()) return;
        if (inputMode == MODE_SET_HEX || inputMode == MODE_DELETE)
            focusedHexActions(gameController.fieldController.focusedHex);
    }


    boolean isCameraMovementAllowed() {
        return inputMode == MODE_MOVE;
    }


    public void changeLevelSize() {
//        int levSize = gameController.levelSize;
//        switch (levSize) {
//            case GameController.SIZE_SMALL: levSize = GameController.SIZE_MEDIUM; break;
//            case GameController.SIZE_MEDIUM: levSize = GameController.SIZE_BIG; break;
//            case GameController.SIZE_BIG: levSize = GameController.SIZE_SMALL; break;
//        }
//        gameController.setLevelSize(levSize);
//        gameController.yioGdxGame.menuControllerLighty.showNotification(getLangManager().getString("map_size"), true);
    }


    public void changeNumberOfPlayers() {
        int numPlayers = gameController.playersNumber;
        numPlayers++;
        if (numPlayers > GameRules.MAX_COLOR_NUMBER) numPlayers = 0;
        gameController.setPlayersNumber(numPlayers);
        gameController.yioGdxGame.menuControllerYio.showNotification(getLangManager().getString("player_number") + " " + numPlayers, true);
    }


    public void changeDifficulty() {
        int diff = GameRules.difficulty;
        diff++;
        if (diff > ArtificialIntelligence.DIFFICULTY_BALANCER) diff = ArtificialIntelligence.DIFFICULTY_EASY;
        GameRules.setDifficulty(diff);
        gameController.yioGdxGame.menuControllerYio.showNotification(getLangManager().getString("difficulty") + " " + getDiffString(diff), true);
    }


    private String getDiffString(int diff) {
        switch (diff) {
            default:
            case ArtificialIntelligence.DIFFICULTY_EASY:
                return getLangManager().getString("easy");
            case ArtificialIntelligence.DIFFICULTY_NORMAL:
                return getLangManager().getString("normal");
            case ArtificialIntelligence.DIFFICULTY_HARD:
                return getLangManager().getString("hard");
            case ArtificialIntelligence.DIFFICULTY_EXPERT:
                return getLangManager().getString("expert");
            case ArtificialIntelligence.DIFFICULTY_BALANCER:
                return getLangManager().getString("balancer");
        }
    }


    private LanguagesManager getLangManager() {
        return MenuControllerYio.languagesManager;
    }


    public void randomize() {
        gameSaver.detectRules();
        GameRules.colorNumber = countUpColorNumber();
        gameController.fieldController.clearField();
        gameController.fieldController.createFieldMatrix();
        if (GameRules.slay_rules) {
            gameController.mapGeneratorSlay.generateMap(gameController.random, gameController.fieldController.field);
        } else {
            gameController.mapGeneratorGeneric.generateMap(gameController.random, gameController.fieldController.field);
        }
        gameController.yioGdxGame.gameView.updateCacheLevelTextures();
    }


    public void setInputMode(int inputMode) {
        this.inputMode = inputMode;
        gameController.cameraController.camDx = 0;
        gameController.cameraController.camDy = 0;
        gameController.cameraController.camDZoom = 0;
    }


    public void setInputColor(int inputColor) {
        this.inputColor = inputColor;
        setRandomColor(false);
        if (inputColor >= GameRules.MAX_COLOR_NUMBER && inputColor != FieldController.NEUTRAL_LANDS_INDEX) setRandomColor(true);
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

        GameRules.inEditorMode = true;
        currentSlotNumber = TEMPORARY_SLOT_NUMBER;
        saveSlot();
        gameController.yioGdxGame.menuControllerYio.createEditorActionsMenu();
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
