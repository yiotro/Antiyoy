package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.ai.master.AiData;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;


public class Hex implements ReusableYio, EncodeableYio{

    public boolean active, selected, changingFraction, flag, inMoveZone, genFlag, ignoreTouch;
    public int index1, index2, moveZoneNumber, genPotential, visualDiversityIndex;
    public PointYio pos, fieldPos;
    private GameController gameController;
    public FieldManager fieldManager;
    float cos60, sin60;
    public int fraction, previousFraction, objectInside;
    long animStartTime;
    boolean blockToTreeFromExpanding, canContainObjects;
    public FactorYio animFactor, selectionFactor;
    public Unit unit;
    public Hex algoLink;
    public int algoValue;
    public AiData aiData;


    public Hex(int index1, int index2, PointYio fieldPos, FieldManager fieldManager) {
        this(index1, index2, fieldPos, fieldManager, false);
    }


    public Hex(int index1, int index2, PointYio fieldPos, FieldManager fieldManager, boolean snapshot) {
        this.index1 = index1;
        this.index2 = index2;
        this.fieldPos = fieldPos;
        this.fieldManager = fieldManager;
        if (fieldManager == null) return;
        if (!snapshot) {
            aiData = new AiData(this);
        }

        gameController = fieldManager.gameController;
        active = false;
        pos = new PointYio();
        cos60 = (float) Math.cos(Math.PI / 3d);
        sin60 = (float) Math.sin(Math.PI / 3d);
        animFactor = new FactorYio();
        selectionFactor = new FactorYio();
        unit = null;
        visualDiversityIndex = (101 * index1 * index2 + 7 * index2) % 3;
        canContainObjects = true;
        algoLink = null;
        algoValue = 0;
        updatePos();
    }


    @Override
    public void reset() {
        // this is just blank method, don't use it
    }


    void updateCanContainsObjects() {
        canContainObjects = fieldManager.gameController.levelSizeManager.isPointInsideLevelBoundsHorizontally(pos);
    }


    void updatePos() {
        pos.x = fieldPos.x + fieldManager.hexStep2 * index2 * sin60;
        pos.y = fieldPos.y + fieldManager.hexStep1 * index1 + fieldManager.hexStep2 * index2 * cos60;
    }


    boolean isInProvince() { // can cause bugs if province not detected right
        Hex adjHex;
        for (int i = 0; i < 6; i++) {
            adjHex = getAdjacentHex(i);
            if (adjHex.active && adjHex.sameFraction(this)) return true;
        }
        return false;
    }


    public boolean isNearWater() {
        if (!this.active) return false;
        for (int i = 0; i < 6; i++) {
            if (!gameController.fieldManager.adjacentHex(this, i).active) return true;
        }
        return false;
    }


    public void setFraction(int fraction) {
        previousFraction = this.fraction;
        this.fraction = fraction;
        animFactor.appear(1, 1);
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
        gameController.matchStatistics.onUnitProduced();
    }


    public boolean isFree() {
        return !containsObject() && !containsUnit();
    }


    public boolean isEmpty() {
        return isFree();
    }


    public boolean nothingBlocksWayForUnit() {
        return !containsUnit() && !containsBuilding();
    }


    public boolean containsTree() {
        return objectInside == Obj.PALM || objectInside == Obj.PINE;
    }


    public boolean containsObject() {
        return objectInside > 0;
    }


    public boolean containsTower() {
        return objectInside == Obj.TOWER || objectInside == Obj.STRONG_TOWER;
    }


    public boolean containsBuilding() {
        return objectInside == Obj.TOWN
                || objectInside == Obj.TOWER
                || objectInside == Obj.FARM
                || objectInside == Obj.STRONG_TOWER;
    }


    public Hex getSnapshotCopy() {
        Hex record = new Hex(index1, index2, fieldPos, fieldManager, true);
        record.active = active;
        record.fraction = fraction;
        record.objectInside = objectInside;
        record.selected = selected;
        if (unit != null) {
            record.unit = unit.getSnapshotCopy();
        }
        return record;
    }


    public void setObjectInside(int objectInside) {
        this.objectInside = objectInside;
    }


    public boolean containsUnit() {
        return unit != null;
    }


    public boolean hasUnit() {
        return containsUnit();
    }


    public int numberOfActiveHexesNearby() {
        return numberOfFriendlyHexesNearby() + howManyEnemyHexesNear();
    }


