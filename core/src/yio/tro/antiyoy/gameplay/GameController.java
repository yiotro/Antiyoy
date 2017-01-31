package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.ai.*;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.rules.Ruleset;
import yio.tro.antiyoy.gameplay.rules.RulesetGeneric;
import yio.tro.antiyoy.gameplay.rules.RulesetSlay;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.SliderYio;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ivan on 05.08.14.
 */
public class GameController {

    public YioGdxGame yioGdxGame;
    int screenX;
    int screenY;

    public final SelectionController selectionController;
    public final FieldController fieldController;
    public final CameraController cameraController;

    public Random random, predictableRandom;
    private LanguagesManager languagesManager;
    public boolean letsUpdateCacheByAnim;
    public boolean updateWholeCache;
    long currentTime;
    public boolean checkToMarch;
    public boolean ignoreMarch;
    int turn;
    private long timeToUpdateCache;
    float boundWidth;
    float boundHeight;
    boolean readyToEndTurn;
    private boolean proposedSurrender;
    private boolean cityNamesEnabled;
    private ArrayList<ArtificialIntelligence> aiList;
    public ArrayList<Unit> unitList;
    public int marchDelay;
    public int playersNumber;
    public int colorIndexViewOffset;
    String balanceString;
    String currentPriceString;
    String levelInitialString;
    private ArrayList<LevelSnapshot> levelSnapshots;
    float priceStringWidth;
    MapGenerator mapGeneratorSlay;
    MapGenerator mapGeneratorGeneric;
    Unit jumperUnit;
    public Statistics statistics;
    GameSaver gameSaver;
    Forefinger forefinger;
    public TutorialScript tutorialScript;
    private LevelEditor levelEditor;
    Ruleset ruleset;


