package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;

import java.util.ArrayList;

public class RaUnitDiedFromStarvation extends RepAction{

    Hex hex;


    public RaUnitDiedFromStarvation(Hex hex) {
        this.hex = hex;
    }


    @Override
    public void initType() {
        type = UNIT_DIED_FROM_STARVATION;
    }


    @Override
    public String saveInfo() {
        return convertHexToTwoTokens(hex);
    }


    @Override
    public void loadInfo(FieldManager fieldManager, String source) {
        ArrayList<String> strings = convertSourceStringToList(source);
        hex = getHexByTwoTokens(fieldManager, strings.get(0), strings.get(1));
    }


    @Override
    public void perform(GameController gameController) {
        gameController.fieldManager.killUnitByStarvation(hex);
    }


    @Override
    public String toString() {
        return "[Unit dies from starvation:" +
                hex +
                "]";
    }
}
