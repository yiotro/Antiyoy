package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.PointYio;
import yio.tro.antiyoy.factor_yio.FactorYio;

/**
 * Created by ivan on 19.10.2014.
 */
public class Hex {

    public boolean active, selected, changingColor, flag, inMoveZone, genFlag, ignoreTouch;
    public int index1, index2, moveZoneNumber, genPotential, viewDiversityIndex;
    PointYio pos, fieldPos;
    private GameController gameController;
    FieldController fieldController;
    float cos60, sin60;
    public int colorIndex, lastColorIndex, objectInside;
    long animStartTime;
    boolean blockToTreeFromExpanding;
    public static final int OBJECT_PINE = 1;
    public static final int OBJECT_PALM = 2;
    public static final int OBJECT_TOWN = 3;
    public static final int OBJECT_TOWER = 4;
    public static final int OBJECT_GRAVE = 5;
    public static final int OBJECT_FARM = 6;
    public static final int OBJECT_STRONG_TOWER = 7;
    FactorYio animFactor, selectionFactor;
    public Unit unit;


    public Hex(int index1, int index2, PointYio fieldPos, FieldController fieldController) {
        this.index1 = index1;
        this.index2 = index2;
        this.fieldPos = fieldPos;
        this.fieldController = fieldController;
        gameController = fieldController.gameController;
        active = false;
        pos = new PointYio();
        cos60 = (float) Math.cos(Math.PI / 3d);
        sin60 = (float) Math.sin(Math.PI / 3d);
        animFactor = new FactorYio();
        selectionFactor = new FactorYio();
        unit = null;
        viewDiversityIndex = (101 * index1 + 1001 * index2) % 3;
        updatePos();
    }


    private void updatePos() {
        pos.x = fieldPos.x + fieldController.hexStep2 * index2 * sin60;
        pos.y = fieldPos.y + fieldController.hexStep1 * index1 + fieldController.hexStep2 * index2 * cos60;
    }


    boolean isInProvince() { // can cause bugs if province not detected right
        Hex adjHex;
        for (int i = 0; i < 6; i++) {
            adjHex = adjacentHex(i);
            if (adjHex.active && adjHex.sameColor(this)) return true;
        }
        return false;
    }


    boolean isNearWater() {
        if (!this.active) return false;
        for (int i = 0; i < 6; i++) {
            if (!gameController.fieldController.adjacentHex(this, i).active) return true;
        }
        return false;
    }


    public void setColorIndex(int colorIndex) {
        lastColorIndex = this.colorIndex;
        this.colorIndex = colorIndex;
        animFactor.beginSpawning(1, 1);
        animFactor.setValues(0, 0);
    }


    void move() {
        animFactor.move();
        if (selected) {
            selectionFactor.move();
        }
//        if (unit != null) unit.move();
    }


    void addUnit(int strength) {
        unit = new Unit(gameController, this, strength);
        gameController.unitList.add(unit);
        gameController.statistics.unitWasProduced();
    }


    public boolean isFree() {
        return !containsSolidObject() && !containsUnit();
    }


    public boolean nothingBlocksWayForUnit() {
        return !containsUnit() && !containsBuilding();
    }


    public boolean containsTree() {
        return objectInside == OBJECT_PALM || objectInside == OBJECT_PINE;
    }


    public boolean containsSolidObject() {
        return objectInside > 0;
    }


    public boolean containsTower() {
        return objectInside == OBJECT_TOWER || objectInside == OBJECT_STRONG_TOWER;
    }


    public boolean containsBuilding() {
        return objectInside == OBJECT_TOWN
                || objectInside == OBJECT_TOWER
                || objectInside == OBJECT_FARM
                || objectInside == OBJECT_STRONG_TOWER;
    }


    public Hex getSnapshotCopy() {
        Hex record = new Hex(index1, index2, fieldPos, fieldController);
        record.active = active;
        record.colorIndex = colorIndex;
        record.objectInside = objectInside;
        record.selected = selected;
        if (unit != null) record.unit = unit.getSnapshotCopy();
        return record;
    }


    public void setObjectInside(int objectInside) {
        this.objectInside = objectInside;
    }


    public boolean containsUnit() {
        return unit != null;
    }


    public int numberOfActiveHexesNearby() {
        return numberOfFriendlyHexesNearby() + howManyEnemyHexesNear();
    }


