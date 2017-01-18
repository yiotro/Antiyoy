package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.Fonts;
import yio.tro.antiyoy.LanguagesManager;
import yio.tro.antiyoy.YioGdxGame;

import java.util.*;

/**
 * Created by ivan on 27.05.2015.
 */
public class Province {

    int money;
    public ArrayList<Hex> hexList, tempList;
    private GameController gameController;
    public String name;
    public float nameWidth;
    private static String partA[], partB[], partC[];


    public Province(GameController gameController, ArrayList<Hex> hexList) {
        this.gameController = gameController;
        this.hexList = new ArrayList<Hex>(hexList);
        tempList = new ArrayList<Hex>();
        money = 10;
    }


    public static void decodeCityNameParts() {
        LanguagesManager languagesManager = LanguagesManager.getInstance();

        ArrayList<String> tokensA = decodeCityNamePart(languagesManager.getString("city_name_one"));
        partA = new String[tokensA.size()];
        for (int i = 0; i < tokensA.size(); i++) {
            partA[i] = tokensA.get(i);
        }

        ArrayList<String> tokensB = decodeCityNamePart(languagesManager.getString("city_name_two"));
        partB = new String[tokensB.size()];
        for (int i = 0; i < tokensB.size(); i++) {
            partB[i] = tokensB.get(i);
        }

        ArrayList<String> tokensC = decodeCityNamePart(languagesManager.getString("city_name_three"));
        partC = new String[tokensC.size()];
        for (int i = 0; i < tokensC.size(); i++) {
            partC[i] = tokensC.get(i);
        }
    }


    private static ArrayList<String> decodeCityNamePart(String src) {
        StringTokenizer tokenizer = new StringTokenizer(src, ", ");

        ArrayList<String> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("X")) continue;
            tokens.add(token);
        }

        return tokens;
    }


    void placeCapitalInRandomPlace(Random random) {
        Hex randomPlace = getFreeHex(random);
        if (randomPlace == null) randomPlace = getPlaceToBuildUnit();
        if (randomPlace == null) randomPlace = getRandomHex();
        gameController.cleanOutHex(randomPlace);
        gameController.addSolidObject(randomPlace, Hex.OBJECT_TOWN);
        gameController.addAnimHex(randomPlace);
        gameController.updateCacheOnceAfterSomeTime();
        randomPlace.lastColorIndex = randomPlace.colorIndex;
        randomPlace.animFactor.setValues(0, 0);
        randomPlace.animFactor.beginSpawning(1, 2);
        updateName();
    }


    boolean hasCapital() {
        for (Hex hex : hexList)
            if (hex.objectInside == Hex.OBJECT_TOWN)
                return true;
        return false;
    }


    public Hex getCapital() {
        for (Hex hex : hexList)
            if (hex.objectInside == Hex.OBJECT_TOWN)
                return hex;
        return hexList.get(0);
    }


    private Hex getRandomHex() {
        return hexList.get(gameController.random.nextInt(hexList.size()));
    }


    private Hex getPlaceToBuildUnit() {
        tempList.clear();
        for (Hex hex : hexList)
            if (hex.isFree() || hex.containsTree())
                tempList.add(hex);
        if (tempList.size() == 0) return null;
        return tempList.get(YioGdxGame.random.nextInt(tempList.size()));
    }


    Province getSnapshotCopy() {
        Province copy = new Province(gameController, hexList);
        copy.money = money;
//        copy.capital = capital.getSnapshotCopy();
        return copy;
    }


    private Hex getFreeHex(Random random) {
        tempList.clear();
        for (Hex hex : hexList)
            if (hex.isFree())
                tempList.add(hex);
        if (tempList.size() == 0) return null;
        return tempList.get(random.nextInt(tempList.size()));
    }


    String getBalanceString() {
        int balance = getIncome() - getTaxes();
        if (balance > 0) return "+" + balance;
        return "" + balance;
    }


    public int getIncome() {
        int income = 0;
        for (Hex hex : hexList) {
            if (!hex.containsTree()) income++;
            if (!GameRules.slay_rules && hex.objectInside == Hex.OBJECT_FARM) income += 4;
        }
        return income;
    }


    int getTaxes() {
        int taxes = 0;
        for (Hex hex : hexList) {
            if (hex.containsUnit()) taxes += hex.unit.getTax();
            if (!GameRules.slay_rules) {
                if (hex.objectInside == Hex.OBJECT_TOWER) taxes += 1;
                if (hex.objectInside == Hex.OBJECT_STRONG_TOWER) taxes += 10;
            }
        }
        return taxes;
    }


    private void clearFromHouses() {
        for (Hex hex : hexList)
            if (hex.objectInside == Hex.OBJECT_TOWN)
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
        StringBuffer stringBuffer = new StringBuffer();
        Hex capitalHex = getCapital();
        Random random = new Random(capitalHex.index1 * capitalHex.index2);

        stringBuffer.append(partA[random.nextInt(partA.length)]);
        stringBuffer.append(partB[random.nextInt(partB.length)]);
        stringBuffer.append(partC[random.nextInt(partC.length)]);
        stringBuffer.setCharAt(0, Character.toUpperCase(stringBuffer.charAt(0)));

        setName(stringBuffer.toString());
    }


    public void setName(String name) {
        this.name = name;
        nameWidth = 0.5f * YioGdxGame.getTextWidth(Fonts.cityFont, name) + 0.1f * gameController.yioGdxGame.gameView.hexViewSize;
    }


    void setCapital(Hex hex) {
        clearFromHouses();
        gameController.addSolidObject(hex, Hex.OBJECT_TOWN);
        updateName();
    }


    boolean hasSomeoneReadyToMove() {
        for (Hex hex : hexList) {
            if (hex.containsUnit() && hex.unit.isReadyToMove()) return true;
        }
        return false;
    }


    public boolean hasEnoughIncomeToAffordUnit(int strength) {
        return hasEnoughIncomeToAffordUnit(strength, 2);
    }


    public boolean hasEnoughIncomeToAffordUnit(int strength, int turnsToSurvive) {
        int newIncome = getIncome() - getTaxes() - Unit.getTax(strength);
        if (money + turnsToSurvive * newIncome >= 0) return true;
        return false;
    }


    public boolean hasMoneyForUnit(int strength) {
        return money >= GameRules.PRICE_UNIT * strength;
    }


    public boolean hasMoneyForTower() {
        return money >= GameRules.PRICE_TOWER;
    }


    public boolean hasMoneyForFarm() {
        return money >= GameRules.PRICE_FARM + getExtraFarmCost();
    }


    public boolean hasMoneyForStrongTower() {
        return money >= GameRules.PRICE_STRONG_TOWER;
    }


    public int getExtraFarmCost() {
        int c = 0;

        for (Hex hex : hexList) {
            if (hex.objectInside == Hex.OBJECT_FARM) {
                c += 2;
            }
        }

        return c;
    }


    boolean containsHex(Hex hex) {
        return hexList.contains(hex);
    }


    public int getColor() {
        if (hexList.size() == 0) return -1;
        return hexList.get(0).colorIndex;
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
}
