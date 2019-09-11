package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
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
                id +
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
