package yio.tro.antiyoy.ai.master;

import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.rules.Ruleset;

import java.util.ArrayList;

public class AttackManager {

    AiMaster aiMaster;
    ArrayList<Unit> readyUnits;
    PropagationCaster casterArmyPresense;
    double tempValue;
    public ArrayList<Hex> firstAttackLine;
    public ArrayList<Hex> secondAttackLine;
    ArrayList<Province> nearbyProvinces;
    Hex mostTastefulHex;
    Hex entryHex;
    ArrayList<Hex> readyArea;
    PropagationCaster casterReadyArea;
    PropagationCaster casterPotentialAttackers;
    Unit tempUnit;
    ArrayList<Unit> tempUnitList;
    Hex magnetHex;
    ArrayList<Hex> pattern;
    ArrayList<Unit> reachableUnits;
    PropagationCaster casterReachableUnits;
    PropagationCaster casterCheckPotentialValidity;
    Province mostTastefulProvince;
    ArrayList<Hex> slice;
    ArrayList<Hex> tempHexList;
    PropagationCaster casterSlice;
    boolean cantCover;


    public AttackManager(AiMaster aiMaster) {
        this.aiMaster = aiMaster;
        readyUnits = new ArrayList<>();
        firstAttackLine = new ArrayList<>();
        secondAttackLine = new ArrayList<>();
        nearbyProvinces = new ArrayList<>();
        readyArea = new ArrayList<>();
        tempUnitList = new ArrayList<>();
        pattern = new ArrayList<>();
        reachableUnits = new ArrayList<>();
        slice = new ArrayList<>();
        tempHexList = new ArrayList<>();
        initCasters();
    }