    public GameController(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        random = new Random();
        predictableRandom = new Random(0);
        languagesManager = MenuControllerYio.languagesManager;
        CampaignController.getInstance().init(this);
        selectionController = new SelectionController(this);
        marchDelay = 500;
        cameraController = new CameraController(this);
        unitList = new ArrayList<Unit>();
        levelSnapshots = new ArrayList<LevelSnapshot>();
        mapGeneratorSlay = new MapGenerator(this);
        mapGeneratorGeneric = new MapGeneratorGeneric(this);

        fieldController = new FieldController(this);
        jumperUnit = new Unit(this, fieldController.emptyHex, 0);

        statistics = new Statistics(this);
        gameSaver = new GameSaver(this);
        forefinger = new Forefinger(this);
        levelEditor = new LevelEditor(this);
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
        checkForAiToMove();
        checkToEndTurn();
        checkToUpdateCacheByAnim();

        if (fieldController.letsCheckAnimHexes && currentTime > fieldController.timeToCheckAnimHexes && (isPlayerTurn() || isCurrentTurn(0)))
            fieldController.checkAnimHexes();

        moveCheckToMarch();
        moveUnits();
        fieldController.moveAnimHexes();
        selectionController.moveSelections();

        jumperUnit.moveJumpAnim();
        fieldController.moveZoneFactor.move();
        selectionController.getBlackoutFactor().move();
        selectionController.moveDefenseTips();

        if (fieldController.moveZone.size() > 0 && fieldController.moveZoneFactor.get() < 0.01)
            fieldController.clearMoveZone();
        selectionController.tipFactor.move();

        fieldController.moveResponseAnimHex();
        cameraController.move();
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
        if (!cameraController.checkConditionsToMarch()) return;

        checkToMarch = false;
        fieldController.updateFocusedHex();
        selectionController.setSelectedUnit(null);
        if (fieldController.focusedHex != null && fieldController.focusedHex.active) {
            fieldController.marchUnitsToHex(fieldController.focusedHex);
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
        }

        // propose surrender
        if (!proposedSurrender) {
            int possibleWinner = fieldController.possibleWinner();
            if (possibleWinner >= 0 && isPlayerTurn(possibleWinner)) {
                yioGdxGame.menuControllerYio.createTutorialTip(yioGdxGame.menuControllerYio.getArrayListFromString(languagesManager.getString("win_or_continue")));
                yioGdxGame.menuControllerYio.addWinButtonToTutorialTip();
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
//        yioGdxGame.setGamePaused(true);
        CampaignController campaignController = CampaignController.getInstance();
        if (campaignController.completedCampaignLevel(winColor)) {
            int ls = campaignController.currentLevelIndex;
//            yioGdxGame.increaseLevelSelection();
            if (campaignController.currentLevelIndex >= campaignController.progress) {
                campaignController.progress = campaignController.currentLevelIndex;
                if (ls == campaignController.progress) campaignController.progress++; // last level completed
                Preferences preferences = Gdx.app.getPreferences("main");
                preferences.putInteger("progress", campaignController.progress);
                preferences.flush();
//                yioGdxGame.menuControllerYio.updateScrollerLinesBeforeIndex(progress);
                yioGdxGame.menuControllerYio.levelSelector.update();
            }
        }
        yioGdxGame.menuControllerYio.createAfterGameMenu(winColor, isPlayerTurn(winColor));
        if (DebugFlags.CHECKING_BALANCE_MODE) {
            yioGdxGame.balanceIndicator[winColor]++;
            yioGdxGame.startGame(true, false);
        }
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
            cameraController.resetCurrentTouchCount();
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


    private void defaultValues() {
        cameraController.defaultCameraValues();
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
        proposedSurrender = false;
        colorIndexViewOffset = 0;
    }


    private int getLevelSizeBySliderPos(SliderYio sliderYio) {
        switch (sliderYio.getCurrentRunnerIndex()) {
            default:
            case 0:
                return FieldController.SIZE_SMALL;
            case 1:
                return FieldController.SIZE_MEDIUM;
            case 2:
                return FieldController.SIZE_BIG;
        }
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


    public void prepareForNewGame(int index, boolean generateMap, boolean readParametersFromSliders) {
        defaultValues();
        yioGdxGame.beginBackgroundChange(4, false, true);

        predictableRandom = new Random(index); // used in map generation
        fieldController.playerHexCount = new int[GameRules.colorNumber];
        if (readParametersFromSliders) {
            setLevelSize(getLevelSizeBySliderPos(yioGdxGame.menuControllerYio.sliders.get(0)));
            setPlayersNumberBySlider(yioGdxGame.menuControllerYio.sliders.get(1));
            GameRules.setColorNumberBySlider(yioGdxGame.menuControllerYio.sliders.get(2));
            GameRules.setDifficultyBySlider(yioGdxGame.menuControllerYio.sliders.get(3));
            readColorOffsetFromSlider();
            GameRules.setSlayRules(yioGdxGame.menuControllerYio.getCheckButtonById(16).isChecked());
        }
        fieldController.createField(generateMap); // generating map

//        campaignLevelFactory.generateLevels();

        // finishing
        cameraController.createCamera();
        yioGdxGame.gameView.updateCacheLevelTextures();
        fieldController.clearAnims();
        createAiList(GameRules.difficulty);
        updateLevelInitialString();
        if (DebugFlags.CHECKING_BALANCE_MODE) {
            while (true) {
                move();
                checkToEndGame();
            }
        }
    }


    public void readColorOffsetFromSlider() {
        int sliderIndex = yioGdxGame.menuControllerYio.sliders.get(4).getCurrentRunnerIndex();
        if (sliderIndex == 0) { // random
            colorIndexViewOffset = predictableRandom.nextInt(GameRules.colorNumber);
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


    public void createCamera() {
        cameraController.createCamera();
    }


    void createAiList() {
        createAiList(GameRules.difficulty);
    }


    private void createAiList(int difficulty) {
        aiList = new ArrayList<ArtificialIntelligence>();

        boolean testingNewAi = false;
        if (DebugFlags.CHECKING_BALANCE_MODE && testingNewAi && GameRules.colorNumber == 5) {
//            aiList.add(new AiExpertSlayRules(this, 0));
//            aiList.add(new AiExpertSlayRules(this, 1));
//            aiList.add(new AiExpertSlayRules(this, 2));
//            aiList.add(new AiBalancerSlayRules(this, 3));
//            aiList.add(new AiBalancerSlayRules(this, 4));

            aiList.add(new AiExpertGenericRules(this, 0));
            aiList.add(new AiExpertGenericRules(this, 1));
            aiList.add(new AiExpertGenericRules(this, 2));
            aiList.add(new AiBalancerGenericRules(this, 3));
            aiList.add(new AiBalancerGenericRules(this, 4));
            return;
        }

        for (int i = 0; i < GameRules.colorNumber; i++) {
            switch (difficulty) {
                default:
                case ArtificialIntelligence.DIFFICULTY_EASY:
                    aiList.add(new AiEasy(this, i));
                    break;
                case ArtificialIntelligence.DIFFICULTY_NORMAL:
                    if (GameRules.slay_rules) {
                        aiList.add(new AiNormalSlayRules(this, i));
                    } else {
                        aiList.add(new AiNormalGenericRules(this, i));
                    }
                    break;
                case ArtificialIntelligence.DIFFICULTY_HARD:
                    if (GameRules.slay_rules) {
                        aiList.add(new AiHardSlayRules(this, i));
                    } else {
                        aiList.add(new AiHardGenericRules(this, i));
                    }
                    break;
                case ArtificialIntelligence.DIFFICULTY_EXPERT:
                    if (GameRules.slay_rules) {
                        aiList.add(new AiExpertSlayRules(this, i));
                    } else {
                        aiList.add(new AiExpertGenericRules(this, i));
                    }
                    break;
                case ArtificialIntelligence.DIFFICULTY_BALANCER:
                    if (GameRules.slay_rules) {
                        aiList.add(new AiBalancerSlayRules(this, i));
                    } else {
                        aiList.add(new AiBalancerGenericRules(this, i));
                    }
                    break;
            }
        }
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
        cameraController.updateZoomUpperLimit(size);
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
        fieldController.levelSize = size;
        yioGdxGame.gameView.createLevelCacheTextures();
    }


    public GameSaver getGameSaver() {
        return gameSaver;
    }


    public void debugActions() {
//        System.out.println("" + gameSaver.getActiveHexesString());

        for (Hex activeHex : fieldController.activeHexes) {
            if (random.nextDouble() > 0.5)
                fieldController.setHexColor(activeHex, 0);
        }

//        StringBuilder builder = new StringBuilder();
//        for (Province province : provinces) {
//            if (province.getColor() == 0) continue;
//            String balance = province.getBalanceString();
//            if (balance.equals("0")) balance = "+0";
//            builder.append(province.money).append(balance).append("  ");
//        }
//        System.out.println(builder.toString());
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
        int currentColorOffset = colorIndexViewOffset;

        gameSaver.setActiveHexesString(levelInitialString);
        gameSaver.beginRecreation(false);
        colorIndexViewOffset = currentColorOffset;
        gameSaver.endRecreation();
    }


    public void undoAction() {
        int lastIndex = levelSnapshots.size() - 1;
        if (lastIndex < 0) return;
        cameraController.resetCurrentTouchCount();
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
        if (GameRules.inEditorMode) levelEditor.touchDown(screenX, screenY);
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


    void addAnimHex(Hex hex) {
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


    boolean isPlayerTurn(int turn) {
        return turn < playersNumber;
    }


    boolean isPlayerTurn() {
        return isPlayerTurn(turn);
    }


    boolean isCurrentTurn(int turn) {
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


    private void moveUnitWithAttack(Unit unit, Hex toWhere, Province unitProvince) {
        fieldController.setHexColor(toWhere, turn); // must be called before object in hex destroyed
        fieldController.cleanOutHex(toWhere);
        unit.moveToHex(toWhere);
        unitProvince.addHex(toWhere);
        if (isPlayerTurn()) {
            fieldController.selectedHexes.add(toWhere);
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
        if (GameRules.inEditorMode) levelEditor.touchUp(screenX, screenY);
        this.screenX = screenX;
        this.screenY = screenY;
        cameraController.touchUp();
    }


    public void touchDragged(int screenX, int screenY, int pointer) {
        if (GameRules.inEditorMode) levelEditor.touchDrag(screenX, screenY);

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
        cameraController.scrolled(amount);
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


    public CameraController getCameraController() {
        return cameraController;
    }


    public int getTurn() {
        return turn;
    }
}
