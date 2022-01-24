package yio.tro.antiyoy.ai.master;

import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.MassMarchManager;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.Unit;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.rules.Ruleset;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.Arrays;

public class DefenseManager {

    AiMaster aiMaster;
    ArrayList<Unit> tempUnitList;
    ArrayList<DmGroup> groups;
    ObjectPoolYio<DmGroup> poolGroups;
    PropagationCaster casterStartGroup;
    DmGroup tempGroup;
    ArrayList<Hex> entryList;
    ArrayList<Unit> readyUnits;
    double tempValue;
    PropagationCaster casterArmyPresense;
    Hex entryHex;
    ArrayList<Hex> pattern;
    PropagationCaster casterPattern;
    PropagationCaster casterPotentialAttackers;
    Unit tempUnit;
    PropagationCaster casterPotentialCapture;
    PropagationCaster casterDirectFight;
    boolean failure;
    PropagationCaster casterRemoveFromTempUnitList;
    ArrayList<Unit> reachableUnits;
    PropagationCaster casterReachableUnits;


    public DefenseManager(AiMaster aiMaster) {
        this.aiMaster = aiMaster;
        groups = new ArrayList<>();
        tempUnitList = new ArrayList<>();
        entryList = new ArrayList<>();
        readyUnits = new ArrayList<>();
        pattern = new ArrayList<>();
        reachableUnits = new ArrayList<>();
        initPools();
        initCasters();
    }


