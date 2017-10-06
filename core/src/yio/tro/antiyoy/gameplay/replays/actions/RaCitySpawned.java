package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Obj;

import java.util.ArrayList;

public class RaCitySpawned extends RepAction{

    Hex hex;


    public RaCitySpawned(Hex hex) {
        this.hex = hex;
    }


    @Override
    public void initType() {
        type = CITY_SPAWNED;
    }


    @Override
    public String saveInfo() {
        return convertHexToTwoTokens(hex);
    }


    @Override
    public void loadInfo(FieldController fieldController, String source) {
        ArrayList<String> strings = convertSourceStringToList(source);
        hex = getHexByTwoTokens(fieldController, strings.get(0), strings.get(1));
    }


    @Override
    public void perform(GameController gameController) {
        if (hex.containsUnit()) {
            gameController.fieldController.cleanOutHex(hex);
        }

        gameController.addSolidObject(hex, Obj.TOWN);
    }


    @Override
    public String toString() {
        return "[City spawned: " +
                hex +
                "]";
    }
}
