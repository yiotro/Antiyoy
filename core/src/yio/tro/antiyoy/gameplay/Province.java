package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.PointYio;

import java.util.*;

/**
 * Created by yiotro on 27.05.2015.
 */
public class Province {

    public static final int DEFAULT_MONEY = 10;
    public int money;
    public ArrayList<Hex> hexList, tempList;
    private GameController gameController;
    public String name;
    public float nameWidth;
    PointYio tempPoint;


    public Province(GameController gameController, ArrayList<Hex> hexList) {
        this.gameController = gameController;
        this.hexList = new ArrayList<>(hexList);
        tempList = new ArrayList<>();
        money = DEFAULT_MONEY;
        tempPoint = new PointYio();
    }


    void placeCapitalInRandomPlace(Random random) {
        if (GameRules.replayMode) return;

        Hex randomPlace = getFreeHex(random);
        if (randomPlace == null) {
            randomPlace = getAnyHexExceptTowers(random);
        }
        if (randomPlace == null) {
            randomPlace = getRandomHex(random);
        }

        gameController.cleanOutHex(randomPlace);
        gameController.addSolidObject(randomPlace, Obj.TOWN);
        gameController.replayManager.onCitySpawned(randomPlace);
        gameController.addAnimHex(randomPlace);
        gameController.updateCacheOnceAfterSomeTime();
        randomPlace.previousFraction = randomPlace.fraction;
        randomPlace.animFactor.setValues(0, 0);
        randomPlace.animFactor.appear(1, 2);
        updateName();
    }


    boolean hasCapital() {
        for (Hex hex : hexList) {
            if (hex.objectInside == Obj.TOWN) {
                return true;
            }
        }
        return false;
    }


    public Hex getStrongTower() {
        for (Hex hex : hexList) {
            if (hex.objectInside == Obj.STRONG_TOWER) {
                return hex;
            }
        }
        return null;
    }


    public Hex getCapital() {
        for (Hex hex : hexList) {
            if (hex.objectInside != Obj.TOWN) continue;
            return hex;
        }
        return hexList.get(0);
    }


    public Hex getRandomHex() {
        return getRandomHex(gameController.random);
    }


    public Hex getRandomHex(Random random) {
        return hexList.get(random.nextInt(hexList.size()));
    }


    private Hex getAnyHexExceptTowers(Random random) {
        tempList.clear();
        for (Hex hex : hexList) {
            if (hex.containsTower()) continue;
            tempList.add(hex);
        }
        if (tempList.size() == 0) return null;
        return tempList.get(random.nextInt(tempList.size()));
    }


    Province getSnapshotCopy() {
        Province copy = new Province(gameController, hexList);
        copy.money = money;
//        copy.capital = capital.getSnapshotCopy();
        return copy;
    }


    private Hex getFreeHex(Random random) {
        tempList.clear();
        for (Hex hex : hexList) {
            if (!hex.isFree()) continue;
            tempList.add(hex);
        }
        if (tempList.size() == 0) return null;
        return tempList.get(random.nextInt(tempList.size()));
    }


    public int getProfit() {
        return getIncome() - getTaxes() + getDotations();
    }


    public String getProfitString() {
        int balance = getProfit();
        if (balance > 0) return "+" + balance;
        return "" + balance;
    }


    public int getIncome() {
        int income = 0;

        for (Hex hex : hexList) {
            income += gameController.ruleset.getHexIncome(hex);
        }

        return income;
    }


    public boolean containsTrees() {
        for (Hex hex : hexList) {
            if (hex.containsTree()) return true;
        }
        return false;
    }


    public int countUnits(int strength) {
        int c = 0;
        for (Hex hex : hexList) {
            if (!hex.hasUnit()) continue;
            if (strength != -1 && hex.unit.strength != strength) continue;
            c++;
        }
        return c;
    }


    public int countObjects(int objType) {
        int c = 0;
        for (Hex hex : hexList) {
            if (hex.objectInside != objType) continue;
            c++;
        }
        return c;
    }


    public int getTaxes() {
        int taxes = 0;

        for (Hex hex : hexList) {
            taxes += gameController.ruleset.getHexTax(hex);
        }

        return taxes;
    }


    public int getUnitsTaxes() {
        int sum = 0;
        for (Hex hex : hexList) {
            if (!hex.containsUnit()) continue;
            sum += gameController.ruleset.getUnitTax(hex.unit.strength);
        }
        return sum;
    }


    public int getTowerTaxes() {
        int sum = 0;
        for (Hex hex : hexList) {
            if (!hex.containsTower()) continue;
            sum += gameController.ruleset.getHexTax(hex);
        }
        return sum;
    }