    private void initCasters() {
        casterStartGroup = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return src.sameFraction(dst) && dst.containsUnit();
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                tempGroup.areaList.add(hex);
            }
        };

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

        casterPattern = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return tempGroup.supportLands.contains(dst);
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                pattern.add(hex);
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

        casterPotentialCapture = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                if (pattern.contains(dst) && dst.aiData.canBeCaptured) return true;
                if (dst.fraction == getFraction()) return true;
                return false;
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                if (hex.fraction != getFraction()) return;
                if (!hex.containsUnit()) return;
                Unit unit = hex.unit;
                if (!unit.isReadyToMove()) return;
                if (unit.currentHex.aiData.unitPotentiallyUsed) return;
                if (tempUnitList.contains(unit)) return;
                tempUnitList.add(unit);
            }
        };

        casterDirectFight = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return dst.fraction == getFraction();
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                if (!hex.containsUnit()) return;
                Unit unit = hex.unit;
                if (!unit.isReadyToMove()) return;
                if (tempUnitList.contains(unit)) return;
                tempUnitList.add(unit);
            }
        };

        casterRemoveFromTempUnitList = new PropagationCaster(aiMaster) {
            @Override
            public boolean isPropagationAllowed(Hex src, Hex dst) {
                return src.sameFraction(dst);
            }


            @Override
            public void onHexReached(Hex previousHex, Hex hex) {
                if (!hex.containsUnit()) return;
                Unit unit = hex.unit;
                if (!tempUnitList.contains(unit)) return;
                tempUnitList.remove(unit);
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
    }


    private void initPools() {
        poolGroups = new ObjectPoolYio<DmGroup>(groups) {
            @Override
            public DmGroup makeNewObject() {
                return new DmGroup();
            }
        };
    }


    void onTurnStarted() {
        update();
        failure = false;
    }


    void update() {
        updateTempUnitListByAdjacentHexes();
        updateGroups();
        analyzeGroups();
    }


    double getThirst() {
        if (failure) return 0;
        DmGroup mostDangerousGroup = getMostDangerousGroup();
        if (mostDangerousGroup == null) return 0;
        if (mostDangerousGroup.danger <= 0.45) return 0;
        return 1.5 + 5 * mostDangerousGroup.danger;
    }


    void perform() {
        DmGroup mostDangerousGroup = getMostDangerousGroup();
        if (mostDangerousGroup == null) return;

        boolean success = performForSingleGroup(mostDangerousGroup);
        if (!success) {
            failure = true;
            update();
            return;
        }

        checkForCasualGrab();
        update();
    }


    boolean performForSingleGroup(DmGroup group) {
        if (tryToCutOff(group)) return true;
        if (fightDirectly(group)) {
            if (isGroupStillAlive(group)) {
                bringReinforcementsCloser(group);
            }
            return true;
        }
        return false;
    }


    private boolean isGroupStillAlive(DmGroup group) {
        for (Hex hex : group.areaList) {
            if (hex.aiData.currentlyOwned) continue;
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (unit.strength == 1) continue;
            return true;
        }
        return false;
    }


    private void bringReinforcementsCloser(DmGroup group) {
        updateReadyUnits();
        Hex targetHex = getContactHexInMostDanger(group);

        tempUnitList.clear();
        tempUnitList.addAll(readyUnits);
        casterRemoveFromTempUnitList.perform(targetHex, GameRules.UNIT_MOVE_LIMIT - 1);
        if (targetHex.containsUnit() && tempUnitList.contains(targetHex.unit)) {
            tempUnitList.remove(targetHex.unit);
        }

        MassMarchManager massMarchManager = getMassMarchManager();
        massMarchManager.clearChosenUnits();
        for (Unit unit : tempUnitList) {
            massMarchManager.addChosenUnit(unit);
        }
        massMarchManager.performMarch(targetHex);
    }


    Hex getContactHexInMostDanger(DmGroup group) {
        Hex bestHex = null;
        int maxStrengthNearby = 0;
        for (Hex hex : group.contactZone) {
            int currentStrengthNearby = calculateEnemyStrengthNearby(hex, group.fraction);
            if (bestHex == null || currentStrengthNearby > maxStrengthNearby) {
                bestHex = hex;
                maxStrengthNearby = currentStrengthNearby;
            }
        }
        return bestHex;
    }


    int calculateEnemyStrengthNearby(Hex hex, int enemyFraction) {
        int c = 0;
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (adjacentHex.fraction != enemyFraction) continue;
            if (!adjacentHex.containsUnit()) continue;
            c += adjacentHex.unit.strength;
        }
        return c;
    }


    private boolean fightDirectly(DmGroup group) {
        boolean success = false;
        int c = 0;
        while (true) {
            c++;
            if (c == 1000) {
                System.out.println("DefenseManager.fightDirectly");
            }
            Unit enemyUnit = getStrongestAdjacentUnit(group);
            if (enemyUnit == null) break;
            if (enemyUnit.strength == 1) break;
            if (tryToFightUnitDirectly(enemyUnit)) {
                success = true;
                continue;
            }
            if (tryToFightUnitWithMerge(enemyUnit)) {
                success = true;
                continue;
            }
            if (tryToFightWithReinforcements(enemyUnit)) {
                success = true;
                continue;
            }
            if (tryToFightUnitWithMoney(enemyUnit)) {
                success = true;
                continue;
            }
            break;
        }
        return success;
    }


    boolean tryToFightWithReinforcements(Unit enemyUnit) {
        updateTempUnitListForDirectAttackOnUnit(enemyUnit);
        if (tempUnitList.size() == 0) return false;

        Unit strongestUnit = getStrongestUnitFromList(tempUnitList);
        int necessaryStrength = aiMaster.getStrengthNecessaryToCapture(enemyUnit.currentHex);
        int additionalStrength = necessaryStrength - strongestUnit.strength;

        int price = additionalStrength * GameRules.PRICE_UNIT;
        if (getCurrentProvince().money < price) return false;

        int taxChange = aiMaster.predictTaxChangeFromMerge(strongestUnit.strength, additionalStrength);
        taxChange += getRuleset().getUnitTax(additionalStrength);
        Hex ownedHex = getMostImportantAdjacentOwnedHex(enemyUnit.currentHex);
        boolean isReallyNeeded = doesHexReallyNeedDefense(ownedHex);
        if (!isReallyNeeded && !aiMaster.canAffordTaxChange(taxChange + 2)) return false;

        aiMaster.updateMoveZone(strongestUnit);
        Hex emptyOwnedHex = getEmptyOwnedHex(aiMaster.moveZone);
        if (emptyOwnedHex == null) return false;

        aiMaster.buildUnit(emptyOwnedHex, additionalStrength);
        Unit builtUnit = emptyOwnedHex.unit;
        Unit newUnit = aiMaster.mergeUnits(builtUnit, strongestUnit);
        aiMaster.sendUnitDirectly(newUnit, enemyUnit.currentHex);
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


    Unit getStrongestUnitFromList(ArrayList<Unit> list) {
        Unit bestUnit = null;
        for (Unit unit : list) {
            if (bestUnit == null || unit.strength > bestUnit.strength) {
                bestUnit = unit;
            }
        }
        return bestUnit;
    }


    boolean tryToFightUnitWithMerge(Unit enemyUnit) {
        updateTempUnitListForDirectAttackOnUnit(enemyUnit);
        if (tempUnitList.size() == 0) return false;

        ArrayList<Unit> potentialAttackers = enemyUnit.currentHex.aiData.potentialAttackers;
        potentialAttackers.clear();
        potentialAttackers.addAll(tempUnitList);

        Hex ownedHex = getMostImportantAdjacentOwnedHex(enemyUnit.currentHex);
        int necessaryStrength = aiMaster.getStrengthNecessaryToCapture(enemyUnit.currentHex);
        for (Unit unit : potentialAttackers) {
            if (!unit.isReadyToMove()) continue;
            int additionalStrength = necessaryStrength - unit.strength;
            updateTempUnitListByPossibleMerge(unit);
            filterTempUnitListByStrength(additionalStrength, 4 - unit.strength);
            filterTempUnitListByMergePermission(unit, ownedHex);
            if (tempUnitList.size() == 0) continue;
            int minStrength = getMinimumStrengthInTempUnitList();
            Unit kamikaze = getFurthestUnitFromTempList(enemyUnit.currentHex, minStrength);
            Unit newUnit = aiMaster.mergeUnits(kamikaze, unit);
            aiMaster.sendUnitDirectly(newUnit, enemyUnit.currentHex);
            return true;
        }
        return false;
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


    void filterTempUnitListByMergePermission(Unit unitToMerge, Hex ownedHex) {
        for (int i = tempUnitList.size() - 1; i >= 0; i--) {
            Unit unit = tempUnitList.get(i);
            int taxChange = aiMaster.predictTaxChangeFromMerge(unit.strength, unitToMerge.strength);
            boolean isReallyNeeded = doesHexReallyNeedDefense(ownedHex);
            if (isReallyNeeded || aiMaster.canAffordTaxChange(taxChange)) continue;
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


    void updateReachableUnits(Hex hex) {
        reachableUnits.clear();
        casterReachableUnits.perform(hex, GameRules.UNIT_MOVE_LIMIT);
    }


    boolean tryToFightUnitWithMoney(Unit enemyUnit) {
        if (enemyUnit.strength == 2) return false;

        Hex targetHex = enemyUnit.currentHex;
        int necessaryStrength = aiMaster.getStrengthNecessaryToCapture(targetHex);
        int price = GameRules.PRICE_UNIT * necessaryStrength;
        if (getCurrentProvince().money < price) return false;

        int taxChange = getRuleset().getUnitTax(necessaryStrength);
        Hex ownedHex = getMostImportantAdjacentOwnedHex(targetHex);
        boolean isReallyNeeded = doesHexReallyNeedDefense(ownedHex);
        if (!isReallyNeeded && !aiMaster.canAffordTaxChange(taxChange)) return false;

        aiMaster.buildUnit(targetHex, necessaryStrength);
        return true;
    }


    boolean doesHexReallyNeedDefense(Hex hex) {
        if (hex == null) return false;
        return hex.aiData.importance > 2.5;
    }


    Hex getMostImportantAdjacentOwnedHex(Hex hex) {
        Hex bestHex = null;
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!adjacentHex.aiData.currentlyOwned) continue;
            if (bestHex == null || adjacentHex.aiData.importance > bestHex.aiData.importance) {
                bestHex = adjacentHex;
            }
        }
        return bestHex;
    }


    boolean tryToFightUnitDirectly(Unit enemyUnit) {
        updateTempUnitListForDirectAttackOnUnit(enemyUnit);
        if (tempUnitList.size() == 0) return false;

        filterTempUnitListByAbilityToCapture(enemyUnit.currentHex);
        if (tempUnitList.size() == 0) return false;

        Unit unit = getFurthestUnitInTempUnitList(enemyUnit.currentHex);
        aiMaster.sendUnitDirectly(unit, enemyUnit.currentHex);
        return true;
    }


    private void updateTempUnitListForDirectAttackOnUnit(Unit enemyUnit) {
        tempUnitList.clear();
        casterDirectFight.perform(enemyUnit.currentHex, GameRules.UNIT_MOVE_LIMIT);
    }


    Unit getStrongestAdjacentUnit(DmGroup group) {
        Unit bestUnit = null;
        for (Hex hex : group.areaList) {
            if (hex.aiData.currentlyOwned) continue; // result of direct fight
            if (!hasOwnedLandsNearby(hex)) continue;
            Unit unit = hex.unit;
            if (unit == null) continue;
            if (bestUnit == null || unit.strength > bestUnit.strength) {
                bestUnit = unit;
            }
        }
        return bestUnit;
    }


    boolean hasOwnedLandsNearby(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (adjacentHex.aiData.currentlyOwned) return true;
        }
        return false;
    }


    boolean tryToCutOff(DmGroup group) {
        updateEntryList(group);
        if (entryList.size() == 0) return false;
        updateReadyUnits();
        updateArmyPresense();
        updateEntryHex();
        updatePattern(group);
        if (pattern.size() < group.supportLands.size()) return false; // can't be cut easily
        if (pattern.size() > 3) return false;

        updatePotentialAttackers();
        if (!canPatternBeCaptured()) return false;

        for (Hex hex : pattern) {
            Hex referenceHex = hex.aiData.referenceHex;
            if (referenceHex == null) {
                int strength = aiMaster.getStrengthNecessaryToCapture(hex);
                aiMaster.buildUnit(hex, strength);
            } else {
                Unit unit = referenceHex.unit;
                if (unit == null) return false;
                aiMaster.sendUnitWithCheck(unit, hex);
            }
        }
        return true;
    }


    boolean canPatternBeCaptured() {
        resetFlags();
        for (Hex hex : pattern) {
            updateTempUnitListByPotentialCapture(hex);
            filterTempUnitListByAbilityToCapture(hex);
            if (tempUnitList.size() == 0) break;
            Unit unit = getFurthestUnitInTempUnitList(hex);
            unit.currentHex.aiData.unitPotentiallyUsed = true;
            hex.aiData.canBeCaptured = true;
            hex.aiData.referenceHex = unit.currentHex;
        }
        int price = getPriceToFinishPotentialCapture();
        if (getCurrentProvince().money < price) return false;

        int additionalTax = getAdditionalTaxToFinishPotentialCapture();
        if (aiMaster.profit < additionalTax) return false;

        return true;
    }


    int getAdditionalTaxToFinishPotentialCapture() {
        int sum = 0;
        for (Hex hex : pattern) {
            if (hex.aiData.canBeCaptured) continue;
            int strength = aiMaster.getStrengthNecessaryToCapture(hex);
            int tax = getRuleset().getUnitTax(strength);
            sum += tax;
        }
        return sum;
    }


    int getPriceToFinishPotentialCapture() {
        int price = 0;
        for (Hex hex : pattern) {
            if (hex.aiData.canBeCaptured) continue;
            int strength = aiMaster.getStrengthNecessaryToCapture(hex);
            price += GameRules.PRICE_UNIT * strength;
        }
        return price;
    }


    Unit getFurthestUnitInTempUnitList(Hex hex) {
        Unit bestUnit = null;
        double maxDistance = 0;
        for (Unit unit : tempUnitList) {
            double currentDistance = unit.currentHex.pos.fastDistanceTo(hex.pos);
            if (bestUnit == null || currentDistance > maxDistance) {
                bestUnit = unit;
                maxDistance = currentDistance;
            }
        }
        return bestUnit;
    }


    void filterTempUnitListByAbilityToCapture(Hex hex) {
        int necessaryStrength = aiMaster.getStrengthNecessaryToCapture(hex);
        if (necessaryStrength == -1) {
            tempUnitList.clear();
            return;
        }
        for (int i = tempUnitList.size() - 1; i >= 0; i--) {
            Unit unit = tempUnitList.get(i);
            if (unit.strength >= necessaryStrength) continue;
            tempUnitList.remove(unit);
        }
    }


    void updateTempUnitListByPotentialCapture(Hex hex) {
        tempUnitList.clear();
        casterPotentialCapture.perform(hex, GameRules.UNIT_MOVE_LIMIT);
    }


    private void resetFlags() {
        for (Unit unit : readyUnits) {
            unit.currentHex.aiData.unitPotentiallyUsed = false;
        }
        for (Hex hex : pattern) {
            hex.aiData.canBeCaptured = false;
            hex.aiData.referenceHex = null;
        }
    }


    private void updatePotentialAttackers() {
        for (Hex hex : pattern) {
            hex.aiData.potentialAttackers.clear();
        }
        for (Unit unit : readyUnits) {
            tempUnit = unit;
            casterPotentialAttackers.perform(unit.currentHex, GameRules.UNIT_MOVE_LIMIT);
        }
    }


    private void updatePattern(DmGroup group) {
        pattern.clear();
        tempGroup = group;
        casterPattern.perform(entryHex, 999);
    }


    private void updateEntryHex() {
        entryHex = null;
        for (Hex hex : entryList) {
            if (entryHex == null || hex.aiData.armyPresense > entryHex.aiData.armyPresense) {
                entryHex = hex;
            }
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


    private void updateEntryList(DmGroup group) {
        entryList.clear();
        for (Hex hex : group.supportLands) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (!isWorkable(adjacentHex)) continue;
                if (!adjacentHex.aiData.currentlyOwned) continue;
                if (entryList.contains(adjacentHex)) continue;
                entryList.add(adjacentHex);
            }
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


    DmGroup getMostDangerousGroup() {
        DmGroup bestGroup = null;
        for (DmGroup group : groups) {
            if (bestGroup == null || group.danger > bestGroup.danger) {
                bestGroup = group;
            }
        }
        return bestGroup;
    }


    private void checkForCasualGrab() {
        aiMaster.checkForCasualGrab();
    }


    private void updateGroups() {
        poolGroups.clearExternalList();
        for (Unit unit : tempUnitList) {
            startGroup(unit);
        }
    }


    private void analyzeGroups() {
        for (DmGroup group : groups) {
            analyzeSingleGroup(group);
        }
    }


    private void analyzeSingleGroup(DmGroup group) {
        group.fraction = group.areaList.get(0).fraction;
        group.maxStrength = calculateMaxStrength(group);
        group.averageStrength = calculateAverageStrength(group);
        updateContactZone(group);
        updateSupportLands(group);
        group.danger = calculateDanger(group);
    }


    void updateSupportLands(DmGroup group) {
        ArrayList<Hex> supportLands = group.supportLands;
        supportLands.clear();
        for (Hex hex : group.areaList) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (!isWorkable(adjacentHex)) continue;
                if (adjacentHex.fraction != group.fraction) continue;
                if (adjacentHex.containsUnit()) continue;
                if (supportLands.contains(adjacentHex)) continue;
                supportLands.add(adjacentHex);
            }
        }
    }


    void updateContactZone(DmGroup group) {
        ArrayList<Hex> contactZone = group.contactZone;
        contactZone.clear();
        for (Hex hex : group.areaList) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (!isWorkable(adjacentHex)) continue;
                if (adjacentHex.fraction != getFraction()) continue;
                if (contactZone.contains(adjacentHex)) continue;
                contactZone.add(adjacentHex);
            }
        }
    }


    double calculateDanger(DmGroup group) {
        if (group.supportLands.size() == 0) return 0; // already cut off

        double k = 0.25;

        int strongestUnitsQuantity = countUnitsByStrength(group, group.maxStrength);
        if (strongestUnitsQuantity == 1) {
            k *= 0.8;
        }

        double averageImportance = calculateAverageImportanceInContactZone(group);
        if (averageImportance < 1) {
            k *= 0.9;
        }

        if (group.areaList.size() == 1 && countOwnedAdjacentHexes(group.areaList.get(0)) == 1) {
            k *= 0.66;
        }

        return k * group.maxStrength;
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


    double calculateAverageImportanceInContactZone(DmGroup group) {
        double sum = 0;
        for (Hex hex : group.contactZone) {
            sum += hex.aiData.importance;
        }
        return sum / group.contactZone.size();
    }


    int countUnitsByStrength(DmGroup group, int strength) {
        int c = 0;
        for (Hex hex : group.areaList) {
            if (hex.unit.strength != strength) continue;
            c++;
        }
        return c;
    }


    double calculateAverageStrength(DmGroup group) {
        double sum = 0;
        for (Hex hex : group.areaList) {
            sum += hex.unit.strength;
        }
        return sum / group.areaList.size();
    }


    int calculateMaxStrength(DmGroup group) {
        int maxStrength = 0;
        for (Hex hex : group.areaList) {
            int strength = hex.unit.strength;
            if (strength > maxStrength) {
                maxStrength = strength;
            }
        }
        return maxStrength;
    }


    DmGroup getGroupByUnit(Unit unit) {
        for (DmGroup group : groups) {
            if (!group.contains(unit)) continue;
            return group;
        }
        return null;
    }


    private void startGroup(Unit unit) {
        if (getGroupByUnit(unit) != null) return;

        tempGroup = poolGroups.getFreshObject();
        tempGroup.areaList.add(unit.currentHex);

        casterStartGroup.perform(unit.currentHex, 9999);
    }


    private void updateTempUnitListByAdjacentHexes() {
        tempUnitList.clear();
        for (Hex hex : aiMaster.firstLine) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (!isWorkable(adjacentHex)) continue;
                if (adjacentHex.aiData.currentlyOwned) continue;
                if (adjacentHex.isNeutral()) continue;
                if (!adjacentHex.containsUnit()) continue;
                if (!aiMaster.canFractionBeAttacked(adjacentHex.fraction)) continue;
                Unit unit = adjacentHex.unit;
                if (tempUnitList.contains(unit)) continue;
                tempUnitList.add(unit);
            }
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


    private int getFraction() {
        return aiMaster.getFraction();
    }


    private MassMarchManager getMassMarchManager() {
        return aiMaster.fieldManager.massMarchManager;
    }


    private Ruleset getRuleset() {
        return aiMaster.gameController.ruleset;
    }

}
