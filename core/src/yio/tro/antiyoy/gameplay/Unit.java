package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.PointYio;
import yio.tro.antiyoy.factor_yio.FactorYio;

import java.util.ArrayList;

/**
 * Created by ivan on 24.05.2015.
 */
public class Unit {
    public Hex lastHex, currHex;
    final PointYio currentPos;
    final FactorYio moveFactor;
    public int strength;
    final GameController gameController;
    boolean readyToMove;
    float jumpPos, jumpGravity, jumpDy, jumpStartingImpulse;


    public Unit(GameController gameController, Hex currHex, int strength) {
        this.gameController = gameController;
        this.currHex = currHex;
        this.strength = strength;
        moveFactor = new FactorYio();
        moveFactor.setValues(1, 0);
        lastHex = currHex;
        jumpStartingImpulse = 0.015f;
        currentPos = new PointYio();
        updateCurrentPos();
    }


    boolean canMoveToFriendlyHex(Hex hex) {
        if (hex == currHex) return false;
        if (hex.containsBuilding()) return false;
        if (hex.containsUnit() && !gameController.canMergeUnits(this, hex.unit)) return false;
        return true;
    }


    boolean moveToHex(Hex destinationHex) {
        if (destinationHex.sameColor(currHex) && destinationHex.containsBuilding()) return false;
        if (destinationHex.containsSolidObject()) {
            if (!GameRules.slay_rules && destinationHex.containsTree()) {
                gameController.getProvinceByHex(destinationHex).money += 5;
                gameController.selectionController.updateSelectedProvinceMoney();
            }
            gameController.cleanOutHex(destinationHex); // unit crushes object
            gameController.updateCacheOnceAfterSomeTime();
        }
        stopJumping();
        setReadyToMove(false);
        lastHex = currHex;
        currHex = destinationHex;
        moveFactor.setValues(0, 0);
        moveFactor.beginSpawning(1, 4);
        lastHex.unit = null;
        destinationHex.unit = this;
//        YioGdxGame.say("anim hexes: " + gameController.animHexes.size() + "        selected hexes: " + gameController.selectedHexes.size());
//        this was wonderful bug. Hexes were added to list several times which caused method move() to be called to many times
        return true;
    }


    int getTax() {
        return getTax(strength);
    }


    static int getTax(int strength) {
        switch (strength) {
            default:
            case 1:
                return 2;
            case 2:
                return 6;
            case 3:
                return 18;
            case 4:
                return 54;
        }
    }


    public Hex getCurrHex() {
        return currHex;
    }


    public int getColor() {
        return currHex.colorIndex;
    }


    private void updateCurrentPos() {
        currentPos.x = lastHex.pos.x + moveFactor.get() * (currHex.pos.x - lastHex.pos.x);
        currentPos.y = lastHex.pos.y + moveFactor.get() * (currHex.pos.y - lastHex.pos.y);
    }


    public void setReadyToMove(boolean readyToMove) {
        this.readyToMove = readyToMove;
    }


    public boolean isReadyToMove() {
        return readyToMove;
    }


    public Unit getSnapshotCopy() {
        Unit copy = new Unit(gameController, currHex, strength);
        copy.readyToMove = readyToMove;
        return copy;
    }


    public void marchToHex(Hex toWhere, Province province) {
        if (toWhere == currHex) return;
        ArrayList<Hex> moveZone = gameController.detectMoveZone(currHex, strength, GameRules.UNIT_MOVE_LIMIT);
        if (moveZone.size() == 0) return;
        double minDistance, currentDistance;
        minDistance = FieldController.distanceBetweenHexes(moveZone.get(0), toWhere);
        Hex closestHex = moveZone.get(0);
        for (Hex hex : moveZone) {
            if (hex.sameColor(currHex) && hex.nothingBlocksWayForUnit()) {
                currentDistance = FieldController.distanceBetweenHexes(toWhere, hex);
                if (currentDistance < minDistance) {
                    minDistance = currentDistance;
                    closestHex = hex;
                }
            }
        }
        if (closestHex != null && closestHex != currHex) gameController.moveUnit(this, closestHex, province);
    }


    void startJumping() {
        jumpPos = 0;
        jumpDy = jumpStartingImpulse;
        jumpGravity = 0.001f;
    }


    void stopJumping() {
        jumpPos = 0;
        jumpDy = 0;
        jumpGravity = 0;
    }


    void move() {
        moveFactor.move();
        updateCurrentPos();
    }


    void moveJumpAnim() {
        jumpDy -= jumpGravity;
        jumpPos += jumpDy;
        if (jumpPos < 0) {
            jumpPos = 0;
            jumpDy = jumpStartingImpulse;
        }
    }
}
