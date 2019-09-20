package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.stuff.TimeMeasureYio;

import java.util.ArrayList;

public class EdcProvinceMaker {

    EditorProvinceManager editorProvinceManager;
    ArrayList<Hex> propagationList;
    private ArrayList<Hex> hexList;


    public EdcProvinceMaker(EditorProvinceManager editorProvinceManager) {
        this.editorProvinceManager = editorProvinceManager;
        propagationList = new ArrayList<>();
    }


    public void makeProvince(EditorProvinceData provinceData, Hex startHex) {
        hexList = provinceData.hexList;
        prepare(startHex);
        while (propagationList.size() > 0) {
            Hex hex = propagationList.get(0);
            propagationList.remove(0);
            applyIteration(hex);
        }
        provinceData.updateGeometricalCenter();
    }


    private void prepare(Hex startHex) {
        propagationList.clear();
        hexList.clear();
        addHexToPropagationList(startHex);
    }


    private void applyIteration(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (adjacentHex.isNullHex()) continue;
            if (!adjacentHex.active) continue;
            if (adjacentHex.fraction != hex.fraction) continue;
            if (adjacentHex.flag) continue;
            addHexToPropagationList(adjacentHex);
        }
    }


    private void addHexToPropagationList(Hex hex) {
        hex.flag = true;
        propagationList.add(hex);
        hexList.add(hex);
    }

}
