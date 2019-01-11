package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;

import java.util.ArrayList;

public class RaHexColorChanged extends RepAction{

    // should be used only when color changed without normal reason (cheats, for example)
    Hex hex;
    int newColor;


    public RaHexColorChanged(Hex hex, int newColor) {
        this.hex = hex;
        this.newColor = newColor;
    }


    @Override
    public void initType() {
        type = HEX_CHANGED_COLOR;
    }


    @Override
    public String saveInfo() {
        return convertHexToTwoTokens(hex) + newColor;
    }


    @Override
    public void loadInfo(FieldController fieldController, String source) {
        ArrayList<String> strings = convertSourceStringToList(source);
        hex = getHexByTwoTokens(fieldController, strings.get(0), strings.get(1));
        newColor = Integer.valueOf(strings.get(2));
    }


    @Override
    public void perform(GameController gameController) {
        gameController.fieldController.setHexColor(hex, newColor);
        gameController.fieldController.tryToDetectAddiotionalProvinces();
    }


    @Override
    public String toString() {
        return "[Hex changed color]";
    }
}
