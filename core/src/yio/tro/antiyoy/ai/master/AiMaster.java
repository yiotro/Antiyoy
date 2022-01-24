package yio.tro.antiyoy.ai.master;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Clipboard;
import yio.tro.antiyoy.PlatformType;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.ai.AbstractAi;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.rules.Ruleset;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;

public class AiMaster extends AbstractAi {

    FieldManager fieldManager;
    PropagationCaster casterLoneliness;
    PropagationCaster casterAttractiveness;
    double tempValue;
    int tempCounter;
    PossibleSpending[] possibleSpendings;
    Province currentProvince;
    int income;
    int money;
    int profit;
    MasterAction[] actions;
    ArrayList<Hex> moveZone;
    ArrayList<Hex> tempHexList;
    PropagationCaster casterTempHexListInsideFraction;
    PropagationCaster casterImportance;
    ArrayList<Hex> firstLine;
    ArrayList<Hex> secondLine;
    AttackManager attackManager;
    PropagationCaster casterNearbyOwnedLands;
    ArrayList<Province> ownedProvinces;
    private ArrayList<Hex> junkList;
    int adjacentNeutralLandsQuantity;
    ArrayList<Hex> adjacentNeutralLandsList;
    DefenseManager defenseManager;
    ArrayList<Unit> readyUnits;
    String lastStateString;
    PropagationCaster casterPathIndex;
    double averageOwnedAttractiveness;


    public AiMaster(GameController gameController, int fraction) {
        super(gameController, fraction);
        fieldManager = gameController.fieldManager;
        tempHexList = new ArrayList<>();
        firstLine = new ArrayList<>();
        secondLine = new ArrayList<>();
        attackManager = new AttackManager(this);
        ownedProvinces = new ArrayList<>();
        junkList = new ArrayList<>();
        defenseManager = new DefenseManager(this);
        adjacentNeutralLandsList = new ArrayList<>();
        readyUnits = new ArrayList<>();
        initPossibleSpendings();
        initActions();
        initCasters();
        prepare();
    }


    void initActions() {
        actions = new MasterAction[MaType.values().length];
        for (int i = 0; i < MaType.values().length; i++) {
            actions[i] = new MasterAction(MaType.values()[i]);
        }
    }


    void initPossibleSpendings() {
        possibleSpendings = new PossibleSpending[PsType.values().length];
        for (int i = 0; i < PsType.values().length; i++) {
            possibleSpendings[i] = new PossibleSpending(PsType.values()[i]);
        }
    }


