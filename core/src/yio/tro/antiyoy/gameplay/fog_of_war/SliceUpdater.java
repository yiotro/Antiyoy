package yio.tro.antiyoy.gameplay.fog_of_war;

import yio.tro.antiyoy.gameplay.Hex;

public class SliceUpdater {


    FogOfWarManager fogOfWarManager;
    int upDir;
    int downDir;


    public SliceUpdater(FogOfWarManager fogOfWarManager) {
        this.fogOfWarManager = fogOfWarManager;

        upDir = 0;
        downDir = 3;
    }


    void perform() {
        fogOfWarManager.clearSlices();

        for (FogPoint fogPoint : fogOfWarManager.fogMap.values()) {
            if (!isPointGoodToStartSlice(fogPoint)) continue;

            makeSlice(fogPoint);
        }
    }


    void makeSlice(FogPoint startPoint) {
        FogSlice next = fogOfWarManager.poolSlices.getNext();

        FogPoint endPoint = startPoint;

        while (true) {
            next.points.add(endPoint);
            FogPoint adjacentFogPoint = getAdjacentFogPoint(endPoint, upDir);
            if (adjacentFogPoint == null) break;
            if (adjacentFogPoint.status) break;

            endPoint = adjacentFogPoint;
        }

        next.setBottomPoint(startPoint);
        next.setTopPoint(endPoint);
        fogOfWarManager.viewSlices.add(next);
    }


    FogPoint getAdjacentFogPoint(FogPoint src, int dir) {
        Hex hex = src.hex;
        if (hex == null) return null;

        Hex adjacentHex = hex.getAdjacentHex(dir);
        if (adjacentHex == fogOfWarManager.fieldManager.nullHex) return null;

        return fogOfWarManager.fogMap.get(adjacentHex);
    }


    boolean isPointGoodToStartSlice(FogPoint fogPoint) {
        if (fogPoint.status) return false;

        FogPoint belowPoint = getBelowPoint(fogPoint);
        if (belowPoint == null) return true;

        return belowPoint.status;
    }


    FogPoint getBelowPoint(FogPoint srcPoint) {
        return getAdjacentFogPoint(srcPoint, downDir);
    }
}
