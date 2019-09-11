package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.data_storage.DecodeManager;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.gameplay.name_generator.CityNameGenerator;
import yio.tro.antiyoy.stuff.RepeatYio;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class EditorProvinceManager implements EncodeableYio{

    LevelEditor levelEditor;
    public ArrayList<EditorProvinceData> provincesList;
    ObjectPoolYio<EditorProvinceData> poolProvinces;
    RepeatYio<EditorProvinceManager> repeatRemoveProvinces;
    ArrayList<Hex> tempList;
    EdcLinkedChecker edcLinkedChecker;
    ArrayList<EditorProvinceData> tempProvList;


    public EditorProvinceManager(LevelEditor levelEditor) {
        this.levelEditor = levelEditor;
        provincesList = new ArrayList<>();
        tempList = new ArrayList<>();
        edcLinkedChecker = new EdcLinkedChecker(this);
        tempProvList = new ArrayList<>();
        initPools();
        initRepeats();
    }


    private void initRepeats() {
        repeatRemoveProvinces = new RepeatYio<EditorProvinceManager>(this, 300) {
            @Override
            public void performAction() {
                parent.removeEmptyProvinces();
            }
        };
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
        repeatRemoveProvinces.move();
    }


    private void removeEmptyProvinces() {
        for (int i = provincesList.size() - 1; i >= 0; i--) {
            EditorProvinceData editorProvinceData = provincesList.get(i);
            if (!editorProvinceData.isEmpty()) continue;
            poolProvinces.removeFromExternalList(editorProvinceData);
        }
    }


    public void defaultValues() {
        poolProvinces.clearExternalList();
    }


    public void onEndCreation() {
        for (Hex activeHex : getFieldController().activeHexes) {
            onHexModified(activeHex);
        }
    }


    private FieldController getFieldController() {
        return getGameController().fieldController;
    }


    private GameController getGameController() {
        return levelEditor.gameController;
    }


    public EditorProvinceData getProvinceByHex(Hex hex) {
        for (EditorProvinceData editorProvince : provincesList) {
            if (!editorProvince.contains(hex)) continue;
            return editorProvince;
        }
        return null;
    }


    private EditorProvinceData findNearbyProvince(Hex hex, int fraction, EditorProvinceData ignoredProvince) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (!adjacentHex.active) continue;
            if (adjacentHex.isNeutral()) continue;
            if (adjacentHex.fraction != fraction) continue;
            if (ignoredProvince != null && ignoredProvince.contains(adjacentHex)) continue;
            EditorProvinceData provinceByHex = getProvinceByHex(adjacentHex);
            if (provinceByHex == null) continue;
            return provinceByHex;
        }
        return null;
    }


    EditorProvinceData findNearbyProvince(EditorProvinceData provinceData, int fraction) {
        for (Hex hex : provinceData.hexList) {
            EditorProvinceData nearbyProvince = findNearbyProvince(hex, fraction, provinceData);
            if (nearbyProvince == null) continue;
            return nearbyProvince;
        }
        return null;
    }


    private void removeHexFromProvinces(Hex hex) {
        EditorProvinceData provinceByHex = getProvinceByHex(hex);
        if (provinceByHex == null) return;
        provinceByHex.removeHex(hex);
    }


    private void startNewProvince(Hex hex) {
        EditorProvinceData freshObject = poolProvinces.getFreshObject();
        freshObject.name = CityNameGenerator.getInstance().generateName(hex);
        freshObject.setId(getIdForNewProvince());
        freshObject.addHex(hex);
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


    public void onHexModified(Hex hex) {
        if (hex.active && !hex.isNeutral()) {
            onHexActivated(hex);
        } else {
            removeHexFromProvinces(hex);
        }
        checkToSplitProvinces();
    }


    private void checkToSplitProvinces() {
        for (int i = provincesList.size() - 1; i >= 0; i--) {
            EditorProvinceData editorProvinceData = provincesList.get(i);
            if (edcLinkedChecker.isLinked(editorProvinceData)) continue;
            splitProvince(editorProvinceData);
        }
    }


    private void splitProvince(EditorProvinceData editorProvinceData) {
        tempList.clear();
        tempList.addAll(editorProvinceData.hexList);
        editorProvinceData.kill();
        for (Hex hex : tempList) {
            onHexActivated(hex);
        }

        tempProvList.clear();
        for (Hex hex : tempList) {
            if (!hex.active) continue;
            if (hex.isNeutral()) continue;
            EditorProvinceData provinceByHex = getProvinceByHex(hex);
            if (provinceByHex == null) continue;
            if (tempProvList.contains(provinceByHex)) continue;
            tempProvList.add(provinceByHex);
        }

        EditorProvinceData biggestProvince = getBiggestProvince(tempProvList);
        if (biggestProvince == null) return;

        biggestProvince.copyStoredDataFrom(editorProvinceData);
    }


    private EditorProvinceData getBiggestProvince(ArrayList<EditorProvinceData> list) {
        EditorProvinceData result = null;
        for (EditorProvinceData editorProvinceData : list) {
            if (result == null || editorProvinceData.hexList.size() > result.hexList.size()) {
                result = editorProvinceData;
            }
        }
        return result;
    }


    public void onLevelRandomlyCreated() {
        onLevelCleared();
        for (Hex activeHex : getFieldController().activeHexes) {
            onHexModified(activeHex);
        }
    }


    public void onLevelCleared() {
        poolProvinces.clearExternalList();
    }


    private void onHexActivated(Hex hex) {
        EditorProvinceData nearbyProvince = findNearbyProvince(hex, hex.fraction, null);
        if (nearbyProvince != null) {
            nearbyProvince.addHex(hex);
            checkToUniteProvinces();
        } else {
            startNewProvince(hex);
        }
    }


    private void uniteProvinces(EditorProvinceData mainProvince, EditorProvinceData source) {
        for (Hex hex : source.hexList) {
            mainProvince.addHex(hex);
        }
        source.kill();
    }


    private void checkToUniteProvinces() {
        for (int i = provincesList.size() - 1; i >= 0; i--) {
            EditorProvinceData editorProvinceData = provincesList.get(i);
            EditorProvinceData nearbyProvince = findNearbyProvince(editorProvinceData, editorProvinceData.getFraction());
            if (nearbyProvince == null) continue;
            uniteProvinces(nearbyProvince, editorProvinceData);
        }
    }


    public void showProvincesInConsole() {
        removeEmptyProvinces();
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
        removeEmptyProvinces();
        for (EditorProvinceData editorProvinceData : provincesList) {
            builder.append(editorProvinceData.encode()).append(",");
        }
        return builder.toString();
    }


    @Override
    public void decode(String source) {
        for (String token : source.split(",")) {
            String[] split = token.split("@");
            int index1 = Integer.valueOf(split[0]);
            int index2 = Integer.valueOf(split[1]);
            Hex hex = getGameController().fieldController.getHex(index1, index2);
            EditorProvinceData provinceByHex = getProvinceByHex(hex);
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


    public void checkToApplyProvincesData() {
        String source = getGameController().initialParameters.editorProvincesData;
        if (source == null) return;
        if (source.length() < 5) return;

        onEndCreation();
        decode(source);
        removeEmptyProvinces();

        for (EditorProvinceData editorProvinceData : provincesList) {
            Province provinceByHex = getGameController().fieldController.getProvinceByHex(editorProvinceData.hexList.get(0));
            if (provinceByHex == null) continue;
            editorProvinceData.applyDataToRealProvince(provinceByHex);
        }
    }
}
