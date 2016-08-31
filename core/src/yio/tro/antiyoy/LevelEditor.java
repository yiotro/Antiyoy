package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;

import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Created by ivan on 27.11.2015.
 */
public class LevelEditor {

    private final GameController gameController;
    private int inputMode, inputColor, inputObject;
    private boolean randomColor;
    public static final int MODE_MOVE = 0;
    public static final int MODE_SET_HEX = 1;
    public static final int MODE_SET_OBJECT = 2;
    public static final int MODE_DELETE = 3;
    private int scrX, scrY;
    private long lastTimeTouched;
    private int currentSlotNumber;


    public LevelEditor(GameController gameController) {
        this.gameController = gameController;
    }


    private void focusedHexActions(Hex focusedHex) {
        if (focusedHex == null) return;
        if (randomColor) inputColor = gameController.random.nextInt(gameController.MAX_COLOR_NUMBER);
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
        if (focusedHex.active) {
            gameController.cleanOutHex(focusedHex);
            if (inputObject == 0) { // delete object
                focusedHex.setObjectInside(0);
                gameController.addAnimHex(focusedHex);
            } else if (inputObject < 5) { // objects
                gameController.addSolidObject(focusedHex, inputObject);
                gameController.addAnimHex(focusedHex);
            } else { // units
                gameController.addUnit(focusedHex, inputObject - 4);
                focusedHex.unit.stopJumping();
            }
        }
    }


    private void inputModeMoveActions(Hex focusedHex) {
        // nothing here
    }


    private String getBasicInfoString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(gameController.difficulty + " ");
        stringBuffer.append(gameController.levelSize + " ");
        stringBuffer.append(gameController.playersNumber + " ");
        stringBuffer.append(countUpColorNumber() + "");
        return stringBuffer.toString();
    }


    private int countUpColorNumber() {
        int cn = 0;
        for (Hex activeHex : gameController.activeHexes) {
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
        Preferences prefs = Gdx.app.getPreferences("editor");
        prefs.putString("slot" + currentSlotNumber, fullLevel);
        prefs.flush();
    }


    private void recreateLevelFromString(String fullLevel, boolean editorMode) {
        gameController.editorMode = editorMode;
        String basicInfo, activeHexes;
        int delimiterChar = fullLevel.indexOf("/");
        if (delimiterChar < 0) { // empty slot
            GameController.setColorNumber(0); // to notify yio gdx game
            gameController.yioGdxGame.startInEditorMode();
            return;
        }
        basicInfo = fullLevel.substring(0, delimiterChar);
        activeHexes = fullLevel.substring(delimiterChar + 1, fullLevel.length());
        int basicInfoValues[] = new int[4];
        StringTokenizer stringTokenizer = new StringTokenizer(basicInfo, " ");
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            basicInfoValues[i] = Integer.valueOf(token);
            i++;
        }

        GameSaver gameSaver = gameController.gameSaver;
        gameSaver.setActiveHexesString(activeHexes);
        gameSaver.beginRecreation();
        gameSaver.setBasicInfo(0, basicInfoValues[2], basicInfoValues[3], basicInfoValues[1], basicInfoValues[0]);
        gameSaver.endRecreation();

        if (editorMode) {
            for (Unit unit : gameController.unitList) {
                unit.stopJumping();
            }
        }
    }


    public void loadSlot() {
        Preferences prefs = Gdx.app.getPreferences("editor");
        String fullLevel = prefs.getString("slot" + currentSlotNumber, "");
        recreateLevelFromString(fullLevel, true);
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
            recreateLevelFromString(fromClipboard, true);
        }
    }


    public void exportLevel() {
        String fullLevel = getFullLevelString();
        Clipboard clipboard = Gdx.app.getClipboard();
        clipboard.setContents(fullLevel);
        gameController.yioGdxGame.menuControllerYio.showNotification(gameController.yioGdxGame.menuControllerYio.languagesManager.getString("exported"), true);
    }


    void clearLevel() {
        for (int i = 0; i < gameController.fWidth; i++) {
            for (int j = 0; j < gameController.fHeight; j++) {
                deactivateHex(gameController.field[i][j]);
            }
        }
    }


    private void deactivateHex(Hex hex) {
        if (!hex.active) return;
        gameController.cleanOutHex(hex);
        hex.active = false;
        ListIterator activeIterator = gameController.activeHexes.listIterator();
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
        Preferences prefs = Gdx.app.getPreferences("editor");
        String fullLevel = prefs.getString("slot" + currentSlotNumber, "");
        recreateLevelFromString(fullLevel, false);
    }


    private void activateHex(Hex hex, int color) {
        if (hex.active) return;
        hex.active = true;
        hex.setColorIndex(color);
        ListIterator activeIterator = gameController.activeHexes.listIterator();
        activeIterator.add(hex);
        gameController.addAnimHex(hex);
    }


    private void inputModeHexActions(Hex focusedHex) {
        if (focusedHex.active) {
            gameController.setHexColor(focusedHex, inputColor);
        } else {
            activateHex(focusedHex, inputColor);
        }
    }


    private boolean updateFocusedHex() {
        Hex lastFocHex = gameController.focusedHex;
        gameController.updateFocusedHex(scrX, scrY);
        if (gameController.focusedHex == lastFocHex) return false; // focused hex is same
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
            focusedHexActions(gameController.focusedHex);
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
            focusedHexActions(gameController.focusedHex);
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
        if (numPlayers > GameController.MAX_COLOR_NUMBER) numPlayers = 1;
        gameController.setPlayersNumber(numPlayers);
        gameController.yioGdxGame.menuControllerYio.showNotification(getLangManager().getString("player_number") + " " + numPlayers, true);
    }


    public void changeDifficulty() {
        int diff = gameController.difficulty;
        diff++;
        if (diff > GameController.EXPERT) diff = GameController.EASY;
        gameController.setDifficulty(diff);
        gameController.yioGdxGame.menuControllerYio.showNotification(getLangManager().getString("difficulty") + " " + getDiffString(diff), true);
    }


    private String getDiffString(int diff) {
        switch (diff) {
            default:
            case GameController.EASY:
                return getLangManager().getString("easy");
            case GameController.NORMAL:
                return getLangManager().getString("normal");
            case GameController.HARD:
                return getLangManager().getString("hard");
            case GameController.EXPERT:
                return getLangManager().getString("expert");
        }
    }


    private LanguagesManager getLangManager() {
        return gameController.yioGdxGame.menuControllerYio.languagesManager;
    }


    public void randomize() {
        gameController.clearField();
        gameController.createFieldMatrix();
        gameController.mapGenerator.generateMap(gameController.random, gameController.field);
        gameController.yioGdxGame.gameView.updateCacheLevelTextures();
    }


    public void setInputMode(int inputMode) {
        this.inputMode = inputMode;
        gameController.camDx = 0;
        gameController.camDy = 0;
        gameController.camDZoom = 0;
    }


    public void setInputColor(int inputColor) {
        this.inputColor = inputColor;
        setRandomColor(false);
        if (inputColor >= GameController.MAX_COLOR_NUMBER) setRandomColor(true);
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
}
