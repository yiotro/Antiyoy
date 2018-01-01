package yio.tro.antiyoy.gameplay.fog_of_war;

import yio.tro.antiyoy.gameplay.Hex;

import java.util.ArrayList;

public class LightUpAlgorithm {

    FogOfWarManager fogOfWarManager;
    ArrayList<Hex> queue, used;


    public LightUpAlgorithm(FogOfWarManager fogOfWarManager) {
        this.fogOfWarManager = fogOfWarManager;

        queue = new ArrayList<>();
        used = new ArrayList<>();
    }


    void perform(Hex start, int radius) {
        begin(start, radius);

        while (queue.size() > 0) {
            Hex hex = queue.get(0);
            queue.remove(0);
            deactivateFogPoint(hex);
            tagAsUsed(hex);

            if (!hex.active) continue;
            if (hex.moveZoneNumber == 0) continue;

            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (adjacentHex == null) continue;
                if (isUsed(adjacentHex)) continue;
                if (queue.contains(adjacentHex)) continue;

                adjacentHex.moveZoneNumber = hex.moveZoneNumber - 1;
                queue.add(adjacentHex);
            }
        }
    }


    private boolean isUsed(Hex hex) {
        return used.contains(hex);
    }


    private void tagAsUsed(Hex hex) {
        used.add(hex);
    }


    private void deactivateFogPoint(Hex hex) {
        FogPoint fogPoint = fogOfWarManager.fogMap.get(hex);
        if (fogPoint == null) return;

        fogPoint.setStatus(false);
    }


    private void begin(Hex start, int radius) {
        queue.clear();
        used.clear();

        queue.add(start);
        start.moveZoneNumber = radius;
    }
}
