package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.gameplay.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AiBalancerSlayRules extends AiExpertSlayRules implements Comparator<Hex>{

    private int[] playerHexCount;
    private ArrayList<Hex> propagationList;
    private ArrayList<Hex> result;


    public AiBalancerSlayRules(GameController gameController, int fraction) {
        super(gameController, fraction);
        propagationList = new ArrayList<>();
        result = new ArrayList<Hex>();
    }


    private void updateSortConditions() {
        playerHexCount = gameController.fieldManager.getPlayerHexCount();
    }


    @Override
    void decideAboutUnit(Unit unit, ArrayList<Hex> moveZone, Province province) {
        // cleaning palms has highest priority
        if (unit.strength <= 2 && checkToCleanSomePalms(unit, moveZone, province)) return;

        boolean cleanedTrees = checkToCleanSomeTrees(unit, moveZone, province);
        if (cleanedTrees) return;

        ArrayList<Hex> attackableHexes = findAttackableHexes(unit.getFraction(), moveZone);
        if (attackableHexes.size() > 0) { // attack something
            tryToAttackSomething(unit, province, attackableHexes);
        } else { // nothing to attack
            if (unit.currentHex.isInPerimeter()) {
                pushUnitToBetterDefense(unit, province);
            }

//            checkToSwapUnitForTower(unit, moveZone, province);
        }
    }


    @Override
    void pushUnitToBetterDefense(Unit unit, Province province) {
        if (!unit.isReadyToMove()) return;

        for (int i = 0; i < 6; i++) {
            Hex adjHex = unit.currentHex.getAdjacentHex(i);
            if (!adjHex.active) continue;
            if (!adjHex.sameFraction(unit.currentHex)) continue;
            if (!adjHex.isFree()) continue;

            if (predictDefenseGainWithUnit(adjHex, unit) < 3) continue;

            gameController.moveUnit(unit, adjHex, province);
            break;
        }
    }


    protected int predictDefenseGainWithUnit(Hex hex, Unit unit) {
        int defenseGain = 0;

        defenseGain -= hex.getDefenseNumber();
        defenseGain += unit.strength;

        for (int i = 0; i < 6; i++) {
            Hex adjHex = unit.currentHex.getAdjacentHex(i);
            if (!adjHex.active) continue;
            if (!adjHex.sameFraction(unit.currentHex)) continue;

            defenseGain -= adjHex.getDefenseNumber();
            defenseGain += unit.strength;
        }

        return defenseGain;
    }


    private void checkToSwapUnitForTower(Unit unit, ArrayList<Hex> moveZone, Province province) {
        if (!unit.isReadyToMove()) return;
        if (!province.hasMoneyForTower()) return;
        if (unit.currentHex.hasThisSupportiveObjectNearby(Obj.TOWER)) return;

        // remember that hex
        int x = unit.currentHex.index1;
        int y = unit.currentHex.index2;

        // move unit away
        gameController.moveUnit(unit, moveZone.get(random.nextInt(moveZone.size())), province);

        // place tower
        gameController.fieldManager.buildTower(province, gameController.fieldManager.field[x][y]);
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
            int currNum = getAttackAllure(attackableHex, unit.getFraction());
            if (currNum > currMax) {
                currMax = currNum;
                result = attackableHex;
            }
        }
        return result;
    }


    @Override
    int getAttackAllure(Hex hex, int fraction) {
        int c = 0;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && adjHex.sameFraction(fraction)) {
                c++;
            }
            if (adjHex.active && adjHex.sameFraction(fraction) && adjHex.objectInside == Obj.TOWN) {
                c += 5;
            }
        }
        return c;
    }


    @Override
    void tryToBuildUnits(Province province) {
        tryToBuildUnitsOnPalms(province);

        for (int i = 1; i <= 4; i++) {
            if (!province.canAiAffordUnit(i, 5)) break;
            while (canProvinceBuildUnit(province, i)) {
                if (!tryToAttackWithStrength(province, i)) break;
            }
        }

        // this is to kick start province
        if (canProvinceBuildUnit(province, 1) && howManyUnitsInProvince(province) <= 1)
            tryToAttackWithStrength(province, 1);
    }


    @Override
    protected boolean isHexDefendedBySomethingElse(Hex hex, Unit unit) {
        if (hex.getDefenseNumber(unit) == 0) return false;
        return hex.getDefenseNumber() - hex.getDefenseNumber(unit) < 2;
    }


    protected int predictDefenseLossWithoutUnit(Unit unit) {
        int defenseLoss = 0;

        defenseLoss += unit.currentHex.getDefenseNumber() - unit.currentHex.getDefenseNumber(unit);

        for (int i = 0; i < 6; i++) {
            Hex adjHex = unit.currentHex.getAdjacentHex(i);
            if (!adjHex.active) continue;
            if (!adjHex.sameFraction(unit.currentHex)) continue;
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
            if (hex.objectInside == Obj.TOWN) return true;
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (!adjHex.active) continue;
                if (!adjHex.sameFraction(startHex)) continue;
                if (adjHex.flag) continue;
                if (adjHex.getDefenseNumber(attackUnit) == 0) continue;
                adjHex.flag = true;
                propagationList.add(adjHex);
            }
        }

        return false;
    }


    @Override
    ArrayList<Hex> findAttackableHexes(int attackerFraction, ArrayList<Hex> moveZone) {
        result.clear();
        for (Hex hex : moveZone) {
            if (hex.fraction == attackerFraction) continue;
            result.add(hex);
        }

        updateSortConditions();
        // top players will be attacked first
        Collections.sort(result, this);

        return result;
    }


    private int unitsNearby(Hex hex) {
        int c = 0;

        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (!adjHex.active) continue;
            if (!adjHex.sameFraction(fraction)) continue;
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
            return getHexCount(b.fraction) - getHexCount(a.fraction);
        }

        return bDefense - aDefense;
    }


    protected int getHexCount(int fraction) {
        if (fraction < 0) return 0;
        if (fraction >= playerHexCount.length) return 0;
        return playerHexCount[fraction];
    }
}
