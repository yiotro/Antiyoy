package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.data_storage.DecodeManager;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class EditorProvinceManager implements EncodeableYio{

    LevelEditorManager levelEditorManager;
    public ArrayList<EditorProvinceData> provincesList;
    ObjectPoolYio<EditorProvinceData> poolProvinces;
    ArrayList<Hex> tempList;
    EdcProvinceMaker edcProvinceMaker;
    ArrayList<EditorProvinceData> tempProvList;
    EdcSnapshot snapshot;


    public EditorProvinceManager(LevelEditorManager levelEditorManager) {
        this.levelEditorManager = levelEditorManager;
        provincesList = new ArrayList<>();
        tempList = new ArrayList<>();
        edcProvinceMaker = new EdcProvinceMaker(this);
        tempProvList = new ArrayList<>();
        snapshot = new EdcSnapshot(this);
        initPools();
    }


    private void initPools() {
        poolProvinces = new ObjectPoolYio<EditorProvinceData>(provincesList) {
            @Override
            public EditorProvinceData makeNewObject() {
                return new EditorProvinceData();
            }
        };
    }


    public void move() {

    }


    public void onExitedToPauseMenu() {
        performUpdate();
    }


    public void performUpdate() {
        snapshot.update();
        clear();
        resetFlags();
        makeProvinces();
        restoreDataFromSnapshot();
    }


    private void restoreDataFromSnapshot() {
        for (EditorProvinceData editorProvinceData : provincesList) {
            EditorProvinceData parent = snapshot.getParentFor(editorProvinceData);
            if (parent != null) {
                editorProvinceData.copySomeDataFrom(parent);
            } else {
                editorProvinceData.fillWithDefaultData();
            }
        }
    }


    private void makeProvinces() {
        for (Hex activeHex : getFieldController().activeHexes) {
            if (activeHex.isNeutral()) continue;
            if (activeHex.flag) continue;
            makeProvince(activeHex);
        }
    }


    private void makeProvince(Hex startHex) {
        EditorProvinceData freshObject = poolProvinces.getFreshObject();
        freshObject.setId(getIdForNewProvince());
        edcProvinceMaker.makeProvince(freshObject, startHex);

        if (!freshObject.isBigEnough()) {
            poolProvinces.removeFromExternalList(freshObject);
        }
    }


    private void resetFlags() {
        for (Hex activeHex : getFieldController().activeHexes) {
            activeHex.flag = false;
        }
    }


    public void defaultValues() {
        poolProvinces.clearExternalList();
    }


    public void onEndCreation() {

    }


    private FieldManager getFieldController() {
        return getGameController().fieldManager;
    }


    private GameController getGameController() {
        return levelEditorManager.gameController;
    }


    public EditorProvinceData getProvinceByHex(Hex hex) {
        for (EditorProvinceData editorProvince : provincesList) {
            if (!editorProvince.contains(hex)) continue;
            return editorProvince;
        }
        return null;
    }


    private int getIdForNewProvince() {
        int maxId = 0;
        for (EditorProvinceData editorProvinceData : provincesList) {
            if (editorProvinceData.id > maxId) {
                maxId = editorProvinceData.id;
            }
        }
        return maxId + 1;
    }


    public void onLevelRandomlyCreated() {
        clear();
    }


    public void clear() {
        poolProvinces.clearExternalList();
    }


    public void showProvincesInConsole() {
        System.out.println();
        System.out.println("EditorProvinceManager.showProvincesInConsole");
        System.out.println("provincesList.size() = " + provincesList.size());
        for (EditorProvinceData editorProvinceData : provincesList) {
            System.out.println("editorProvinceData = " + editorProvinceData);
        }
    }


    @Override
    public String encode() {
        StringBuilder builder = new StringBuilder();
        for (EditorProvinceData editorProvinceData : provincesList) {
            builder.append(editorProvinceData.encode()).append(",");
        }
        return builder.toString();
    }


    @Override
    public void decode(String source) {
        performUpdate();
        for (String token : source.split(",")) {
            String[] split = token.split("@");
            if (split.length < 2) continue;
            if (split[0].length() == 0) continue;
            int index1 = Integer.valueOf(split[0]);
            int index2 = Integer.valueOf(split[1]);
            Hex hex = getGameController().fieldManager.getHex(index1, index2);
            EditorProvinceData provinceByHex = getProvinceByHex(hex);
            if (provinceByHex == null) continue;
            provinceByHex.decode(token);
        }
    }


    public void onLevelImported(String levelCode) {
        DecodeManager decodeManager = getGameController().decodeManager;
        decodeManager.setSource(levelCode);
        String provincesSection = decodeManager.getSection("provinces");
        if (provincesSection == null) return;

        decode(provincesSection);
    }


    public EditorProvinceData getLargestProvince(int fraction) {
        EditorProvinceData result = null;
        for (EditorProvinceData editorProvinceData : provincesList) {
            if (editorProvinceData.getFraction() != fraction) continue;
            if (result == null || editorProvinceData.hexList.size() > result.hexList.size()) {
                result = editorProvinceData;
            }
        }
        return result;
    }


    public void checkToApplyData() {
        String source = getGameController().initialParameters.editorProvincesData;
        if (source == null) return;
        if (source.length() < 5) return;

        onEndCreation();
        decode(source);

        for (EditorProvinceData editorProvinceData : provincesList) {
            Province provinceByHex = getGameController().fieldManager.getProvinceByHex(editorProvinceData.hexList.get(0));
            if (provinceByHex == null) continue;
            editorProvinceData.applyDataToRealProvince(provinceByHex);
            getGameController().namingManager.setHexName(provinceByHex.getCapital(), editorProvinceData.name);
        }

        if (GameRules.diplomacyEnabled) {
            getGameController().fieldManager.diplomacyManager.updateAllNames();
        }
    }
}
