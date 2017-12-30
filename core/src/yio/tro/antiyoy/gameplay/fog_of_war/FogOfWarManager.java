package yio.tro.antiyoy.gameplay.fog_of_war;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Obj;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FogOfWarManager {


    public static final int OFFSET = 4;
    public FieldController fieldController;
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


    public FogOfWarManager(FieldController fieldController) {
        this.fieldController = fieldController;

        fogMap = new HashMap<>();
        viewOffset = 2 * fieldController.hexSize;
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

        foundPlayersProvince = false;
        for (Province province : fieldController.provinces) {
            if (fieldController.gameController.isPlayerTurn(province.getColor())) {
                foundPlayersProvince = true;
                for (Hex hex : province.hexList) {
                    applyHex(hex);
                }
            }
        }

        if (!foundPlayersProvince) {
            lightUpAllMap();
        }

        updateVisibleArea();
        updateBlocks();
        updateSlices();
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
        rightBlock.width = fieldController.gameController.boundWidth - rightBlock.x;

        bottomBlock.x = 0;
        bottomBlock.y = 0;
        bottomBlock.width = fieldController.gameController.boundWidth;
        bottomBlock.height = visibleArea.y;

        topBlock.x = 0;
        topBlock.y = visibleArea.y + visibleArea.height;
        topBlock.width = fieldController.gameController.boundWidth;
        topBlock.height = fieldController.gameController.boundHeight - topBlock.y;
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
            lightUp(hex, 3);
            return;
        }

        if (hex.containsTower()) {
            lightUp(hex, 5);
            return;
        }

        if (hex.objectInside == Obj.TOWN) {
            lightUp(hex, 6);
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

        for (int i = -OFFSET; i < fieldController.fWidth + OFFSET; i++) {
            for (int j = -OFFSET; j < fieldController.fHeight + OFFSET; j++) {
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
        return fieldController.isPointInsideLevelBoundsWithOffset(next.position, 2 * fieldController.hexSize);
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
