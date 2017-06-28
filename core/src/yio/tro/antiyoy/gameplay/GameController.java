package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.*;
import yio.tro.antiyoy.ai.*;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.rules.Ruleset;
import yio.tro.antiyoy.gameplay.rules.RulesetGeneric;
import yio.tro.antiyoy.gameplay.rules.RulesetSlay;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.SliderYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ivan on 05.08.14.
 */
public class GameController {

    private final DebugActionsManager debugActionsManager;
    public YioGdxGame yioGdxGame;
    int screenX;
    int screenY;

    public final SelectionController selectionController;
    public final FieldController fieldController;
    public CameraController cameraController;
    public final AiFactory aiFactory;

    public Random random, predictableRandom;
    private LanguagesManager languagesManager;
    public boolean letsUpdateCacheByAnim;
    public boolean updateWholeCache;
    public long currentTime;
    public boolean checkToMarch;
    public boolean ignoreMarch;
    public int turn;
    private long timeToUpdateCache;
    public float boundWidth;
    public float boundHeight;
    boolean readyToEndTurn;
    private boolean proposedSurrender;
    private boolean cityNamesEnabled;
    public boolean backgroundVisible;
    private ArrayList<ArtificialIntelligence> aiList;
    public ArrayList<Unit> unitList;
    public int marchDelay;
    public int playersNumber;
    public int colorIndexViewOffset;
    public String balanceString;
    public String currentPriceString;
    LoadingParameters initialParameters;
    private ArrayList<LevelSnapshot> levelSnapshots;
    public float priceStringWidth;
    public MapGenerator mapGeneratorSlay;
    public MapGenerator mapGeneratorGeneric;
    public Unit jumperUnit;
    public Statistics statistics;
    public GameSaver gameSaver;
    public Forefinger forefinger;
    public TutorialScript tutorialScript;
    private LevelEditor levelEditor;
    public int currentTouchCount;
    public Ruleset ruleset;


    public GameController(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        random = new Random();
        predictableRandom = new Random(0);
        languagesManager = LanguagesManager.getInstance();
        CampaignProgressManager.getInstance();
        selectionController = new SelectionController(this);
        marchDelay = 500;
        cameraController = new CameraController(this);
        unitList = new ArrayList<Unit>();
        levelSnapshots = new ArrayList<LevelSnapshot>();
        mapGeneratorSlay = new MapGenerator(this);
        mapGeneratorGeneric = new MapGeneratorGeneric(this);
        aiList = new ArrayList<ArtificialIntelligence>();
        initialParameters = new LoadingParameters();

        fieldController = new FieldController(this);
        jumperUnit = new Unit(this, fieldController.emptyHex, 0);

        statistics = new Statistics(this);
        gameSaver = new GameSaver(this);
        forefinger = new Forefinger(this);
        levelEditor = new LevelEditor(this);
        aiFactory = new AiFactory(this);
        debugActionsManager = new DebugActionsManager(this);

        LoadingManager.getInstance().setGameController(this);
    }


    public void clearLevel() {
        if (GameRules.inEditorMode) {
            levelEditor.clearLevel();
        }
    }


    void takeAwaySomeMoneyToAchieveBalance() {
        // so the problem is that all players except first
        // get income in the first turn
        updateRuleset();
        for (Province province : fieldController.provinces) {
            if (province.getColor() == 0) continue; // first player is not getting income at first turn
            province.money -= province.getIncome() - province.getTaxes();
        }
    }


    private void checkForAloneUnits() {
        for (int i = 0; i < unitList.size(); i++) {
            Unit unit = unitList.get(i);
            if (isCurrentTurn(unit.getColor()) && unit.currentHex.numberOfFriendlyHexesNearby() == 0) {
                fieldController.killUnitOnHex(unit.currentHex);
                i--;
            }
        }
    }


