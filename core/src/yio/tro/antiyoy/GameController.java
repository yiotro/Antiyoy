package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import yio.tro.antiyoy.factor_yio.FactorYio;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by ivan on 05.08.14.
 */
public class GameController {

    YioGdxGame yioGdxGame;
    int w, h, screenX, screenY, touchDownX, touchDownY;
    int maxTouchCount, currentTouchCount, lastTouchCount;
    public static int colorNumber = 5;
    public static boolean slay_rules = false;
    public static float sensitivity;
    public static final int MAX_COLOR_NUMBER = 7;
    public static final int SIZE_SMALL = 1;
    public static final int SIZE_MEDIUM = 2;
    public static final int SIZE_BIG = 4;
    public static final int UNIT_MOVE_LIMIT = 4;
    public static final int PRICE_UNIT = 10;
    public static final int PRICE_TOWER = 15;
    public static final int PRICE_FARM = 12;
    public static final int PRICE_STRONG_TOWER = 50;
    public static final int EASY = 0;
    public static final int NORMAL = 1;
    public static final int HARD = 2;
    public static final int EXPERT = 3;
    Random random, predictableRandom;
    private LanguagesManager languagesManager;
    boolean tutorialMode, multiTouchDetected, letsUpdateCacheByAnim, updateWholeCache, campaignMode, editorMode;
    int progress; // progress - index of unlocked level
    long currentTime;
    private long lastTimeTouched;
    private long lastTimeDragged;
    private boolean blockMultiInput;
    private boolean letsCheckAnimHexes;
    private boolean checkToMarch, ignoreMarch;
    float defaultBubbleRadius, hexSize, hexStep1, hexStep2;
    Hex field[][];
    ArrayList<Hex> activeHexes, selectedHexes, animHexes;
    int fWidth, fHeight, turn, levelSize;
    PointYio fieldPos;
    float camDx, camDy, lastMultiTouchDistance, camDZoom, trackerZoom;
    float fieldX1, fieldY1, fieldX2, fieldY2; // bounds of field
    float frameX1, frameY1, frameX2, frameY2; // what is visible
    private long timeToUnblockMultiInput, timeToUpdateCache, touchDownTime;
    private OrthographicCamera orthoCam;
    float cos60, sin60, selectX, selectY, deltaMovementFactor, boundWidth, boundHeight;
    Hex focusedHex, emptyHex, responseAnimHex, defTipHex;
    boolean readyToEndTurn, blockDragToRight, blockDragToLeft, blockDragToUp, blockDragToDown, backgroundVisible;
    private boolean proposedSurrender, showCityNames;
    private ArrayList<ArtificialIntelligence> aiList;
    ArrayList<Hex> solidObjects, moveZone, defenseTips;
    ArrayList<Unit> unitList;
    Unit selectedUnit;
    FactorYio selUnitFactor, selMoneyFactor, responseAnimFactor, tipFactor, moveZoneFactor, blackoutFactor, defenseTipFactor;
    ArrayList<Province> provinces;
    Province selectedProvince;
    int selectedProvinceMoney, tipType, tipShowType, marchDelay, playersNumber, currentLevelIndex, compensationOffsetY, defTipDelay;
    int difficulty, colorIndexViewOffset, neutralLandsIndex;
    String balanceString, currentPriceString, levelInitialString;
    double currentCamSpeed, zoomUpperLimit, cameraOffset;
    private ArrayList<LevelSnapshot> levelSnapshots;
    long timeToCheckAnimHexes, defTipSpawnTime;
    MapGenerator mapGeneratorSlay, mapGeneratorGeneric;
    Unit jumperUnit;
    public Statistics statistics;
    GameSaver gameSaver;
    Forefinger forefinger;
    public TutorialScript tutorialScript;
    private CampaignLevelFactory campaignLevelFactory;
    private LevelEditor levelEditor;


    public GameController(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        cos60 = (float) Math.cos(Math.PI / 3d);
        sin60 = (float) Math.sin(Math.PI / 3d);
        random = new Random();
        predictableRandom = new Random(0);
        languagesManager = yioGdxGame.menuControllerYio.languagesManager;
        progress = yioGdxGame.selectedLevelIndex;
        defaultBubbleRadius = 0.01f * Gdx.graphics.getWidth();
        fieldPos = new PointYio();
        fieldPos.y = -0.5f * h;
        hexSize = 0.05f * Gdx.graphics.getWidth();
        hexStep1 = (float) Math.sqrt(3) * hexSize;
        hexStep2 = (float) YioGdxGame.distance(0, 0, 1.5 * hexSize, 0.5 * hexStep1);
        fWidth = 46;
        fHeight = 30;
        deltaMovementFactor = 48;
        marchDelay = 500;
        defTipDelay = 1000;
        cameraOffset = 0.1 * w;
        activeHexes = new ArrayList<Hex>();
        selectedHexes = new ArrayList<Hex>();
        animHexes = new ArrayList<Hex>();
        solidObjects = new ArrayList<Hex>();
        moveZone = new ArrayList<Hex>();
        field = new Hex[fWidth][fHeight];
        selUnitFactor = new FactorYio();
        selMoneyFactor = new FactorYio();
        responseAnimFactor = new FactorYio();
        moveZoneFactor = new FactorYio();
        blackoutFactor = new FactorYio();
        tipFactor = new FactorYio();
        unitList = new ArrayList<Unit>();
        provinces = new ArrayList<Province>();
        emptyHex = new Hex(-1, -1, new PointYio(), this);
        emptyHex.active = false;
        levelSnapshots = new ArrayList<LevelSnapshot>();
        mapGeneratorSlay = new MapGenerator(this);
        mapGeneratorGeneric = new MapGeneratorGeneric(this);
        jumperUnit = new Unit(this, emptyHex, 0);
        statistics = new Statistics(this);
        gameSaver = new GameSaver(this);
        forefinger = new Forefinger(this);
        campaignLevelFactory = new CampaignLevelFactory(this);
        levelEditor = new LevelEditor(this);
        defenseTipFactor = new FactorYio();
        defenseTips = new ArrayList<Hex>();
    }


    public void clearField() {
        selectedUnit = null;
        solidObjects.clear();
        unitList.clear();
        provinces.clear();
        moveZone.clear();
        clearActiveHexesList();
    }


    void clearActiveHexesList() {
        ListIterator listIterator = activeHexes.listIterator();
        while (listIterator.hasNext()) {
            listIterator.next();
            listIterator.remove();
        }
    }


    public void clearLevel() {
        if (editorMode) {
            levelEditor.clearLevel();
        }
    }


    void createField(boolean generateMap) {
        clearField();
        fieldPos.y = -0.5f * h;
        levelSnapshots.clear();
        if (generateMap) {
            if (slay_rules) {
                mapGeneratorSlay.generateMap(predictableRandom, field);
            } else {
                mapGeneratorGeneric.generateMap(predictableRandom, field);
            }
            detectProvinces();
            deselectAll();
            detectNeutralLands();
        }
        prepareCertainUnitsToMove();
    }


    private void detectNeutralLands() {
        if (slay_rules) return;

        for (Hex activeHex : activeHexes) {
            activeHex.genFlag = false;
        }

        for (Province province : provinces) {
            for (Hex hex : province.hexList) {
                hex.genFlag = true;
            }
        }

        for (Hex activeHex : activeHexes) {
            if (activeHex.genFlag) continue;

            activeHex.setColorIndex(neutralLandsIndex);
        }
    }


    private void killUnitOnHex(Hex hex) {
        cleanOutHex(hex);
        addSolidObject(hex, Hex.OBJECT_GRAVE);
        hex.animFactor.beginSpawning(1, 2);
    }


    private void killEveryoneInProvince(Province province) {
        for (Hex hex : province.hexList) {
            if (hex.containsUnit()) {
                killUnitOnHex(hex);
            }
        }
    }


    private void checkForAloneUnits() {
        for (int i = 0; i < unitList.size(); i++) {
            Unit unit = unitList.get(i);
            if (isCurrentTurn(unit.getColor()) && unit.currHex.numberOfFriendlyHexesNearby() == 0) {
                killUnitOnHex(unit.currHex);
                i--;
            }
        }
    }


    private void checkForBankrupts() {
        for (Province province : provinces) {
            if (isCurrentTurn(province.getColor())) {
                if (province.money < 0) {
                    province.money = 0;
                    killEveryoneInProvince(province);
                }
            }
        }
    }


    public void move() {
        currentTime = System.currentTimeMillis();
        checkForAiToMove();
        checkToEndTurn();
        checkToUpdateCacheByAnim();

        if (letsCheckAnimHexes && currentTime > timeToCheckAnimHexes && (isPlayerTurn() || isCurrentTurn(0)))
            checkAnimHexes();

        moveCheckToMarch();
        moveUnits();
        moveAnimHexes();
        moveSelections();

        jumperUnit.moveJumpAnim();
        moveZoneFactor.move();
        blackoutFactor.move();
        moveDefenseTips();

        if (moveZone.size() > 0 && moveZoneFactor.get() < 0.01) clearMoveZone();
        tipFactor.move();

        moveResponseAnimHex();
        cameraMovement();
        moveTutorialStuff();
    }


