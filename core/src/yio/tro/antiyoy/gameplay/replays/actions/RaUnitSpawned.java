package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;

public class RaUnitSpawned extends RepAction{

    Hex hex;
    int strength;


    public RaUnitSpawned(Hex hex, int strength) {
        this.hex = hex;
        this.strength = strength;
    }


    @Override
    public void initType() {
        type = UNIT_SPAWNED;
    }


    @Override
    public String saveInfo() {
        return convertHexToTwoTokens(hex) + strength;
    }


    @Override
    public void loadInfo(FieldManager fieldManager, String source) {
        String[] split = source.split(" ");
        hex = getHexByTwoTokens(fieldManager, split[0], split[1]);
        strength = Integer.valueOf(split[2]);
    }


    @Override
    public void perform(GameController gameController) {
        gameController.fieldManager.addUnit(hex, strength);
    }


    @Override
    public String toString() {
        return "[Unit spawned on " +
                hex + " with strength " + strength +
                "]";
    }
}
