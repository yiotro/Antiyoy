package yio.tro.antiyoy.gameplay.fog_of_war;

import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FogOfWarManager {


    public static final int OFFSET = 4;
    public FieldManager fieldManager;
    public HashMap<Hex, FogPoint> fogMap;
    ObjectPoolYio<FogPoint> poolFogPoints;
    ObjectPoolYio<Hex> poolKeys;
    float viewOffset;
    LightUpAlgorithm lightUpAlgorithm;
    private boolean foundPlayersProvince;
    public RectangleYio visibleArea;
    public RectangleYio topBlock, bottomBlock, leftBlock, rightBlock;
    public ArrayList<FogSlice> viewSlices;
    ObjectPoolYio<FogSlice> poolSlices;
    SliceUpdater sliceUpdater;


    public FogOfWarManager(FieldManager fieldManager) {
        this.fieldManager = fieldManager;

        fogMap = new HashMap<>();
        viewOffset = 2 * fieldManager.hexSize;
        lightUpAlgorithm = new LightUpAlgorithm(this);
        visibleArea = new RectangleYio();
        topBlock = new RectangleYio();
        bottomBlock = new RectangleYio();
        leftBlock = new RectangleYio();
        rightBlock = new RectangleYio();
        viewSlices = new ArrayList<>();
        sliceUpdater = new SliceUpdater(this);

        initPools();
    }


    private void initPools() {
        poolFogPoints = new ObjectPoolYio<FogPoint>() {
            @Override
            public FogPoint makeNewObject() {
                return new FogPoint(FogOfWarManager.this);
            }
        };

        poolKeys = new ObjectPoolYio<Hex>() {
            @Override
            public Hex makeNewObject() {
                return new Hex(-1, -1, null, null);
            }
        };

        poolSlices = new ObjectPoolYio<FogSlice>() {
            @Override
            public FogSlice makeNewObject() {
                return new FogSlice();
            }
        };
    }


    public void updateFog() {
        if (!GameRules.fogOfWarEnabled) return;

        resetStatuses();

        int playersNumber = fieldManager.gameController.playersNumber;

        if (playersNumber == 0) {
            lightUpAllMap();
        } else if (playersNumber == 1) {
            lightUpInSingleplayerMode();
        } else {
            lightUpInMultiplayerMode();
        }

        updateVisibleArea();
        updateBlocks();
        updateSlices();
    }


    private void lightUpInMultiplayerMode() {
        for (Province province : fieldManager.provinces) {
            if (isCurrentPlayerProvince(province)) {
                lightUpProvince(province);
                continue;
            }
            if (isFriendOfCurrentPlayer(province)) {
                lightUpProvince(province);
            }
        }
    }


    private void lightUpInSingleplayerMode() {
        foundPlayersProvince = false;
        for (Province province : fieldManager.provinces) {
            if (isFirstPlayerProvince(province)) {
                foundPlayersProvince = true;
                lightUpProvince(province);
                continue;
            }
            if (isFriendOfFirstPlayer(province)) {
                lightUpProvince(province);
            }
        }

        if (!foundPlayersProvince) {
            lightUpAllMap();
        }
    }


    boolean isFriendOfCurrentPlayer(Province province) {
        if (!GameRules.diplomacyEnabled) return false;

        DiplomaticEntity entity = fieldManager.diplomacyManager.getEntity(province.getFraction());
        if (entity == null) return false;

        DiplomaticEntity currentEntity = fieldManager.diplomacyManager.getEntity(fieldManager.gameController.turn);
        if (currentEntity == entity) return false;

        int relation = entity.getRelation(currentEntity);

        return relation == DiplomaticRelation.FRIEND;
    }


    public boolean isHexCoveredByFog(Hex hex) {
        return fogMap.containsKey(hex) && fogMap.get(hex).status;
    }


    boolean isFriendOfFirstPlayer(Province province) {
        if (!GameRules.diplomacyEnabled) return false;

        DiplomaticEntity entity = fieldManager.diplomacyManager.getEntity(province.getFraction());
        if (entity == null) return false;

        DiplomaticEntity firstEntity = fieldManager.diplomacyManager.getEntity(0);
        if (firstEntity == entity) return false;

        int relation = entity.getRelation(firstEntity);

        return relation == DiplomaticRelation.FRIEND;
    }


    private boolean isCurrentPlayerProvince(Province province) {
        return province.getFraction() == fieldManager.gameController.turn;
    }


    private boolean isFirstPlayerProvince(Province province) {
        return province.getFraction() == 0;
    }


    private void lightUpProvince(Province province) {
        for (Hex hex : province.hexList) {
            applyHex(hex);
        }
    }


    public void onEndCreation() {
        init();
        updateFog();
    }


    private void updateSlices() {
        sliceUpdater.perform();
    }


    void clearSlices() {
        for (FogSlice viewSlice : viewSlices) {
            poolSlices.add(viewSlice);
        }

        viewSlices.clear();
    }


    private void updateBlocks() {
        leftBlock.setBy(visibleArea);
        leftBlock.x = 0;
        leftBlock.width = visibleArea.x;

        rightBlock.setBy(visibleArea);
        rightBlock.x = visibleArea.x + visibleArea.width;
        rightBlock.width = getBoundWidth() - rightBlock.x;

        bottomBlock.x = 0;
        bottomBlock.y = 0;
        bottomBlock.width = getBoundWidth();
        bottomBlock.height = visibleArea.y;

        topBlock.x = 0;
        topBlock.y = visibleArea.y + visibleArea.height;
        topBlock.width = getBoundWidth();
        topBlock.height = getLevelSizeManager().boundHeight - topBlock.y;
    }


    private float getBoundWidth() {
        return getLevelSizeManager().boundWidth;
    }


    private LevelSizeManager getLevelSizeManager() {
        return fieldManager.gameController.levelSizeManager;
    }


    private void updateVisibleArea() {
        FogPoint anyDeactivatedFogPoint = getAnyDeactivatedFogPoint();
        if (anyDeactivatedFogPoint == null) return;

        visibleArea.set(anyDeactivatedFogPoint.position.x, anyDeactivatedFogPoint.position.y, 0, 0);

        double delta;
        for (FogPoint fogPoint : fogMap.values()) {
            if (fogPoint.status) continue;
            if (fogPoint.hex == null) continue;

            PointYio pos = fogPoint.position;

            if (pos.x < visibleArea.x) {
                delta = visibleArea.x - pos.x;
                visibleArea.x -= delta;
                visibleArea.width += delta;
            }

            if (pos.x > visibleArea.x + visibleArea.width) {
                delta = pos.x - (visibleArea.x + visibleArea.width);
                visibleArea.width += delta;
            }

            if (pos.y < visibleArea.y) {
                delta = visibleArea.y - pos.y;
                visibleArea.y -= delta;
                visibleArea.height += delta;
            }

            if (pos.y > visibleArea.y + visibleArea.height) {
                delta = pos.y - (visibleArea.y + visibleArea.height);
                visibleArea.height += delta;
            }
        }

        visibleArea.x -= viewOffset;
        visibleArea.y -= viewOffset;
        visibleArea.width += 2 * viewOffset;
        visibleArea.height += 2 * viewOffset;
    }


    private FogPoint getAnyDeactivatedFogPoint() {
        for (FogPoint fogPoint : fogMap.values()) {
            if (fogPoint.status) continue;

            return fogPoint;
        }

        return null;
    }


    private void lightUpAllMap() {
        for (FogPoint fogPoint : fogMap.values()) {
            fogPoint.setStatus(false);
        }
    }


    private void applyHex(Hex hex) {
        if (hex.containsUnit()) {
            lightUp(hex, 2);
            return;
        }

        if (hex.objectInside == Obj.TOWER) {
            lightUp(hex, 3);
            return;
        }

        if (hex.objectInside == Obj.STRONG_TOWER) {
            lightUp(hex, 5);
            return;
        }

        if (hex.objectInside == Obj.TOWN) {
            lightUp(hex, 4);
            return;
        }

        lightUp(hex, 1);
    }


    private void lightUp(Hex hex, int radius) {
        lightUpAlgorithm.perform(hex, radius);
    }


    private void resetStatuses() {
        for (FogPoint fogPoint : fogMap.values()) {
            fogPoint.setStatus(true);
        }
    }


    private void clearPoints() {
        for (Map.Entry<Hex, FogPoint> entry : fogMap.entrySet()) {
            poolFogPoints.add(entry.getValue());
            poolKeys.add(entry.getKey());
        }

        fogMap.clear();
    }


    public void init() {
        if (!GameRules.fogOfWarEnabled) return;

        clearPoints();

        for (int i = -OFFSET; i < fieldManager.fWidth + OFFSET; i++) {
            for (int j = -OFFSET; j < fieldManager.fHeight + OFFSET; j++) {
                FogPoint next = poolFogPoints.getNext();

                next.setHexByIndexes(i, j);
                if (!isFogPointValid(next)) continue;

                fogMap.put(getKey(next), next);
            }
        }
    }


    private Hex getKey(FogPoint fogPoint) {
        if (fogPoint.hex == null) {
            return poolKeys.getNext();
        }

        return fogPoint.hex;
    }


    private boolean isFogPointValid(FogPoint next) {
        return getLevelSizeManager().isPointInsideLevelBoundsWithOffset(next.position, 2 * fieldManager.hexSize);
    }


    public void showFogMapInConsole() {
        System.out.println();
        System.out.println("FogOfWarManager.showFogMapInConsole");
        for (Map.Entry<Hex, FogPoint> entry : fogMap.entrySet()) {
            Hex key = entry.getKey();
            FogPoint value = entry.getValue();
            System.out.println(key + " -> " + value);
        }
    }


    private int getNumberOfDeactivatedFogPoints() {
        int c = 0;

        for (FogPoint fogPoint : fogMap.values()) {
            if (!fogPoint.status) {
                c++;
            }
        }

        return c;
    }


}