    private void moveTutorialStuff() {
        if (tutorialMode) {
            forefinger.move();
            tutorialScript.move();
        }
    }


    private void moveResponseAnimHex() {
        if (responseAnimHex != null) {
            responseAnimFactor.move();
            if (responseAnimFactor.get() < 0.01) responseAnimHex = null;
        }
    }


    private void checkForAiToMove() {
        if (!isPlayerTurn() && !readyToEndTurn) {
            aiList.get(turn).makeMove();
            updateCacheOnceAfterSomeTime();
            readyToEndTurn = true;
        }
    }


    private void moveSelections() {
        for (Hex hex : selectedHexes) hex.move();
        if (selectedUnit != null && selUnitFactor.needsToMove()) {
            selUnitFactor.move();
        }
    }


    private void moveAnimHexes() {
        for (Hex hex : animHexes) {
            if (!hex.selected) hex.move(); // to prevent double call of move()
            if (!letsCheckAnimHexes && hex.animFactor.get() > 0.99) {
                letsCheckAnimHexes = true;
            }

            // animation is off because it's buggy
            if (hex.animFactor.get() < 1) hex.animFactor.setValues(1, 0);
        }
    }


    private void moveUnits() {
        for (Unit unit : unitList) {
            unit.moveJumpAnim();
            unit.move();
        }
    }


    public void setIgnoreMarch(boolean ignoreMarch) {
        this.ignoreMarch = ignoreMarch;
    }


    private void moveCheckToMarch() {
        if (ignoreMarch) return;
        if (!checkToMarch) return;
        if (currentTouchCount != 1) return;
        if (currentTime - touchDownTime <= marchDelay) return;
        if (!touchedAsClick()) return;

        checkToMarch = false;
        updateFocusedHex();
        if (focusedHex.active) {
            marchUnitsToHex(focusedHex);
        }
    }


    private void checkToUpdateCacheByAnim() {
        if (letsUpdateCacheByAnim && currentTime > timeToUpdateCache && (isPlayerTurn() || isCurrentTurn(0)) && !isSomethingMoving()) {
            letsUpdateCacheByAnim = false;
            if (updateWholeCache) yioGdxGame.gameView.updateCacheLevelTextures();
            else yioGdxGame.gameView.updateCacheNearAnimHexes();
            updateWholeCache = false;
        }
    }


    private void moveDefenseTips() {
        defenseTipFactor.move();
        if (currentTime > defTipSpawnTime + defTipDelay) {
            if (defenseTipFactor.getDy() >= 0) defenseTipFactor.beginDestroying(1, 1);
            if (defenseTipFactor.get() == 0 && defenseTips.size() > 0) {
                ListIterator iterator = defenseTips.listIterator();
                while (iterator.hasNext()) {
                    Hex hex = (Hex) iterator.next();
                    iterator.remove();
                }
            }
        }
    }


    private boolean canEndTurn() {
        if (YioGdxGame.CHECKING_BALANCE_MODE) return true; // fast forward when measuring balance
        if (!readyToEndTurn) return false;
        if (currentCamSpeed > 0.01) return false;
        if (Math.abs(camDZoom) > 0.01) return false;
        if (isPlayerTurn() || isCurrentTurn(0)) {
            return animHexes.size() == 0;
        } else {
            return true;
        }
    }


    private void checkToEndTurn() {
        if (canEndTurn()) {
            readyToEndTurn = false;
            turnEndActions();
            turn = getNextTurnIndex();
            turnStartActions();
        }
    }


    private void prepareCertainUnitsToMove() {
        for (Unit unit : unitList) {
            if (isCurrentTurn(unit.getColor())) {
                unit.setReadyToMove(true);
                unit.startJumping();
            }
        }
    }


    private int checkIfWeHaveWinner() {
        if (activeHexes.size() == 0) return -1;
        int color = activeHexes.get(0).colorIndex;
        for (Hex activeHex : activeHexes) {
            if (activeHex.colorIndex != color) return -1;
        }
        return color;
    }