    public int getDotations() {
        if (!GameRules.diplomacyEnabled) return 0;

        return gameController.fieldManager.diplomacyManager.getProvinceDotations(this);
    }


    public float getIncomeCoefficient() {
        int n = 0;
        int fraction = getFraction();

        for (Province province : gameController.fieldManager.provinces) {
            if (province.getFraction() != fraction) continue;

            n++;
        }

        return 1f / (float) n;
    }


    private void clearFromHouses() {
        for (Hex hex : hexList)
            if (hex.objectInside == Obj.TOWN)
                gameController.cleanOutHex(hex);
    }


    public boolean isSelected() {
        if (hexList.size() == 0) return false;
        return hexList.get(0).isSelected();
    }


    public String getName() {
        if (name == null) {
            updateName();
        }
        return name;
    }


    public void updateName() {
        setName(gameController.namingManager.getProvinceName(this));
    }


    public void setName(String name) {
        this.name = name;
        nameWidth = 0.5f * YioGdxGame.getTextWidth(Fonts.microFont, name) + 0.1f * gameController.yioGdxGame.gameView.hexViewSize;
    }


    void setCapital(Hex hex) {
        clearFromHouses();
        gameController.addSolidObject(hex, Obj.TOWN);
        updateName();
    }


    boolean hasSomeoneReadyToMove() {
        for (Hex hex : hexList) {
            if (hex.containsUnit() && hex.unit.isReadyToMove()) return true;
        }
        return false;
    }


    public boolean canAiAffordUnit(int strength) {
        return canAiAffordUnit(strength, strength + 1);
    }


    public boolean canAiAffordUnit(int strength, int turnsToSurvive) {
        if (GameRules.diplomacyEnabled) {
            if (!gameController.fieldManager.diplomacyManager.isProvinceAllowedToBuildUnit(this, strength)) {
                return false;
            }
        }

        int newIncome = getProfit() - gameController.ruleset.getUnitTax(strength);
        return money + turnsToSurvive * newIncome >= 0;
    }


    public boolean canBuildUnit(int strength) {
        if (GameRules.replayMode) return true;

        return gameController.ruleset.canBuildUnit(this, strength);
    }


    public boolean isNearFraction(int otherFraction) {
        for (Hex hex : hexList) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (adjacentHex == null) continue;
                if (adjacentHex.isNullHex()) continue;
                if (!adjacentHex.active) continue;
                if (adjacentHex.fraction != otherFraction) continue;
                return true;
            }
        }
        return false;
    }


    public boolean hasMoneyForTower() {
        return money >= GameRules.PRICE_TOWER;
    }


    public boolean hasMoneyForFarm() {
        return money >= getCurrentFarmPrice();
    }


    public int getCurrentFarmPrice() {
        return GameRules.PRICE_FARM + getExtraFarmCost();
    }


    public boolean hasMoneyForStrongTower() {
        return money >= GameRules.PRICE_STRONG_TOWER;
    }


    public boolean hasMoneyForTree() {
        return money >= GameRules.PRICE_TREE;
    }


    public int getExtraFarmCost() {
        int c = 0;

        for (Hex hex : hexList) {
            if (hex.objectInside == Obj.FARM) {
                c += 2;
            }
        }

        return c;
    }


    public boolean equals(Province province) {
        for (Hex hex : hexList) {
            if (!province.containsHex(hex)) return false;
        }

        for (Hex hex : province.hexList) {
            if (!containsHex(hex)) return false;
        }

        return true;
    }


    public boolean containsHex(Hex hex) {
        return hexList.contains(hex);
    }


    public void focusCameraOnThis() {
        tempPoint.reset();

        for (Hex hex : hexList) {
            tempPoint.x += hex.pos.x;
            tempPoint.y += hex.pos.y;
        }

        tempPoint.x /= hexList.size();
        tempPoint.y /= hexList.size();

        gameController.cameraController.focusOnPoint(tempPoint);
    }


    public int getFraction() {
        if (hexList.size() == 0) return -1;
        return hexList.get(0).fraction;
    }


    void addHex(Hex hex) {
        if (containsHex(hex)) return;
        ListIterator iterator = hexList.listIterator();
        iterator.add(hex);
    }


    void setHexList(ArrayList<Hex> list) {
        hexList = new ArrayList<Hex>(list);
    }


    void close() {
        gameController = null;
    }


    public boolean intersects(Province province) {
        for (Hex hex : province.hexList) {
            if (containsHex(hex)) return true;
        }
        return false;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Province" + "(").append(hexList.size()).append(")").append(":");
        for (Hex hex : hexList) {
            builder.append(" ").append(hex);
        }
        builder.append("]");

        return builder.toString();
    }
}
