package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class EdcSnapshot {

    EditorProvinceManager editorProvinceManager;
    public ArrayList<EditorProvinceData> list;
    ObjectPoolYio<EditorProvinceData> pool;


    public EdcSnapshot(EditorProvinceManager editorProvinceManager) {
        this.editorProvinceManager = editorProvinceManager;
        list = new ArrayList<>();
        initPools();
    }


    void clear () {
        pool.clearExternalList();
    }


    void update() {
        clear();
        for (EditorProvinceData editorProvinceData : editorProvinceManager.provincesList) {
            EditorProvinceData freshObject = pool.getFreshObject();
            freshObject.copySomeDataFrom(editorProvinceData);
            freshObject.hexList.clear();
            freshObject.hexList.addAll(editorProvinceData.hexList);
        }
    }


    EditorProvinceData getParentFor(EditorProvinceData editorProvinceData) {
        EditorProvinceData parent = null;
        int bestResult = 0;
        for (EditorProvinceData provinceData : list) {
            int currentResult = provinceData.countIntersection(editorProvinceData);
            if (currentResult > bestResult) {
                bestResult = currentResult;
                parent = provinceData;
            }
        }
        return parent;
    }


    private void initPools() {
        pool = new ObjectPoolYio<EditorProvinceData>(list) {
            @Override
            public EditorProvinceData makeNewObject() {
                return new EditorProvinceData();
            }
        };
    }


    public void showInConsole() {
        System.out.println();
        System.out.println("EdcSnapshot.showInConsole");
        for (EditorProvinceData editorProvinceData : list) {
            System.out.println("- " + editorProvinceData);
        }
    }
}
