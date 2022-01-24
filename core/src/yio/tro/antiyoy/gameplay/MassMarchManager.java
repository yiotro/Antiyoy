package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;

public class MassMarchManager {

    FieldManager fieldManager;
    ArrayList<Unit> chosenUnits;
    ArrayList<Hex> propagationList;
    private Hex target;


    public MassMarchManager(FieldManager fieldManager) {
        this.fieldManager = fieldManager;
        chosenUnits = new ArrayList<>();
        propagationList = new ArrayList<>();
    }


    public void clearChosenUnits() {
        chosenUnits.clear();
    }


    public void addChosenUnit(Unit unit) {
        chosenUnits.add(unit);
    }


    public void performMarch(Hex target) {
        this.target = target;
        buildLinks();
        moveUnits();
    }


    private void moveUnits() {
        for (Unit unit : chosenUnits) {
            if (unit.currentHex.algoLink == null) continue;
            ArrayList<Hex> moveZone = fieldManager.moveZoneManager.detectMoveZone(unit.currentHex, unit.strength, GameRules.UNIT_MOVE_LIMIT);
            Hex bestHexToGo = findBestHexToGo(moveZone, unit.getFraction());
            if (bestHexToGo == null) continue;
            moveUnit(unit, bestHexToGo);
        }
    }


    private Hex findBestHexToGo(ArrayList<Hex> moveZone, int fraction) {
        Hex bestHex = null;
        for (Hex hex : moveZone) {
            if (!hex.isEmpty() && !hex.containsTree()) continue;
            if (hex.fraction != fraction) continue;
            if (bestHex == null || hex.algoValue < bestHex.algoValue) {
                bestHex = hex;
            }
        }
        return bestHex;
    }


    private void buildLinks() {
        prepareHexes();
        addToPropagationList(target, target);
        while (propagationList.size() > 0) {
            Hex hex = propagationList.get(0);
            propagationList.remove(0);
            iterate(hex);
        }
    }


    private void iterate(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (adjacentHex.isNullHex()) continue;
            if (!adjacentHex.active) continue;
            if (adjacentHex.fraction != hex.fraction) continue;
            if (adjacentHex.algoLink != null) continue;
            addToPropagationList(adjacentHex, hex);
        }
    }


    private void addToPropagationList(Hex child, Hex parent) {
        propagationList.add(child);
        child.algoLink = parent;

        if (parent != child) {
            child.algoValue = parent.algoValue + 1;
        } else {
            child.algoValue = 0;
        }
    }


    private void prepareHexes() {
        for (Hex activeHex : fieldManager.activeHexes) {
            activeHex.algoLink = null;
            activeHex.algoValue = 0;
        }
    }


    public void performForSingleUnit(Unit unit, Hex target) {
        clearChosenUnits();
        addChosenUnit(unit);
        performMarch(target);
    }


    void moveUnit(Unit unit, Hex hex) {
        Province provinceByHex = fieldManager.getProvinceByHex(unit.currentHex);
        fieldManager.gameController.moveUnit(unit, hex, provinceByHex);
    }
}