    private void initCasters() {
        casterArmyPresense = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return src.sameFraction(dst);
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                hex.aiData.armyPresense = Math.max(tempValue * hex.aiData.propCastValue, hex.aiData.armyPresense);
            }
        };

        casterReadyArea = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return dst.aiData.currentlyOwned;
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                readyArea.add(hex);
            }
        };

        casterPotentialAttackers = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return true;
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                if (hex.fraction == getFraction()) return;
                if (!pattern.contains(hex)) return;
                hex.aiData.potentialAttackers.add(tempUnit);
            }
        };

        casterReachableUnits = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return dst.aiData.currentlyOwned;
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                if (!hex.containsUnit()) return;
                reachableUnits.add(hex.unit);
            }
        };

        casterCheckPotentialValidity = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return src.aiData.currentlyOwned;
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                hex.aiData.reached = true;
            }
        };

        casterSlice = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return dst.sameFraction(src);
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                if (slice.contains(hex)) return;
                slice.add(hex);
            }
        };
    }


    void onTurnStarted() {
        cantCover = false;
    }


    public void update() {
        mostTastefulHex = null;
        updateReadyUnits();
        checkToGrabFreeLands();
        updateArmyPresense();
        updateAttackLines();
        updateNearbyProvinces();
        updateMostTastefulProvince();
        if (mostTastefulProvince == null) return;
        updateTastiness();
        updateMostTastefulHex();
    }


    public void perform() {
        update();
        if (mostTastefulHex == null) return;
        applyArmyPresenseToFirstLine();
        updateEntryHex();
        updateMagnetHex();
        updateReadyArea();
        updatePattern();
        updateReadyUnits();
        updatePotentialAttackers();
        if (!ensureCover()) {
            cantCover = true;
            return; // do not give up units too easily
        }
        applyPattern();
        checkForCasualGrab();
        pullUnitsToReadyArea();
    }


    boolean areAllOwnedHexesTaggedAsCurrentlyOwned() {
        return aiMaster.areAllOwnedHexesTaggedAsCurrentlyOwned();
    }


    public boolean ensureCover() {
        updatePathToCapital();
        updateTempHexListByPathToCapital(magnetHex);
        filterTempHexListByVulnerability();
        if (tempHexList.size() == 0) return true; // no vulnerable hexes
        Hex bestHexForCover = getBestHexForCover();
        if (bestHexForCover == null) return false; // unable to cover
        return applyCover(bestHexForCover);
    }


    boolean applyCover(Hex hex) {
        if (hex.containsUnit()) {
            hex.unit.setReadyToMove(false);
            return true;
        }
        updateReachableUnits(hex);
        if (tryToCoverHexWithUnit(hex, false)) return true;
        if (tryToCoverHexWithMoney(hex)) return true;
        if (tryToCoverHexWithUnit(hex, true)) return true;
        return false;
    }


    boolean tryToCoverHexWithMoney(Hex hex) {
        if (getCurrentProvince().money < GameRules.PRICE_UNIT) return false;

        int strength = 1;
        if (getCurrentProvince().money >= 2 * GameRules.PRICE_UNIT && canAffordTaxChange(getRuleset().getUnitTax(2))) {
            strength = 2;
        }

        aiMaster.buildUnit(hex, strength);
        hex.unit.setReadyToMove(false);
        return true;
    }


    boolean tryToCoverHexWithUnit(Hex hex, boolean useReadyArea) {
        Unit bestUnit = null;
        for (Unit unit : reachableUnits) {
            if (!unit.isReadyToMove()) continue;
            if (unit.currentHex.aiData.inReadyArea != useReadyArea) continue;
            if (bestUnit == null || unit.strength < bestUnit.strength) {
                bestUnit = unit;
            }
        }
        if (bestUnit == null) return false;
        sendUnitDirectly(bestUnit, hex);
        return true;
    }


    Hex getBestHexForCover() {
        Hex bestHex = null;
        int maxGain = 0;
        for (Hex hex : tempHexList) {
            if (!isHexAdjacentToWholeTempHexList(hex)) continue;
            int currentGain = aiMaster.getDefenseGainPrediction(hex, 1);
            if (bestHex == null || currentGain > maxGain) {
                bestHex = hex;
                maxGain = currentGain;
            }
        }
        return bestHex;
    }


    boolean isHexAdjacentToWholeTempHexList(Hex checkHex) {
        for (Hex hex : tempHexList) {
            if (hex == checkHex) continue;
            if (!hex.isAdjacentTo(checkHex)) return false;
        }
        return true;
    }


    private void filterTempHexListByVulnerability() {
        for (int i = tempHexList.size() - 1; i >= 0; i--) {
            Hex hex = tempHexList.get(i);
            if (isHexVulnerable(hex)) continue;
            tempHexList.remove(hex);
        }
    }


    boolean isHexVulnerable(Hex hex) {
        if (!hex.isEmpty()) return false;
        if (aiMaster.hasOwnedTowerNearby(hex)) return false;
        if (!hex.aiData.firstLine && !hex.aiData.secondLine) return false;
        return true;
    }


    void updateTempHexListByPathToCapital(Hex startHex) {
        tempHexList.clear();
        Hex currentHex = startHex;
        while (currentHex != null) {
            tempHexList.add(currentHex);
            currentHex = currentHex.aiData.referenceHex;
        }
    }


    private void updatePathToCapital() {
        Hex capital = getCurrentProvince().getCapital();
        for (Hex hex : getCurrentProvince().hexList) {
            hex.aiData.referenceHex = null;
        }
        aiMaster.casterPathIndex.perform(capital, 9999);
    }


    public double getThirst() {
        if (cantCover) return 0;

        update();
        if (mostTastefulHex == null) return 0;
        if (mostTastefulHex.aiData.tastiness < 0.25 && aiMaster.money < 60) return 0;

        if (aiMaster.countWholePerimeter() > aiMaster.adjacentNeutralLandsQuantity) {
            return 2;
        }

        return 0;
    }


    private void updateMostTastefulProvince() {
        mostTastefulProvince = null;
        Province smallNearbyProvince = getBestSmallNearbyProvince();
        if (smallNearbyProvince != null) {
            mostTastefulProvince = smallNearbyProvince;
            return;
        }

        Province closestProvince = getClosestToCapitalNearbyProvince();
        if (closestProvince == null) return;
        double minDistance = getDistanceToCapital(closestProvince);
        float hexSize = aiMaster.gameController.fieldManager.hexSize;
        double maxAllowedDistance = Math.max(1.5 * minDistance, 5 * hexSize);
        filterNearbyProvinces(maxAllowedDistance);

        double maxTastiness = 0;
        mostTastefulProvince = null;
        for (Province nearbyProvince : nearbyProvinces) {
            double currentTastiness = getProvinceTastinessSlow(nearbyProvince);
            if (mostTastefulProvince == null || currentTastiness > maxTastiness) {
                mostTastefulProvince = nearbyProvince;
                maxTastiness = currentTastiness;
            }
        }
    }


    double getProvinceTastinessSlow(Province province) {
        updateSlice(province);
        double averageDefense = getAverageDefense(slice);
        double defenseValue = 1 - averageDefense / 4;
        double farmsPercentage = getFarmsPercentage(slice);
        double averageAttractiveness = getAverageAttractiveness(slice);
        return (defenseValue + farmsPercentage + 2 * averageAttractiveness) / 4;
    }


    double getAverageAttractiveness(ArrayList<Hex> list) {
        double sum = 0;
        for (Hex hex : list) {
            sum += hex.aiData.attractiveness;
        }
        return sum / list.size();
    }


    double getFarmsPercentage(ArrayList<Hex> list) {
        double c = 0;
        for (Hex hex : list) {
            if (hex.objectInside != Obj.FARM) continue;
            c++;
        }
        return c / list.size();
    }


    void updateSlice(Province province) {
        slice.clear();
        updateTempHexListByAdjacentProvince(province);
        casterSlice.perform(tempHexList, 3);
    }


    void updateTempHexListByAdjacentProvince(Province province) {
        tempHexList.clear();
        for (Hex hex : province.hexList) {
            if (!hex.aiData.attack1) continue;
            tempHexList.add(hex);
        }
    }


    void filterNearbyProvinces(double maxAllowedDistance) {
        for (int i = nearbyProvinces.size() - 1; i >= 0; i--) {
            Province province = nearbyProvinces.get(i);
            if (getDistanceToCapital(province) <= maxAllowedDistance) continue;
            nearbyProvinces.remove(province);
        }
    }


    Province getClosestToCapitalNearbyProvince() {
        Province bestProvince = null;
        double minDistance = 0;
        for (Province nearbyProvince : nearbyProvinces) {
            double currentDistance = getDistanceToCapital(nearbyProvince);
            if (bestProvince == null || currentDistance < minDistance) {
                bestProvince = nearbyProvince;
                minDistance = currentDistance;
            }
        }
        return bestProvince;
    }


    Province getBestSmallNearbyProvince() {
        Province bestProvince = null;
        double minDefense = 0;
        for (Province nearbyProvince : nearbyProvinces) {
            if (!isMuchSmallerThanCurrentProvince(nearbyProvince)) continue;
            double currentDefense = getAverageDefense(nearbyProvince.hexList);
            if (bestProvince == null || currentDefense < minDefense) {
                bestProvince = nearbyProvince;
                minDefense = currentDefense;
            }
        }
        return bestProvince;
    }


    double getAverageDefense(ArrayList<Hex> list) {
        double sum = 9;
        for (Hex hex : list) {
            sum += hex.getDefenseNumber();
        }
        return sum / list.size();
    }


    boolean isMuchSmallerThanCurrentProvince(Province province) {
        return 6 * province.hexList.size() < getCurrentProvince().hexList.size();
    }


    double getDistanceToCapital(Province province) {
        Hex hex = getClosestToCapitalHex(province);
        return getCurrentProvince().getCapital().pos.fastDistanceTo(hex.pos);
    }


    Hex getClosestToCapitalHex(Province province) {
        double minDistance = 0;
        Hex closestHex = null;
        Hex capital = getCurrentProvince().getCapital();
        for (Hex hex : province.hexList) {
            double currentDistance = hex.pos.fastDistanceTo(capital.pos);
            if (closestHex == null || currentDistance < minDistance) {
                closestHex = hex;
                minDistance = currentDistance;
            }
        }
        return closestHex;
    }


    void updateReachableUnits(Hex hex) {
        reachableUnits.clear();
        casterReachableUnits.perform(hex, GameRules.UNIT_MOVE_LIMIT);
    }


    private void checkForCasualGrab() {
        aiMaster.checkForCasualGrab();
    }


    private void applyPattern() {
        for (Hex hex : pattern) {
            int defenseNumber = hex.getDefenseNumber();
            if (defenseNumber == 4) {
                if (GameRules.slayRules) continue;
                defenseNumber = 3;
            }
            if (!isHexAdjacentToOwnedLands(hex)) continue;
            filterPotentialAttackers(hex);
            if (tryToCaptureWithStrongEnoughUnit(hex, defenseNumber)) continue;
            if (tryToCaptureWithMerge(hex, defenseNumber)) continue;
            if (tryToCaptureWithReinforcement(hex, defenseNumber)) continue;
            tryToCaptureWithMoney(hex, defenseNumber);
        }
    }


    boolean tryToCaptureWithMoney(Hex hex, int defenseNumber) {
        int strength = defenseNumber + 1;
        int price = GameRules.PRICE_UNIT * strength;
        if (getCurrentProvince().money < price) return false;

        if (!isItAcceptableToAcquireUnitToAttack(hex, strength)) return false;

        aiMaster.buildUnit(hex, strength);
        return true;
    }


    boolean isItAcceptableToAcquireUnitToAttack(Hex hexToCapture, int strength) {
        int taxChange = getRuleset().getUnitTax(strength);
        return canAffordTaxChange(taxChange + 2);
    }


    boolean canAffordTaxChange(int taxChange) {
        return aiMaster.canAffordTaxChange(taxChange);
    }


    boolean tryToCaptureWithReinforcement(Hex hex, int defenseNumber) {
        if (hex.aiData.potentialAttackers.size() == 0) return false;
        int strength = getMaxPotentialAttackerStrength(hex);
        Unit unit = getFurthestStrongEnoughPotentialAttacker(hex, strength - 1);
        if (unit == null) return false;
        if (unit.strength != strength) {
            say("AttackManager.tryToCaptureWithReinforcement: problem");
        }
        int additionalStrength = defenseNumber - strength + 1;
        int price = GameRules.PRICE_UNIT * additionalStrength;
        if (getCurrentProvince().money < price) return false;
        int taxChange = predictTaxChangeFromMerge(strength, additionalStrength);
        taxChange += getRuleset().getUnitTax(additionalStrength);
        if (!canAffordTaxChange(taxChange + 2)) return false;

        aiMaster.updateMoveZone(unit);
        Hex emptyOwnedHex = getEmptyOwnedHex(aiMaster.moveZone);
        if (emptyOwnedHex == null) return false;

        aiMaster.buildUnit(emptyOwnedHex, additionalStrength);
        Unit builtUnit = emptyOwnedHex.unit;
        Unit newUnit = mergeUnits(builtUnit, unit);
        sendUnitDirectly(newUnit, hex);
        return true;
    }


    Hex getEmptyOwnedHex(ArrayList<Hex> list) {
        for (Hex hex : list) {
            if (hex.fraction != getFraction()) continue;
            if (!hex.isEmpty()) continue;
            if (!hex.aiData.currentlyOwned) continue;
            return hex;
        }
        return null;
    }


    int getMaxPotentialAttackerStrength(Hex hex) {
        int maxStrength = 0;
        for (Unit unit : hex.aiData.potentialAttackers) {
            if (!unit.isReadyToMove()) continue;
            if (unit.strength > maxStrength) {
                maxStrength = unit.strength;
            }
        }
        return maxStrength;
    }


    boolean tryToCaptureWithMerge(Hex hex, int defenseNumber) {
        if (hex.aiData.potentialAttackers.size() == 0) return false;
        for (Unit unit : hex.aiData.potentialAttackers) {
            if (!unit.isReadyToMove()) continue;
            updateTempUnitListByPossibleMerge(unit);
            filterTempUnitListByStrength(defenseNumber - unit.strength + 1, 4 - unit.strength);
            filterTempUnitListByMergePermission(unit);
            if (tempUnitList.size() == 0) continue;
            int minStrength = getMinimumStrengthInTempUnitList();
            Unit kamikaze = getFurthestUnitFromTempList(hex, minStrength);
            Unit newUnit = mergeUnits(kamikaze, unit);
            sendUnitWithCheck(newUnit, hex);
            return true;
        }
        return false;
    }


    int getMinimumStrengthInTempUnitList() {
        if (tempUnitList.size() == 0) return -1;
        int minStrength = 99;
        for (Unit unit : tempUnitList) {
            if (unit.strength < minStrength) {
                minStrength = unit.strength;
            }
        }
        return minStrength;
    }


    Unit getFurthestUnitFromTempList(Hex hex, int strength) {
        Unit bestUnit = null;
        double maxDistance = 0;
        for (Unit unit : tempUnitList) {
            if (unit.strength != strength) continue;
            double currentDistance = hex.pos.fastDistanceTo(unit.currentHex.pos);
            if (bestUnit == null || currentDistance > maxDistance) {
                bestUnit = unit;
                maxDistance = currentDistance;
            }
        }
        return bestUnit;
    }


    void filterTempUnitListByMergePermission(Unit unitToMerge) {
        for (int i = tempUnitList.size() - 1; i >= 0; i--) {
            Unit unit = tempUnitList.get(i);
            int change = predictTaxChangeFromMerge(unit.strength, unitToMerge.strength);
            if (canAffordTaxChange(change + 2)) continue;
            tempUnitList.remove(unit);
        }
    }


    void filterTempUnitListByStrength(int minStrength, int maxStrength) {
        for (int i = tempUnitList.size() - 1; i >= 0; i--) {
            Unit unit = tempUnitList.get(i);
            if (unit.strength >= minStrength && unit.strength <= maxStrength) continue;
            tempUnitList.remove(unit);
        }
    }


    void updateTempUnitListByPossibleMerge(Unit targetUnit) {
        tempUnitList.clear();
        updateReachableUnits(targetUnit.currentHex);
        for (Unit unit : reachableUnits) {
            if (unit == targetUnit) continue;
            if (!unit.isReadyToMove()) continue;
            if (unit.strength + targetUnit.strength > 4) continue;
            tempUnitList.add(unit);
        }
    }


    int predictTaxChangeFromMerge(int strength1, int strength2) {
        return aiMaster.predictTaxChangeFromMerge(strength1, strength2);
    }


    private Ruleset getRuleset() {
        return aiMaster.gameController.ruleset;
    }


    boolean tryToCaptureWithStrongEnoughUnit(Hex hex, int defenseNumber) {
        if (hex.aiData.potentialAttackers.size() == 0) return false;
        if (!canHexBeCapturedWithStrongEnoughUnit(hex, defenseNumber)) return false;
        Unit unit = getFurthestStrongEnoughPotentialAttacker(hex, defenseNumber);
        if (unit == null) {
            say("AttackManager.tryToCaptureWithStrongEnoughUnit: problem");
        }
        sendUnitDirectly(unit, hex);
        return true;
    }


    Unit getFurthestStrongEnoughPotentialAttacker(Hex hex, int defenseNumber) {
        Unit bestUnit = null;
        double maxDistance = 0;
        for (Unit unit : hex.aiData.potentialAttackers) {
            if (!unit.isReadyToMove()) continue;
            if (unit.strength <= defenseNumber) continue;
            double currentDistance = hex.pos.fastDistanceTo(unit.currentHex.pos);
            if (bestUnit == null || currentDistance > maxDistance) {
                bestUnit = unit;
                maxDistance = currentDistance;
            }
        }
        return bestUnit;
    }


    boolean canHexBeCapturedWithStrongEnoughUnit(Hex hex, int defenseNumber) {
        for (Unit potentialAttacker : hex.aiData.potentialAttackers) {
            if (!potentialAttacker.isReadyToMove()) continue;
            if (potentialAttacker.strength > defenseNumber) return true;
        }
        return false;
    }


    void filterPotentialAttackers(Hex hex) {
        ArrayList<Unit> potentialAttackers = hex.aiData.potentialAttackers;
        for (int i = potentialAttackers.size() - 1; i >= 0; i--) {
            Unit unit = potentialAttackers.get(i);
            if (!unit.isReadyToMove()) {
                potentialAttackers.remove(unit);
                continue;
            }
            if (canUnitPotentiallyReachHex(unit, hex)) continue;
            potentialAttackers.remove(i);
        }
    }


    boolean canUnitPotentiallyReachHex(Unit unit, Hex hex) {
        hex.aiData.reached = false;
        casterCheckPotentialValidity.perform(unit.currentHex, GameRules.UNIT_MOVE_LIMIT);
        return hex.aiData.reached;
    }


    boolean isHexAdjacentToOwnedLands(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (adjacentHex.aiData.currentlyOwned) return true;
        }
        return false;
    }


    private void updatePotentialAttackers() {
        for (Hex hex : pattern) {
            hex.aiData.potentialAttackers.clear();
        }
        for (Unit unit : readyUnits) {
            if (!unit.currentHex.aiData.inReadyArea) continue;
            tempUnit = unit;
            casterPotentialAttackers.perform(unit.currentHex, GameRules.UNIT_MOVE_LIMIT);
        }
    }


    private int countAllUnits() {
        int c = 0;
        for (Hex hex : getCurrentProvince().hexList) {
            if (hex.containsUnit()) c++;
        }
        return c;
    }


    private void clearViewValues() {
        for (Hex hex : getActiveHexes()) {
            hex.aiData.setViewValue("");
        }
    }


    private void updatePattern() {
        pattern.clear();

        if (entryHex == mostTastefulHex) {
            pattern.add(entryHex);
            addSidesToPattern(magnetHex, entryHex);
            return;
        }

        pattern.add(entryHex);
        pattern.add(mostTastefulHex);
        addSidesToPattern(magnetHex, entryHex);
        addSidesToPattern(entryHex, mostTastefulHex);
    }


    void addSidesToPattern(Hex src, Hex dst) {
        int dir = detectDirection(src, dst);
        if (dir == -1) {
            say("AttackManager.addSidesToPattern: problem");
        }

        addSingleSide(src, limitDirection(dir - 1));
        addSingleSide(src, limitDirection(dir + 1));
        addSingleSide(src, limitDirection(dir - 2));
        addSingleSide(src, limitDirection(dir + 2));
    }


    void addSingleSide(Hex hex, int dir) {
        Hex adjacentHex = hex.getAdjacentHex(dir);
        if (!isWorkable(adjacentHex)) return;
        if (!adjacentHex.aiData.attack1 && !adjacentHex.aiData.attack2) return;
        if (pattern.contains(adjacentHex)) return;
        pattern.add(adjacentHex);
    }


    int detectDirection(Hex src, Hex dst) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = src.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (adjacentHex == dst) return dir;
        }
        return -1;
    }


    int limitDirection(int dir) {
        while (dir < 0) {
            dir += 6;
        }
        while (dir > 5) {
            dir -= 6;
        }
        return dir;
    }


    private void updateMagnetHex() {
        magnetHex = null;
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = entryHex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!adjacentHex.aiData.currentlyOwned) continue;
            if (magnetHex == null || adjacentHex.aiData.armyPresense > magnetHex.aiData.armyPresense) {
                magnetHex = adjacentHex;
            }
        }
    }


    private void pullUnitsToReadyArea() {
        updateReadyUnits();
        tempUnitList.clear();
        for (Unit unit : readyUnits) {
            if (unit.currentHex.aiData.inReadyArea) continue;
            tempUnitList.add(unit);
        }
        if (tempUnitList.size() == 0) return;

        MassMarchManager massMarchManager = getMassMarchManager();
        massMarchManager.clearChosenUnits();
        for (Unit unit : tempUnitList) {
            massMarchManager.addChosenUnit(unit);
        }
        massMarchManager.performMarch(magnetHex);
    }


    private int getFraction() {
        return aiMaster.getFraction();
    }


    private MassMarchManager getMassMarchManager() {
        return aiMaster.fieldManager.massMarchManager;
    }


    private void updateReadyArea() {
        readyArea.clear();
        readyArea.add(magnetHex);
        casterReadyArea.perform(magnetHex, GameRules.UNIT_MOVE_LIMIT - 1);

        for (Hex hex : getCurrentProvince().hexList) {
            hex.aiData.inReadyArea = false;
        }
        for (Hex hex : readyArea) {
            hex.aiData.inReadyArea = true;
        }
    }


    private void updateEntryHex() {
        if (mostTastefulHex.aiData.attack1) {
            entryHex = mostTastefulHex;
            return;
        }

        entryHex = null;
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = mostTastefulHex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!adjacentHex.aiData.attack1) continue;
            if (entryHex == null || adjacentHex.aiData.armyPresense > entryHex.aiData.armyPresense) {
                entryHex = adjacentHex;
            }
        }
    }


    private void applyArmyPresenseToFirstLine() {
        for (Hex hex : firstAttackLine) {
            double sum = 0;
            int c = 0;
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (!isWorkable(adjacentHex)) continue;
                if (!adjacentHex.aiData.currentlyOwned) continue;
                c++;
                sum += adjacentHex.aiData.armyPresense;
            }
            hex.aiData.armyPresense = sum / c;
        }
    }


    private void checkToGrabFreeLands() {
        if (!doesFirstAttackLineHaveFreeLands()) return;
        for (int i = readyUnits.size() - 1; i >= 0; i--) {
            Unit unit = readyUnits.get(i);
            if (unit.strength > 1) continue;
            tryToSendUnitForFreeLandGrab(unit);
        }
    }


    private void tryToSendUnitForFreeLandGrab(Unit unit) {
        aiMaster.updateMoveZone(unit);
        for (Hex mzHex : aiMaster.moveZone) {
            if (!mzHex.aiData.attack1) continue;
            if (mzHex.getDefenseNumber() > 0) continue;
            if (!canFractionBeAttacked(mzHex.fraction)) continue;
            sendUnitDirectly(unit, mzHex);
            return;
        }
    }


    void sendUnitWithCheck(Unit unit, Hex targetHex) {
        aiMaster.sendUnitWithCheck(unit, targetHex);
        readyUnits.remove(unit);
    }


    void sendUnitDirectly(Unit unit, Hex targetHex) {
        aiMaster.sendUnitDirectly(unit, targetHex);
        readyUnits.remove(unit);
    }


    Unit mergeUnits(Unit kamikaze, Unit target) {
        if (kamikaze.strength + target.strength > 4) {
            say("AttackManager.mergeUnits: problem");
            return null;
        }
        readyUnits.remove(kamikaze);
        readyUnits.remove(target);
        Unit newUnit = aiMaster.mergeUnits(kamikaze, target);
        kamikaze.setReadyToMove(false); // to filter out in potential attackers
        target.setReadyToMove(false);
        return newUnit;
    }


    private boolean doesFirstAttackLineHaveFreeLands() {
        for (Hex hex : firstAttackLine) {
            if (hex.getDefenseNumber() > 0) continue;
            return true;
        }
        return false;
    }


    private void updateMostTastefulHex() {
        mostTastefulHex = null;
        for (Hex hex : mostTastefulProvince.hexList) {
            if (!hex.aiData.attack1 && !hex.aiData.attack2) continue;
            if (mostTastefulHex == null || hex.aiData.tastiness > mostTastefulHex.aiData.tastiness) {
                mostTastefulHex = hex;
            }
        }
    }


    private void updateTastiness() {
        for (Hex hex : mostTastefulProvince.hexList) {
            if (hex.aiData.attack1) {
                hex.aiData.tastiness = 0.5 * calculateTastiness(hex);
            }
            if (hex.aiData.attack2) {
                hex.aiData.tastiness = calculateTastiness(hex);
            }
        }
    }


    private double calculateTastiness(Hex hex) {
        double v;
        Hex closestOwnedHex = getClosestOwnedHex(hex);
        if (closestOwnedHex == null) return 0;
        v = hex.aiData.attractiveness;
        v += 3 * closestOwnedHex.aiData.armyPresense;
        v += 1 - (hex.getDefenseNumber() / 4d);
        v += getSupposedImportance(hex);
        v += 2 * hex.aiData.vicinity;
        if (hex.containsTower()) {
            v += 1;
        }
        v /= 9;
        return v;
    }


    int countOwnedAdjacentHexes(Hex hex) {
        int c = 0;
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!adjacentHex.aiData.currentlyOwned) continue;
            c++;
        }
        return c;
    }


    private double getSupposedImportance(Hex hex) {
        double distanceToFarms = getDistanceToFarms(hex);
        float hexSize = aiMaster.gameController.fieldManager.hexSize;
        double distanceInSteps = distanceToFarms / hexSize;
        double x = 1 - distanceInSteps / 5;
        if (x < 0) {
            x = 0;
        }
        if (hex.containsTower()) {
            x *= 3;
        }
        if (x > 1) {
            x = 1;
        }
        return x;
    }


    private double getDistanceToFarms(Hex srcHex) {
        Province provinceByHex = getProvinceByHex(srcHex);
        if (provinceByHex == null) return 0;
        Hex closestFarmHex = null;
        double minDistance = 0;
        for (Hex hex : provinceByHex.hexList) {
            if (hex.isEmpty()) continue;
            if (hex.objectInside != Obj.FARM) continue;
            double currentDistance = hex.pos.fastDistanceTo(srcHex.pos);
            if (closestFarmHex == null || currentDistance < minDistance) {
                closestFarmHex = hex;
                minDistance = currentDistance;
            }
        }
        return minDistance;
    }


    private Hex getClosestOwnedHex(Hex srcHex) {
        Hex resultHex = null;
        double minDistance = 0;
        for (Hex hex : getCurrentProvince().hexList) {
            double currentDistance = hex.pos.fastDistanceTo(srcHex.pos);
            if (resultHex == null || currentDistance < minDistance) {
                resultHex = hex;
                minDistance = currentDistance;
            }
        }
        return resultHex;
    }


    private void updateNearbyProvinces() {
        nearbyProvinces.clear();
        for (Hex hex : firstAttackLine) {
            Province provinceByHex = getProvinceByHex(hex);
            if (provinceByHex == null) continue;
            if (nearbyProvinces.contains(provinceByHex)) continue;
            nearbyProvinces.add(provinceByHex);
        }
    }


    public Province getProvinceByHex(Hex hex) {
        FieldManager fieldManager = aiMaster.gameController.fieldManager;
        for (Province province : fieldManager.provinces) {
            if (!hex.sameFraction(province)) continue;
            if (!province.containsHex(hex)) continue;
            return province;
        }
        return null;
    }


    private void updateAttackLines() {
        resetAttackLines();
        aiMaster.updatePerimeter();

        for (Hex hex : aiMaster.firstLine) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (!isWorkable(adjacentHex)) continue;
                if (adjacentHex.sameFraction(hex)) continue;
                if (adjacentHex.isNeutral()) continue;
                if (!canFractionBeAttacked(adjacentHex.fraction)) continue;
                adjacentHex.aiData.attack1 = true;
                firstAttackLine.add(adjacentHex);
            }
        }

        for (Hex hex : firstAttackLine) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (!isWorkable(adjacentHex)) continue;
                if (!adjacentHex.sameFraction(hex)) continue;
                if (adjacentHex.aiData.attack1) continue;
                adjacentHex.aiData.attack2 = true;
                secondAttackLine.add(adjacentHex);
            }
        }
    }


    boolean canFractionBeAttacked(int anotherFraction) {
        return aiMaster.canFractionBeAttacked(anotherFraction);
    }


    private void resetAttackLines() {
        firstAttackLine.clear();
        secondAttackLine.clear();
        for (Hex hex : getActiveHexes()) {
            hex.aiData.attack1 = false;
            hex.aiData.attack2 = false;
        }
    }


    private void updateReadyUnits() {
        readyUnits.clear();
        for (Hex hex : getCurrentProvince().hexList) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (!unit.isReadyToMove()) continue;
            readyUnits.add(hex.unit);
        }
    }


    private void updateArmyPresense() {
        for (Hex hex : getCurrentProvince().hexList) {
            hex.aiData.armyPresense = 0;
        }
        int depth = 5;
        for (Unit readyUnit : readyUnits) {
            tempValue = readyUnit.strength / (4d * depth); // 4d because max strength is 4
            Hex hex = readyUnit.currentHex;
            hex.aiData.armyPresense = Math.max(tempValue * depth, hex.aiData.armyPresense);
            casterArmyPresense.perform(hex, depth);
        }
    }


    Province getCurrentProvince() {
        return aiMaster.currentProvince;
    }


    boolean isWorkable(Hex hex) {
        return aiMaster.isWorkable(hex);
    }


    private ArrayList<Hex> getActiveHexes() {
        return aiMaster.fieldManager.activeHexes;
    }


    void say(String string) {
        aiMaster.say(string);
    }
}
