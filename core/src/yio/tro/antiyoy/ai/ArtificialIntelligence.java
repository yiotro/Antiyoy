package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.gameplay.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ivan on 26.10.2014.
 */
public abstract class ArtificialIntelligence {

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_NORMAL = 1;
    public static final int DIFFICULTY_HARD = 2;
    public static final int DIFFICULTY_EXPERT = 3;
    public static final int DIFFICULTY_BALANCER = 4;

    final GameController gameController;
    final Random random;
    protected final int color;


    ArtificialIntelligence(GameController gameController, int color) {
        this.gameController = gameController;
        this.color = color;
        random = gameController.random;
    }


    ArrayList<Unit> detectUnitsReadyToMove() {
        ArrayList<Unit> unitsReadyToMove = new ArrayList<Unit>();
        for (Province province : gameController.fieldController.provinces) {
            if (province.getColor() == color) {
                for (int k = province.hexList.size() - 1; k >= 0; k--) {
                    Hex hex = province.hexList.get(k);
                    if (hex.containsUnit() && hex.unit.isReadyToMove())
                        unitsReadyToMove.add(hex.unit);
                }
            }
        }
        return unitsReadyToMove;
    }


    void moveUnits(ArrayList<Unit> unitsReadyToMove) {
        for (Unit unit : unitsReadyToMove) {
            ArrayList<Hex> moveZone = gameController.detectMoveZone(unit.currHex, unit.strength, GameRules.UNIT_MOVE_LIMIT);
            excludeFriendlyBuildingsFromMoveZone(moveZone);
            excludeFriendlyUnitsFromMoveZone(moveZone);
            if (moveZone.size() == 0) continue;
            decideAboutUnit(unit, moveZone, gameController.getProvinceByHex(unit.currHex));
        }
    }


    void spendMoneyAndMergeUnits() {
        for (int i = 0; i < gameController.fieldController.provinces.size(); i++) {
            Province province = gameController.fieldController.provinces.get(i);
            if (province.getColor() == color) {
                spendMoney(province);
                mergeUnits(province);
            }
        }
    }


    void moveAfkUnits() {
        for (Unit unit : detectUnitsReadyToMove()) {
            Province province = gameController.getProvinceByHex(unit.currHex);
            if (province.hexList.size() > 20) {
                moveAfkUnit(province, unit);
            }
        }
    }


    public abstract void makeMove();


    void moveAfkUnit(Province province, Unit unit) {
        ArrayList<Hex> moveZone = gameController.detectMoveZone(unit.currHex, unit.strength, GameRules.UNIT_MOVE_LIMIT);
        excludeFriendlyUnitsFromMoveZone(moveZone);
        excludeFriendlyBuildingsFromMoveZone(moveZone);
        if (moveZone.size() == 0) return;
        gameController.moveUnit(unit, moveZone.get(random.nextInt(moveZone.size())), province);
    }


    void mergeUnits(Province province) {
        for (int i = 0; i < province.hexList.size(); i++) {
            Hex hex = province.hexList.get(i);
            if (hex.containsUnit() && hex.unit.isReadyToMove()) {
                tryToMergeWithSomeone(province, hex.unit);
            }
        }
    }


    private void tryToMergeWithSomeone(Province province, Unit unit) {
        ArrayList<Hex> moveZone = gameController.detectMoveZone(unit.currHex, unit.strength, GameRules.UNIT_MOVE_LIMIT);
        if (moveZone.size() == 0) return;
        for (Hex hex : moveZone) {
            if (hex.sameColor(unit.currHex) && hex.containsUnit() && hex.unit.isReadyToMove() && unit != hex.unit &&
                    province.hasEnoughIncomeToAffordUnit(gameController.mergedUnitStrength(unit, hex.unit))) {

                gameController.mergeUnits(unit.currHex, unit, hex.unit);
            }
        }
    }


    protected void spendMoney(Province province) {
        tryToBuildTowers(province);
        tryToBuildUnits(province);
    }


    void tryToBuildTowers(Province province) {
        while (province.hasMoneyForTower()) {
            Hex hex = findHexThatNeedsTower(province);
            if (hex == null) return;
            gameController.fieldController.buildTower(province, hex);
        }
    }


    protected Hex findHexThatNeedsTower(Province province) {
        for (Hex hex : province.hexList) {
            if (needTowerOnHex(hex)) return hex;
        }
        return null;
    }


