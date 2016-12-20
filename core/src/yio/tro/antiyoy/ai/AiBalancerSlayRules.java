package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AiBalancerSlayRules extends AiExpertSlayRules implements Comparator<Hex>{

    private int[] playerHexCount;
    private ArrayList<Hex> propagationList;


    public AiBalancerSlayRules(GameController gameController, int color) {
        super(gameController, color);
        propagationList = new ArrayList<>();
    }


    private void updateSortConditions() {
        playerHexCount = gameController.getPlayerHexCount();
    }


    @Override
    void decideAboutUnit(Unit unit, ArrayList<Hex> moveZone, Province province) {
        // cleaning palms has highest priority
        if (unit.strength <= 2 && checkToCleanSomePalms(unit, moveZone, province)) return;

        boolean cleanedTrees = checkToCleanSomeTrees(unit, moveZone, province);
        if (cleanedTrees) return;

        ArrayList<Hex> attackableHexes = findAttackableHexes(unit.getColor(), moveZone);
        if (attackableHexes.size() > 0) { // attack something
            tryToAttackSomething(unit, province, attackableHexes);
        } else { // nothing to attack
            if (unit.currHex.isInPerimeter()) {
                pushUnitToBetterDefense(unit, province);
            }

            checkToSwapUnitForTower(unit, moveZone, province);
        }
    }


    @Override
    void pushUnitToBetterDefense(Unit unit, Province province) {
        if (!unit.isReadyToMove()) return;

        for (int i = 0; i < 6; i++) {
            Hex adjHex = unit.currHex.adjacentHex(i);
            if (!adjHex.active) continue;
            if (!adjHex.sameColor(unit.currHex)) continue;
            if (!adjHex.isFree()) continue;

            if (predictDefenseGainWithUnit(adjHex, unit) < 3) continue;

            gameController.moveUnit(unit, adjHex, province);
        }
    }


    protected int predictDefenseGainWithUnit(Hex hex, Unit unit) {
        int defenseGain = 0;

        defenseGain -= hex.getDefenseNumber();
        defenseGain += unit.strength;

        for (int i = 0; i < 6; i++) {
            Hex adjHex = unit.currHex.adjacentHex(i);
            if (!adjHex.active) continue;
            if (!adjHex.sameColor(unit.currHex)) continue;

            defenseGain -= adjHex.getDefenseNumber();
            defenseGain += unit.strength;
        }

        return defenseGain;
    }


    private void checkToSwapUnitForTower(Unit unit, ArrayList<Hex> moveZone, Province province) {
        if (!unit.isReadyToMove()) return;
        if (!province.hasMoneyForTower()) return;
        if (unit.currHex.hasThisObjectNearby(Hex.OBJECT_TOWER)) return;

        // remember that hex
        int x = unit.currHex.index1;
        int y = unit.currHex.index2;

        // move unit away
        gameController.moveUnit(unit, moveZone.get(random.nextInt(moveZone.size())), province);

        // place tower
        gameController.buildTower(province, gameController.field[x][y]);
    }


    @Override
    protected void tryToAttackSomething(Unit unit, Province province, ArrayList<Hex> attackableHexes) {
        if (!unitCanMoveSafely(unit)) return;
        Hex mostAttackableHex = findMostAttractiveHex(attackableHexes, unit, unit.strength);
        if (mostAttackableHex == null) return;
        gameController.moveUnit(unit, mostAttackableHex, province);
    }


    Hex findMostAttractiveHex(ArrayList<Hex> attackableHexes, Unit unit, int strength) {
        if (strength == 3 || strength == 4) {
            Hex hex = findHexAttractiveToBaron(attackableHexes, strength);
            if (hex != null) return hex;
        }

        Hex result = null;
        int currMax = -1;
        for (Hex attackableHex : attackableHexes) {
            // this doesn't fucking work...
//            if (!hasSafePathToTown(getNearbyHexWithColor(attackableHex, unit.getColor()), unit)) continue;
            int currNum = getAttackAllure(attackableHex, unit.getColor());
            if (currNum > currMax) {
                currMax = currNum;
                result = attackableHex;
            }
        }
        return result;
    }


    protected Hex getNearbyHexWithColor(Hex src, int color) {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = src.adjacentHex(i);
            if (!adjHex.active) continue;
            if (!adjHex.sameColor(color)) continue;
            if (adjHex.numberOfFriendlyHexesNearby() == 0) continue;
            return adjHex;
        }
        return null;
    }


    @Override
    int getAttackAllure(Hex hex, int color) {
        int c = 0;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.adjacentHex(i);
            if (adjHex.active && adjHex.sameColor(color)) c++;
            if (adjHex.active && adjHex.sameColor(color) && adjHex.objectInside == Hex.OBJECT_TOWN) c += 5;
        }
        return c;
    }


    @Override
    void tryToBuildUnits(Province province) {
        tryToBuildUnitsOnPalms(province);

        for (int i = 1; i <= 4; i++) {
            if (!province.hasEnoughIncomeToAffordUnit(i, 5)) break;
            while (province.hasMoneyForUnit(i)) {
                if (!tryToAttackWithStrength(province, i)) break;
            }
        }

        // this is to kick start province
        if (province.hasMoneyForUnit(1) && howManyUnitsInProvince(province) <= 1)
            tryToAttackWithStrength(province, 1);
    }


    @Override
    protected boolean isHexDefendedBySomethingElse(Hex hex, Unit unit) {
        if (hex.getDefenseNumber(unit) == 0) return false;
        return hex.getDefenseNumber() - hex.getDefenseNumber(unit) < 2;
    }


    protected int predictDefenseLossWithoutUnit(Unit unit) {
        int defenseLoss = 0;

        defenseLoss += unit.currHex.getDefenseNumber() - unit.currHex.getDefenseNumber(unit);

        for (int i = 0; i < 6; i++) {
            Hex adjHex = unit.currHex.adjacentHex(i);
            if (!adjHex.active) continue;
            if (!adjHex.sameColor(unit.currHex)) continue;
            defenseLoss += adjHex.getDefenseNumber() - adjHex.getDefenseNumber(unit);
        }

        return defenseLoss;
    }


    protected boolean hasSafePathToTown(Hex startHex, Unit attackUnit) {
        propagationList.clear();

        Province provinceByHex = gameController.getProvinceByHex(startHex);
        for (Hex hex : provinceByHex.hexList) {
            hex.flag = false;
        }

        propagationList.add(startHex);

        while (propagationList.size() > 0) {
            Hex hex = propagationList.get(0);
            propagationList.remove(hex);
            if (hex.objectInside == Hex.OBJECT_TOWN) return true;
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.adjacentHex(i);
                if (!adjHex.active) continue;
                if (!adjHex.sameColor(startHex)) continue;
                if (adjHex.flag) continue;
                if (adjHex.getDefenseNumber(attackUnit) == 0) continue;
                adjHex.flag = true;
                propagationList.add(adjHex);
            }
        }

        return false;
    }


    @Override
    ArrayList<Hex> findAttackableHexes(int attackerColor, ArrayList<Hex> moveZone) {
        ArrayList<Hex> result = new ArrayList<Hex>();
        for (Hex hex : moveZone) {
            if (hex.colorIndex != attackerColor) result.add(hex);
        }

        updateSortConditions();
        // top players will be attacked first
        Collections.sort(result, this);

        return result;
    }


    private int unitsNearby(Hex hex) {
        int c = 0;

        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.adjacentHex(i);
            if (!adjHex.active) continue;
            if (!adjHex.sameColor(color)) continue;
            if (!adjHex.containsUnit() || !adjHex.containsTower()) continue;
            c++;
        }

        return c;
    }


    @Override
    public int compare(Hex a, Hex b) {
        int aDefense = unitsNearby(a);
        int bDefense = unitsNearby(b);

        if (aDefense == bDefense) {
            return playerHexCount[b.colorIndex] - playerHexCount[a.colorIndex];
        }

        return bDefense - aDefense;
    }
}
