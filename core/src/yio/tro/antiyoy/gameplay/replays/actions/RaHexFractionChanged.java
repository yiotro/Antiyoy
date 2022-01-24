package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.FieldManager;
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
    public void loadInfo(FieldManager fieldManager, String source) {
        ArrayList<String> strings = convertSourceStringToList(source);
        hex = getHexByTwoTokens(fieldManager, strings.get(0), strings.get(1));
        newFraction = Integer.valueOf(strings.get(2));
    }


    @Override
    public void perform(GameController gameController) {
        gameController.fieldManager.setHexFraction(hex, newFraction);
        gameController.fieldManager.tryToDetectAdditionalProvinces();
    }


    @Override
    public String toString() {
        return "[Hex changed fraction]";
    }
}
