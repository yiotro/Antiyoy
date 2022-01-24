package yio.tro.antiyoy.ai.master;

import yio.tro.antiyoy.ai.AbstractAi;
import yio.tro.antiyoy.gameplay.Hex;

import java.util.ArrayList;

public abstract class PropagationCaster {

    AbstractAi abstractAi;
    ArrayList<Hex> propagationList;


    public PropagationCaster(AbstractAi abstractAi) {
        this.abstractAi = abstractAi;
        propagationList = new ArrayList<>();
    }


    public abstract boolean isPropagationAllowed(Hex src, Hex dst);


    public abstract void onHexReached(Hex previousHex, Hex hex);


    public void perform(Hex start, int depth) {
        resetFlags();

        propagationList.add(start);
        start.aiData.propCastValue = depth;

        applyMainCycle();
    }


    public void perform(ArrayList<Hex> launchList, int depth) {
        resetFlags();

        propagationList.addAll(launchList);
        for (Hex hex : launchList) {
            hex.aiData.propCastValue = depth;
        }

        applyMainCycle();
    }


    private void applyMainCycle() {
        while (propagationList.size() > 0) {
            Hex hex = propagationList.get(0);
            propagationList.remove(0);
            if (hex.aiData.propCastValue == 0) continue;

            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (adjacentHex == null) continue;
                if (!adjacentHex.active) continue;
                if (adjacentHex.isNullHex()) continue;
                if (adjacentHex.aiData.propCastValue != -1) continue;
                if (!isPropagationAllowed(hex, adjacentHex)) continue;
                propagationList.add(adjacentHex);
                adjacentHex.aiData.propCastValue = hex.aiData.propCastValue - 1;
                onHexReached(hex, adjacentHex);
            }
        }
    }


    private void resetFlags() {
        for (Hex activeHex : abstractAi.gameController.fieldManager.activeHexes) {
            activeHex.aiData.propCastValue = -1;
        }
    }
}