    public boolean noProvincesNearby() {
        if (numberOfFriendlyHexesNearby() > 0) return false;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = getAdjacentHex(i);
            if (adjHex.active && adjHex.numberOfFriendlyHexesNearby() > 0) return false;
        }
        return true;
    }


    public int numberOfFriendlyHexesNearby() {
        int c = 0;
        for (int dir = 0; dir < 6; dir++) {
            Hex adjHex = getAdjacentHex(dir);
            if (adjHex == null) continue;
            if (adjHex.isNullHex()) continue;
            if (!adjHex.active) continue;
            if (adjHex.isNeutral()) continue;
            if (!adjHex.sameFraction(this)) continue;
            c++;
        }
        return c;
    }


    public int getDefenseNumber() {
        return getDefenseNumber(null);
    }


    public int getDefenseNumber(Unit ignoreUnit) {
        int defenseNumber = 0;
        if (this.objectInside == Obj.TOWN) defenseNumber = 1;
        if (this.objectInside == Obj.TOWER) defenseNumber = 2;
        if (this.objectInside == Obj.STRONG_TOWER) defenseNumber = 3;

        if (this.containsUnit() && unit != ignoreUnit) {
            defenseNumber = Math.max(defenseNumber, this.unit.strength);
        }
        Hex neighbour;
        for (int i = 0; i < 6; i++) {
            neighbour = getAdjacentHex(i);
            if (!(neighbour.active && neighbour.sameFraction(this))) continue;
            if (neighbour.objectInside == Obj.TOWN) defenseNumber = Math.max(defenseNumber, 1);
            if (neighbour.objectInside == Obj.TOWER) defenseNumber = Math.max(defenseNumber, 2);
            if (neighbour.objectInside == Obj.STRONG_TOWER) defenseNumber = Math.max(defenseNumber, 3);
            if (neighbour.containsUnit() && neighbour.unit != ignoreUnit) {
                defenseNumber = Math.max(defenseNumber, neighbour.unit.strength);
            }
        }
        return defenseNumber;
    }


    public boolean isNearHouse() {
        Hex adjHex;
        for (int i = 0; i < 6; i++) {
            adjHex = getAdjacentHex(i);
            if (adjHex.active && adjHex.sameFraction(this) && adjHex.objectInside == Obj.TOWN) return true;
        }
        return false;
    }


    public void forAdjacentHexes(HexActionPerformer hexActionPerformer) {
        Hex adjHex;
        for (int i = 0; i < 6; i++) {
            adjHex = getAdjacentHex(i);
            hexActionPerformer.doAction(this, adjHex);
        }
    }


    public boolean isInPerimeter() {
        Hex adjHex;
        for (int i = 0; i < 6; i++) {
            adjHex = getAdjacentHex(i);
            if (adjHex.active && !adjHex.sameFraction(this) && adjHex.isInProvince()) return true;
        }
        return false;
    }


    public boolean hasThisSupportiveObjectNearby(int objectIndex) {
        if (objectInside == objectIndex) return true;
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (adjacentHex.isNullHex()) continue;
            if (!adjacentHex.active) continue;
            if (adjacentHex.fraction != fraction) continue;
            if (adjacentHex.objectInside != objectIndex) continue;
            return true;
        }
        return false;
    }


    public boolean hasPalmReadyToExpandNearby() {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = getAdjacentHex(i);
            if (!adjHex.blockToTreeFromExpanding && adjHex.objectInside == Obj.PALM) return true;
        }
        return false;
    }


    public boolean hasPineReadyToExpandNearby() {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = getAdjacentHex(i);
            if (!adjHex.blockToTreeFromExpanding && adjHex.objectInside == Obj.PINE) return true;
        }
        return false;
    }


    public boolean sameFraction(int fraction) {
        return this.fraction == fraction;
    }


    public boolean sameFraction(Province province) {
        return fraction == province.getFraction();
    }


    public boolean sameFraction(Hex hex) {
        return fraction == hex.fraction;
    }


    public int howManyEnemyHexesNear() {
        int c = 0;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = getAdjacentHex(i);
            if (adjHex.active && !adjHex.sameFraction(this)) c++;
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
            Hex adjHex = getAdjacentHex(i);
            if (adjHex.active && adjHex.sameFraction(this) && adjHex.containsTower()) return true;
        }
        return false;
    }


    public Hex getAdjacentHex(int direction) {
        return gameController.fieldManager.adjacentHex(this, direction);
    }


    public boolean isAdjacentTo(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (adjacentHex.isNullHex()) continue;
            if (adjacentHex != hex) continue;
            return true;
        }
        return false;
    }


    public void setIgnoreTouch(boolean ignoreTouch) {
        this.ignoreTouch = ignoreTouch;
    }


    public boolean isNullHex() {
        return index1 == -1 && index2 == -1;
    }


    void select() {
        if (!selected) {
            selected = true;
            selectionFactor.setValues(0, 0);
            selectionFactor.appear(3, 1.5);
        }
    }


    public boolean isSelected() {
        return selected;
    }


    public PointYio getPos() {
        return pos;
    }


    public boolean isNeutral() {
        return fraction == GameRules.NEUTRAL_FRACTION;
    }


    public boolean canBeAttackedBy(Unit unit) {
        if (unit == null) return false; // normally this shouldn't happen, but it happened once in replay

        boolean canUnitAttackHex = gameController.canUnitAttackHex(unit.strength, unit.getFraction(), this);

        if (GameRules.replayMode) {
            if (!canUnitAttackHex) {
                System.out.println("Problem in Hex.canBeAttackedBy(): " + this);
            }
            return true;
        }

        return canUnitAttackHex;
    }


    public boolean isInMoveZone() {
        return inMoveZone;
    }


    void close() {
        gameController = null;
    }


    @Override
    public String toString() {
        if (!active) {
            return "[Hex (not active): f" + fraction + " (" + index1 + ", " + index2 + ")]";
        }
        return "[Hex: f" + fraction + " (" + index1 + ", " + index2 + ")]";
    }


    @Override
    public String encode() {
        return index1 + " " + index2 + " " + fraction + " " + objectInside;
    }


    @Override
    public void decode(String source) {
        String[] split = source.split(" ");
        int obj = Integer.valueOf(split[3]);
        if (obj > 0) {
            fieldManager.addSolidObject(this, obj);
        }
    }
}
