package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.gameplay.Hex;

import java.util.ArrayList;

public class EdcLinkedChecker {

    EditorProvinceManager editorProvinceManager;
    ArrayList<Hex> propagationList;
    private ArrayList<Hex> hexList;
    EditorProvinceData provinceData;


    public EdcLinkedChecker(EditorProvinceManager editorProvinceManager) {
        this.editorProvinceManager = editorProvinceManager;
        propagationList = new ArrayList<>();
    }


    public boolean isLinked(EditorProvinceData province) {
        provinceData = province;
        hexList = province.hexList;
        if (hexList.size() < 2) return true;

        prepare();
        while (propagationList.size() > 0) {
            Hex hex = propagationList.get(0);
            propagationList.remove(0);
            applyIteration(hex);
        }

        return isWholeHexListFlagged();
    }


    private void prepare() {
        propagationList.clear();
        prepareHexList();
        Hex hex = hexList.get(0);
        addHexToPropagationList(hex);
    }


    private boolean isWholeHexListFlagged() {
        for (Hex hex : hexList) {
            if (hex.flag) continue;
            return false;
        }
        return true;
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
    }


    private void prepareHexList() {
        for (Hex hex : hexList) {
            hex.flag = false;
        }
    }
}