    private int zeroesInArray(int array[]) {
        int zeroes = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) zeroes++;
        }
        return zeroes;
    }


    public int[] getPlayerHexCount() {
        int playerHexCount[] = new int[colorNumber];
        for (Hex activeHex : activeHexes) {
            if (activeHex.isNeutral()) continue;
            if (activeHex.isInProvince())
                playerHexCount[activeHex.colorIndex]++;
        }
        return playerHexCount;
    }


    private int possibleWinner() {
        int numberOfAllHexes = activeHexes.size();
        for (Province province : provinces) {
            if (province.hexList.size() > 0.5 * numberOfAllHexes) {
                return province.getColor();
            }
        }

        int playerHexCount[] = getPlayerHexCount();
        for (int i = 0; i < playerHexCount.length; i++) {
            if (playerHexCount[i] > 0.7 * numberOfAllHexes) {
                return i;
            }
        }

        return -1;
    }


    private void checkToEndGame() {
        // captured everything
        int winner = checkIfWeHaveWinner();
        if (winner >= 0) {
            endGame(winner);
        }

        // propose surrender
        if (!proposedSurrender) {
            int possibleWinner = possibleWinner();
            if (possibleWinner >= 0 && isPlayerTurn(possibleWinner)) {
                yioGdxGame.menuControllerYio.createTutorialTip(yioGdxGame.menuControllerYio.getArrayListFromString(languagesManager.getString("win_or_continue")));
                yioGdxGame.menuControllerYio.addWinButtonToTutorialTip();
                proposedSurrender = true;
            }
        }

        // too long game
        if (statistics.turnsMade == 299) {
            int playerHexCount[] = getPlayerHexCount();
            endGame(indexOfNumberInArray(playerHexCount, maxNumberFromArray(playerHexCount)));
        }
    }


    private int indexOfNumberInArray(int array[], int number) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == number) return i;
        }
        return -1;
    }


    public static int maxNumberFromArray(int array[]) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) max = array[i];
        }
        return max;
    }


    public void forceGameEnd() {
        int playerHexCount[] = getPlayerHexCount();
        int maxNumber = maxNumberFromArray(playerHexCount);
        int maxColor = 0;
        for (int i = 0; i < playerHexCount.length; i++) {
            if (maxNumber == playerHexCount[i]) {
                maxColor = i;
                break;
            }
        }
        for (Hex activeHex : activeHexes) {
            activeHex.colorIndex = maxColor;
        }
        checkToEndGame();
    }


    boolean completedCampaignLevel(int winColor) {
        return campaignMode && winColor == 0;
    }


    private void endGame(int winColor) {
//        yioGdxGame.setGamePaused(true);
        if (completedCampaignLevel(winColor)) {
            int ls = currentLevelIndex;
//            yioGdxGame.increaseLevelSelection();
            if (currentLevelIndex >= progress) {
                progress = currentLevelIndex;
                if (ls == progress) progress++; // last level completed
                Preferences preferences = Gdx.app.getPreferences("main");
                preferences.putInteger("progress", progress);
                preferences.flush();
//                yioGdxGame.menuControllerYio.updateScrollerLinesBeforeIndex(progress);
                yioGdxGame.menuControllerYio.levelSelector.update();
            }
        }
        yioGdxGame.menuControllerYio.createAfterGameMenu(winColor, isPlayerTurn(winColor));
        if (YioGdxGame.CHECKING_BALANCE_MODE) {
            yioGdxGame.balanceIndicator[winColor]++;
            yioGdxGame.startGame(true, false);
        }
    }


    private void turnEndActions() {
        checkToEndGame();
        collectTributesAndPayTaxes();
        for (Unit unit : unitList) {
            unit.setReadyToMove(false);
            unit.stopJumping();
        }
        if (!isPlayerTurn()) {

        }
    }


    private void turnStartActions() {
        deselectAll();
        if (isCurrentTurn(0)) {
            statistics.turnWasMade();
            expandTrees();
        }
        prepareCertainUnitsToMove();
        transformGraves(); // this must be called before 'check for bankrupts' and after 'expand trees'
        checkForBankrupts();
        checkForAloneUnits();
        if (isCurrentTurn(0)) yioGdxGame.gameView.updateCacheLevelTextures();
        if (isPlayerTurn()) {
            currentTouchCount = 0;
            levelSnapshots.clear();
            jumperUnit.startJumping();
            if (numberOfProvincesWithColor(turn) == 0) {
                endTurnButtonPressed();
            }
        } else {
            for (Hex animHex : animHexes) {
                animHex.animFactor.setValues(1, 0);
            }
        }
        if (YioGdxGame.autosave && turn == 0 && playersNumber == 1) autoSave();
    }


    int numberOfProvincesWithColor(int color) {
        int count = 0;
        for (Province province : provinces) {
            if (province.getColor() == color)
                count++;
        }
        return count;
    }


    private void collectTributesAndPayTaxes() {
        for (Province province : provinces) {
            if (isCurrentTurn(province.getColor())) {
                province.money += province.getIncome();
                province.money -= province.getTaxes();
            }
        }
    }


    private void transformGraves() {
        for (Hex hex : activeHexes) {
            if (isCurrentTurn(hex.colorIndex) && hex.objectInside == Hex.OBJECT_GRAVE) {
                spawnTree(hex);
                hex.blockToTreeFromExpanding = true;
            }
        }
    }


    private void unFlagAllHexesInArrayList(ArrayList<Hex> hexList) {
        for (int i = hexList.size() - 1; i >= 0; i--) {
            hexList.get(i).flag = false;
            hexList.get(i).inMoveZone = false;
        }
    }


    void detectProvinces() {
        if (editorMode) return;
        unFlagAllHexesInArrayList(activeHexes);
        ArrayList<Hex> tempList = new ArrayList<Hex>();
        ArrayList<Hex> propagationList = new ArrayList<Hex>();
        Hex tempHex, adjHex;
        for (Hex hex : activeHexes) {
            if (!slay_rules && hex.colorIndex == neutralLandsIndex) continue;
            if (!hex.flag) {
                tempList.clear();
                propagationList.clear();
                propagationList.add(hex);
                hex.flag = true;
                while (propagationList.size() > 0) {
                    tempHex = propagationList.get(0);
                    tempList.add(tempHex);
                    propagationList.remove(0);
                    for (int i = 0; i < 6; i++) {
                        adjHex = tempHex.adjacentHex(i);
                        if (adjHex.active && adjHex.sameColor(tempHex) && !adjHex.flag) {
                            propagationList.add(adjHex);
                            adjHex.flag = true;
                        }
                    }
                }
                if (tempList.size() >= 2) {
                    Province province = new Province(this, tempList);
                    if (!province.hasCapital()) province.placeCapitalInRandomPlace(YioGdxGame.random);
                    addProvince(province);
                }
            }
        }
    }


    private void forceAnimEndInHex(Hex hex) {
        hex.animFactor.setValues(1, 0);
    }


    int howManyPalms() {
        int c = 0;
        for (Hex activeHex : activeHexes) {
            if (activeHex.objectInside == Hex.OBJECT_PALM) c++;
        }
        return c;
    }


    private void expandTrees() {
        ArrayList<Hex> newPalmsList = new ArrayList<Hex>();
        for (Hex hex : activeHexes) {
            if (canSpawnPalmOnHex(hex)) {
                newPalmsList.add(hex);
            }
        }

        ArrayList<Hex> newPinesList = new ArrayList<Hex>();
        for (Hex hex : activeHexes) {
            if (canSpawnPineOnHex(hex)) {
                newPinesList.add(hex);
            }
        }

        for (int i = newPalmsList.size() - 1; i >= 0; i--) {
            addSolidObject(newPalmsList.get(i), Hex.OBJECT_PALM);
            addAnimHex(newPalmsList.get(i));
            newPalmsList.get(i).animFactor.setValues(1, 0);
        }

        for (int i = newPinesList.size() - 1; i >= 0; i--) {
            addSolidObject(newPinesList.get(i), Hex.OBJECT_PINE);
            addAnimHex(newPinesList.get(i));
            newPinesList.get(i).animFactor.setValues(1, 0);
        }

        for (Hex activeHex : activeHexes) {
            if (activeHex.containsTree() && activeHex.blockToTreeFromExpanding)
                activeHex.blockToTreeFromExpanding = false;
        }
    }


    private boolean canSpawnPineOnHex(Hex hex) {
        if (slay_rules) {
            return canSpawnPineOnHexSlayRules(hex);
        } else {
            return canSpawnPineOnHexGenericRules(hex);
        }
    }


    private boolean canSpawnPalmOnHex(Hex hex) {
        if (slay_rules) {
            return canSpawnPalmOnHexSlayRules(hex);
        } else {
            return canSpawnPalmOnHexGenericRules(hex);
        }
    }


    private boolean canSpawnPineOnHexSlayRules(Hex hex) {
        return hex.isFree() && howManyTreesNearby(hex) >= 2 && hex.hasPineReadyToExpandNearby() && random.nextDouble() < 0.8;
    }


    private boolean canSpawnPalmOnHexSlayRules(Hex hex) {
        return hex.isFree() && hex.isNearWater() && hex.hasPalmReadyToExpandNearby();
    }


    private boolean canSpawnPineOnHexGenericRules(Hex hex) {
        return hex.isFree() && howManyTreesNearby(hex) >= 2 && hex.hasPineReadyToExpandNearby() && random.nextDouble() < 0.2;
    }


    private boolean canSpawnPalmOnHexGenericRules(Hex hex) {
        return hex.isFree() && hex.isNearWater() && hex.hasPalmReadyToExpandNearby() && random.nextDouble() < 0.3;
    }


    private int howManyTreesNearby(Hex hex) {
        if (!hex.active) return 0;
        int c = 0;
        for (int i = 0; i < 6; i++)
            if (hex.adjacentHex(i).containsTree()) c++;
        return c;
    }


    private void checkAnimHexes() {
        // important
        // this fucking anims hexes have to live long enough
        // if killed too fast, graphic bugs will show
        if (isSomethingMoving()) {
            timeToCheckAnimHexes = currentTime + 100;
            return;
        }
        letsCheckAnimHexes = false;
        ListIterator iterator = animHexes.listIterator();
        while (iterator.hasNext()) {
            Hex h = (Hex) iterator.next();
            if (h.animFactor.get() > 0.99 && !(h.containsUnit() && h.unit.moveFactor.get() < 1) && System.currentTimeMillis() > h.animStartTime + 250) {
                h.changingColor = false;
                iterator.remove();
            }
        }
    }


    private void cameraMovement() {
        if (editorMode && !levelEditor.isCameraMovementAllowed()) return;

        float k = sensitivity * deltaMovementFactor * 0.025f;
        yioGdxGame.gameView.orthoCam.translate(k * camDx, k * camDy);
        yioGdxGame.gameView.updateCam();
        if ((currentTouchCount == 0 && currentTime > lastTimeTouched + 10) || (currentTouchCount == 1 && currentTime > lastTimeDragged + 10)) {
            camDx *= 0.8;
            camDy *= 0.8;
        }
        currentCamSpeed = YioGdxGame.distance(0, 0, camDx, camDy);
        if (Math.abs(camDZoom) > 0.01) {
            if (trackerZoom > zoomUpperLimit) {
                camDZoom = -0.1f;
                blockMultiInputForSomeTime(50);
            }
            if (trackerZoom < 0.5) {
                camDZoom = 0.1f;
                blockMultiInputForSomeTime(50);
            }
            yioGdxGame.gameView.orthoCam.zoom += 0.2 * camDZoom;
            trackerZoom += 0.2 * camDZoom;
            yioGdxGame.gameView.updateCam();
            if ((currentTouchCount == 0 && currentTime > lastTimeTouched + 10) || (currentTouchCount == 1 && currentTime > lastTimeDragged + 10)) {
                camDZoom *= 0.75;
            }
        }
        fieldX1 = 0.5f * w - orthoCam.position.x / orthoCam.zoom;
        fieldX2 = fieldX1 + boundWidth / orthoCam.zoom;
        fieldY1 = 0.5f * h - orthoCam.position.y / orthoCam.zoom;
        fieldY2 = fieldY1 + boundHeight / orthoCam.zoom;
        updateFrame();
        if (blockDragToLeft) blockDragToLeft = false;
        if (blockDragToRight) blockDragToRight = false;
        if (blockDragToUp) blockDragToUp = false;
        if (blockDragToDown) blockDragToDown = false;
        backgroundVisible = false;
        if (fieldX2 - fieldX1 < 1.1f * w) { //center
            float deltaX = 0.2f * (0.5f * boundWidth / orthoCam.zoom - orthoCam.position.x / orthoCam.zoom);
            yioGdxGame.gameView.orthoCam.translate(deltaX, 0);
            backgroundVisible = true;
        } else {
            if (fieldX1 > 0 || fieldX2 < w) {
                backgroundVisible = true;
            }
            if (fieldX1 > 0 + cameraOffset) {
                camDx = boundPower();
            }
            if (fieldX1 > -0.1 * w + cameraOffset) blockDragToLeft = true;
            if (fieldX2 < w - cameraOffset) {
                camDx = -boundPower();
            }
            if (fieldX2 < 1.1 * w - cameraOffset) blockDragToRight = true;
        }
        if (fieldY2 - fieldY1 < 1.1f * h) {
            float deltaY = 0.2f * (0.5f * boundHeight / orthoCam.zoom - orthoCam.position.y / orthoCam.zoom);
            yioGdxGame.gameView.orthoCam.translate(0, deltaY);
            backgroundVisible = true;
        } else {
            if (fieldY1 > 0 || fieldY2 < h) {
                backgroundVisible = true;
            }
            if (fieldY1 > 0 + cameraOffset) {
                camDy = boundPower();
            }
            if (fieldY1 > -0.1 * w + cameraOffset) blockDragToDown = true;
            if (fieldY2 < h - cameraOffset) {
                camDy = -boundPower();
            }
            if (fieldY2 < 1.1 * h - cameraOffset) blockDragToUp = true;
        }
    }


    private float boundPower() {
        return 0.002f * w * trackerZoom * (1 + trackerZoom);
    }


    void updateCacheOnceAfterSomeTime() {
        letsUpdateCacheByAnim = true;
        timeToUpdateCache = System.currentTimeMillis() + 30;
    }


    private void updateFrame() {
        frameX1 = (0 - 0.5f * w) * orthoCam.zoom + orthoCam.position.x;
        frameX2 = (w - 0.5f * w) * orthoCam.zoom + orthoCam.position.x;
        frameY1 = (0 - 0.5f * h) * orthoCam.zoom + orthoCam.position.y;
        frameY2 = (h - 0.5f * h) * orthoCam.zoom + orthoCam.position.y;
    }


    public void endTurnButtonPressed() {
        camDx = 0;
        camDy = 0;
        currentTouchCount = 0;
        if (!isPlayerTurn()) return;
        readyToEndTurn = true;
    }


    private void blockMultiInputForSomeTime(int time) {
        blockMultiInput = true;
        timeToUnblockMultiInput = System.currentTimeMillis() + time;
    }


    public void setProgress(int progress) {
        this.progress = progress;
    }


    private void defaultValues() {
        trackerZoom = 1;
        maxTouchCount = 0;
        currentTouchCount = 0;
        readyToEndTurn = false;
        ignoreMarch = false;
        selectedProvince = null;
        selectedUnit = null;
        turn = 0;
        compensationOffsetY = 0;
        moveZoneFactor.setValues(0, 0);
        jumperUnit.startJumping();
        statistics.defaultValues();
//        ReactBehavior.rbShowColorStats.loadTexturesIfNotLoaded();
        tutorialMode = false;
        campaignMode = false;
        proposedSurrender = false;
        colorIndexViewOffset = 0;
        neutralLandsIndex = 7;
    }


    private int getLevelSizeBySliderPos(SliderYio sliderYio) {
        switch (sliderYio.getCurrentRunnerIndex()) {
            default:
            case 0:
                return SIZE_SMALL;
            case 1:
                return SIZE_MEDIUM;
            case 2:
                return SIZE_BIG;
        }
    }


    public void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
        if (YioGdxGame.CHECKING_BALANCE_MODE) this.playersNumber = 0;
    }


    private void setPlayersNumberBySlider(SliderYio slider) {
        setPlayersNumber(slider.getCurrentRunnerIndex());
    }


    public static void setColorNumber(int colorNumber) {
        GameController.colorNumber = colorNumber;
    }


    private void setColorNumberBySlider(SliderYio slider) {
        setColorNumber(slider.getCurrentRunnerIndex() + 2);
    }


    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }


    private void setDifficultyBySlider(SliderYio slider) {
        difficulty = slider.getCurrentRunnerIndex();
    }


    public void initTutorial() {
        if (slay_rules) {
            tutorialScript = new TutorialScriptSlayRules(this);
        } else {
            tutorialScript = new TutorialScriptGenericRules(this);
        }
        tutorialScript.createTutorialGame();
        tutorialMode = true;
    }


    void prepareForNewGame(int index, boolean generateMap, boolean readParametersFromSliders) {
        defaultValues();
        yioGdxGame.beginBackgroundChange(4, false, true);

        predictableRandom = new Random(index); // used in map generation
        if (readParametersFromSliders) {
            setLevelSize(getLevelSizeBySliderPos(yioGdxGame.menuControllerYio.sliders.get(0)));
            setPlayersNumberBySlider(yioGdxGame.menuControllerYio.sliders.get(1));
            setColorNumberBySlider(yioGdxGame.menuControllerYio.sliders.get(2));
            setDifficultyBySlider(yioGdxGame.menuControllerYio.sliders.get(3));
            readColorOffsetFromSlider();
            slay_rules = yioGdxGame.menuControllerYio.getCheckButtonById(6).isChecked();
        }
        createField(generateMap); // generating map

//        campaignLevelFactory.generateLevels();

        // finishing
        createCamera();
        yioGdxGame.gameView.updateCacheLevelTextures();
        clearAnims();
        createAiList(difficulty);
        updateLevelInitialString();
        if (YioGdxGame.CHECKING_BALANCE_MODE) {
            while (true) {
                move();
                checkToEndGame();
            }
        }
    }


    public void readColorOffsetFromSlider() {
        int sliderIndex = yioGdxGame.menuControllerYio.sliders.get(4).getCurrentRunnerIndex();
        if (sliderIndex == 0) { // random
            colorIndexViewOffset = predictableRandom.nextInt(colorNumber);
            return;
        }
        colorIndexViewOffset = sliderIndex - 1;
    }


    void updateLevelInitialString() {
        levelInitialString = gameSaver.getActiveHexesString();
    }


    private void sayArray(int array[]) {
        System.out.print("[ ");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println("]");
    }


    int getPredictionForWinner() {
        int numbers[] = new int[colorNumber];
        for (Hex activeHex : activeHexes) {
            if (activeHex.isNeutral()) continue;
            numbers[activeHex.colorIndex]++;
        }

        int max = numbers[0];
        int maxIndex = 0;
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }


    boolean areConditionsGoodForPlayer() {
        int numbers[] = new int[colorNumber];
        for (Hex activeHex : activeHexes) {
            if (activeHex.isNeutral()) continue;
            numbers[activeHex.colorIndex]++;
        }

        int max = maxNumberFromArray(numbers);
        return max - numbers[0] < 2;
    }


    public void createCamera() {
        yioGdxGame.gameView.createOrthoCam();
        orthoCam = yioGdxGame.gameView.orthoCam;
        orthoCam.translate((boundWidth - w) / 2, (boundHeight - h) / 2); // focus camera of center
        yioGdxGame.gameView.updateCam();
        updateFrame();
    }


    void createAiList() {
        createAiList(difficulty);
    }


    private void createAiList(int difficulty) {
        aiList = new ArrayList<ArtificialIntelligence>();

        boolean testingNewAi = false;
        if (YioGdxGame.CHECKING_BALANCE_MODE && testingNewAi && colorNumber == 5) {
            aiList.add(new AiHardSlayRules(this, 0));
            aiList.add(new AiHardSlayRules(this, 1));
            aiList.add(new AiHardSlayRules(this, 2));
            aiList.add(new AiExpertSlayRules(this, 3));
            aiList.add(new AiExpertSlayRules(this, 4));
            return;
        }

        for (int i = 0; i < colorNumber; i++) {
            switch (difficulty) {
                default:
                case EASY:
                    aiList.add(new AiEasy(this, i));
                    break;
                case NORMAL:
                    if (GameController.slay_rules) {
                        aiList.add(new AiNormalSlayRules(this, i));
                    } else {
                        aiList.add(new AiNormalGenericRules(this, i));
                    }
                    break;
                case HARD:
                    if (GameController.slay_rules) {
                        aiList.add(new AiHardSlayRules(this, i));
                    } else {
                        aiList.add(new AiHardGenericRules(this, i));
                    }
                    break;
                case EXPERT:
                    if (GameController.slay_rules) {
                        aiList.add(new AiExpertSlayRules(this, i));
                    } else {
                        aiList.add(new AiExpertGenericRules(this, i));
                    }
                    break;
            }
        }
    }


    void clearAnims() {
        ListIterator iterator = animHexes.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }


    int getRandomLevelSize() {
        switch (random.nextInt(3)) {
            default:
            case 0:
                return SIZE_SMALL;
            case 1:
                return SIZE_MEDIUM;
            case 2:
                return SIZE_BIG;
        }
    }


    void setLevelSize(int size) {
        switch (size) {
            case SIZE_SMALL:
                boundWidth = w;
                boundHeight = h;
                zoomUpperLimit = 1.1;
                break;
            case SIZE_MEDIUM:
                boundWidth = 2 * w;
                boundHeight = h;
                zoomUpperLimit = 1.7;
                break;
            case SIZE_BIG:
                boundWidth = 2 * w;
                boundHeight = 2 * h;
                zoomUpperLimit = 2.1;
                break;
            default:
                return;
        }
        levelSize = size;
        yioGdxGame.gameView.createLevelCacheTextures();
    }


    void createFieldMatrix() {
        for (int i = 0; i < fWidth; i++) {
            field[i] = new Hex[fHeight];
            for (int j = 0; j < fHeight; j++) {
                field[i][j] = new Hex(i, j, fieldPos, this);
                field[i][j].ignoreTouch = false;
            }
        }
    }


    private void marchUnitsToHex(Hex toWhere) {
        if (!isSomethingSelected()) return;
        if (!toWhere.isSelected()) return;
        if (selectedProvince.hasSomeoneReadyToMove()) {
            takeSnapshot();
            for (Hex hex : selectedProvince.hexList) {
                if (hex.containsUnit() && hex.unit.isReadyToMove()) {
                    hex.unit.marchToHex(toWhere, selectedProvince);
                }
            }
        }
        setResponseAnimHex(toWhere);
        SoundControllerYio.playSound(SoundControllerYio.soundHoldToMarch);
    }


    private void setResponseAnimHex(Hex hex) {
        responseAnimHex = hex;
        responseAnimFactor.setValues(1, 0.07);
        responseAnimFactor.beginDestroying(1, 2);
    }


    public void awakeTip(int type) {
        tipFactor.setValues(0, 0);
        tipFactor.beginSpawning(3, 2);
        tipType = type;
        tipShowType = type;
        selectedUnit = null;
        if ((tipType == 0 || tipType >= 5) && moveZone.size() > 0) hideMoveZone();
        updateCurrentPriceString();
    }


    private void hideTip() {
        tipFactor.beginDestroying(1, 2);
        tipType = -1;
    }


    private void hideMoveZone() {
        moveZoneFactor.beginDestroying(1, 5);
        blackoutFactor.beginDestroying(1, 5);
    }


    public int getTipType() {
        return tipType;
    }


    public GameSaver getGameSaver() {
        return gameSaver;
    }


    public void debugActions() {
//        System.out.println("" + gameSaver.getActiveHexesString());
//        for (Hex activeHex : activeHexes) {
//            if (random.nextDouble() > 0.5)
//                setHexColor(activeHex, 0);
//        }
    }


    public boolean isSomethingSelected() {
        return selectedHexes.size() > 0;
    }


    public boolean isShowCityNames() {
        return showCityNames;
    }


    public void setShowCityNames(int cityNames) {
        if (cityNames == 1) {
            showCityNames = true;
        } else {
            showCityNames = false;
        }
    }


    public void deselectAll() {
        for (int i = 0; i < fWidth; i++)
            for (int j = 0; j < fHeight; j++) {
                field[i][j].selected = false;
            }
        ListIterator listIterator = selectedHexes.listIterator();
        while (listIterator.hasNext()) {
            listIterator.next();
            listIterator.remove();
        }
//        if (selectedUnit != null) selectedUnit.selected = false;
        selectedUnit = null;
        selMoneyFactor.beginDestroying(3, 2);
        tipFactor.setValues(0, 0);
        tipFactor.beginDestroying(1, 1);
        hideMoveZone();
        yioGdxGame.menuControllerYio.hideBuildButtons();
        tipType = -1;
    }


    void selectAdjacentHexes(Hex startHex) {
        setSelectedProvince(startHex);
        ListIterator listIterator = selectedHexes.listIterator();
        for (Hex hex : selectedProvince.hexList) {
            hex.select();
            if (!selectedHexes.contains(hex)) listIterator.add(hex);
        }
        yioGdxGame.menuControllerYio.revealBuildButtons();
        updateBalanceString();
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
    }


    private void setSelectedProvince(Hex hex) {
        selectedProvince = getProvinceByHex(hex);
        selectedProvinceMoney = selectedProvince.money;
        selMoneyFactor.setDy(0);
        selMoneyFactor.beginSpawning(3, 2);
    }


    Hex getHexByPos(double x, double y) {
        int j = (int) ((x - fieldPos.x) / (hexStep2 * sin60));
        int i = (int) ((y - fieldPos.y - hexStep2 * j * cos60) / hexStep1);
        if (i < 0 || i > fWidth - 1 || j < 0 || j > fHeight - 1) return null;
        Hex adjHex, resHex = field[i][j];
        x -= yioGdxGame.gameView.hexViewSize;
        y -= yioGdxGame.gameView.hexViewSize;
        double currentDistance, minDistance = YioGdxGame.distance(resHex.pos.x, resHex.pos.y, x, y);
        for (int k = 0; k < 6; k++) {
            adjHex = adjacentHex(field[i][j], k);
            if (adjHex == null || !adjHex.active) continue;
            currentDistance = YioGdxGame.distance(adjHex.pos.x, adjHex.pos.y, x, y);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                resHex = adjHex;
            }
        }
        return resHex;
    }


    private Hex adjacentHex(int i, int j, int neighbourNumber) {
        switch (neighbourNumber) {
            case 0:
                if (i >= fWidth - 1) return emptyHex;
                return field[i + 1][j];
            case 1:
                if (j >= fHeight - 1) return emptyHex;
                return field[i][j + 1];
            case 2:
                if (i <= 0 || j >= fHeight - 1) return emptyHex;
                return field[i - 1][j + 1];
            case 3:
                if (i <= 0) return emptyHex;
                return field[i - 1][j];
            case 4:
                if (j <= 0) return emptyHex;
                return field[i][j - 1];
            case 5:
                if (i >= fWidth - 1 || j <= 0) return emptyHex;
                return field[i + 1][j - 1];
            default:
                return emptyHex;
        }
    }


    void spawnTree(Hex hex) {
        if (!hex.active) return;
        if (hex.isNearWater()) addSolidObject(hex, Hex.OBJECT_PALM);
        else addSolidObject(hex, Hex.OBJECT_PINE);
    }


    public void addSolidObject(Hex hex, int type) {
        if (hex == null || !hex.active) return;
        if (solidObjects.contains(hex)) cleanOutHex(hex);
        hex.setObjectInside(type);
        solidObjects.listIterator().add(hex);
    }


    public void cleanOutHex(Hex hex) {
        if (hex.containsUnit()) {
            statistics.unitWasKilled();
            unitList.remove(hex.unit);
            hex.unit = null;
        }
        hex.setObjectInside(0);
        addAnimHex(hex);
        ListIterator iterator = solidObjects.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next() == hex) {
                iterator.remove();
                return;
            }
        }
    }


    private void destroyBuildingsOnHex(Hex hex) {
        boolean hadHouse = (hex.objectInside == Hex.OBJECT_HOUSE);
        if (hex.containsBuilding()) cleanOutHex(hex);
//        if (hex.containsUnit()) killUnitOnHex(hex);
        if (hadHouse) {
            spawnTree(hex);
        }
    }


    public int getColorIndexWithOffset(int srcIndex) {
        if (editorMode) return srcIndex;
        srcIndex += colorIndexViewOffset;
        if (srcIndex >= colorNumber) {
            srcIndex -= colorNumber;
        }
        return srcIndex;
    }


    public void setCurrentLevelIndex(int currentLevelIndex) {
        this.currentLevelIndex = currentLevelIndex;
    }


    public int getNextLevelIndex() {
        int nextLevelIndex = currentLevelIndex + 1;
        if (nextLevelIndex > YioGdxGame.INDEX_OF_LAST_LEVEL) nextLevelIndex = YioGdxGame.INDEX_OF_LAST_LEVEL;
        return nextLevelIndex;
    }


    public boolean loadCampaignLevel(int index) {
        setCurrentLevelIndex(index);
        yioGdxGame.setSelectedLevelIndex(index);
        if (index == 0) { // tutorial level
            slay_rules = false;
            initTutorial();
            campaignMode = true;
            return true;
        }
        if (isLevelLocked(index)) return false;
        campaignLevelFactory.createCampaignLevel(index);
        campaignMode = true;
        return true;
    }


    private boolean isLevelLocked(int index) {
        return yioGdxGame.isLevelLocked(index);
    }


    boolean isLevelComplete(int index) {
        return yioGdxGame.isLevelComplete(index);
    }


    private void autoSave() {
        if (YioGdxGame.interface_type == YioGdxGame.INTERFACE_SIMPLE) {
            gameSaver.saveGame();
        } else if (YioGdxGame.interface_type == YioGdxGame.INTERFACE_COMPLICATED) {
            gameSaver.saveGameToSlot(0);
        }
    }


    public void saveGame() {
        gameSaver.saveGame();
    }


    public void loadGame() {
        gameSaver.loadGame();
    }


    private void takeSnapshot() {
        if (!isPlayerTurn()) return;
        LevelSnapshot snapshot = new LevelSnapshot(this);
        snapshot.takeSnapshot();
        levelSnapshots.add(snapshot);
    }


    int mergedUnitStrength(Unit unit1, Unit unit2) {
        return unit1.strength + unit2.strength;
    }


    boolean canMergeUnits(Unit unit1, Unit unit2) {
        return mergedUnitStrength(unit1, unit2) <= 4;
    }


    private boolean canMergeUnits(int strength1, int strength2) {
        return strength1 + strength2 <= 4;
    }


    boolean mergeUnits(Hex hex, Unit unit1, Unit unit2) {
        if (canMergeUnits(unit1, unit2)) {
            cleanOutHex(unit1.currHex);
            cleanOutHex(unit2.currHex);
            Unit mergedUnit = addUnit(hex, mergedUnitStrength(unit1, unit2));
            mergedUnit.setReadyToMove(true);
            if (!unit1.isReadyToMove() || !unit2.isReadyToMove()) {
                mergedUnit.setReadyToMove(false);
                mergedUnit.stopJumping();
            }
            return true;
        }
        return false;
    }


    private void tickleMoneySign() {
        ButtonYio coinButton = yioGdxGame.menuControllerYio.getButtonById(37);
        coinButton.factorModel.setValues(1, 0.13);
        coinButton.factorModel.beginSpawning(4, 1);
    }


    boolean buildUnit(Province province, Hex hex, int strength) {
        if (province == null || hex == null) return false;
        if (province.hasMoneyForUnit(strength)) {
            // check if can build unit
            if (hex.sameColor(province) && hex.containsUnit() && !canMergeUnits(strength, hex.unit.strength))
                return false;
            takeSnapshot();
            province.money -= PRICE_UNIT * strength;
            statistics.moneyWereSpent(PRICE_UNIT * strength);
            updateSelectedProvinceMoney();
            if (hex.sameColor(province)) { // build unit peacefully inside province
                if (hex.containsUnit()) { // merge units
                    Unit bUnit = new Unit(this, hex, strength);
                    bUnit.setReadyToMove(true);
                    mergeUnits(hex, bUnit, hex.unit);
                } else {
                    addUnit(hex, strength);
                }
            } else { // attack on other province
                setHexColor(hex, province.getColor()); // must be called before object in hex destroyed
                addUnit(hex, strength);
                hex.unit.setReadyToMove(false);
                hex.unit.stopJumping();
                province.addHex(hex);
                addAnimHex(hex);
                updateCacheOnceAfterSomeTime();
            }
            updateBalanceString();
            statistics.unitWasProduced();
            return true;
        }
        // can't build unit
        if (isPlayerTurn()) tickleMoneySign();
        return false;
    }


    boolean buildTower(Province province, Hex hex) {
        if (province == null) return false;
        if (province.hasMoneyForTower()) {
            takeSnapshot();
            addSolidObject(hex, Hex.OBJECT_TOWER);
            addAnimHex(hex);
            province.money -= PRICE_TOWER;
            statistics.moneyWereSpent(PRICE_TOWER);
            updateSelectedProvinceMoney();
            updateCacheOnceAfterSomeTime();
            return true;
        }

        // can't build tower
        if (isPlayerTurn()) tickleMoneySign();
        return false;
    }


    boolean buildStrongTower(Province province, Hex hex) {
        if (province == null) return false;
        if (province.hasMoneyForStrongTower()) {
            takeSnapshot();
            addSolidObject(hex, Hex.OBJECT_STRONG_TOWER);
            addAnimHex(hex);
            province.money -= PRICE_STRONG_TOWER;
            statistics.moneyWereSpent(PRICE_STRONG_TOWER);
            updateSelectedProvinceMoney();
            updateCacheOnceAfterSomeTime();
            return true;
        }

        // can't build tower
        if (isPlayerTurn()) tickleMoneySign();
        return false;
    }


    boolean buildFarm(Province province, Hex hex) {
        if (province == null) return false;
        if (!hex.hasThisObjectNearby(Hex.OBJECT_HOUSE) && !hex.hasThisObjectNearby(Hex.OBJECT_FARM)) {
            return false;
        }
        if (province.hasMoneyForFarm()) {
            takeSnapshot();
            province.money -= PRICE_FARM + province.getExtraFarmCost();
            statistics.moneyWereSpent(PRICE_FARM + province.getExtraFarmCost());
            addSolidObject(hex, Hex.OBJECT_FARM);
            addAnimHex(hex);
            updateSelectedProvinceMoney();
            updateCacheOnceAfterSomeTime();
            return true;
        }

        // can't build farm
        if (isPlayerTurn()) tickleMoneySign();
        return false;
    }


    public void restartGame() {
        gameSaver.setActiveHexesString(levelInitialString);
        gameSaver.beginRecreation(false);
        gameSaver.endRecreation();
    }


    public void undoAction() {
        int lastIndex = levelSnapshots.size() - 1;
        if (lastIndex < 0) return;
        currentTouchCount = 0;
        LevelSnapshot lastSnapshot = levelSnapshots.get(lastIndex);
        lastSnapshot.recreateSnapshot();
        levelSnapshots.remove(lastIndex);
    }


    public void turnOffEditorMode() {
        editorMode = false;
    }


    private void updateCurrentPriceString() {
        if (tipType == 0) {
            currentPriceString = "$" + PRICE_TOWER;
            return;
        }
        if (tipType >= 1 && tipType <= 4) {
            currentPriceString = "$" + (PRICE_UNIT * tipType);
            return;
        }
        if (tipType == 5) {
            currentPriceString = "$" + (PRICE_FARM + selectedProvince.getExtraFarmCost());
            return;
        }
        if (tipType == 6) {
            currentPriceString = "$" + PRICE_STRONG_TOWER;
            return;
        }
    }


    private void updateBalanceString() {
        if (selectedProvince != null) {
            balanceString = selectedProvince.getBalanceString();
        }
    }


    void updateSelectedProvinceMoney() {
        if (selectedProvince != null)
            selectedProvinceMoney = selectedProvince.money;
        else selectedProvinceMoney = -1;
        updateBalanceString();
    }


    public Unit addUnit(Hex hex, int strength) {
        if (hex == null) return null;
        if (hex.containsSolidObject()) {
            if (!GameController.slay_rules && hex.containsTree()) {
                getProvinceByHex(hex).money += 5;
                updateSelectedProvinceMoney();
            }
            cleanOutHex(hex);
            updateCacheOnceAfterSomeTime();
            hex.addUnit(strength);
        } else {
            hex.addUnit(strength);
            if (isCurrentTurn(hex.colorIndex)) {
                hex.unit.setReadyToMove(true);
                hex.unit.startJumping();
            }
        }
        return hex.unit;
    }


    private void addProvince(Province province) {
        if (provinces.contains(province)) return;
        provinces.add(province);
    }


    private boolean isSomethingMoving() {
        for (Hex hex : animHexes) {
            if (hex.containsUnit() && hex.unit.moveFactor.get() < 1) return true;
        }
        if (editorMode && levelEditor.isSomethingMoving()) return true;
        return false;
    }


    Hex adjacentHex(Hex hex, int neighbourNumber) {
        return adjacentHex(hex.index1, hex.index2, neighbourNumber);
    }


    boolean hexHasSelectedNearby(Hex hex) {
        for (int i = 0; i < 6; i++)
            if (hex.adjacentHex(i).selected) return true;
        return false;
    }


    void timeCorrection(long correction) {

    }


    public LevelEditor getLevelEditor() {
        return levelEditor;
    }


    void touchDown(int screenX, int screenY, int pointer, int button) {
        if (editorMode) levelEditor.touchDown(screenX, screenY);
        currentTouchCount++;
        this.screenX = screenX;
        this.screenY = screenY;
        touchDownX = screenX;
        touchDownY = screenY;
        if (blockMultiInput) blockMultiInput = false;
        if (blockMultiInput) return;
        if (currentTouchCount == 1) { // initial touch
            maxTouchCount = 1;
            multiTouchDetected = false;
            touchDownTime = System.currentTimeMillis();
            checkToMarch = true;
        } else { // second finger or more
            multiTouchDetected = true;
            lastMultiTouchDistance = (float) YioGdxGame.distance(Gdx.input.getX(0), Gdx.input.getY(0), Gdx.input.getX(1), Gdx.input.getY(1));
        }

        if (currentTouchCount > maxTouchCount) maxTouchCount = currentTouchCount;
        lastTouchCount = currentTouchCount;
    }


    public static float distanceBetweenHexes(Hex one, Hex two) {
        PointYio pOne = one.getPos();
        PointYio pTwo = two.getPos();
        return (float) pOne.distanceTo(pTwo);
    }


    public void detectAndShowMoveZoneForBuildingUnit(int strength) {
//        if (selectedHexes.size() == 0) {
//            YioGdxGame.say("detected bug #3128739172, GameController.detectAndShowMoveZoneForBuildingUnit()");
//            return;
//        }
        detectAndShowMoveZone(selectedHexes.get(0), strength);
    }


    public void detectAndShowMoveZoneForFarm() {
        moveZone = detectMoveZoneForFarm();
        checkToForceMoveZoneAnims();
        moveZoneFactor.setValues(0, 0);
        moveZoneFactor.beginSpawning(3, 1.5);
        blackoutFactor.beginSpawning(3, 1.5);
    }


    ArrayList<Hex> detectMoveZoneForFarm() {
        clearMoveZone();
        unFlagAllHexesInArrayList(activeHexes);
        ArrayList<Hex> result = new ArrayList<Hex>();
        for (Hex hex : selectedProvince.hexList) {
            if (hex.hasThisObjectNearby(Hex.OBJECT_FARM) || hex.hasThisObjectNearby(Hex.OBJECT_HOUSE)) {
                hex.inMoveZone = true;
                result.add(hex);
            }
        }

        return result;
    }


    ArrayList<Hex> detectMoveZone(Hex startHex, int strength) {
        return detectMoveZone(startHex, strength, 9001); // move limit is almost infinite
    }


    ArrayList<Hex> detectMoveZone(Hex startHex, int strength, int moveLimit) {
        unFlagAllHexesInArrayList(activeHexes);
        ArrayList<Hex> result = new ArrayList<Hex>();
        ArrayList<Hex> propagationList = new ArrayList<Hex>();
        Hex tempHex, adjHex;
        propagationList.add(startHex);
        startHex.moveZoneNumber = moveLimit;
        while (propagationList.size() > 0) {
            tempHex = propagationList.get(0);
            result.add(tempHex);
            tempHex.inMoveZone = true;
            propagationList.remove(0);
            if (!tempHex.sameColor(startHex) || tempHex.moveZoneNumber == 0) continue;
            for (int i = 0; i < 6; i++) {
                adjHex = tempHex.adjacentHex(i);
                if (adjHex.active && !adjHex.flag) {
                    if (adjHex.sameColor(startHex)) {
                        propagationList.add(adjHex);
                        adjHex.moveZoneNumber = tempHex.moveZoneNumber - 1;
                        adjHex.flag = true;
                    } else {
                        if (adjHex.getDefenseNumber() < strength) {
                            propagationList.add(adjHex);
                            adjHex.flag = true;
                        }
                    }
                }
            }
        }
        return result;
    }


    private void detectAndShowMoveZone(Hex startHex, int strength) {
        detectAndShowMoveZone(startHex, strength, 9001); // move limit is almost infinite
    }


    private void detectAndShowMoveZone(Hex startHex, int strength, int moveLimit) {
        moveZone = detectMoveZone(startHex, strength, moveLimit);
        checkToForceMoveZoneAnims();
        moveZoneFactor.setValues(0, 0);
        moveZoneFactor.beginSpawning(3, 1.5);
        blackoutFactor.beginSpawning(3, 1.5);
    }


    private void checkToForceMoveZoneAnims() {
        if (moveZone.get(0).selectionFactor.get() < 1) {
            for (Hex hex : moveZone) {
                hex.animFactor.setValues(1, 0);
            }
        }
    }


    private void clearMoveZone() {
        for (int i = moveZone.size() - 1; i >= 0; i--)
            moveZone.get(i).inMoveZone = false;
        moveZone.clear();
    }


    private boolean hexHasNeighbourWithColor(Hex hex, int color) {
        Hex neighbour;
        for (int i = 0; i < 6; i++) {
            neighbour = hex.adjacentHex(i);
            if (neighbour != null && neighbour.active && neighbour.sameColor(color)) return true;
        }
        return false;
    }


    private void showDefenseTip(Hex hex) {
        defenseTips = new ArrayList<Hex>();
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.adjacentHex(i);
            if (adjHex.active && adjHex.sameColor(hex)) {
                defenseTips.add(adjHex);
            }
        }
        defenseTipFactor.setValues(0, 0);
        defenseTipFactor.beginSpawning(3, 1);
        defTipSpawnTime = System.currentTimeMillis();
        defTipHex = hex;
    }


    void addAnimHex(Hex hex) {
        if (animHexes.contains(hex)) return;
        ListIterator animIterator = animHexes.listIterator();
        animIterator.add(hex);
        hex.animFactor.setValues(0, 0);
        hex.animFactor.beginSpawning(1, 1);
        hex.animStartTime = System.currentTimeMillis();
        updateCacheOnceAfterSomeTime();
    }


    Province findProvinceCopy(Province src) {
        Province result;
        for (Hex hex : src.hexList) {
            result = getProvinceByHex(hex);
            if (result == null) continue;
            return result;
        }
        return null;
    }


    Province getProvinceByHex(Hex hex) {
        for (Province province : provinces) {
            if (province.containsHex(hex))
                return province;
        }
        return null;
    }


    private Province getMaxProvinceFromList(ArrayList<Province> list) {
        if (list.size() == 0) return null;
        Province max, temp;
        max = list.get(0);
        for (int k = list.size() - 1; k >= 0; k--) {
            temp = list.get(k);
            if (temp.hexList.size() > max.hexList.size()) max = temp;
        }
        return max;
    }


    private void splitProvince(Hex hex, int color) {
        Province oldProvince = getProvinceByHex(hex);
        if (oldProvince == null) return;
        unFlagAllHexesInArrayList(oldProvince.hexList);
        ArrayList<Hex> tempList = new ArrayList<Hex>();
        ArrayList<Hex> propagationList = new ArrayList<Hex>();
        ArrayList<Province> provincesAdded = new ArrayList<Province>();
        Hex startHex, tempHex, adjHex;
        hex.flag = true;
        predictableRandom.setSeed(hex.index1 + hex.index2);
        for (int k = 0; k < 6; k++) {
            startHex = hex.adjacentHex(k);
            if (!startHex.active || startHex.colorIndex != color || startHex.flag) continue;
            tempList.clear();
            propagationList.clear();
            propagationList.add(startHex);
            startHex.flag = true;
            while (propagationList.size() > 0) {
                tempHex = propagationList.get(0);
                tempList.add(tempHex);
                propagationList.remove(0);
                for (int i = 0; i < 6; i++) {
                    adjHex = tempHex.adjacentHex(i);
                    if (adjHex.active && adjHex.sameColor(tempHex) && !adjHex.flag) {
                        propagationList.add(adjHex);
                        adjHex.flag = true;
                    }
                }
            }
            if (tempList.size() >= 2) {
                Province province = new Province(this, tempList);
                province.money = 0;
                if (!province.hasCapital()) {
                    province.placeCapitalInRandomPlace(predictableRandom);
//                    YioGdxGame.say("placed capital of " + province.getColor() + ", variants = " + province.hexList.size());
                }
                addProvince(province);
                provincesAdded.add(province);
//                YioGdxGame.say("added province with size " + province.hexList.size() + ", color = " + province.getColor());
            } else {
                destroyBuildingsOnHex(startHex);
            }
        }
        if (provincesAdded.size() > 0 && !(hex.objectInside == Hex.OBJECT_HOUSE)) {
            getMaxProvinceFromList(provincesAdded).money = oldProvince.money;
//            YioGdxGame.say("transferring money: " + oldProvince.money + ", color = " + getMaxProvinceFromList(provincesAdded).getColor());
        }
        provinces.remove(oldProvince);
    }


    private void checkToUniteProvinces(Hex hex) {
        ArrayList<Province> adjacentProvinces = new ArrayList<Province>();
        Province p;
        for (int i = 0; i < 6; i++) {
            p = getProvinceByHex(hex.adjacentHex(i));
            if (p != null && hex.sameColor(p) && !adjacentProvinces.contains(p)) adjacentProvinces.add(p);
        }
        if (adjacentProvinces.size() >= 2) {
            int sum = 0;
            Hex capital = getMaxProvinceFromList(adjacentProvinces).getCapital();
            ArrayList<Hex> hexArrayList = new ArrayList<Hex>();
//            YioGdxGame.say("uniting provinces: " + adjacentProvinces.size());
            for (Province province : adjacentProvinces) {
                sum += province.money;
                hexArrayList.addAll(province.hexList);
                provinces.remove(province);
            }
            Province unitedProvince = new Province(this, hexArrayList);
            unitedProvince.money = sum;
            unitedProvince.setCapital(capital);
            addProvince(unitedProvince);
        }
    }


    private void joinHexToAdjacentProvince(Hex hex) {
        Province p;
        for (int i = 0; i < 6; i++) {
            p = getProvinceByHex(hex.adjacentHex(i));
            if (p != null && hex.sameColor(p)) {
                p.addHex(hex);
                Hex h;
                for (int j = 0; j < 6; j++) {
                    h = adjacentHex(hex, j);
                    if (h.active && h.sameColor(hex) && getProvinceByHex(h) == null) p.addHex(h);
                }
                return;
            }
        }
    }


    public void setHexColor(Hex hex, int color) {
        cleanOutHex(hex);
        int oldColor = hex.colorIndex;
        hex.setColorIndex(color);
        splitProvince(hex, oldColor);
        checkToUniteProvinces(hex);
        joinHexToAdjacentProvince(hex);
        ListIterator animIterator = animHexes.listIterator();
        for (int i = 0; i < 6; i++) {
            Hex h = hex.adjacentHex(i);
            if (h != null && h.active && h.sameColor(hex)) {
                if (!animHexes.contains(h)) animIterator.add(h);
                if (!h.changingColor) h.animFactor.setValues(1, 0);
            }
        }
        hex.changingColor = true;
        if (!animHexes.contains(hex)) animIterator.add(hex);
        hex.animFactor.setValues(0, 0);
        hex.animFactor.beginSpawning(1, 1);
        if (!isPlayerTurn()) forceAnimEndInHex(hex);
    }


    private int getNextTurnIndex() {
        int res = turn + 1;
        if (res >= colorNumber) res = 0;
        return res;
    }


    boolean isPlayerTurn(int turn) {
        return turn < playersNumber;
    }


    boolean isPlayerTurn() {
        return isPlayerTurn(turn);
    }


    boolean isCurrentTurn(int turn) {
        return this.turn == turn;
    }


    void moveUnit(Unit unit, Hex toWhere, Province unitProvince) {
        if (unit.currHex.sameColor(toWhere)) { // move peacefully
            if (!toWhere.containsUnit()) {
                unit.moveToHex(toWhere);
            } else {
                mergeUnits(toWhere, unit, toWhere.unit);
            }
            if (isPlayerTurn()) setResponseAnimHex(toWhere);
        } else {
            setHexColor(toWhere, turn); // must be called before object in hex destroyed
            cleanOutHex(toWhere);
            unit.moveToHex(toWhere);
            unitProvince.addHex(toWhere);
            if (isPlayerTurn()) {
                selectedHexes.add(toWhere);
                updateCacheOnceAfterSomeTime();
            }
        }

        if (isPlayerTurn()) {
            hideMoveZone();
            updateBalanceString();
        }
    }


    private void focusedHexActions(Hex focusedHex) {
        // don't change order in this method
//        YioGdxGame.say(focusedHex.index1 + " " + focusedHex.index2);
        if (focusedHex.ignoreTouch) return;
        if (editorMode) return;

        if (!focusedHex.active) {
            deselectAll();
            return;
        }

        boolean isSomethingSelected = false;
        if (selectedHexes.size() > 0) isSomethingSelected = true;

        // building stuff
        if (tipFactor.get() > 0 && tipFactor.getDy() >= 0) {
            // build peacefully inside province
            if (focusedHex.selected && !focusedHex.containsBuilding()) {
                if (tipType == 0) {
                    // build tower
                    if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                        buildTower(selectedProvince, focusedHex);
                    }
                } else if (!GameController.slay_rules && tipType == 5) {
                    // build farm
                    if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                        buildFarm(selectedProvince, focusedHex);
                    }
                } else if (!GameController.slay_rules && tipType == 6) {
                    // build strong tower
                    if (!focusedHex.containsTree() && !focusedHex.containsUnit()) {
                        buildStrongTower(selectedProvince, focusedHex);
                    }
                } else {
                    // build unit
                    buildUnit(selectedProvince, focusedHex, tipType);
                    tipType = -1;
                }
                setResponseAnimHex(focusedHex);
                SoundControllerYio.playSound(SoundControllerYio.soundBuild);
                // else attack by building unit
            } else if (focusedHex.isInMoveZone() && focusedHex.colorIndex != turn && tipType > 0 && selectedProvince.hasMoneyForUnit(tipType)) {
                buildUnit(selectedProvince, focusedHex, tipType);
                selectedProvince = getProvinceByHex(focusedHex); // when uniting provinces, selected province object may change
                selectAdjacentHexes(focusedHex);
                tipType = -1;
                SoundControllerYio.playSound(SoundControllerYio.soundBuild);
            } else setResponseAnimHex(focusedHex);
            hideTip();
            hideMoveZone();
            return;
        }

        // deselect
        if (isSomethingSelected) {
            if (!focusedHex.selected && !focusedHex.inMoveZone) deselectAll();
            if (moveZone.size() > 0 && !focusedHex.inMoveZone) {
                selectedUnit = null;
                hideMoveZone();
            }
            if (focusedHex.selected && moveZone.size() == 0 && focusedHex.containsBuilding() && focusedHex.objectInside != Hex.OBJECT_FARM) { // check to show defense tip
                showDefenseTip(focusedHex);
            }
        }

        // attack enemy province
        if (focusedHex.colorIndex != turn && focusedHex.inMoveZone && selectedUnit != null) {
            takeSnapshot();
            moveUnit(selectedUnit, focusedHex, selectedProvince);
            SoundControllerYio.playSound(SoundControllerYio.soundAttack);
            selectedUnit = null;
        }

        // select province
        if (isCurrentTurn(focusedHex.colorIndex) && hexHasNeighbourWithColor(focusedHex, turn)) {
            selectAdjacentHexes(focusedHex);
            isSomethingSelected = true;
        }

        // select and move unit peacefully
        if (isSomethingSelected) {
            if (selectedUnit == null) { // check to select unit
                if (focusedHex.containsUnit() && focusedHex.unit.isReadyToMove() && focusedHex.unit.moveFactor.get() == 1) {
                    selectedUnit = focusedHex.unit;
                    SoundControllerYio.playSound(SoundControllerYio.soundSelectUnit);
                    detectAndShowMoveZone(selectedUnit.currHex, selectedUnit.strength, UNIT_MOVE_LIMIT);
                    selUnitFactor.setValues(0, 0);
                    selUnitFactor.beginSpawning(3, 2);
                    hideTip();
                }
            } else { // move unit peacefully
                if (focusedHex.inMoveZone && isCurrentTurn(focusedHex.colorIndex) && selectedUnit.canMoveToFriendlyHex(focusedHex)) {
                    takeSnapshot();
                    SoundControllerYio.playSound(SoundControllerYio.soundWalk);
                    moveUnit(selectedUnit, focusedHex, selectedProvince);
                    selectedUnit = null;
                }
            }
        }
    }


    private boolean touchedAsClick() {
        return !multiTouchDetected && YioGdxGame.distance(screenX, screenY, touchDownX, touchDownY) < 0.03 * w && Math.abs(camDx) < 0.01 * w && Math.abs(camDy) < 0.01 * w;
    }


    private void updateFocusedHex() {
        updateFocusedHex(screenX, screenY);
    }


    void updateFocusedHex(int screenX, int screenY) {
        selectX = (screenX - 0.5f * w) * orthoCam.zoom + orthoCam.position.x;
        selectY = (screenY - 0.5f * h) * orthoCam.zoom + orthoCam.position.y;
        focusedHex = getHexByPos(selectX + yioGdxGame.gameView.hexViewSize, selectY + yioGdxGame.gameView.hexViewSize);
    }


    void touchUp(int screenX, int screenY, int pointer, int button) {
        if (editorMode) levelEditor.touchUp(screenX, screenY);
        this.screenX = screenX;
        this.screenY = screenY;
        lastTimeTouched = System.currentTimeMillis();
        currentTouchCount--;
        if (currentTouchCount < 0) currentTouchCount = 0;
        if (blockMultiInput) return;
        if (currentTouchCount == maxTouchCount - 1) {

        }
        if (currentTouchCount == 0) {
            if (touchedAsClick()) {
                updateFocusedHex();
                if (focusedHex != null && isPlayerTurn()) {
                    focusedHexActions(focusedHex);
                }
            }
            multiTouchDetected = false;
        }
        lastTouchCount = currentTouchCount;
        // some stuff here
    }


    void touchDragged(int screenX, int screenY, int pointer) {
        if (editorMode) levelEditor.touchDrag(screenX, screenY);
        lastTimeDragged = System.currentTimeMillis();
        if (multiTouchDetected) {
            if (blockMultiInput) return;
            float currentMultiTouchDistance = (float) YioGdxGame.distance(Gdx.input.getX(0), Gdx.input.getY(0), Gdx.input.getX(1), Gdx.input.getY(1));
            camDZoom = lastMultiTouchDistance / currentMultiTouchDistance - 1;
            if (camDZoom < 0) camDZoom *= 0.3;
        } else {
            float currX, currY;
            currX = (this.screenX - screenX) * trackerZoom;
            currY = (this.screenY - screenY) * trackerZoom;
            this.screenX = screenX;
            this.screenY = screenY;
            if (blockDragToLeft && currX < 0) currX = 0;
            if (blockDragToRight && currX > 0) currX = 0;
            if (blockDragToUp && currY > 0) currY = 0;
            if (blockDragToDown && currY < 0) currY = 0;
            if (notTooSlow(currX, camDx)) {
                camDx = currX;
            }
            if (notTooSlow(currY, camDy)) {
                camDy = currY;
            }
        }
    }


    private boolean notTooSlow(float curr, float cam) {
        return Math.abs(curr) > 0.5 * Math.abs(cam);
    }


    void scrolled(int amount) {
        if (amount == 1) {
            camDZoom += 0.15f;
        } else if (amount == -1) {
            camDZoom -= 0.2f;
        }
    }


    public void close() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                if (field[i][j] != null) field[i][j].close();
            }
        }
        if (provinces != null) {
            for (Province province : provinces) {
                province.close();
            }
        }

        provinces = null;
        field = null;
        yioGdxGame = null;
    }
}
