package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.gameplay.touch_mode.TouchMode;
import yio.tro.antiyoy.stuff.PointYio;

import java.util.ArrayList;

public class AutomaticTransitionWorker {

    FieldManager fieldManager;
    boolean waitMode;
    int waitCounter;
    Unit targetUnit;
    ArrayList<Province> availableProvinces;
    private int fraction;
    ArrayList<Unit> availableUnits;
    private int maxStrength;
    private Province currentProvince;
    PointYio geometricalCenter;


    public AutomaticTransitionWorker(FieldManager fieldManager) {
        this.fieldManager = fieldManager;
        waitMode = false;
        waitCounter = -1;
        targetUnit = null;
        availableProvinces = new ArrayList<>();
        availableUnits = new ArrayList<>();
        geometricalCenter = new PointYio();
    }


    public Unit findNextUnit(int fraction) {
        this.fraction = fraction;

        updateAvailableProvinces();
        if (availableProvinces.size() == 0) return null;

        updateAvailableUnits();
        if (availableUnits.size() == 0) return null;

        cutOffWeakUnits();
        if (availableUnits.size() == 0) return null;

        updateGeometricalCenter();
        leaveOnlyFurthestUnit();
        if (availableUnits.size() == 0) return null;

        return availableUnits.get(0);
    }


    private void leaveOnlyFurthestUnit() {
        Unit furthestUnit = null;
        float maxDistance = 0;
        float currentDistance;
        for (Unit availableUnit : availableUnits) {
            currentDistance = (float) geometricalCenter.distanceTo(availableUnit.currentHex.pos);
            if (furthestUnit == null || currentDistance > maxDistance) {
                furthestUnit = availableUnit;
                maxDistance = currentDistance;
            }
        }

        for (int i = availableUnits.size() - 1; i >= 0; i--) {
            Unit unit = availableUnits.get(i);
            if (unit == furthestUnit) continue;
            availableUnits.remove(unit);
        }
    }


    private void updateGeometricalCenter() {
        geometricalCenter.reset();
        for (Hex hex : currentProvince.hexList) {
            geometricalCenter.add(hex.pos);
        }
        int size = currentProvince.hexList.size();
        geometricalCenter.x /= size;
        geometricalCenter.y /= size;
    }


    private void cutOffWeakUnits() {
        if (availableUnits.size() == 0) return;
        updateMaxStrength();
        for (int i = availableUnits.size() - 1; i >= 0; i--) {
            Unit unit = availableUnits.get(i);
            if (unit.strength == maxStrength) continue;
            availableUnits.remove(unit);
        }
    }


    private void updateMaxStrength() {
        maxStrength = -1;
        for (Unit availableUnit : availableUnits) {
            if (availableUnit.strength <= maxStrength) continue;
            maxStrength = availableUnit.strength;
        }
    }


    private void updateAvailableUnits() {
        availableUnits.clear();
        updateCurrentProvince();
        if (currentProvince == null) return;
        for (Hex hex : currentProvince.hexList) {
            if (!hex.hasUnit()) continue;
            Unit unit = hex.unit;
            if (!unit.isReadyToMove()) continue;
            availableUnits.add(unit);
        }
    }


    private void updateCurrentProvince() {
        currentProvince = null;
        for (Province availableProvince : availableProvinces) {
            if (!availableProvince.hasSomeoneReadyToMove()) continue;
            currentProvince = availableProvince;
            break;
        }
    }


    private void updateAvailableProvinces() {
        availableProvinces.clear();
        for (Province province : fieldManager.provinces) {
            if (province.getFraction() != fraction) continue;
            availableProvinces.add(province);
        }
    }


    void applyUnitSelection(Unit unit) {
        waitMode = true;
        waitCounter = 15;
        fieldManager.gameController.setTouchMode(TouchMode.tmIgnore);
        targetUnit = unit;
    }


    void move() {
        if (!waitMode) return;
        if (waitCounter > 0) {
            waitCounter--;
            return;
        }

        waitMode = false;
        fieldManager.gameController.resetTouchMode();
        CameraController cameraController = fieldManager.gameController.cameraController;
        cameraController.focusOnPoint(targetUnit.currentHex.pos);
        fieldManager.gameController.selectionManager.applyUnitSelection(targetUnit);
    }
}
