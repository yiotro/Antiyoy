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
    Unit previousUnit;
    PointYio tempA, tempB;


    public AutomaticTransitionWorker(FieldManager fieldManager) {
        this.fieldManager = fieldManager;
        waitMode = false;
        waitCounter = -1;
        targetUnit = null;
        availableProvinces = new ArrayList<>();
        availableUnits = new ArrayList<>();
        geometricalCenter = new PointYio();
        previousUnit = null;
        tempA = new PointYio();
        tempB = new PointYio();
    }


    public Unit findNextUnit(Unit previousUnit, int fraction) {
        this.fraction = fraction;
        this.previousUnit = previousUnit;

        updateAvailableProvinces();
        if (availableProvinces.size() == 0) return null;

        updateCurrentProvince();
        updateAvailableUnits();
        if (availableUnits.size() == 0) return null;

        cutOffUselessUnits();
        cutOffWeakUnits();
        if (availableUnits.size() == 0) return null;

        updateGeometricalCenter();
        leaveOnlyFurthestUnit();
        if (availableUnits.size() == 0) return null;

        return availableUnits.get(0);
    }


    private void cutOffUselessUnits() {
        if (availableUnits.size() == 0) return;
        for (int i = availableUnits.size() - 1; i >= 0; i--) {
            Unit unit = availableUnits.get(i);
            if (!isUnitUseless(unit)) continue;
            availableUnits.remove(unit);
        }
    }


    private boolean isUnitUseless(Unit unit) {
        MoveZoneManager moveZoneManager = fieldManager.moveZoneManager;
        ArrayList<Hex> moveZone = moveZoneManager.detectMoveZone(unit.currentHex, unit.strength);
        for (Hex hex : moveZone) {
            if (hex.fraction == unit.getFraction()) continue;
            return false;
        }
        return true;
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

        if (previousUnit != null) {
            updateCurrentProvinceByPreviousUnit();
            return;
        }

        for (Province availableProvince : availableProvinces) {
            if (!isProvinceValid(availableProvince)) continue;
            currentProvince = availableProvince;
            break;
        }
    }


    private void updateCurrentProvinceByPreviousUnit() {
        Province province = previousUnit.gameController.getProvinceByHex(previousUnit.currentHex);
        if (isProvinceValid(province)) {
            currentProvince = province;
        } else {
            currentProvince = getClosestAvailableValidProvince(province);
        }
    }


    private Province getClosestAvailableValidProvince(Province province) {
        Province bestProvince = null;
        double minDistance = 0;
        for (Province availableProvince : availableProvinces) {
            if (availableProvince == province) continue;
            if (!isProvinceValid(availableProvince)) continue;
            double currentDistance = getFastDistanceBetweenProvinces(province, availableProvince);
            if (bestProvince == null || currentDistance < minDistance) {
                bestProvince = availableProvince;
                minDistance = currentDistance;
            }
        }
        return bestProvince;
    }


    private double getFastDistanceBetweenProvinces(Province province1, Province province2) {
        updatePointByProvince(tempA, province1);
        updatePointByProvince(tempB, province2);
        return tempA.fastDistanceTo(tempB);
    }


    private void updatePointByProvince(PointYio pointYio, Province province) {
        pointYio.reset();
        for (Hex hex : province.hexList) {
            pointYio.add(hex.pos);
        }
        pointYio.x /= province.hexList.size();
        pointYio.y /= province.hexList.size();
    }


    private boolean isProvinceValid(Province province) {
        if (!province.hasSomeoneReadyToMove()) return false;
        for (Hex hex : province.hexList) {
            if (!hex.hasUnit()) continue;
            if (isUnitUseless(hex.unit)) continue;
            return true;
        }
        return false;
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

        if (fieldManager.gameController.isSomethingMoving()) return;

        if (waitCounter > 0) {
            waitCounter--;
            return;
        }

        waitMode = false;
        fieldManager.gameController.resetTouchMode();

        SelectionManager selectionManager = fieldManager.gameController.selectionManager;
        if (targetUnit == null) {
            selectionManager.deselectAll();
            return;
        }

        CameraController cameraController = fieldManager.gameController.cameraController;
        cameraController.focusOnPoint(targetUnit.currentHex.pos);
        selectionManager.applyProvinceSelection(targetUnit.currentHex);
        selectionManager.applyUnitSelection(targetUnit);
    }
}
