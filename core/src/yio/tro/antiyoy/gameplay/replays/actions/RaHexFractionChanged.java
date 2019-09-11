package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;

import java.util.ArrayList;

public class RaHexFractionChanged extends RepAction{

    // should be used only when fraction changed without normal reason (cheats, for example)
    Hex hex;
    int newFraction;


    public RaHexFractionChanged(Hex hex, int newFraction) {
        this.hex = hex;
        this.newFraction = newFraction;
    }


    @Override
    public void initType() {
        type = HEX_CHANGED_FRACTION;
    }


    @Override
    public String saveInfo() {
        return convertHexToTwoTokens(hex) + newFraction;
    }


    @Override
    public void loadInfo(FieldController fieldController, String source) {
        ArrayList<String> strings = convertSourceStringToList(source);
        hex = getHexByTwoTokens(fieldController, strings.get(0), strings.get(1));
        newFraction = Integer.valueOf(strings.get(2));
    }


    @Override
    public void perform(GameController gameController) {
        gameController.fieldController.setHexFraction(hex, newFraction);
        gameController.fieldController.tryToDetectAddiotionalProvinces();
    }


    @Override
    public String toString() {
        return "[Hex changed fraction]";
    }
}
