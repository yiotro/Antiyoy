package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;
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


    void copyStoredDataFrom(EditorProvinceData src) {
        name = src.name;
        startingMoney = src.startingMoney;
        id = src.id;
    }


    public int countFraction(int fraction) {
        int c = 0;
        for (Hex hex : hexList) {
            if (hex.fraction != fraction) continue;
            c++;
        }
        return c;
    }


    public Hex getHex(int fraction) {
        for (Hex hex : hexList) {
            if (hex.fraction != fraction) continue;
            return hex;
        }
        return null;
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


    void addHex(Hex hex) {
        if (hexList.contains(hex)) return;
        hexList.add(hex);
        updateGeometricalCenter();
    }


    void removeHex(Hex hex) {
        hexList.remove(hex);
        updateGeometricalCenter();
    }


    public boolean contains(Hex hex) {
        return hexList.contains(hex);
    }


    public boolean isEmpty() {
        return hexList.size() == 0;
    }


    int getFraction() {
        if (isEmpty()) return -1;
        return hexList.get(0).fraction;
    }


    void kill() {
        hexList.clear();
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


    @Override
    public void decode(String source) {
        String[] split = source.split("@");
        id = Integer.valueOf(split[2]);
        name = split[3];
        startingMoney = Integer.valueOf(split[4]);
    }


}
