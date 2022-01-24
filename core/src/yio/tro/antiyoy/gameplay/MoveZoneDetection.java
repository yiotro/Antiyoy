package yio.tro.antiyoy.gameplay;

import java.util.ArrayList;

public class MoveZoneDetection {

    private final FieldManager fieldManager;
    private ArrayList<Hex> result;
    private ArrayList<Hex> propagationList;
    private Hex tempHex;
    private Hex adjHex;


    public MoveZoneDetection(FieldManager fieldManager) {
        this.fieldManager = fieldManager;

        result = new ArrayList<>();
        propagationList = new ArrayList<>();
    }


    public static void unFlagAllHexesInArrayList(ArrayList<Hex> hexList) {
        for (int i = hexList.size() - 1; i >= 0; i--) {
            hexList.get(i).flag = false;
            hexList.get(i).inMoveZone = false;
        }
    }


    public ArrayList<Hex> detectMoveZoneForFarm() {
        fieldManager.moveZoneManager.clear();
        unFlagAllHexesInArrayList(fieldManager.activeHexes);
        result.clear();
        for (Hex hex : fieldManager.selectedProvince.hexList) {
            if (canBuildFarmOnHex(hex)) {
                hex.inMoveZone = true;
                result.add(hex);
            }
        }

        return result;
    }


    public static boolean canBuildFarmOnHex(Hex hex) {
        return hex.hasThisSupportiveObjectNearby(Obj.FARM) || hex.hasThisSupportiveObjectNearby(Obj.TOWN);
    }


    public ArrayList<Hex> detectMoveZone(Hex startHex, int strength) {
        return detectMoveZone(startHex, strength, 9001); // move limit is almost infinite
    }


    public ArrayList<Hex> detectMoveZone(Hex startHex, int strength, int moveLimit) {
        unFlagAllHexesInArrayList(fieldManager.activeHexes);
        beginDetection(startHex, moveLimit);

        while (propagationList.size() > 0) {
            iteratePropagation(startHex, strength);
        }

        return result;
    }


    private void iteratePropagation(Hex startHex, int strength) {
        tempHex = propagationList.get(0);
        propagationList.remove(0);

        tempHex.inMoveZone = true;
        result.add(tempHex);

        if (!tempHex.sameFraction(startHex)) return;
        if (tempHex.moveZoneNumber == 0) return;

        for (int dir = 0; dir < 6; dir++) {
            adjHex = tempHex.getAdjacentHex(dir);
            if (!adjHex.active) continue;
            if (adjHex.flag) continue;

            if (adjHex.sameFraction(startHex)) {
                propagationList.add(adjHex);
                adjHex.moveZoneNumber = tempHex.moveZoneNumber - 1;
                adjHex.flag = true;
            } else {
                if (fieldManager.gameController.canUnitAttackHex(strength, startHex.fraction, adjHex)) {
                    propagationList.add(adjHex);
                    adjHex.flag = true;
                }
            }
        }
    }


    private void beginDetection(Hex startHex, int moveLimit) {
        result.clear();
        propagationList.clear();
        propagationList.add(startHex);
        startHex.moveZoneNumber = moveLimit;
    }
}