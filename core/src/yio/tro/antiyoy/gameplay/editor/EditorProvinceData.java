package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.gameplay.name_generator.CityNameGenerator;
import yio.tro.antiyoy.gameplay.name_generator.NameGenerator;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.ArrayList;

public class EditorProvinceData implements ReusableYio, EncodeableYio{

    public String name;
    public int startingMoney;
    public int id;
    public ArrayList<Hex> hexList;
    public PointYio geometricalCenter;


    public EditorProvinceData() {
        hexList = new ArrayList<>();
        geometricalCenter = new PointYio();
    }


    @Override
    public void reset() {
        name = "";
        startingMoney = 10;
        hexList.clear();
        id = -1;
    }


    void copySomeDataFrom(EditorProvinceData src) {
        name = src.name;
        startingMoney = src.startingMoney;
        id = src.id;
    }


    public void fillWithDefaultData() {
        startingMoney = 10;
        generateRandomName();
    }


    private void generateRandomName() {
        name = CityNameGenerator.getInstance().generateName(hexList.get(0));
    }


    int countIntersection(EditorProvinceData anotherProvince) {
        for (Hex hex : hexList) {
            hex.flag = false;
        }
        for (Hex hex : anotherProvince.hexList) {
            hex.flag = true;
        }
        int c = 0;
        for (Hex hex : hexList) {
            if (!hex.flag) continue;
            c++;
        }
        return c;
    }


    public int countFraction(int fraction) {
        int c = 0;
        for (Hex hex : hexList) {
            if (hex.fraction != fraction) continue;
            c++;
        }
        return c;
    }


    public int getMajorFraction() {
        int majorFraction = -1;
        int majorValue = 0;
        for (int fraction = 0; fraction < GameRules.MAX_FRACTIONS_QUANTITY; fraction++) {
            int value = countFraction(fraction);
            if (value == 0) continue;
            if (majorFraction == -1 || value > majorValue) {
                majorFraction = fraction;
                majorValue = value;
            }
        }
        return majorFraction;
    }


    public void applyDataToRealProvince(Province province) {
        province.setName(name);
        province.money = startingMoney;
    }


    public boolean contains(Hex hex) {
        return hexList.contains(hex);
    }


    int getFraction() {
        return hexList.get(0).fraction;
    }


    public boolean isBigEnough() {
        return hexList.size() > 1;
    }


    public void updateGeometricalCenter() {
        geometricalCenter.reset();
        for (Hex hex : hexList) {
            geometricalCenter.x += hex.pos.x;
            geometricalCenter.y += hex.pos.y;
        }
        geometricalCenter.x /= hexList.size();
        geometricalCenter.y /= hexList.size();
    }


    public String getUniqueCode() {
        String s = super.toString();
        return s.substring(s.indexOf("@"));
    }


    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "[ProvinceData " +
                getUniqueCode() +
                ": " +
                name +
                " " +
                "f" + getFraction() +
                " " +
                hexList.size() +
                "]";
    }


    @Override
    public String encode() {
        Hex firstHex = hexList.get(0);
        return firstHex.index1 + "@" + firstHex.index2 + "@" + id + "@" + name + "@" + startingMoney;
    }


    public boolean containsInvalidSymbols() {
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == ' ') continue;
            if (c == '.') continue;
            if (!Fonts.getAllCharacters().contains("" + c)) return true;
        }
        return false;
    }


    @Override
    public void decode(String source) {
        String[] split = source.split("@");
        id = Integer.valueOf(split[2]);
        if (split.length < 4) {
            generateRandomName();
            return;
        }
        name = split[3];
        if (split.length > 4) {
            startingMoney = Integer.valueOf(split[4]);
        }

        if (containsInvalidSymbols()) {
            generateRandomName();
        }
    }


}
