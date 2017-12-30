package yio.tro.antiyoy.gameplay;

import java.util.ArrayList;

public class MoveZoneDetection {

    private final FieldController fieldController;
    private ArrayList<Hex> result;
    private ArrayList<Hex> propagationList;
    private Hex tempHex;
    private Hex adjHex;


    public MoveZoneDetection(FieldController fieldController) {
        this.fieldController = fieldController;

        result = new ArrayList<Hex>();
        propagationList = new ArrayList<Hex>();
    }


    public static void unFlagAllHexesInArrayList(ArrayList<Hex> hexList) {
        for (int i = hexList.size() - 1; i >= 0; i--) {
            hexList.get(i).flag = false;
            hexList.get(i).inMoveZone = false;
        }
    }


    public ArrayList<Hex> detectMoveZoneForFarm() {
        fieldController.clearMoveZone();
        unFlagAllHexesInArrayList(fieldController.activeHexes);
        result.clear();
        for (Hex hex : fieldController.selectedProvince.hexList) {
            if (canBuildFarmOnHex(hex)) {
                hex.inMoveZone = true;
                result.add(hex);
            }
        }

        return result;
    }


    public static boolean canBuildFarmOnHex(Hex hex) {
        return hex.hasThisObjectNearby(Obj.FARM) || hex.hasThisObjectNearby(Obj.TOWN);
    }


    public ArrayList<Hex> detectMoveZone(Hex startHex, int strength) {
        return detectMoveZone(startHex, strength, 9001); // move limit is almost infinite
    }


    public ArrayList<Hex> detectMoveZone(Hex startHex, int strength, int moveLimit) {
        unFlagAllHexesInArrayList(fieldController.activeHexes);
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

        if (!tempHex.sameColor(startHex)) return;
        if (tempHex.moveZoneNumber == 0) return;

        for (int i = 0; i < 6; i++) {
            adjHex = tempHex.getAdjacentHex(i);
            if (!adjHex.active) continue;
            if (adjHex.flag) continue;

            if (adjHex.sameColor(startHex)) {
                propagationList.add(adjHex);
                adjHex.moveZoneNumber = tempHex.moveZoneNumber - 1;
                adjHex.flag = true;
            } else {
                if (fieldController.gameController.canUnitAttackHex(strength, startHex.colorIndex, adjHex)) {
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