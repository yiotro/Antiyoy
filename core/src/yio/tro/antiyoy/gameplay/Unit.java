package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;

/**
 * Created by yiotro on 24.05.2015.
 */
public class Unit implements EncodeableYio{

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


    boolean moveToHex(Hex targetHex) {
        if (targetHex.sameFraction(currentHex) && targetHex.containsBuilding()) return false;

        gameController.ruleset.onUnitMoveToHex(this, targetHex);
        if (targetHex.containsObject()) {
            gameController.cleanOutHex(targetHex); // unit crushes object
            gameController.updateCacheOnceAfterSomeTime();
        }
        stopJumping();
        setReadyToMove(false);
        lastHex = currentHex;
        currentHex = targetHex;
        moveFactor.setValues(0, 0);
        moveFactor.appear(1, 4);
        lastHex.unit = null;
        targetHex.unit = this;
//        YioGdxGame.say("anim hexes: " + gameController.animHexes.size() + "        selected hexes: " + gameController.selectedHexes.size());
//        this was wonderful bug. Hexes were added to list several times which caused method move() to be called to many times

        return true;
    }


    public int getFraction() {
        return currentHex.fraction;
    }


    void updateCurrentPos() {
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


    public void marchToHex(Hex target, Province province) {
        if (target == currentHex) return;
        ArrayList<Hex> moveZone = gameController.detectMoveZone(currentHex, strength, GameRules.UNIT_MOVE_LIMIT);
        if (moveZone.size() == 0) return;
        double minDistance, currentDistance;
        minDistance = FieldManager.distanceBetweenHexes(moveZone.get(0), target);
        Hex closestHex = moveZone.get(0);
        for (Hex hex : moveZone) {
            if (!hex.sameFraction(currentHex) || !hex.nothingBlocksWayForUnit()) continue;
            currentDistance = FieldManager.distanceBetweenHexes(target, hex);
            if (currentDistance >= minDistance) continue;
            minDistance = currentDistance;
            closestHex = hex;
        }

        if (closestHex != null && closestHex != currentHex) {
            gameController.moveUnit(this, closestHex, province);
        }
    }


    public void startJumping() {
        if (GameRules.replayMode) return;

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


    @Override
    public String encode() {
        return currentHex.index1 + " " + currentHex.index2 + " " + strength + " " + isReadyToMove();
    }


    @Override
    public void decode(String source) {
        String[] split = source.split(" ");
        boolean ready = Boolean.valueOf(split[3]);
        if (ready) {
            setReadyToMove(true);
            startJumping();
        } else {
            setReadyToMove(false);
            stopJumping();
        }
    }


    @Override
    public String toString() {
        return "[Unit: " +
                encode() +
                "]";
    }
}