    private void checkForBankrupts() {
        for (Province province : fieldController.provinces) {
            if (isCurrentTurn(province.getColor())) {
                if (province.money < 0) {
                    province.money = 0;
                    fieldController.killEveryoneInProvince(province);
                }
            }
        }
    }


    public void move() {
        currentTime = System.currentTimeMillis();
        statistics.increaseTimeCount();
        cameraController.move();

        checkForAiToMove();
        checkToEndTurn();
        checkToUpdateCacheByAnim();

        if (fieldController.letsCheckAnimHexes && currentTime > fieldController.timeToCheckAnimHexes && (isPlayerTurn() || isCurrentTurn(0))) {
            fieldController.checkAnimHexes();
        }

        moveCheckToMarch();
        moveUnits();
        fieldController.moveAnimHexes();
        selectionController.moveSelections();

        jumperUnit.moveJumpAnim();
        fieldController.moveZoneFactor.move();
        selectionController.getBlackoutFactor().move();
        selectionController.moveDefenseTips();

        if (fieldController.moveZone.size() > 0 && fieldController.moveZoneFactor.get() < 0.01) {
            fieldController.clearMoveZone();
        }
        selectionController.tipFactor.move();

        fieldController.moveResponseAnimHex();
        moveTutorialStuff();
    }


    private void moveTutorialStuff() {
        if (GameRules.tutorialMode) {
            forefinger.move();
            tutorialScript.move();
        }
    }