    boolean needTowerOnHex(Hex hex) {
        if (!hex.active) return false;
        if (!hex.isFree()) return false;
        int c = 0;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.adjacentHex(i);
            if (adjHex.active && hex.sameColor(adjHex) && !adjHex.isDefendedByTower()) c++;
        }
        return c >= 5;
    }


    boolean tryToBuiltUnitInsideProvince(Province province, int strength) {
        for (Hex hex : province.hexList) {
            if (hex.nothingBlocksWayForUnit()) {
                gameController.fieldController.buildUnit(province, hex, strength);
                return true;
            }
        }
        return false;
    }


    boolean tryToAttackWithStrength(Province province, int strength) {
        ArrayList<Hex> moveZone = gameController.detectMoveZone(province.getCapital(), strength);
        ArrayList<Hex> attackableHexes = findAttackableHexes(province.getColor(), moveZone);
        if (attackableHexes.size() == 0) return false;
        Hex bestHexForAttack = findMostAttractiveHex(attackableHexes, province, strength);
        gameController.fieldController.buildUnit(province, bestHexForAttack, strength);
        return true;
    }


    void tryToBuildUnitsOnPalms(Province province) {
        if (!province.hasEnoughIncomeToAffordUnit(1)) return;
        while (province.hasMoneyForUnit(1)) {
            ArrayList<Hex> moveZone = gameController.detectMoveZone(province.getCapital(), 1);
            boolean killedPalm = false;
            for (Hex hex : moveZone) {
                if (hex.objectInside == Hex.OBJECT_PALM && hex.sameColor(province)) {
                    gameController.fieldController.buildUnit(province, hex, 1);
                    killedPalm = true;
                }
            }
            if (!killedPalm) break;
        }
    }


    void tryToBuildUnits(Province province) {
        tryToBuildUnitsOnPalms(province);

        for (int i = 1; i <= 4; i++) {
            if (!province.hasEnoughIncomeToAffordUnit(i)) break;
            while (province.hasMoneyForUnit(i)) {
                if (!tryToAttackWithStrength(province, i)) break;
            }
        }

        // this is to kick start province
        if (province.hasMoneyForUnit(1) && howManyUnitsInProvince(province) <= 1)
            tryToAttackWithStrength(province, 1);
    }


    boolean checkToCleanSomeTrees(Unit unit, ArrayList<Hex> moveZone, Province province) {
        for (Hex hex : moveZone) {
            if (hex.containsTree() && hex.sameColor(unit.currHex)) {
                gameController.moveUnit(unit, hex, province);
                return true;
            }
        }
        return false;
    }


    boolean checkToCleanSomePalms(Unit unit, ArrayList<Hex> moveZone, Province province) {
        for (Hex hex : moveZone) {
            if (hex.objectInside == Hex.OBJECT_PALM && hex.sameColor(unit.currHex)) {
                gameController.moveUnit(unit, hex, province);
                return true;
            }
        }
        return false;
    }


    void decideAboutUnit(Unit unit, ArrayList<Hex> moveZone, Province province) {
        // cleaning palms has highest priority
        if (unit.strength <= 2 && checkToCleanSomePalms(unit, moveZone, province)) return;
        ArrayList<Hex> attackableHexes = findAttackableHexes(unit.getColor(), moveZone);
        if (attackableHexes.size() > 0) { // attack something
            Hex mostAttackableHex = findMostAttractiveHex(attackableHexes, province, unit.strength);
            gameController.moveUnit(unit, mostAttackableHex, province);
        } else { // nothing to attack
            boolean cleanedTrees = checkToCleanSomeTrees(unit, moveZone, province);
            if (!cleanedTrees) {
                if (unit.currHex.isInPerimeter()) {
                    pushUnitToBetterDefense(unit, province);
                }
            }
        }
    }


    boolean checkChance(double chance) {
        return random.nextDouble() < chance;
    }


    void pushUnitToBetterDefense(Unit unit, Province province) {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = unit.currHex.adjacentHex(i);
            if (adjHex.active && adjHex.sameColor(unit.currHex) && adjHex.isFree() && adjHex.howManyEnemyHexesNear() == 0) {
                gameController.moveUnit(unit, adjHex, province);
            }
        }
    }


    int getAttackAllure(Hex hex, int color) {
        int c = 0;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.adjacentHex(i);
            if (adjHex.active && adjHex.sameColor(color)) c++;
        }
        return c;
    }


    Hex findHexAttractiveToBaron(ArrayList<Hex> attackableHexes, int strength) {
        for (Hex attackableHex : attackableHexes) {
            if (attackableHex.objectInside == Hex.OBJECT_TOWER) return attackableHex;
            if (strength == 4 && attackableHex.objectInside == Hex.OBJECT_STRONG_TOWER) return attackableHex;
        }
        for (Hex attackableHex : attackableHexes) {
            if (attackableHex.isDefendedByTower()) return attackableHex;
        }
        return null;
    }


    Hex findMostAttractiveHex(ArrayList<Hex> attackableHexes, Province province, int strength) {
        if (strength == 3 || strength == 4) {
            Hex hex = findHexAttractiveToBaron(attackableHexes, strength);
            if (hex != null) return hex;
        }

        Hex result = null;
        int currMax = -1;
        for (Hex attackableHex : attackableHexes) {
            int currNum = getAttackAllure(attackableHex, province.getColor());
            if (currNum > currMax) {
                currMax = currNum;
                result = attackableHex;
            }
        }
        return result;
    }


    ArrayList<Hex> findAttackableHexes(int attackerColor, ArrayList<Hex> moveZone) {
        ArrayList<Hex> result = new ArrayList<Hex>();
        for (Hex hex : moveZone) {
            if (hex.colorIndex != attackerColor) result.add(hex);
        }
        return result;
    }


    private void excludeFriendlyBuildingsFromMoveZone(ArrayList<Hex> moveZone) {
        ArrayList<Hex> junkList = new ArrayList<Hex>();
        for (Hex hex : moveZone) {
            if (hex.sameColor(color)) {
                if (hex.containsBuilding()) junkList.add(hex);
            }
        }
        moveZone.removeAll(junkList);
    }


    private void excludeFriendlyUnitsFromMoveZone(ArrayList<Hex> moveZone) {
        ArrayList<Hex> junkList = new ArrayList<Hex>();
        for (Hex hex : moveZone) {
            if (hex.sameColor(color)) {
                if (hex.containsUnit()) junkList.add(hex);
            }
        }
        moveZone.removeAll(junkList);
    }


    int numberOfFriendlyHexesNearby(Hex hex) {
        return hex.numberOfFriendlyHexesNearby();
    }


    int howManyUnitsInProvince(Province province) {
        int c = 0;
        for (Hex hex : province.hexList) {
            if (hex.containsUnit()) c++;
        }
        return c;
    }
}
