package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.PointYio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class AiRestoredBalancerGeneric extends AbstractAi {

    public static final int MAX_EXTRA_FARM_COST = 80;
    private int[] playerHexCount;
    private ArrayList<Hex> propagationList;
    private ArrayList<Hex> result;
    private final Hex tempHex;
    private ArrayList<Hex> hexesInPerimeter;
    Comparator<Hex> comparator;
    final Random random;
    protected ArrayList<Province> nearbyProvinces;
    protected ArrayList<Unit> unitsReadyToMove;
    private ArrayList<Hex> tempResultList;
    private ArrayList<Hex> junkList;
    int numberOfUnitsBuiltThisTurn;


    public AiRestoredBalancerGeneric(GameController gameController, int fraction) {
        super(gameController, fraction);
        result = new ArrayList<>();
        tempHex = new Hex(0, 0, new PointYio(), gameController.fieldManager);
        hexesInPerimeter = new ArrayList<>();
        random = gameController.random;
        nearbyProvinces = new ArrayList<>();
        unitsReadyToMove = new ArrayList<>();
        tempResultList = new ArrayList<>();
        junkList = new ArrayList<>();
        initComparator();
    }


    private void initComparator() {
        comparator = new Comparator<Hex>() {
            @Override
            public int compare(Hex a, Hex b) {
                int aDefense = unitsNearby(a);
                int bDefense = unitsNearby(b);

                if (aDefense == bDefense) {
                    return getHexCount(b.fraction) - getHexCount(a.fraction);
                }

                return bDefense - aDefense;
            }
        };
    }


    private void updateSortConditions() {
        playerHexCount = gameController.fieldManager.getPlayerHexCount();
    }


    public void makeMove() {
        moveUnits();
        spendMoneyAndMergeUnits();
        checkToKillRedundantUnits();
        moveAfkUnits();
    }


    private void checkToKillRedundantUnits() {
        for (Province province : gameController.fieldManager.provinces) {
            checkToKillRedundantUnits(province);
        }
    }


    private void checkToKillRedundantUnits(Province province) {
        boolean detectedStrong = false;
        for (Hex hex : province.hexList) {
            if (!hex.containsUnit()) continue;
            if (!hex.unit.isReadyToMove()) return;
            if (hex.unit.strength >= 3) detectedStrong = true;
        }

        if (!detectedStrong) return;

        // so units are not doing anything. Time to kill them
        killRedundantUnits(province);
    }


    private void killRedundantUnits(Province province) {
        while (province.money >= GameRules.PRICE_UNIT && province.getProfit() >= 0) {
            Unit unitWithMaxStrengh = findUnitWithMaxStrenghExceptKnight(province);
            if (unitWithMaxStrengh == null) break;
            if (!canProvinceBuildUnit(province, 1)) break;
            buildUnit(province, unitWithMaxStrengh.currentHex, 1);
        }
    }


    protected boolean isOkToBuildNewFarm(Province srcProvince) {
        if (srcProvince.money > 2 * srcProvince.getCurrentFarmPrice()) return true;

        int srcArmyStrength = getArmyStrength(srcProvince);
        updateNearbyProvinces(srcProvince);
        for (Province province : nearbyProvinces) {
            if (province == srcProvince) continue;
            int armyStrength = getArmyStrength(province);
            if (srcArmyStrength < armyStrength / 2) return false;
        }

        if (findHexThatNeedsTower(srcProvince) != null) return false;

        return true;
    }


    private Unit findUnitWithMaxStrenghExceptKnight(Province province) {
        Unit result = null;

        for (Hex hex : province.hexList) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (unit.strength == 4) continue;
            if (result == null || unit.strength > result.strength) {
                result = unit;
            }
        }

        return result;
    }


    void decideAboutUnit(Unit unit, ArrayList<Hex> moveZone, Province province) {
        if (!unit.isReadyToMove()) {
            System.out.println("AiBalancerGenericRules.decideAboutUnit: received unit that is not ready to move");
            return;
        }

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


    // this is not really a good thing
    // it places towers in bad places
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

        if (hex.objectInside == Obj.FARM) {
            c *= 2;
        }

        return c;
    }


    void tryToBuildUnits(Province province) {
        tryToBuildUnitsOnPalms(province);
        tryToReinforceUnits(province);

        for (int i = 1; i <= 4; i++) {
            if (!province.canAiAffordUnit(i, 5)) break;
            while (canProvinceBuildUnit(province, i)) {
                if (!tryToAttackWithStrength(province, i)) break;
            }
        }

        // this is to kick start province
        if (canProvinceBuildUnit(province, 1) && howManyUnitsInProvince(province) <= 1) {
            tryToAttackWithStrength(province, 1);
        }
    }


    private void tryToReinforceUnits(Province province) {
        for (Hex hex : province.hexList) {
            if (!hex.containsUnit()) continue;
            Unit unit = hex.unit;
            if (unitHasToBeReinforced(unit) && province.canAiAffordUnit(unit.strength + 1)) {
                buildUnit(province, hex, 1);
            }
        }
    }


    private boolean unitHasToBeReinforced(Unit unit) {
        if (unit.strength == 4) return false;

        ArrayList<Hex> moveZone = gameController.detectMoveZone(unit.currentHex, unit.strength);
        if (!moveZoneContainsEnemyHexes(moveZone, unit.getFraction())) return false;

        ArrayList<Hex> attackableHexes = findAttackableHexes(unit.getFraction(), moveZone);
        if (attackableHexes.size() > 0) return false;

        return true;
    }


    private boolean moveZoneContainsEnemyHexes(ArrayList<Hex> moveZone, int unitFraction) {
        for (Hex hex : moveZone) {
            if (!hex.sameFraction(unitFraction)) return true;
        }
        return false;
    }


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


    ArrayList<Hex> findAttackableHexes(int attackerFraction, ArrayList<Hex> moveZone) {
        result.clear();
        for (Hex hex : moveZone) {
            if (hex.fraction == attackerFraction) continue;
            result.add(hex);
        }

        updateSortConditions();
        // top players will be attacked first
        Collections.sort(result, comparator);

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


    protected int getHexCount(int index) {
        if (index < 0) return 0;
        if (index >= playerHexCount.length) return 0;
        return playerHexCount[index];
    }


    boolean needTowerOnHex(Hex hex) {
        if (!hex.active) return false;
        if (!hex.isFree()) return false;

        updateNearbyProvinces(hex);
        if (nearbyProvinces.size() == 0) return false; // build towers only at front line

        return getPredictedDefenseGainByNewTower(hex) >= 3;
    }


    // expert


    protected boolean unitCanMoveSafely(Unit unit) {
        int leftBehindNumber = 0;
        for (int i = 0; i < 6; i++) {
            Hex adjHex = unit.currentHex.getAdjacentHex(i);
            if (adjHex.active && adjHex.sameFraction(unit.currentHex) && !isHexDefendedBySomethingElse(adjHex, unit) && adjHex.isInPerimeter())
                leftBehindNumber++;
        }
        return leftBehindNumber <= 3;
    }


    boolean hexHasFriendlyBuildingNearby(Hex hex) {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && adjHex.sameFraction(hex) && adjHex.containsBuilding()) return true;
        }
        return false;
    }


    Hex findMostAttractiveHex(ArrayList<Hex> attackableHexes, Province province, int strength) {
        if (strength == 3 || strength == 4) {
            Hex hex = findHexAttractiveToBaron(attackableHexes, strength);
            if (hex != null) return hex;
        }

        Hex result = null;
        int currMax = -1;
        for (Hex attackableHex : attackableHexes) {
            int currNum = getAttackAllure(attackableHex, province.getFraction());
            if (currNum > currMax) {
                currMax = currNum;
                result = attackableHex;
            }
        }
        return result;
    }


    private Hex findRandomHexInPerimeter(Province province) {
        hexesInPerimeter.clear();
        for (Hex hex : province.hexList) {
            if (hex.isInPerimeter()) hexesInPerimeter.add(hex);
        }
        if (hexesInPerimeter.size() == 0) return null;
        return hexesInPerimeter.get(random.nextInt(hexesInPerimeter.size()));
    }


    void moveAfkUnit(Province province, Unit unit) {
        Hex hexToMove = findRandomHexInPerimeter(province);
        if (hexToMove == null) return;
        tempHex.set(unit.currentHex);
        gameController.fieldManager.massMarchManager.performForSingleUnit(unit, hexToMove);
        if (tempHex.equals(unit.currentHex)) {
            moveAfkUnitQuickly(province, unit); // to prevent infinite loop
        }
    }


    void moveAfkUnitQuickly(Province province, Unit unit) {
        ArrayList<Hex> moveZone = gameController.detectMoveZone(unit.currentHex, unit.strength, GameRules.UNIT_MOVE_LIMIT);
        excludeFriendlyUnitsFromMoveZone(moveZone);
        excludeFriendlyBuildingsFromMoveZone(moveZone);
        if (moveZone.size() == 0) return;
        gameController.moveUnit(unit, moveZone.get(random.nextInt(moveZone.size())), province);
    }


    void tryToBuildTowers(Province province) {
        // try to build normal towers
        while (province.hasMoneyForTower()) {
            Hex hex = findHexThatNeedsTower(province);
            if (hex == null) break;

            gameController.fieldManager.buildTower(province, hex);
        }

        // try to build strong towers
        while (provinceCanAffordStrongTower(province)) {
            Hex hex = findHexForStrongTower(province);
            if (hex == null) break;

            gameController.fieldManager.buildStrongTower(province, hex);
        }
    }


    protected Hex findHexForStrongTower(Province province) {
        for (Hex hex : province.hexList) {
            if (hex.objectInside != Obj.TOWER) continue;

            if (needsStrongTowerOnHex(province, hex)) {
                return hex;
            }
        }

        return null;
    }


    protected boolean needsStrongTowerOnHex(Province province, Hex hex) {
        updateNearbyProvinces(hex);

        if (nearbyProvinces.size() == 0) return false;

        for (Province nearbyProvince : nearbyProvinces) {
            if (nearbyProvince.hexList.size() > province.hexList.size() / 2) {
                return true;
            }
        }

        return false;
    }


    protected boolean provinceCanAffordStrongTower(Province province) {
        if (!province.hasMoneyForStrongTower()) return false;
        if (province.getProfit() - GameRules.TAX_STRONG_TOWER < GameRules.PRICE_UNIT / 2) return false;

        return true;
    }


    // ai generic


    protected void spendMoney(Province province) {
        tryToBuildTowers(province);
        tryToBuildFarms(province);
        tryToBuildUnits(province);
    }


    protected void tryToBuildFarms(Province province) {
//        if (province.getExtraFarmCost() > province.getIncome()) return;
        if (province.getExtraFarmCost() > MAX_EXTRA_FARM_COST) return;

        while (province.hasMoneyForFarm()) {
            if (!isOkToBuildNewFarm(province)) return;
            Hex hex = findGoodHexForFarm(province);
            if (hex == null) return;
            gameController.fieldManager.buildFarm(province, hex);
        }
    }


    protected int getArmyStrength(Province province) {
        int sum = 0;
        for (Hex hex : province.hexList) {
            if (hex.containsUnit()) {
                sum += hex.unit.strength;
            }
        }
        return sum;
    }


    protected Hex findGoodHexForFarm(Province province) {
        if (!hasProvinceGoodHexForFarm(province)) return null;

        while (true) {
            Hex hex = province.hexList.get(random.nextInt(province.hexList.size()));
            if (isHexGoodForFarm(hex)) return hex;
        }
    }


    protected boolean hasProvinceGoodHexForFarm(Province province) {
        for (Hex hex : province.hexList) {
            if (!isHexGoodForFarm(hex)) continue;
            return true;
        }
        return false;
    }


    protected boolean isHexGoodForFarm(Hex hex) {
        if (!hex.isFree()) return false;
        if (!hex.hasThisSupportiveObjectNearby(Obj.TOWN) && !hex.hasThisSupportiveObjectNearby(Obj.FARM)) return false;
        return true;
    }


    // ai


    void updateUnitsReadyToMove() {
        unitsReadyToMove.clear();
        for (Province province : gameController.fieldManager.provinces) {
            if (province.getFraction() == fraction) {
                searchForUnitsReadyToMoveInProvince(province);
            }
        }
    }


    private void searchForUnitsReadyToMoveInProvince(Province province) {
        for (int k = province.hexList.size() - 1; k >= 0; k--) {
            Hex hex = province.hexList.get(k);
            if (hex.containsUnit() && hex.unit.isReadyToMove()) {
                unitsReadyToMove.add(hex.unit);
            }
        }
    }


    void moveUnits() {
        updateUnitsReadyToMove();
        for (Unit unit : unitsReadyToMove) {
            if (!unit.isReadyToMove()) {
                System.out.println("Problem in ArtificialIntelligence.moveUnits()");
                continue;
            }

            ArrayList<Hex> moveZone = gameController.detectMoveZone(unit.currentHex, unit.strength, GameRules.UNIT_MOVE_LIMIT);
            excludeFriendlyBuildingsFromMoveZone(moveZone);
            excludeFriendlyUnitsFromMoveZone(moveZone);
            if (moveZone.size() == 0) continue;
            Province provinceByHex = gameController.getProvinceByHex(unit.currentHex);
            if (provinceByHex == null) continue;
            decideAboutUnit(unit, moveZone, provinceByHex);
        }
    }


    void spendMoneyAndMergeUnits() {
        for (int i = 0; i < gameController.fieldManager.provinces.size(); i++) {
            Province province = gameController.fieldManager.provinces.get(i);
            if (province.getFraction() != fraction) continue;
            spendMoney(province);
            mergeUnits(province);
        }
    }


    void moveAfkUnits() {
        updateUnitsReadyToMove();
        for (Unit unit : unitsReadyToMove) {
            if (!unit.isReadyToMove()) continue;

            Province province = gameController.getProvinceByHex(unit.currentHex);
            if (province.hexList.size() > 20) {
                moveAfkUnit(province, unit);
            }
        }
    }


    @Override
    public void perform() {
        numberOfUnitsBuiltThisTurn = 0;
        makeMove();
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
        ArrayList<Hex> moveZone = gameController.detectMoveZone(unit.currentHex, unit.strength, GameRules.UNIT_MOVE_LIMIT);
        if (moveZone.size() == 0) return;
        for (Hex hex : moveZone) {
            if (!mergeConditions(province, unit, hex)) continue;
            gameController.moveUnit(unit, hex, province); // should not call mergeUnits() directly
            break;
        }
    }


    protected boolean mergeConditions(Province province, Unit unit, Hex hex) {
        if (!hex.sameFraction(unit.currentHex)) return false;
        if (!hex.containsUnit()) return false;
        if (!hex.unit.isReadyToMove()) return false;
        if (unit == hex.unit) return false;
        if (!gameController.ruleset.canMergeUnits(unit, hex.unit)) return false;
        if (!province.canAiAffordUnit(gameController.mergedUnitStrength(unit, hex.unit))) return false;
        return true;
    }


    protected Hex findHexThatNeedsTower(Province province) {
        for (Hex hex : province.hexList) {
            if (needTowerOnHex(hex)) return hex;
        }
        return null;
    }


    protected int getPredictedDefenseGainByNewTower(Hex hex) {
        int c = 0;

        if (hex.active && !hex.isDefendedByTower()) c++;

        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && hex.sameFraction(adjHex) && !adjHex.isDefendedByTower()) c++;
            if (adjHex.containsTower()) c--;
        }

        return c;
    }


    protected void updateNearbyProvinces(Province srcProvince) {
        nearbyProvinces.clear();

        for (Hex hex : srcProvince.hexList) {
            for (int i = 0; i < 6; i++) {
                Hex adjacentHex = hex.getAdjacentHex(i);
                checkToAddNearbyProvince(hex, adjacentHex);
            }
        }
    }


    protected void updateNearbyProvinces(Hex srcHex) {
        nearbyProvinces.clear();

        int j;
        for (int i = 0; i < 6; i++) {
            Hex adjacentHex = srcHex.getAdjacentHex(i);
            if (!adjacentHex.active) continue;

            Hex adjacentHex2 = adjacentHex.getAdjacentHex(i);
            j = i + 1;
            if (j >= 6) j = 0;
            Hex adjacentHex3 = adjacentHex.getAdjacentHex(j);

            checkToAddNearbyProvince(srcHex, adjacentHex);
            checkToAddNearbyProvince(srcHex, adjacentHex2);
            checkToAddNearbyProvince(srcHex, adjacentHex3);
        }
    }


    private void checkToAddNearbyProvince(Hex srcHex, Hex adjacentHex) {
        if (!adjacentHex.active) return;
        if (adjacentHex.isNeutral()) return;
        if (adjacentHex.sameFraction(srcHex)) return;

        Province provinceByHex = gameController.fieldManager.getProvinceByHex(adjacentHex);
        addProvinceToNearbyProvines(provinceByHex);
    }


    private void addProvinceToNearbyProvines(Province province) {
        if (province == null) return;
        if (nearbyProvinces.contains(province)) return;

        nearbyProvinces.listIterator().add(province);
    }


    boolean tryToBuiltUnitInsideProvince(Province province, int strength) {
        for (Hex hex : province.hexList) {
            if (!hex.nothingBlocksWayForUnit()) continue;
            if (!isAllowedToBuildNewUnit(province)) continue;

            buildUnit(province, hex, strength);
            return true;
        }
        return false;
    }


    protected boolean isAllowedToBuildNewUnit(Province province) {
        if (!GameRules.diplomacyEnabled) return true;
        if (gameController.playersNumber == 0) return true;
        if (numberOfUnitsBuiltThisTurn < getBuildLimitForProvince(province)) return true;
        return false;
    }


    private  int getBuildLimitForProvince(Province province) {
        int bottom = Math.max(3, province.hexList.size() / 4);
        return Math.min(bottom, 10);
    }


    protected void buildUnit(Province province, Hex hex, int strength) {
        boolean success = false;

        if (isAllowedToBuildNewUnit(province)) {
            success = gameController.fieldManager.buildUnit(province, hex, strength);
        }

        if (success) {
            numberOfUnitsBuiltThisTurn++;
        }
    }


    boolean tryToAttackWithStrength(Province province, int strength) {
        if (!isAllowedToBuildNewUnit(province)) return false;

        ArrayList<Hex> moveZone = gameController.detectMoveZone(province.getCapital(), strength);
        ArrayList<Hex> attackableHexes = findAttackableHexes(province.getFraction(), moveZone);
        if (attackableHexes.size() == 0) return false;

        Hex bestHexForAttack = findMostAttractiveHex(attackableHexes, province, strength);
        buildUnit(province, bestHexForAttack, strength);
        return true;
    }


    void tryToBuildUnitsOnPalms(Province province) {
        if (!province.canAiAffordUnit(1)) return;

        while (canProvinceBuildUnit(province, 1)) {
            ArrayList<Hex> moveZone = gameController.detectMoveZone(province.getCapital(), 1);
            boolean killedPalm = false;
            for (Hex hex : moveZone) {
                if (hex.objectInside != Obj.PALM || !hex.sameFraction(province)) continue;
                buildUnit(province, hex, 1);
                killedPalm = true;
            }
            if (!killedPalm) break;
        }
    }


    protected boolean canProvinceBuildUnit(Province province, int strength) {
        return province.canBuildUnit(strength) && isAllowedToBuildNewUnit(province);
    }


    boolean checkToCleanSomeTrees(Unit unit, ArrayList<Hex> moveZone, Province province) {
        for (Hex hex : moveZone) {
            if (hex.containsTree() && hex.sameFraction(unit.currentHex)) {
                gameController.moveUnit(unit, hex, province);
                return true;
            }
        }
        return false;
    }


    boolean checkToCleanSomePalms(Unit unit, ArrayList<Hex> moveZone, Province province) {
        for (Hex hex : moveZone) {
            if (hex.objectInside == Obj.PALM && hex.sameFraction(unit.currentHex)) {
                gameController.moveUnit(unit, hex, province);
                return true;
            }
        }
        return false;
    }


    boolean checkChance(double chance) {
        return random.nextDouble() < chance;
    }


    Hex findHexAttractiveToBaron(ArrayList<Hex> attackableHexes, int strength) {
        for (Hex attackableHex : attackableHexes) {
            if (attackableHex.objectInside == Obj.TOWER) return attackableHex;
            if (strength == 4 && attackableHex.objectInside == Obj.STRONG_TOWER) return attackableHex;
        }
        for (Hex attackableHex : attackableHexes) {
            if (attackableHex.isDefendedByTower()) return attackableHex;
        }
        return null;
    }


    private void excludeFriendlyBuildingsFromMoveZone(ArrayList<Hex> moveZone) {
        junkList.clear();
        for (Hex hex : moveZone) {
            if (hex.sameFraction(fraction)) {
                if (hex.containsBuilding()) junkList.add(hex);
            }
        }
        moveZone.removeAll(junkList);
    }


    private void excludeFriendlyUnitsFromMoveZone(ArrayList<Hex> moveZone) {
        junkList.clear();
        for (Hex hex : moveZone) {
            if (hex.sameFraction(fraction)) {
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


    public int getFraction() {
        return fraction;
    }


    public void setFraction(int fraction) {
        this.fraction = fraction;
    }


    @Override
    public String toString() {
        return "[AI: " + fraction + "]";
    }

}
