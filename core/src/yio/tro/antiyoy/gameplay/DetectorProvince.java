package yio.tro.antiyoy.gameplay;

import java.util.ArrayList;

public class DetectorProvince {


    private ArrayList<Hex> provinceList;
    private ArrayList<Hex> propagationList;
    private Hex tempHex;
    private Hex adjHex;


    public DetectorProvince() {
        provinceList = new ArrayList<Hex>();
        propagationList = new ArrayList<Hex>();
    }


    public ArrayList<Hex> detectProvince(Hex startHex) {
        provinceList.clear();

        propagationList.clear();
        propagationList.add(startHex);

        if (startHex.colorIndex == FieldController.NEUTRAL_LANDS_INDEX) {
            provinceList.add(startHex);
            return provinceList;
        }

        while (propagationList.size() > 0) {
            tempHex = propagationList.get(0);
            provinceList.add(tempHex);
            propagationList.remove(0);

            for (int i = 0; i < 6; i++) {
                adjHex = tempHex.getAdjacentHex(i);
                if (belongsToSameProvince(adjHex)) {
                    propagationList.add(adjHex);
                }
            }
        }
        return provinceList;

    }


    private boolean belongsToSameProvince(Hex adjHex) {
        if (adjHex == null) return false;
        if (!adjHex.active) return false;
        if (!adjHex.sameColor(tempHex)) return false;
        if (propagationList.contains(adjHex)) return false;
        if (provinceList.contains(adjHex)) return false;

        return true;
    }
}