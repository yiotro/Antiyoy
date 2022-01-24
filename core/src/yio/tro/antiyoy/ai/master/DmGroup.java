package yio.tro.antiyoy.ai.master;

import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Unit;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.ArrayList;

public class DmGroup implements ReusableYio {

    ArrayList<Hex> areaList;
    int maxStrength;
    double averageStrength;
    double danger;
    int fraction;
    ArrayList<Hex> supportLands;
    ArrayList<Hex> contactZone;


    public DmGroup() {
        areaList = new ArrayList<>();
        supportLands = new ArrayList<>();
        contactZone = new ArrayList<>();
    }


    boolean contains(Unit unit) {
        for (Hex hex : areaList) {
            if (hex.containsUnit() && hex.unit == unit) return true;
        }
        return false;
    }


    @Override
    public void reset() {
        areaList.clear();
        maxStrength = 0;
        averageStrength = 0;
        danger = 0;
        fraction = -1;
        supportLands.clear();
        contactZone.clear();
    }
}
