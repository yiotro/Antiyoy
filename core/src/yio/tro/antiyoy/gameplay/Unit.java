package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.PointYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;

/**
 * Created by ivan on 24.05.2015.
 */
public class Unit {
    public Hex lastHex, currentHex;
    public final PointYio currentPos;
    public final FactorYio moveFactor;
    public int strength;
    final GameController gameController;
    boolean readyToMove;
    public float jumpPos, jumpGravity, jumpDy, jumpStartingImpulse;


    public Unit(GameController gameController, Hex currentHex, int strength) {
        this.gameController = gameController;
        this.currentHex = currentHex;
        this.strength = strength;
        moveFactor = new FactorYio();
        moveFactor.setValues(1, 0);
        lastHex = currentHex;
        jumpStartingImpulse = 0.015f;
        currentPos = new PointYio();
        updateCurrentPos();
    }


    boolean canMoveToFriendlyHex(Hex hex) {
        if (hex == currentHex) return false;
        if (hex.containsBuilding()) return false;
        if (hex.containsUnit() && !gameController.ruleset.canMergeUnits(this, hex.unit)) return false;
        return true;
    }


    boolean moveToHex(Hex destinationHex) {
        if (destinationHex.sameColor(currentHex) && destinationHex.containsBuilding()) return false;
        gameController.ruleset.onUnitMoveToHex(this, destinationHex);
        if (destinationHex.containsObject()) {
            gameController.cleanOutHex(destinationHex); // unit crushes object
            gameController.updateCacheOnceAfterSomeTime();
        }
        stopJumping();
        setReadyToMove(false);
        lastHex = currentHex;
        currentHex = destinationHex;
        moveFactor.setValues(0, 0);
        moveFactor.beginSpawning(1, 4);
        lastHex.unit = null;
        destinationHex.unit = this;
//        YioGdxGame.say("anim hexes: " + gameController.animHexes.size() + "        selected hexes: " + gameController.selectedHexes.size());
//        this was wonderful bug. Hexes were added to list several times which caused method move() to be called to many times
        return true;
    }


    public int getColor() {
        return currentHex.colorIndex;
    }


    private void updateCurrentPos() {
        currentPos.x = lastHex.pos.x + moveFactor.get() * (currentHex.pos.x - lastHex.pos.x);
        currentPos.y = lastHex.pos.y + moveFactor.get() * (currentHex.pos.y - lastHex.pos.y);
    }


    public void setReadyToMove(boolean readyToMove) {
        this.readyToMove = readyToMove;
    }


    public boolean isReadyToMove() {
        return readyToMove;
    }


    public Unit getSnapshotCopy() {
        Unit copy = new Unit(gameController, currentHex, strength);
        copy.readyToMove = readyToMove;
        return copy;
    }


    public void marchToHex(Hex toWhere, Province province) {
        if (toWhere == currentHex) return;
        ArrayList<Hex> moveZone = gameController.detectMoveZone(currentHex, strength, GameRules.UNIT_MOVE_LIMIT);
        if (moveZone.size() == 0) return;
        double minDistance, currentDistance;
        minDistance = FieldController.distanceBetweenHexes(moveZone.get(0), toWhere);
        Hex closestHex = moveZone.get(0);
        for (Hex hex : moveZone) {
            if (hex.sameColor(currentHex) && hex.nothingBlocksWayForUnit()) {
                currentDistance = FieldController.distanceBetweenHexes(toWhere, hex);
                if (currentDistance < minDistance) {
                    minDistance = currentDistance;
                    closestHex = hex;
                }
            }
        }
        if (closestHex != null && closestHex != currentHex) gameController.moveUnit(this, closestHex, province);
    }


    void startJumping() {
        jumpPos = 0;
        jumpDy = jumpStartingImpulse;
        jumpGravity = 0.001f;
    }


    public void stopJumping() {
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