    void initCasters() {
        casterLoneliness = new PropagationCaster(this) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return true;
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                tempCounter++;
            }
        };

        casterAttractiveness = new PropagationCaster(this) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return true;
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                tempValue += hex.aiData.loneliness;
                tempCounter++;
            }
        };

        casterTempHexListInsideFraction = new PropagationCaster(this) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return src.sameFraction(dst);
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                tempHexList.add(hex);
            }
        };

        casterImportance = new PropagationCaster(this) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return src.sameFraction(dst);
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                hex.aiData.importance = previousHex.aiData.importance - 1;
            }
        };

        casterNearbyOwnedLands = new PropagationCaster(this) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return dst.aiData.currentlyOwned;
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                tempCounter++;
            }
        };

        casterPathIndex = new PropagationCaster(this) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return src.sameFraction(dst);
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                hex.aiData.referenceHex = previousHex;
            }
        };
    }


    void prepare() {
        updateLoneliness();
        updateAttractiveness();
    }


    @Override
    public void perform() {
        updateOwnedProvinces();
        beginDebug();

        for (Province ownedProvince : ownedProvinces) {
            if (!fieldManager.provinces.contains(ownedProvince)) continue;
            performForSingleProvince(ownedProvince);
        }

        endDebug();
    }


    private void endDebug() {
        if (YioGdxGame.platformType != PlatformType.pc) return;
        if (DebugFlags.testMode || fraction != 0) return;
//        gameController.speedManager.setSpeed(0);
    }


    double calculateAverageAttractiveness(Province province) {
        double sum = 0;
        for (Hex hex : province.hexList) {
            sum += hex.aiData.attractiveness;
        }
        return sum / province.hexList.size();
    }


    private void beginDebug() {
        if (fraction != 0) return;
        if (YioGdxGame.platformType != PlatformType.pc) return;
        clearViewValues();
        if (!DebugFlags.testMode && DebugFlags.closerLookMode) {
            showMoneyInConsole();
        }
        updateLastState();
    }


    private void updateLastState() {
        // should be private
        String lastCameraState = gameController.cameraController.encode();
        String lastLevelState = gameController.encodeManager.perform();
        lastStateString = lastCameraState + "/" + lastLevelState;
    }


    public void exportLastStateStringToClipboard() {
        System.out.println();
        System.out.println("AiMaster.exportLastStateStringToClipboard");
        System.out.println(lastStateString);

        Clipboard clipboard = Gdx.app.getClipboard();
        clipboard.setContents(lastStateString);
        Scenes.sceneNotification.show("Last state exported");
    }


    private void showMoneyInConsole() {
        if (ownedProvinces.size() == 0) return;
        currentProvince = getBiggestOwnedProvince();
        updateMoneyStats();
        System.out.println("money: " + money + "       profit: " + profit);
    }


    private Province getBiggestOwnedProvince() {
        Province bestProvince = null;
        for (Province province : ownedProvinces) {
            if (bestProvince == null || province.hexList.size() > bestProvince.hexList.size()) {
                bestProvince = province;
            }
        }
        return bestProvince;
    }


    private void updateOwnedProvinces() {
        ownedProvinces.clear();
        for (Province province : fieldManager.provinces) {
            if (province.getFraction() != fraction) continue;
            ownedProvinces.add(province);
        }
    }


    void performForSingleProvince(Province province) {
        currentProvince = province;
        resetAiData();
        updateMoneyStats();
        updateCurrentlyOwned();
        updateAverageOwnedAttractiveness();
        updatePerimeter();
        updateImportance();
        updateSolidDefense();
        updateVicinity();
        checkToMergePeasants();
        defenseManager.onTurnStarted();
        attackManager.onTurnStarted();
        applyActionsAndSpendings();
        checkForCasualGrab();
        pullUnitsCloserToPerimeter();
        checkToSupplyArmyWithTowers();
        checkToPushUnitsToBettrDefense();
    }


    private void checkToPushUnitsToBettrDefense() {
        updateReadyUnits();
        for (Unit unit : readyUnits) {
            pushUnitToBetterDefense(unit);
        }
    }


    private void updateAverageOwnedAttractiveness() {
        averageOwnedAttractiveness = calculateAverageAttractiveness(currentProvince);
    }


    boolean isInBadSpot() {
        return averageOwnedAttractiveness < 0.4;
    }


    private void checkToSupplyArmyWithTowers() {
        if (getMaxStrengthInProvince() < 2) return;
        Hex capital = currentProvince.getCapital();
        while (currentProvince.money >= GameRules.PRICE_TOWER) {
            prepareForSupplyArmyAlgorithm();
            casterPathIndex.perform(capital, 9999);
            castDependence();
            updateTempHexListWithVulnerableHexes();
            if (tempHexList.size() == 0) break;
            Hex mostVulnerableHex = getMostVulnerableHex(tempHexList);
            int maxVulnerability = calculateVulnerability(mostVulnerableHex);
            filterTempHexListByVulnerability(maxVulnerability);
            if (tempHexList.size() == 0) {
                System.out.println("AiMaster.checkToSupplyArmyWithTowers: problem");
            }
            Hex hexWithMostDefenseGain = getHexWithMostDefenseGain(tempHexList, 2);
            buildTower(hexWithMostDefenseGain, 2);
        }
    }


    int getMaxStrengthInProvince() {
        int maxStrength = 0;
        for (Hex hex : currentProvince.hexList) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (unit.strength > maxStrength) {
                maxStrength = unit.strength;
            }
        }
        return maxStrength;
    }


    Hex getHexWithMostDefenseGain(ArrayList<Hex> list, int targetDefense) {
        Hex bestHex = null;
        int maxGain = 0;
        for (Hex hex : list) {
            int currentGain = getBalancerDefenseGainPrediction(hex);
            if (bestHex == null || currentGain > maxGain) {
                bestHex = hex;
                maxGain = currentGain;
            }
        }
        return bestHex;
    }


    void filterTempHexListByVulnerability(int requiredVulnerability) {
        for (int i = tempHexList.size() - 1; i >= 0; i--) {
            Hex hex = tempHexList.get(i);
            int vulnerability = calculateVulnerability(hex);
            if (vulnerability == requiredVulnerability) continue;
            tempHexList.remove(hex);
        }
    }


    Hex getMostVulnerableHex(ArrayList<Hex> list) {
        Hex bestHex = null;
        int maxVulnerability = 0;
        for (Hex hex : list) {
            int currentVulnerability = calculateVulnerability(hex);
            if (bestHex == null || currentVulnerability > maxVulnerability) {
                bestHex = hex;
                maxVulnerability = currentVulnerability;
            }
        }
        return bestHex;
    }


    int calculateVulnerability(Hex hex) {
        int sum = 0;
        for (Unit unit : hex.aiData.dependentUnits) {
            sum += unit.strength;
        }
        return sum;
    }


    private void updateTempHexListWithVulnerableHexes() {
        tempHexList.clear();
        for (Hex hex : currentProvince.hexList) {
            if (!hex.isEmpty()) continue;
            if (hex.aiData.dependentUnits.size() == 0) continue;
            if (!hasEnemyLandsNearby(hex)) continue;
            if (hasOwnedTowerNearby(hex)) continue;
            tempHexList.add(hex);
        }
    }


    boolean hasEnemyLandsNearby(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (adjacentHex.isNeutral()) continue;
            if (adjacentHex.sameFraction(hex)) continue;
            return true;
        }
        return false;
    }


    private void castDependence() {
        for (Hex hex : currentProvince.hexList) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (unit.strength == 1) continue;
            castUnitDependenceToCapital(unit);
        }
    }


    void castUnitDependenceToCapital(Unit unit) {
        Hex hex = unit.currentHex;
        while (true) {
            if (hex.aiData.referenceHex == null) break;
            hex = hex.aiData.referenceHex;
            hex.aiData.dependentUnits.add(unit);
        }
    }


    private void prepareForSupplyArmyAlgorithm() {
        for (Hex hex : currentProvince.hexList) {
            hex.aiData.dependentUnits.clear();
            hex.aiData.referenceHex = null;
        }
    }


    private void applyActionsAndSpendings() {
        boolean actionTryAllowed = true;
        boolean spendingTryAllowed = true;
        for (int i = 0; i < 7; i++) {
            if (spendingTryAllowed && !tryToApplySingleSpending()) {
                spendingTryAllowed = false;
            }
            if (actionTryAllowed && !tryToPerformAction()) {
                actionTryAllowed = false;
            }
            if (!actionTryAllowed && !spendingTryAllowed) break;
        }
    }


    boolean areAllOwnedHexesTaggedAsCurrentlyOwned() {
        for (Hex hex : currentProvince.hexList) {
            if (hex.aiData.currentlyOwned) continue;
            return false;
        }
        return true;
    }


    Hex getHexThatIsWronglyNotTaggedAsCurrentlyOwned() {
        for (Hex hex : currentProvince.hexList) {
            if (hex.aiData.currentlyOwned) continue;
            return hex;
        }
        return null;
    }


    void checkForCasualGrab() {
        updateReadyUnits();
        for (int i = readyUnits.size() - 1; i >= 0; i--) {
            Unit unit = readyUnits.get(i);
            updateMoveZone(unit);
            Hex hex = getBestHexForCasualGrab(moveZone, unit);
            if (hex == null) continue;
            sendUnitWithCheck(unit, hex);
        }
    }


    private Hex getBestHexForCasualGrab(ArrayList<Hex> list, Unit unit) {
        for (Hex hex : list) {
            if (hex.sameFraction(unit.currentHex) && hex.containsTree()) return hex;
        }
        for (Hex hex : list) {
            if (!hex.sameFraction(unit.currentHex) && unit.strength > hex.getDefenseNumber()) return hex;
        }
        return null;
    }


    private void updateReadyUnits() {
        readyUnits.clear();
        for (Hex hex : currentProvince.hexList) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (!unit.isReadyToMove()) continue;
            readyUnits.add(hex.unit);
        }
    }


    private void pullUnitsCloserToPerimeter() {
        for (Hex hex : currentProvince.hexList) {
            if (hex.aiData.firstLine || hex.aiData.secondLine) continue; // already at perimeter
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (!unit.isReadyToMove()) continue;
            Hex targetHex = getClosestHexInPerimeter(unit);
            if (targetHex == null) {
                targetHex = getClosestHexNearNeutralLands(unit);
            }
            if (targetHex == null) continue;
            getMassMarchManager().performForSingleUnit(unit, targetHex);
        }
    }


    Hex getClosestHexNearNeutralLands(Unit unit) {
        Hex bestHex = null;
        double minDistance = 0;
        for (Hex hex : currentProvince.hexList) {
            if (!hasNeutralLandsNearby(hex)) continue;
            double currentDistance = hex.pos.fastDistanceTo(unit.currentHex.pos);
            if (bestHex == null || currentDistance < minDistance) {
                bestHex = hex;
                minDistance = currentDistance;
            }
        }
        return bestHex;
    }


    boolean hasNeutralLandsNearby(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (adjacentHex.isNeutral()) return true;
        }
        return false;
    }


    private MassMarchManager getMassMarchManager() {
        return fieldManager.massMarchManager;
    }


    Hex getClosestHexInPerimeter(Unit unit) {
        Hex bestHex = null;
        double minDistance = 0;
        for (Hex hex : firstLine) {
            double currentDistance = hex.pos.fastDistanceTo(unit.currentHex.pos);
            if (bestHex == null || currentDistance < minDistance) {
                bestHex = hex;
                minDistance = currentDistance;
            }
        }
        return bestHex;
    }


    int getStrengthNecessaryToCapture(Hex hex) {
        int defenseNumber = hex.getDefenseNumber();
        if (defenseNumber == 4) {
            if (GameRules.slayRules) return -1; // can't be captured
            return 4;
        }
        return defenseNumber + 1;
    }


    void checkToMergePeasants() {
        if (adjacentNeutralLandsQuantity > firstLine.size()) return;

        int peasantsQuantity = 0;
        int othersQuantity = 0;
        for (Hex hex : currentProvince.hexList) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (unit.strength == 1) {
                peasantsQuantity++;
            } else {
                othersQuantity++;
            }
        }
        if (peasantsQuantity < 5) return;

        int merges = peasantsQuantity - 2 * othersQuantity;
        if (merges <= 0) return;
        if (merges > 3) {
            merges = 3;
        }

        for (int i = 0; i < merges; i++) {
            applyMergeTwoPeasants();
        }
    }


    void applyMergeTwoPeasants() {
        for (Hex hex : currentProvince.hexList) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (!unit.isReadyToMove()) continue;
            if (unit.strength != 1) continue;
            updateMoveZone(unit, GameRules.UNIT_MOVE_LIMIT, false);
            Unit anotherUnit = getReadyToMovePeasant(moveZone, unit);
            if (anotherUnit == null) continue;
            mergeUnits(anotherUnit, unit);
            break;
        }
    }


    private Unit getReadyToMovePeasant(ArrayList<Hex> list, Unit excludedUnit) {
        for (Hex hex : list) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (unit == excludedUnit) continue;
            if (!unit.isReadyToMove()) continue;
            if (unit.strength != 1) continue;
            return unit;
        }
        return null;
    }


    boolean needToSaveUpForNormalTower() {
        return countUndefendedPerimeterHexes() > countWholePerimeter() / 3;
    }


    int countWholePerimeter() {
        return firstLine.size() + secondLine.size();
    }


    int countUndefendedPerimeterHexes() {
        int c = 0;
        for (Hex hex : firstLine) {
            if (hasSupportiveTowerNearby(hex)) continue;
            c++;
        }
        for (Hex hex : secondLine) {
            if (hasSupportiveTowerNearby(hex)) continue;
            c++;
        }
        return c;
    }


    private void resetAiData() {
        for (Hex hex : getActiveHexes()) {
            hex.aiData.reset();
        }
    }


    void updateCurrentlyOwned() {
        clearCurrentlyOwned();
        addCurrentlyOwnedTags();
    }


    private void addCurrentlyOwnedTags() {
        for (Hex hex : currentProvince.hexList) {
            if (hex.aiData.currentlyOwned) continue;
            hex.aiData.currentlyOwned = true;
        }
    }


    void syncProvince() {
        addCurrentlyOwnedTags();
        Hex capital = currentProvince.getCapital();
        Province provinceByHex = fieldManager.getProvinceByHex(capital);
        if (provinceByHex == currentProvince) return;
        if (provinceByHex == null) return;

        // province object changed
        currentProvince = provinceByHex;
        updateCurrentlyOwned();
    }


    private void clearCurrentlyOwned() {
        for (Hex hex : getActiveHexes()) {
            hex.aiData.currentlyOwned = false;
        }
    }


    private void updateVicinity() {
        if (adjacentNeutralLandsList.size() == 0) return;

        for (Hex hex : adjacentNeutralLandsList) {
            tempCounter = 0;
            casterNearbyOwnedLands.perform(hex, 3);
            hex.aiData.ownedLandsNearby = tempCounter;
        }

        Hex maxHex = getHexWithMostNearbyLands(adjacentNeutralLandsList);
        double maxValue = maxHex.aiData.ownedLandsNearby;

        for (Hex hex : adjacentNeutralLandsList) {
            hex.aiData.vicinity = hex.aiData.ownedLandsNearby / maxValue;
        }
    }


    private Hex getHexWithMostNearbyLands(ArrayList<Hex> list) {
        Hex bestHex = null;
        for (Hex hex : list) {
            if (bestHex == null || hex.aiData.ownedLandsNearby > bestHex.aiData.ownedLandsNearby) {
                bestHex = hex;
            }
        }
        return bestHex;
    }


    void updateAdjacentNeutralLandsList() {
        adjacentNeutralLandsList.clear();
        for (Hex hex : currentProvince.hexList) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (!isWorkable(adjacentHex)) continue;
                if (!adjacentHex.isNeutral()) continue;
                if (adjacentNeutralLandsList.contains(adjacentHex)) continue;
                adjacentNeutralLandsList.add(adjacentHex);
            }
        }
    }


    void updateSolidDefense() {
        for (Hex hex : currentProvince.hexList) {
            hex.aiData.solidDefense = 0;
        }
        for (Hex hex : currentProvince.hexList) {
            if (hex.objectInside == Obj.TOWN) {
                increaseSolidDefenseNearby(hex, 1);
            }
            if (hex.objectInside == Obj.TOWER) {
                increaseSolidDefenseNearby(hex, 2);
            }
            if (hex.objectInside == Obj.STRONG_TOWER) {
                increaseSolidDefenseNearby(hex, 3);
            }
        }
    }


    boolean isMoreAttractiveThenNearbyHexes(Hex hex) {
        int c = 0;
        double sum = 0;
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!hex.sameFraction(adjacentHex)) continue;
            c++;
            sum += adjacentHex.aiData.attractiveness;
        }
        double medium = sum / c;
        return hex.aiData.attractiveness > medium;
    }


    void increaseSolidDefenseNearby(Hex hex, int defense) {
        hex.aiData.solidDefense = Math.max(hex.aiData.solidDefense, defense);
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!hex.sameFraction(adjacentHex)) continue;
            adjacentHex.aiData.solidDefense = Math.max(adjacentHex.aiData.solidDefense, defense);
        }
    }


    void updateImportance() {
        resetImportance();
        updateTempListByFarms();
        int maxImportance = 4;
        for (Hex hex : tempHexList) {
            hex.aiData.importance = maxImportance;
        }
        casterImportance.perform(tempHexList, maxImportance);
    }


    void resetImportance() {
        for (Hex hex : currentProvince.hexList) {
            hex.aiData.importance = 0;
        }
    }


    void updateTempListByFarms() {
        tempHexList.clear();
        for (Hex hex : currentProvince.hexList) {
            if (hex.objectInside != Obj.FARM) continue;
            tempHexList.add(hex);
        }
    }


    void updatePerimeter() {
        firstLine.clear();
        for (Hex hex : currentProvince.hexList) {
            if (!isInFirstLineSlow(hex)) continue;
            hex.aiData.firstLine = true;
            firstLine.add(hex);
        }

        secondLine.clear();
        for (Hex hex : currentProvince.hexList) {
            if (!isInSecondLineSlow(hex)) continue;
            hex.aiData.secondLine = true;
            secondLine.add(hex);
        }

        updateAdjacentNeutralLands();
    }


    private void updateAdjacentNeutralLands() {
        updateAdjacentNeutralLandsList();
        adjacentNeutralLandsQuantity = adjacentNeutralLandsList.size();
    }


    boolean isInSecondLineSlow(Hex hex) {
        if (hex.aiData.firstLine) return false;
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!adjacentHex.sameFraction(hex)) continue;
            if (!adjacentHex.aiData.firstLine) continue;
            return true;
        }
        return false;
    }


    boolean isInFirstLineSlow(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (adjacentHex.isNeutral()) continue;
            if (adjacentHex.sameFraction(hex)) continue;
            return true;
        }
        return false;
    }


    boolean isWorkable(Hex hex) {
        if (hex == null) return false;
        if (hex.isNullHex()) return false;
        if (!hex.active) return false;
        return true;
    }


    MasterAction getBestAction() {
        MasterAction bestAction = null;
        for (MasterAction action : actions) {
            if (!action.valid) continue;
            if (bestAction == null || action.thirst > bestAction.thirst) {
                bestAction = action;
            }
        }
        return bestAction;
    }


    MasterAction getAction(MaType maType) {
        for (MasterAction action : actions) {
            if (action.maType == maType) return action;
        }
        return null;
    }


    private boolean tryToPerformAction() {
        updateAdjacentNeutralLands();
        updateActions();
        showActionsInConsole();
        MasterAction bestAction = getBestAction();
        if (bestAction == null) return false;
        if (bestAction.thirst < 1) return false;
        applyAction(bestAction);
        return true;
    }


    private void showActionsInConsole() {
        if (!DebugFlags.closerLookMode) return;
        if (!DebugFlags.showDetailedAiMasterInfo) return;
        System.out.println();
        System.out.println("AiMaster.showActionsInConsole");
        showMoneyInConsole();
        for (MasterAction action : actions) {
            System.out.println(action.maType + ": " + Yio.roundUp(action.thirst, 2));
        }
    }


    void applyAction(MasterAction masterAction) {
        if (DebugFlags.closerLookMode) {
            System.out.println("Action applied: " + masterAction.maType);
        }
        switch (masterAction.maType) {
            default:
                break;
            case cut_tree:
                applyCutTreeAction();
                break;
            case peacefully_expand:
                applyPeacefulExpansionAction();
                break;
            case attack:
                attackManager.perform();
                break;
            case defend:
                defenseManager.perform();
                break;
        }
    }


    void applyPeacefulExpansionAction() {
        updateReadyUnits();
        for (Unit unit : readyUnits) {
            if (unit.strength > 2) continue;
            Hex bestOverallHex = getBestHexForPeacefulExpansion(adjacentNeutralLandsList, unit.strength);
            if (bestOverallHex == null) break; // failure
            if (sendUnitWithCheck(unit, bestOverallHex)) return; // success
            updateMoveZone(unit);
            removeNonNeutralLandsFromMoveZone();
            Hex bestLocalHex = getBestHexForPeacefulExpansion(moveZone, unit.strength);
            if (calculateCurrentAttractivenes(bestLocalHex) > 0.5 * calculateCurrentAttractivenes(bestOverallHex)) {
                sendUnitDirectly(unit, bestLocalHex);
                return;
            }
            Hex ownedHex = getOwnedAdjacentHex(bestOverallHex);
            if (ownedHex != null) {
                getMassMarchManager().performForSingleUnit(unit, ownedHex);
            }
            return;
        }
        checkToPeacefullyExpandWithMoney();
        checkToFightTreesWithMoney();
    }


    private void checkToFightTreesWithMoney() {
        while (currentProvince.money >= GameRules.PRICE_UNIT) {
            if (!currentProvince.containsTrees()) break;
            Hex hex = getWorstTree();
            buildUnit(hex, 1);
        }
    }


    private void checkToPeacefullyExpandWithMoney() {
        if (money < 100) return;
        if (!doesHaveDefendedNeutralLandsNearby()) return;
        Hex neutralHex = getRandomAdjacentNeutralHex();
        if (neutralHex == null) return;
        int necessaryStrength = getStrengthNecessaryToCapture(neutralHex);
        buildUnit(neutralHex, necessaryStrength);
    }


    Hex getRandomAdjacentNeutralHex() {
        if (adjacentNeutralLandsQuantity == 0) return null;
        int index = YioGdxGame.random.nextInt(adjacentNeutralLandsList.size());
        return adjacentNeutralLandsList.get(index);
    }


    boolean doesHaveDefendedNeutralLandsNearby() {
        for (Hex hex : adjacentNeutralLandsList) {
            if (hex.getDefenseNumber() == 0) continue;
            return true;
        }
        return false;
    }


    void removeNonNeutralLandsFromMoveZone() {
        for (int i = moveZone.size() - 1; i >= 0; i--) {
            Hex hex = moveZone.get(i);
            if (hex.isNeutral()) continue;
            moveZone.remove(hex);
        }
    }


    Hex getOwnedAdjacentHex(Hex targetHex) {
        for (Hex hex : currentProvince.hexList) {
            if (hex.isAdjacentTo(targetHex)) return hex;
        }
        return null;
    }


    void say(String string) {
        if (fraction != 0) return;
        System.out.println(string);
    }


    void applyCutTreeAction() {
        for (Hex hex : currentProvince.hexList) {
            if (!hex.containsTree()) continue;

            tempHexList.clear();
            casterTempHexListInsideFraction.perform(hex, GameRules.UNIT_MOVE_LIMIT);
            Unit unit = getReadyUnitInTempList();
            if (unit == null) continue;
            sendUnitWithCheck(unit, hex);
            break;
        }
    }


    Unit getReadyUnitInTempList() {
        for (Hex hex : tempHexList) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (unit.isReadyToMove()) return unit;
        }
        return null;
    }


    boolean sendUnitWithCheck(Unit unit, Hex targetHex) {
        // should be careful with calling this method in attackManager
        if (unit.getFraction() != targetHex.fraction && !getRuleset().canUnitAttackHex(unit.strength, targetHex)) return false;
        updateMoveZone(unit);
        if (moveZone.size() == 0) return false;
        if (!doesMoveZoneContain(targetHex)) return false;
        sendUnitDirectly(unit, targetHex);
        return true;
    }


    boolean doesMoveZoneContain(Hex hex) {
        for (Hex mzHex : moveZone) {
            if (mzHex == hex) return true;
        }
        return false;
    }


    void updateMoveZone(Unit unit) {
        updateMoveZone(unit, GameRules.UNIT_MOVE_LIMIT, true);
    }


    void updateMoveZone(Unit unit, int limit, boolean excludeFriendlyUnits) {
        moveZone = gameController.detectMoveZone(unit.currentHex, unit.strength, limit);
        if (excludeFriendlyUnits) {
            excludeFriendlyUnitsFromMoveZone();
        }
        excludeFriendlyBuildingsFromMoveZone();
        moveZone.remove(unit.currentHex);
    }


    private void excludeFriendlyBuildingsFromMoveZone() {
        junkList.clear();
        for (Hex hex : moveZone) {
            if (hex.sameFraction(fraction)) {
                if (hex.containsBuilding()) junkList.add(hex);
            }
        }
        moveZone.removeAll(junkList);
    }


    private void excludeFriendlyUnitsFromMoveZone() {
        junkList.clear();
        for (Hex hex : moveZone) {
            if (hex.sameFraction(fraction)) {
                if (hex.containsUnit()) junkList.add(hex);
            }
        }
        moveZone.removeAll(junkList);
    }


    void sendUnitDirectly(Unit unit, Hex targetHex) {
        // should be careful with calling this method in attackManager
        checkForUnitMovementProblems(unit, targetHex);
        gameController.moveUnit(unit, targetHex, currentProvince);
        syncProvince();
    }


    private void checkForUnitMovementProblems(Unit unit, Hex targetHex) {
        if (YioGdxGame.platformType != PlatformType.pc) return;
        if (unit.getFraction() != fraction) {
            System.out.println("AiMaster.sendUnitDirectly: problem");
        }
        if (unit.getFraction() != targetHex.fraction && !getRuleset().canUnitAttackHex(unit.strength, targetHex)) {
            System.out.println("AiMaster.sendUnitDirectly: problem 2");
        }
    }


    void updateActions() {
        for (MasterAction action : actions) {
            action.valid = isActionValid(action.maType);
            if (!action.valid) continue;
            updateActionThirst(action);
        }
    }


    void updateActionThirst(MasterAction masterAction) {
        switch (masterAction.maType) {
            default:
                break;
            case cut_tree:
                masterAction.thirst = 0.5 + 2 * currentProvince.countObjects(Obj.PALM) + currentProvince.countObjects(Obj.PINE);
                break;
            case peacefully_expand:
                if (adjacentNeutralLandsQuantity < 3) {
                    masterAction.thirst = 0;
                    break;
                }
                masterAction.thirst = 0.5 + 0.3 * adjacentNeutralLandsQuantity;
                break;
            case attack:
                masterAction.thirst = attackManager.getThirst();
                break;
            case defend:
                masterAction.thirst = defenseManager.getThirst();
                break;
        }
    }


    boolean isActionValid(MaType maType) {
        switch (maType) {
            default:
                return true;
            case cut_tree:
                return currentProvince.containsTrees() && canAnyTreeBeCutWithAlreadyBuiltUnit();
            case peacefully_expand:
                return adjacentNeutralLandsQuantity > 0;
            case attack:
                return true;
            case defend:
                return currentProvince.hexList.size() > 6;
        }
    }


    boolean canAnyTreeBeCutWithAlreadyBuiltUnit() {
        for (Hex hex : currentProvince.hexList) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (!unit.isReadyToMove()) continue;
            if (unit.strength > 2) continue;
            updateMoveZone(unit);
            if (doesMoveZoneContainTree()) return true;
        }
        return false;
    }


    boolean doesMoveZoneContainTree() {
        for (Hex hex : moveZone) {
            if (hex.containsTree()) return true;
        }
        return false;
    }


    void updateMoneyStats() {
        money = currentProvince.money;
        income = currentProvince.getIncome();
        profit = income - currentProvince.getTaxes();
    }


    private boolean tryToApplySingleSpending() {
        updatePossibleSpendings();
        showSpendingsInConsole();
        PossibleSpending bestPossibleSpending = getBestPossibleSpending();
        if (bestPossibleSpending == null) return false;
        if (bestPossibleSpending.thirst < 1) return false;
        applySpending(bestPossibleSpending);
        return true;
    }


    void updatePossibleSpendings() {
        for (PossibleSpending possibleSpending : possibleSpendings) {
            possibleSpending.valid = isSpendingValid(possibleSpending.psType);
            if (!possibleSpending.valid) continue;
            updateSpendingThirst(possibleSpending);
        }
        checkToHoldForNormalTower();
        checkForGreatDanger();
    }


    private void checkForGreatDanger() {
        if (defenseManager.getThirst() < 5) return;
        deactivateSpendings(null, null); // instead focus on defeating danger
    }


    Hex getHexThatDeservesToBeUpgradedToStrongTower() {
        for (Hex hex : currentProvince.hexList) {
            if (!deservesToBeUpgradedToStrongTower(hex)) continue;
            return hex;
        }
        return null;
    }


    boolean deservesToBeUpgradedToStrongTower(Hex hex) {
        if (hex.objectInside != Obj.TOWER) return false;
        if (!hex.aiData.firstLine && !hex.aiData.secondLine) return false;
        return hex.numberOfFriendlyHexesNearby() >= 4;
    }


    private void checkToHoldForNormalTower() {
        if (!needToSaveUpForNormalTower()) return;

        deactivateSpendings(PsType.tower1, PsType.tower2);
        getSpending(PsType.tower1).thirst = 2;
    }


    void deactivateSpendings(PsType exception1, PsType exception2) {
        for (PossibleSpending spending : possibleSpendings) {
            if (spending.psType == exception1) continue;
            if (spending.psType == exception2) continue;
            spending.thirst = 0;
        }
    }


    void showSpendingsInConsole() {
        if (!DebugFlags.closerLookMode) return;
        if (!DebugFlags.showDetailedAiMasterInfo) return;
        System.out.println();
        System.out.println("AiMaster.showSpendingsInConsole");
        showMoneyInConsole();
        for (PossibleSpending possibleSpending : possibleSpendings) {
            System.out.println(possibleSpending.psType + ": " + Yio.roundUp(possibleSpending.thirst, 2));
        }
    }


    void applySpending(PossibleSpending possibleSpending) {
        if (DebugFlags.closerLookMode) {
            System.out.println("Spending applied: " + possibleSpending.psType);
        }
        switch (possibleSpending.psType) {
            default:
                break;
            case unit1:
                applyBuildUnitSpending(1);
                break;
            case unit2:
                applyBuildUnitSpending(2);
                break;
            case unit3:
                applyBuildUnitSpending(3);
                break;
            case unit4:
                applyBuildUnitSpending(4);
                break;
            case farm:
                applyFarmSpending();
                break;
            case tower1:
                applyTowerSpending(2);
                break;
            case tower2:
                applyTowerSpending(3);
                break;
        }
    }


    void applyTowerSpending(int targetDefense) {
        if (targetDefense == 3) {
            tryToUpgradeNormalTower();
            return;
        }

        Hex hexFromSecondLine = getMostImportantAndSuitableForTowerHex(secondLine, targetDefense);
        if (hexFromSecondLine != null) {
            buildTower(hexFromSecondLine, targetDefense);
            return;
        }

        Hex hexFromFirstLine = getMostImportantAndSuitableForTowerHex(firstLine, targetDefense);
        if (hexFromFirstLine != null) {
            buildTower(hexFromFirstLine, targetDefense);
        }
    }


    private void tryToUpgradeNormalTower() {
        Hex hex = getMostImportantTowerToUpgrade();
        if (hex != null && isImportantEnoughForStrongTower(hex)) {
            buildTower(hex, 3);
            return;
        }
        hex = getHexThatDeservesToBeUpgradedToStrongTower();
        if (hex != null) {
            buildTower(hex, 3);
        }
    }


    Hex getMostImportantTowerToUpgrade() {
        Hex bestHex = null;
        double maxImportance = 0;
        for (Hex hex : currentProvince.hexList) {
            if (hex.objectInside != Obj.TOWER) continue;
            if (hex.aiData.importance == 0) continue;
            if (bestHex == null || hex.aiData.importance > maxImportance) {
                bestHex = hex;
                maxImportance = hex.aiData.importance;
            }
        }
        return bestHex;
    }


    Hex getMostImportantAndSuitableForTowerHex(ArrayList<Hex> list, int targetDefense) {
        Hex bestHex = null;
        for (Hex hex : list) {
            if (!isHexSuitableForNewTower(hex, targetDefense)) continue;
            if (bestHex == null || hex.aiData.importance > bestHex.aiData.importance) {
                bestHex = hex;
            }
        }
        return bestHex;
    }


    void buildTower(Hex hex, int targetDefense) {
        switch (targetDefense) {
            default:
                System.out.println("AiMaster.buildTower: problem");
                break;
            case 2:
                gameController.fieldManager.buildTower(currentProvince, hex);
                break;
            case 3:
                gameController.fieldManager.buildStrongTower(currentProvince, hex);
                break;
        }
        updateSolidDefense();
        updateMoneyStats();
    }


    boolean canFractionBeAttacked(int anotherFraction) {
        if (anotherFraction == getFraction()) return false;
        if (GameRules.diplomacyEnabled) {
            DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
            DiplomaticEntity mainEntity = diplomacyManager.getEntity(getFraction());
            DiplomaticEntity anotherEntity = diplomacyManager.getEntity(anotherFraction);
            return mainEntity.getRelation(anotherEntity) == DiplomaticRelation.ENEMY;
        }
        return true;
    }


    void applyFarmSpending() {
        Hex bestPlaceForNewFarm = getBestPlaceForNewFarm();
        if (bestPlaceForNewFarm == null) return;
        gameController.fieldManager.buildFarm(currentProvince, bestPlaceForNewFarm);
        updateMoneyStats();
    }


    Hex getBestPlaceForNewFarm() {
        Hex bestHex = null;
        double maxAttractiveness = 0;
        for (Hex hex : currentProvince.hexList) {
            if (!isHexSuitableForNewFarm(hex)) continue;
            double currentAttractiveness = hex.aiData.attractiveness;
            if (hex.aiData.firstLine) {
                currentAttractiveness *= 0.5;
            }
            if (bestHex == null || currentAttractiveness > maxAttractiveness) {
                bestHex = hex;
                maxAttractiveness = currentAttractiveness;
            }
        }
        return bestHex;
    }


    void applyBuildUnitSpending(int strength) {
        Hex bestPlaceForNewUnit = getBestPlaceForNewUnit(strength);
        if (bestPlaceForNewUnit == null) return;
        buildUnit(bestPlaceForNewUnit, strength);
    }


    Hex getBestPlaceForNewUnit(int strength) {
        if (currentProvince.containsTrees()) {
            return getWorstTree();
        }

        Hex bestHexForPeacefulExpansion = getBestHexForPeacefulExpansion(adjacentNeutralLandsList, strength);
        if (bestHexForPeacefulExpansion != null && !bestHexForPeacefulExpansion.containsUnit()) {
            return bestHexForPeacefulExpansion;
        }

        Hex randomEmptyHexInFirstLine = getRandomEmptyHex(firstLine);
        if (randomEmptyHexInFirstLine != null) {
            return randomEmptyHexInFirstLine;
        }

        return getRandomEmptyHex(currentProvince.hexList);
    }


    Hex getRandomEmptyHex(ArrayList<Hex> list) {
        if (!doesListHaveEmptyHexes(list)) return null;

        while (true) {
            int index = YioGdxGame.random.nextInt(list.size());
            Hex hex = list.get(index);
            if (hex.isEmpty()) return hex;
        }
    }


    boolean doesListHaveEmptyHexes(ArrayList<Hex> list) {
        for (Hex hex : list) {
            if (hex.isEmpty()) return true;
        }
        return false;
    }


    Hex getWorstTree() {
        Hex resultHex = null;
        for (Hex hex : currentProvince.hexList) {
            if (!hex.containsTree()) continue;
            if (resultHex == null || hex.aiData.attractiveness > resultHex.aiData.attractiveness) {
                resultHex = hex;
            }
        }
        return resultHex;
    }


    Hex getBestHexForPeacefulExpansion(ArrayList<Hex> list, int strength) {
        if (list.size() == 0) return null;

        tempHexList.clear();
        tempHexList.addAll(list);
        filterTempHexListByMaxDefenseNumber(strength - 1);
        if (tempHexList.size() == 0) return null;

        Hex closestHex = getClosestToCapitalHex(tempHexList);
        Hex capital = currentProvince.getCapital();
        if (closestHex == capital) return null;

        double minDistance = closestHex.pos.fastDistanceTo(capital.pos);
        filterTempHexListByMaxDistanceToCapital(3 * minDistance);

        return getMostCurrentlyAttractiveHex(tempHexList);
    }


    void filterTempHexListByMaxDefenseNumber(int maxAllowedDefenseNumber) {
        for (int i = tempHexList.size() - 1; i >= 0; i--) {
            Hex hex = tempHexList.get(i);
            if (hex.getDefenseNumber() <= maxAllowedDefenseNumber) continue;
            tempHexList.remove(hex);
        }
    }


    void filterTempHexListByMaxDistanceToCapital(double maxAllowedDistance) {
        Hex capital = currentProvince.getCapital();
        for (int i = tempHexList.size() - 1; i >= 0; i--) {
            Hex hex = tempHexList.get(i);
            double distance = hex.pos.fastDistanceTo(capital.pos);
            if (distance < maxAllowedDistance) continue;
            tempHexList.remove(hex);
        }
        if (tempHexList.size() == 0) {
            System.out.println("AiMaster.filterTempHexListByMaxDistanceToCapital: problem");
            updateLastState();
            exportLastStateStringToClipboard();
        }
    }


    Hex getMostCurrentlyAttractiveHex(ArrayList<Hex> list) {
        Hex bestHex = null;
        double maxCurrentAttractivenes = 0;
        for (Hex hex : list) {
            if (hex.aiData.currentlyOwned) continue;
            double currentAttractivenes = calculateCurrentAttractivenes(hex);
            if (bestHex == null || currentAttractivenes > maxCurrentAttractivenes) {
                bestHex = hex;
                maxCurrentAttractivenes = currentAttractivenes;
            }
        }
        return bestHex;
    }


    Hex getClosestToCapitalHex(ArrayList<Hex> list) {
        Hex bestHex = null;
        double minDistance = 0;
        Hex capital = currentProvince.getCapital();
        for (Hex hex : list) {
            double currentDistance = hex.pos.fastDistanceTo(capital.pos);
            if (bestHex == null || currentDistance < minDistance) {
                bestHex = hex;
                minDistance = currentDistance;
            }
        }
        return bestHex;
    }


    double calculateCurrentAttractivenes(Hex hex) {
        if (hex == null) return 0;
        if (hex.numberOfFriendlyHexesNearby() > 3) return 1;
        if (hasOwnedTowerNearby(hex)) {
            return hex.aiData.attractiveness + 0.4;
        }
        return hex.aiData.attractiveness + 0.2 * hex.aiData.vicinity;
    }


    boolean hasOwnedTowerNearby(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!adjacentHex.aiData.currentlyOwned) continue;
            if (!adjacentHex.containsTower()) continue;
            return true;
        }
        return false;
    }


    void buildUnit(Hex hex, int strength) {
        if (strength > 4) {
            System.out.println("AiMaster.buildUnit: problem");
            Yio.printStackTrace();
        }
        gameController.fieldManager.buildUnit(currentProvince, hex, strength);
        syncProvince();
        updateMoneyStats();
    }


    Unit mergeUnits(Unit kamikaze, Unit target) {
        if (kamikaze.strength + target.strength > 4) {
            System.out.println("AiMaster.mergeUnits: problem");
            return null;
        }
        Hex hex = target.currentHex;
        gameController.mergeUnits(hex, kamikaze, target);
        updateMoneyStats();
        return hex.unit;
    }


    PossibleSpending getBestPossibleSpending() {
        PossibleSpending bestSpending = null;
        for (PossibleSpending possibleSpending : possibleSpendings) {
            if (!possibleSpending.valid) continue;
            if (bestSpending == null || possibleSpending.thirst > bestSpending.thirst) {
                bestSpending = possibleSpending;
            }
        }
        return bestSpending;
    }


    void updateSpendingThirst(PossibleSpending spending) {
        spending.thirst = 0;
        switch (spending.psType) {
            default:
                break;
            case unit1:
                updateUnitSpendingThirst(spending);
                break;
            case unit2:
                if (profit < GameRules.TAX_UNIT_GENERIC_2) break;
                //
                break;
            case unit3:
                if (profit < GameRules.TAX_UNIT_GENERIC_3) break;
                //
                break;
            case unit4:
                if (profit < GameRules.TAX_UNIT_GENERIC_4) break;
                //
                break;
            case farm:
                updateFarmSpendingThirst(spending);
                break;
            case tower1:
                updateNormalTowerSpendingThirst(spending);
                break;
            case tower2:
                updateStrongTowerSpendingThirst(spending);
                break;
        }
    }


    private void updateStrongTowerSpendingThirst(PossibleSpending spending) {
        if (GameRules.slayRules) return;
        if (profit < GameRules.TAX_STRONG_TOWER) return;
        if (!hasNormalTowers()) return;
        if (money < GameRules.PRICE_STRONG_TOWER + 10) return;
        spending.thirst = 0.5 + 0.7 * countImportantNormalTowers();
    }


    private void updateNormalTowerSpendingThirst(PossibleSpending spending) {
        if (profit < GameRules.TAX_TOWER) return;
        if (hasHexThatReallyNeedsTower()) {
            spending.thirst = 2 + 3 * countHexesThatReallyNeedTower();
            return;
        }
        spending.thirst = 0.5 + countHexesSuitableForNewTowers(firstLine, 2) + 2 * countHexesSuitableForNewTowers(secondLine, 2);
    }


    private void updateFarmSpendingThirst(PossibleSpending spending) {
        if (GameRules.slayRules) return;
        if (profit > 120) return;
        if (isInBadSpot() && firstLine.size() == 0 && currentProvince.countUnits(-1) < 8) return;
        int targetQuantity = (int) Math.max(10, 0.7 * currentProvince.hexList.size());
        int targetProfit = 50;
        if (money > 2 * getCurrentFarmPrice()) {
            targetProfit = 80;
        }
        int profitOverkill = Math.max(0, profit - targetProfit);
        int farmsQuantity = currentProvince.countObjects(Obj.FARM);
        spending.thirst = 0.5 + targetQuantity - farmsQuantity - profitOverkill;
    }


    private void updateUnitSpendingThirst(PossibleSpending spending) {
        if (currentProvince.hexList.size() < 5) {
            spending.thirst = 2;
            return;
        }
        if (profit < GameRules.TAX_UNIT_GENERIC_1) return;
        double startingThirst = 3.5 - currentProvince.countUnits(1);
        double expandingThirst = 0.5 * adjacentNeutralLandsQuantity - currentProvince.countUnits(1);
        spending.thirst = Math.max(startingThirst, expandingThirst);
    }


    int predictTaxChangeFromMerge(int strength1, int strength2) {
        int change = 0;
        change -= getRuleset().getUnitTax(strength1);
        change -= getRuleset().getUnitTax(strength2);
        change += getRuleset().getUnitTax(strength1 + strength2);
        return change;
    }


    private Ruleset getRuleset() {
        return gameController.ruleset;
    }


    int countHexesThatReallyNeedTower() {
        int c = 0;
        for (Hex hex : firstLine) {
            if (!doesHexReallyNeedsTowers(hex)) continue;
            c++;
        }
        for (Hex hex : secondLine) {
            if (!doesHexReallyNeedsTowers(hex)) continue;
            c++;
        }
        return c;
    }


    boolean hasHexThatReallyNeedsTower() {
        for (Hex hex : firstLine) {
            if (doesHexReallyNeedsTowers(hex)) return true;
        }
        for (Hex hex : secondLine) {
            if (doesHexReallyNeedsTowers(hex)) return true;
        }
        return false;
    }


    boolean doesHexReallyNeedsTowers(Hex hex) {
        if (hex.getDefenseNumber() > 1) return false;
        if (hex.aiData.importance < 3) return false;
        return true;
    }


    int countImportantNormalTowers() {
        int c = 0;
        for (Hex hex : currentProvince.hexList) {
            if (hex.objectInside != Obj.TOWER) continue;
            if (!isImportantEnoughForStrongTower(hex)) continue;
            c++;
        }
        return c;
    }


    boolean isImportantEnoughForStrongTower(Hex hex) {
        if (hex.hasThisSupportiveObjectNearby(Obj.STRONG_TOWER)) return false;
        if (!hex.aiData.firstLine && !hex.aiData.secondLine) return false;
        return hex.aiData.importance > 1.5;
    }


    boolean hasNormalTowers() {
        for (Hex hex : currentProvince.hexList) {
            if (hex.objectInside == Obj.TOWER) return true;
        }
        return false;
    }


    int getCurrentFarmPrice() {
        return currentProvince.getCurrentFarmPrice();
    }


    PossibleSpending getSpending(PsType psType) {
        return possibleSpendings[psType.ordinal()];
    }


    int countHexesSuitableForNewTowers(ArrayList<Hex> list, int targetDefense) {
        int c = 0;
        for (Hex hex : list) {
            if (!isHexSuitableForNewTower(hex, targetDefense)) continue;
            c++;
        }
        return c;
    }


    boolean isHexSuitableForNewTower(Hex hex, int targetDefense) {
        if (!hex.isEmpty()) return false;
        if (hex.aiData.solidDefense >= targetDefense) return false;
        if (hex.getDefenseNumber() < 2 && hex.aiData.firstLine) return true;
//        return getDefenseGainPrediction(hex, targetDefense) >= 3;
        return getBalancerDefenseGainPrediction(hex) >= 5;
    }


    boolean canAffordTaxChange(int taxChange) {
        if (money > 500) {
            return taxChange < profit + 50;
        }
        if (money > 200) {
            return taxChange < profit + 20;
        }
        return taxChange < profit - 7;
    }


    protected int getDefenseGainPrediction(Hex hex, int targetDefense) {
        // this method is worse than balancer gain prediction
        int c = 0;

        if (hex.aiData.solidDefense < targetDefense) {
            c++;
        }

        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) {
                c++;
                continue;
            }
            if (!adjacentHex.sameFraction(hex)) continue;
            if (hasSupportiveTowerNearby(adjacentHex)) {
                c--;
            }
            if (adjacentHex.aiData.importance > 0) {
                c += adjacentHex.aiData.importance / 2;
            }
            if (adjacentHex.aiData.solidDefense >= targetDefense) continue;
            c++;
        }

        return c;
    }


    void pushUnitToBetterDefense(Unit unit) {
        if (!unit.isReadyToMove()) return;

        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = unit.currentHex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!adjacentHex.sameFraction(unit.currentHex)) continue;
            if (!adjacentHex.isFree()) continue;

            if (predictDefenseGainWithUnit(adjacentHex, unit) < 3) continue;

            sendUnitDirectly(unit, adjacentHex);
            break;
        }
    }


    protected int predictDefenseGainWithUnit(Hex hex, Unit unit) {
        int defenseChange = 0;

        defenseChange -= hex.getDefenseNumber();
        defenseChange += unit.strength;

        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = unit.currentHex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!adjacentHex.sameFraction(unit.currentHex)) continue;
            defenseChange -= adjacentHex.getDefenseNumber();
            defenseChange += unit.strength;
        }

        return defenseChange;
    }


    protected int getBalancerDefenseGainPrediction(Hex hex) {
        int c = 0;

        if (hex.active && !hex.isDefendedByTower()) c++;

        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && hex.sameFraction(adjHex) && !adjHex.isDefendedByTower()) c++;
            if (adjHex.containsTower()) c--;
        }

        return c;
    }


    private boolean hasSupportiveTowerNearby(Hex hex) {
        if (hex.hasThisSupportiveObjectNearby(Obj.TOWER)) return true;
        if (hex.hasThisSupportiveObjectNearby(Obj.STRONG_TOWER)) return true;
        return false;
    }


    boolean isSpendingValid(PsType psType) {
        switch (psType) {
            default:
                return true;
            case unit1:
                return money >= GameRules.PRICE_UNIT;
            case unit2:
                return money >= 2 * GameRules.PRICE_UNIT;
            case unit3:
                return money >= 3 * GameRules.PRICE_UNIT;
            case unit4:
                return money >= 4 * GameRules.PRICE_UNIT;
            case farm:
                if (GameRules.slayRules) return false;
                return currentProvince.hasMoneyForFarm() && doesProvinceHaveHexForNewFarm();
            case tower1:
                return currentProvince.hasMoneyForTower() && doesProvinceHaveEmptyHex();
            case tower2:
                if (GameRules.slayRules) return false;
                return currentProvince.hasMoneyForStrongTower() && (doesProvinceHaveEmptyHex() || currentProvince.countObjects(Obj.TOWER) > 0);
        }
    }


    boolean doesProvinceHaveHexForNewFarm() {
        for (Hex hex : currentProvince.hexList) {
            if (isHexSuitableForNewFarm(hex)) return true;
        }
        return false;
    }


    boolean isHexSuitableForNewFarm(Hex hex) {
        if (!hex.isEmpty()) return false;
        if (currentProvince.hexList.size() >= 10 && hex.aiData.firstLine) return false;
        return hex.hasThisSupportiveObjectNearby(Obj.TOWN) || hex.hasThisSupportiveObjectNearby(Obj.FARM);
    }


    boolean doesProvinceHaveEmptyHex() {
        for (Hex hex : currentProvince.hexList) {
            if (hex.isEmpty()) return true;
        }
        return false;
    }


    void updateAttractiveness() {
        for (Hex hex : getActiveHexes()) {
            tempValue = 0;
            tempCounter = 0;
            casterAttractiveness.perform(hex, 1);
            hex.aiData.attractiveness = tempValue / tempCounter;
        }
    }


    void updateLoneliness() {
        double maxValue = 0;
        for (Hex activeHex : getActiveHexes()) {
            tempCounter = 0;
            casterLoneliness.perform(activeHex, 3);
            activeHex.aiData.loneliness = tempCounter;
            if (tempCounter > maxValue) {
                maxValue = tempCounter;
            }
        }

        for (Hex activeHex : getActiveHexes()) {
            activeHex.aiData.loneliness /= maxValue;
            activeHex.aiData.loneliness = 1 - activeHex.aiData.loneliness;
        }
    }


    ArrayList<Hex> getActiveHexes() {
        return fieldManager.activeHexes;
    }


    private void clearViewValues() {
        for (Hex hex : getActiveHexes()) {
            hex.aiData.setViewValue("");
        }
    }
}