    public boolean noProvincesNearby() {
        if (numberOfFriendlyHexesNearby() > 0) return false;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = adjacentHex(i);
            if (adjHex.active && adjHex.numberOfFriendlyHexesNearby() > 0) return false;
        }
        return true;
    }


    public int numberOfFriendlyHexesNearby() {
        int c = 0;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = adjacentHex(i);
            if (adjHex.colorIndex == fieldController.neutralLandsIndex) continue;
            if (adjHex.active && adjHex.sameColor(this)) c++;
        }
        return c;
    }


    public int getDefenseNumber() {
        return getDefenseNumber(null);
    }


    public int getDefenseNumber(Unit ignoreUnit) {
        int defenseNumber = 0;
        if (this.objectInside == Hex.OBJECT_TOWN) defenseNumber = Math.max(defenseNumber, 1);
        if (this.objectInside == Hex.OBJECT_TOWER) defenseNumber = Math.max(defenseNumber, 2);
        if (this.objectInside == Hex.OBJECT_STRONG_TOWER) defenseNumber = Math.max(defenseNumber, 3);
        if (this.containsUnit() && unit != ignoreUnit) defenseNumber = Math.max(defenseNumber, this.unit.strength);
        Hex neighbour;
        for (int i = 0; i < 6; i++) {
            neighbour = adjacentHex(i);
            if (!(neighbour.active && neighbour.sameColor(this))) continue;
            if (neighbour.objectInside == Hex.OBJECT_TOWN) defenseNumber = Math.max(defenseNumber, 1);
            if (neighbour.objectInside == Hex.OBJECT_TOWER) defenseNumber = Math.max(defenseNumber, 2);
            if (neighbour.objectInside == Hex.OBJECT_STRONG_TOWER) defenseNumber = Math.max(defenseNumber, 3);
            if (neighbour.containsUnit() && neighbour.unit != ignoreUnit)
                defenseNumber = Math.max(defenseNumber, neighbour.unit.strength);
        }
        return defenseNumber;
    }


    public boolean isNearHouse() {
        Hex adjHex;
        for (int i = 0; i < 6; i++) {
            adjHex = adjacentHex(i);
            if (adjHex.active && adjHex.sameColor(this) && adjHex.objectInside == OBJECT_TOWN) return true;
        }
        return false;
    }


    public void forAdjacentHexes(HexActionPerformer hexActionPerformer) {
        Hex adjHex;
        for (int i = 0; i < 6; i++) {
            adjHex = adjacentHex(i);
            hexActionPerformer.doAction(this, adjHex);
        }
    }


    public boolean isInPerimeter() {
        Hex adjHex;
        for (int i = 0; i < 6; i++) {
            adjHex = adjacentHex(i);
            if (adjHex.active && !adjHex.sameColor(this) && adjHex.isInProvince()) return true;
        }
        return false;
    }


    public boolean hasThisObjectNearby(int objectIndex) {
        if (objectInside == objectIndex) return true;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = adjacentHex(i);
            if (adjHex.colorIndex != colorIndex) continue;
            if (adjHex.active && adjHex.objectInside == objectIndex) {
                return true;
            }
        }
        return false;
    }


    boolean hasPalmReadyToExpandNearby() {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = adjacentHex(i);
            if (!adjHex.blockToTreeFromExpanding && adjHex.objectInside == Hex.OBJECT_PALM) return true;
        }
        return false;
    }


    boolean hasPineReadyToExpandNearby() {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = adjacentHex(i);
            if (!adjHex.blockToTreeFromExpanding && adjHex.objectInside == Hex.OBJECT_PINE) return true;
        }
        return false;
    }


    public boolean sameColor(int color) {
        return colorIndex == color;
    }


    public boolean sameColor(Province province) {
        return colorIndex == province.getColor();
    }


    public boolean sameColor(Hex hex) {
        return colorIndex == hex.colorIndex;
    }


    public int howManyEnemyHexesNear() {
        int c = 0;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = adjacentHex(i);
            if (adjHex.active && !adjHex.sameColor(this)) c++;
        }
        return c;
    }


    public void set(Hex hex) {
        index1 = hex.index1;
        index2 = hex.index2;
    }


    public boolean equals(Hex hex) {
        return hex.index1 == index1 && hex.index2 == index2;
    }


    public boolean isDefendedByTower() {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = adjacentHex(i);
            if (adjHex.active && adjHex.sameColor(this) && adjHex.containsTower()) return true;
        }
        return false;
    }


    public Hex adjacentHex(int neighbourNumber) {
        return gameController.fieldController.adjacentHex(this, neighbourNumber);
    }


    public void setIgnoreTouch(boolean ignoreTouch) {
        this.ignoreTouch = ignoreTouch;
    }


    boolean isEmptyHex() {
        return index1 == -1 && index2 == -1;
    }


    void select() {
        if (!selected) {
            selected = true;
            selectionFactor.setValues(0, 0);
            selectionFactor.beginSpawning(3, 1.5);
        }
    }


    public boolean isSelected() {
        return selected;
    }


    public PointYio getPos() {
        return pos;
    }


    public boolean isNeutral() {
        if (GameRules.slay_rules) return false;
        return colorIndex == fieldController.neutralLandsIndex;
    }


    public boolean isInMoveZone() {
        return inMoveZone;
    }


    void close() {
        gameController = null;
    }


    @Override
    public String toString() {
        return "[Hex: c" + colorIndex + " (" + index1 + ", " + index2 + ")]";
    }
}