    private void checkForAiToMove() {
        if (!isPlayerTurn() && !readyToEndTurn) {
            aiList.get(turn).makeMove();
            updateCacheOnceAfterSomeTime();
            readyToEndTurn = true;
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
        if (!Settings.long_tap_to_move) return;
        if (ignoreMarch) return;
        if (!checkToMarch) return;
        if (!checkConditionsToMarch()) return;

        checkToMarch = false;
        fieldController.updateFocusedHex();
        selectionController.setSelectedUnit(null);
        if (fieldController.focusedHex != null && fieldController.focusedHex.active) {
            fieldController.marchUnitsToHex(fieldController.focusedHex);
        }
    }


    boolean checkConditionsToMarch() {
        if (currentTouchCount != 1) return false;
        if (currentTime - cameraController.touchDownTime <= marchDelay) return false;
        if (!cameraController.touchedAsClick()) return false;

        return true;
    }


    private void checkToUpdateCacheByAnim() {
        if (letsUpdateCacheByAnim && currentTime > timeToUpdateCache && (isPlayerTurn() || isCurrentTurn(0)) && !isSomethingMoving()) {
            letsUpdateCacheByAnim = false;
            if (updateWholeCache) yioGdxGame.gameView.updateCacheLevelTextures();
            else yioGdxGame.gameView.updateCacheNearAnimHexes();
            updateWholeCache = false;
        }
    }


    private boolean canEndTurn() {
        if (DebugFlags.CHECKING_BALANCE_MODE) return true; // fast forward when measuring balance
        if (isInEditorMode()) return false;
        if (!readyToEndTurn) return false;
        if (!cameraController.checkConditionsToEndTurn()) return false;

        if (isPlayerTurn() || isCurrentTurn(0)) {
            return fieldController.animHexes.size() == 0;
        } else {
            return true;
        }
    }


    private void checkToEndTurn() {
        if (canEndTurn()) {
            readyToEndTurn = false;
            endTurnActions();
            turn = getNextTurnIndex();
            turnStartActions();
        }
    }


    void prepareCertainUnitsToMove() {
        for (Unit unit : unitList) {
            if (isCurrentTurn(unit.getColor())) {
                unit.setReadyToMove(true);
                unit.startJumping();
            }
        }
    }


    private int checkIfWeHaveWinner() {
        if (fieldController.activeHexes.size() == 0) return -1;
        if (fieldController.numberOfActiveProvinces() != 1) return -1;

        for (Province province : fieldController.provinces) {
            if (province.hexList.get(0).isNeutral()) continue;
            return province.getColor();
        }
        System.out.println("wtf?"); // this detects a possible bug
        return -1;

//        color = activeHexes.get(0).colorIndex;
//        for (Hex activeHex : activeHexes) {
//            if (activeHex.colorIndex != color) return -1;
//        }
//        return color;
    }


    private int zeroesInArray(int array[]) {
        int zeroes = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) zeroes++;
        }
        return zeroes;
    }


    private void checkToEndGame() {
        // captured everything
        int winner = checkIfWeHaveWinner();
        if (winner >= 0) {
            endGame(winner);
            return;
        }

        // propose surrender
        if (!proposedSurrender) {
            int possibleWinner = fieldController.possibleWinner();
            if (possibleWinner >= 0 && isPlayerTurn(possibleWinner)) {
                Scenes.sceneSurrenderDialog.create();
                proposedSurrender = true;
            }
        }

        // too long game
        if (Settings.turns_limit && statistics.turnsMade == 299) {
            int playerHexCount[] = fieldController.getPlayerHexCount();
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
        int playerHexCount[] = fieldController.getPlayerHexCount();
        int maxNumber = maxNumberFromArray(playerHexCount);
        int maxColor = 0;
        for (int i = 0; i < playerHexCount.length; i++) {
            if (maxNumber == playerHexCount[i]) {
                maxColor = i;
                break;
            }
        }

        fieldController.provinces.clear();
        ArrayList<Hex> hexList = new ArrayList<>();
        for (Hex activeHex : fieldController.activeHexes) {
            if (activeHex.colorIndex == maxColor) {
                hexList.add(activeHex);
                break;
            }
        }
        fieldController.provinces.add(new Province(this, hexList));

        checkToEndGame();
    }


    private void endGame(int winColor) {
        CampaignProgressManager instance = CampaignProgressManager.getInstance();

        if (instance.completedCampaignLevel(winColor)) {
            instance.markLevelAsCompleted(instance.currentLevelIndex);
            yioGdxGame.menuControllerYio.levelSelector.update();
        }

        Scenes.sceneAfterGameMenu.create(winColor, isPlayerTurn(winColor));

        if (DebugFlags.CHECKING_BALANCE_MODE) {
            yioGdxGame.balanceIndicator[winColor]++;
            ReactBehavior.rbStartSkirmishGame.reactAction(Scenes.sceneSkirmishMenu.startButton);
        }
    }


    public void resetCurrentTouchCount() {
        currentTouchCount = 0;
    }


    public void resetProgress() {
        CampaignProgressManager.getInstance().resetProgress();

        yioGdxGame.selectedLevelIndex = 1;
    }


    private void endTurnActions() {
        checkToEndGame();
        ruleset.onTurnEnd();
        for (Unit unit : unitList) {
            unit.setReadyToMove(false);
            unit.stopJumping();
        }
        if (!isPlayerTurn()) {

        }
    }


    private void turnStartActions() {
        selectionController.deselectAll();
        if (isCurrentTurn(0)) {
            statistics.turnWasMade();
            fieldController.expandTrees();
        }
        prepareCertainUnitsToMove();
        fieldController.transformGraves(); // this must be called before 'check for bankrupts' and after 'expand trees'
        collectTributesAndPayTaxes();
        checkForBankrupts();
        checkForAloneUnits();
        if (isCurrentTurn(0)) yioGdxGame.gameView.updateCacheLevelTextures();
        if (isPlayerTurn()) {
            resetCurrentTouchCount();
            levelSnapshots.clear();
            jumperUnit.startJumping();
            if (fieldController.numberOfProvincesWithColor(turn) == 0) {
                endTurnButtonPressed();
            }
        } else {
            for (Hex animHex : fieldController.animHexes) {
                animHex.animFactor.setValues(1, 0);
            }
        }
        if (Settings.autosave && turn == 0 && playersNumber > 0) autoSave();
    }


    private void collectTributesAndPayTaxes() {
        for (Province province : fieldController.provinces) {
            if (isCurrentTurn(province.getColor())) {
                province.money += province.getIncome();
                province.money -= province.getTaxes();
            }
        }
    }


    void updateCacheOnceAfterSomeTime() {
        letsUpdateCacheByAnim = true;
        timeToUpdateCache = System.currentTimeMillis() + 30;
    }


    public void endTurnButtonPressed() {
        cameraController.onEndTurnButtonPressed();
        if (!isPlayerTurn()) return;
        readyToEndTurn = true;
    }


    public void defaultValues() {
        cameraController.defaultValues();
        ignoreMarch = false;
        readyToEndTurn = false;
        fieldController.defaultValues();
        selectionController.setSelectedUnit(null);
        turn = 0;
        jumperUnit.startJumping();
        statistics.defaultValues();
//        ReactBehavior.rbShowColorStats.loadTexturesIfNotLoaded();
        GameRules.tutorialMode = false;
        GameRules.campaignMode = false;
        GameRules.inEditorMode = false;
        proposedSurrender = false;
        backgroundVisible = true;
        colorIndexViewOffset = 0;
    }


    public void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
        if (DebugFlags.CHECKING_BALANCE_MODE) this.playersNumber = 0;
    }


    private void setPlayersNumberBySlider(SliderYio slider) {
        setPlayersNumber(slider.getCurrentRunnerIndex());
    }


    public void initTutorial() {
        if (GameRules.slay_rules) {
            tutorialScript = new TutorialScriptSlayRules(this);
        } else {
            tutorialScript = new TutorialScriptGenericRules(this);
        }
        tutorialScript.createTutorialGame();
        GameRules.tutorialMode = true;
    }


    public int getColorOffsetBySlider(SliderYio sliderYio, int colorNumber) {
        int colorOffsetSliderIndex = sliderYio.getCurrentRunnerIndex();

        if (colorOffsetSliderIndex == 0) { // random
            return predictableRandom.nextInt(colorNumber);
        } else {
            return colorOffsetSliderIndex - 1;
        }
    }


    public void onEndCreation() {
        getLevelSnapshots().clear();
        prepareCertainUnitsToMove();
        fieldController.createPlayerHexCount();
        updateRuleset();
        createCamera();
        fieldController.clearAnims();
        aiFactory.createAiList(GameRules.difficulty);
        selectionController.deselectAll();

        if (DebugFlags.CHECKING_BALANCE_MODE) {
            while (true) {
                move();
                checkToEndGame();
            }
        }
    }


    public void readColorOffsetFromSlider() {
        SliderYio sliderYio;

        if (GameRules.campaignMode) {
            sliderYio = yioGdxGame.menuControllerYio.sliders.get(6);
        } else {
            sliderYio = yioGdxGame.menuControllerYio.sliders.get(4);
        }

        int sliderIndex = sliderYio.getCurrentRunnerIndex();
        if (sliderIndex == 0) { // random
            colorIndexViewOffset = predictableRandom.nextInt(GameRules.colorNumber);
            return;
        }
        colorIndexViewOffset = sliderIndex - 1;
    }


    public void updateInitialParameters(LoadingParameters parameters) {
        initialParameters.copyFrom(parameters);
    }


    private void sayArray(int array[]) {
        System.out.print("[ ");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println("]");
    }


    public void createCamera() {
        cameraController.createCamera();
    }


    void createAiList() {
        aiFactory.createAiList(GameRules.difficulty);
    }


    int getRandomLevelSize() {
        switch (random.nextInt(3)) {
            default:
            case 0:
                return FieldController.SIZE_SMALL;
            case 1:
                return FieldController.SIZE_MEDIUM;
            case 2:
                return FieldController.SIZE_BIG;
        }
    }


    public void setLevelSize(int size) {
        cameraController.init(size);
        switch (size) {
            case FieldController.SIZE_SMALL:
                boundWidth = GraphicsYio.width;
                boundHeight = GraphicsYio.height;
                break;
            case FieldController.SIZE_MEDIUM:
                boundWidth = 2 * GraphicsYio.width;
                boundHeight = GraphicsYio.height;
                break;
            case FieldController.SIZE_BIG:
                boundWidth = 2 * GraphicsYio.width;
                boundHeight = 2 * GraphicsYio.height;
                break;
            default:
                // to avoid some bugs maybe?
                return;
        }
        cameraController.setBounds(boundWidth, boundHeight);
        fieldController.levelSize = size;
        yioGdxGame.gameView.createLevelCacheTextures();
    }


    public GameSaver getGameSaver() {
        return gameSaver;
    }


    public void debugActions() {
        debugActionsManager.debugActions();
    }


    public boolean isCityNamesEnabled() {
        return cityNamesEnabled;
    }


    public void setCityNamesEnabled(int cityNames) {
        if (cityNames == 1) {
            cityNamesEnabled = true;
        } else {
            cityNamesEnabled = false;
        }
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
        selectionController.selectAdjacentHexes(startHex);
    }


    public void addSolidObject(Hex hex, int type) {
        fieldController.addSolidObject(hex, type);
    }


    public void cleanOutHex(Hex hex) {
        fieldController.cleanOutHex(hex);
    }


    public int getColorIndexWithOffset(int srcIndex) {
        if (GameRules.inEditorMode) return srcIndex;
        return ruleset.getColorIndexWithOffset(srcIndex);
    }


    private void autoSave() {
        if (Settings.interface_type == Settings.INTERFACE_SIMPLE) {
            gameSaver.saveGame();
        } else if (Settings.interface_type == Settings.INTERFACE_COMPLICATED) {
            gameSaver.saveGameToSlot(0);
        }
    }


    public void saveGame() {
        gameSaver.saveGame();
    }


    public void loadGame() {
        gameSaver.loadGame();
    }


    void takeSnapshot() {
        if (!isPlayerTurn()) return;
        LevelSnapshot snapshot = new LevelSnapshot(this);
        snapshot.takeSnapshot();
        levelSnapshots.add(snapshot);
    }


    public int mergedUnitStrength(Unit unit1, Unit unit2) {
        return unit1.strength + unit2.strength;
    }


    public boolean playerHasAtLeastOneUnitWithStrength(int playerColor, int strength) {
        for (Unit unit : unitList) {
            if (unit.getColor() == playerColor && unit.strength == strength) {
                return true;
            }
        }
        return false;
    }


    boolean canMergeUnits(int strength1, int strength2) {
        return strength1 + strength2 <= 4;
    }


    public boolean mergeUnits(Hex hex, Unit unit1, Unit unit2) {
        if (ruleset.canMergeUnits(unit1, unit2)) {
            fieldController.cleanOutHex(unit1.currentHex);
            fieldController.cleanOutHex(unit2.currentHex);
            Unit mergedUnit = fieldController.addUnit(hex, mergedUnitStrength(unit1, unit2));
            statistics.onUnitsMerged();
            mergedUnit.setReadyToMove(true);
            if (!unit1.isReadyToMove() || !unit2.isReadyToMove()) {
                mergedUnit.setReadyToMove(false);
                mergedUnit.stopJumping();
            }
            return true;
        }
        return false;
    }


    void tickleMoneySign() {
        ButtonYio coinButton = yioGdxGame.menuControllerYio.getButtonById(37);
        coinButton.factorModel.setValues(1, 0.13);
        coinButton.factorModel.beginSpawning(4, 1);
    }


    public void restartGame() {
//        int currentColorOffset = colorIndexViewOffset;

        LoadingManager.getInstance().startGame(initialParameters);

//        gameSaver.setActiveHexesString(levelInitialString);
//        gameSaver.beginRecreation(false);
//        colorIndexViewOffset = currentColorOffset;
//        gameSaver.endRecreation();
    }


    public void undoAction() {
        int lastIndex = levelSnapshots.size() - 1;
        if (lastIndex < 0) return;
        resetCurrentTouchCount();
        LevelSnapshot lastSnapshot = levelSnapshots.get(lastIndex);
        lastSnapshot.recreateSnapshot();
        levelSnapshots.remove(lastIndex);
    }


    public void turnOffEditorMode() {
        GameRules.inEditorMode = false;
    }


    void updateCurrentPriceString() {
        currentPriceString = "$" + selectionController.getCurrentTipPrice();
        priceStringWidth = GraphicsYio.getTextWidth(Fonts.gameFont, currentPriceString);
    }


    void updateBalanceString() {
        if (fieldController.selectedProvince != null) {
            balanceString = fieldController.selectedProvince.getBalanceString();
        }
    }


    public Unit addUnit(Hex hex, int strength) {
        return fieldController.addUnit(hex, strength);
    }


    boolean isSomethingMoving() {
        for (Hex hex : fieldController.animHexes) {
            if (hex.containsUnit() && hex.unit.moveFactor.get() < 1) return true;
        }
        if (GameRules.inEditorMode && levelEditor.isSomethingMoving()) return true;
        return false;
    }


    public LevelEditor getLevelEditor() {
        return levelEditor;
    }


    public void touchDown(int screenX, int screenY, int pointer, int button) {
        currentTouchCount++;

        if (GameRules.inEditorMode) {
            if (levelEditor.touchDown(screenX, screenY)) {
                return;
            }
        }

        if (currentTouchCount == 1) {
            setCheckToMarch(true);
        }

        this.screenX = screenX;
        this.screenY = screenY;
        cameraController.touchDown(screenX, screenY);
    }


    public void detectAndShowMoveZoneForBuildingUnit(int strength) {
//        if (selectedHexes.size() == 0) {
//            YioGdxGame.say("detected bug #3128739172, GameController.detectAndShowMoveZoneForBuildingUnit()");
//            return;
//        }
        fieldController.detectAndShowMoveZoneForBuildingUnit(strength);
    }


    public void detectAndShowMoveZoneForFarm() {
        fieldController.detectAndShowMoveZoneForFarm();
    }


    public ArrayList<Hex> detectMoveZone(Hex startHex, int strength) {
        return fieldController.detectMoveZone(startHex, strength);
    }


    public ArrayList<Hex> detectMoveZone(Hex startHex, int strength, int moveLimit) {
        return fieldController.detectMoveZone(startHex, strength, moveLimit);
    }


    public void addAnimHex(Hex hex) {
        fieldController.addAnimHex(hex);
    }


    Province findProvinceCopy(Province src) {
        return fieldController.findProvinceCopy(src);
    }


    public Province getProvinceByHex(Hex hex) {
        return fieldController.getProvinceByHex(hex);
    }


    private int getNextTurnIndex() {
        int res = turn + 1;
        if (res >= GameRules.colorNumber) res = 0;
        return res;
    }


    public boolean isPlayerTurn(int turn) {
        return turn < playersNumber;
    }


    public boolean isPlayerTurn() {
        return isPlayerTurn(turn);
    }


    public boolean isCurrentTurn(int turn) {
        return this.turn == turn;
    }


    public void moveUnit(Unit unit, Hex toWhere, Province unitProvince) {
        if (!unit.isReadyToMove()) {
            System.out.println("AI tried to move unit that is not ready to move");
            Yio.printStackTrace();
            return;
        }

        if (unit.currentHex.sameColor(toWhere)) { // move peacefully
            moveUnitPeacefully(unit, toWhere);
        } else {
            moveUnitWithAttack(unit, toWhere, unitProvince);
        }

        if (isPlayerTurn()) {
            fieldController.hideMoveZone();
            updateBalanceString();
        }
    }


    private void moveUnitWithAttack(Unit unit, Hex destination, Province unitProvince) {
        if (!destination.canBeAttackedBy(unit)) {
            System.out.println("Problem in GameController.moveUnitWithAttack");
            Yio.printStackTrace();
            return;
        }

        fieldController.setHexColor(destination, turn); // must be called before object in hex destroyed
        fieldController.cleanOutHex(destination);
        unit.moveToHex(destination);
        unitProvince.addHex(destination);
        if (isPlayerTurn()) {
            fieldController.selectedHexes.add(destination);
            updateCacheOnceAfterSomeTime();
        }
    }


    private void moveUnitPeacefully(Unit unit, Hex toWhere) {
        if (!toWhere.containsUnit()) {
            unit.moveToHex(toWhere);
        } else {
            mergeUnits(toWhere, unit, toWhere.unit);
        }

        if (isPlayerTurn()) {
            fieldController.setResponseAnimHex(toWhere);
        }
    }


    public void onClick() {
        fieldController.updateFocusedHex();
        if (fieldController.focusedHex != null && isPlayerTurn()) {
            focusedHexActions(fieldController.focusedHex);
        }
    }


    public void focusedHexActions(Hex focusedHex) {
        selectionController.focusedHexActions(focusedHex);
    }


    public void setCheckToMarch(boolean checkToMarch) {
        this.checkToMarch = checkToMarch;
    }


    public void touchUp(int screenX, int screenY, int pointer, int button) {
        currentTouchCount--;
        if (currentTouchCount < 0) {
            currentTouchCount = 0;
        }

        if (GameRules.inEditorMode) {
            if (levelEditor.touchUp(screenX, screenY)) {
                return;
            }
        }

        this.screenX = screenX;
        this.screenY = screenY;
        cameraController.touchUp(screenX, screenY);
    }


    public void touchDragged(int screenX, int screenY, int pointer) {
        if (GameRules.inEditorMode) {
            if (levelEditor.touchDrag(screenX, screenY)) {
                return;
            }
        }

        cameraController.touchDrag(screenX, screenY);
    }


    public void updateRuleset() {
        if (GameRules.slay_rules) {
            ruleset = new RulesetSlay(this);
        } else {
            ruleset = new RulesetGeneric(this);
        }
    }


    public void scrolled(int amount) {
        if (amount == 1) {
            cameraController.changeZoomLevel(0.5);
        } else if (amount == -1) {
            cameraController.changeZoomLevel(-0.6);
        }
    }


    public void close() {
        for (int i = 0; i < fieldController.fWidth; i++) {
            for (int j = 0; j < fieldController.fHeight; j++) {
                if (fieldController.field[i][j] != null) fieldController.field[i][j].close();
            }
        }
        if (fieldController.provinces != null) {
            for (Province province : fieldController.provinces) {
                province.close();
            }
        }

        fieldController.provinces = null;
        fieldController.field = null;
        yioGdxGame = null;
    }


    public ArrayList<Unit> getUnitList() {
        return unitList;
    }


    public ArrayList<LevelSnapshot> getLevelSnapshots() {
        return levelSnapshots;
    }


    public MapGenerator getMapGeneratorSlay() {
        return mapGeneratorSlay;
    }


    public Random getPredictableRandom() {
        return predictableRandom;
    }


    public MapGenerator getMapGeneratorGeneric() {
        return mapGeneratorGeneric;
    }


    public YioGdxGame getYioGdxGame() {
        return yioGdxGame;
    }


    public boolean isInEditorMode() {
        return GameRules.inEditorMode;
    }


    public Random getRandom() {
        return random;
    }


    public long getCurrentTime() {
        return currentTime;
    }


    public Statistics getStatistics() {
        return statistics;
    }


    public int getScreenX() {
        return screenX;
    }


    public int getScreenY() {
        return screenY;
    }


    public int getTurn() {
        return turn;
    }


    public ArrayList<ArtificialIntelligence> getAiList() {
        return aiList;
    }


    public void setBackgroundVisible(boolean backgroundVisible) {
        this.backgroundVisible = backgroundVisible;
    }
}
