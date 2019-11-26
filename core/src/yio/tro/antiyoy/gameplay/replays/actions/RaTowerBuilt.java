package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Obj;

import java.util.ArrayList;

public class RaTowerBuilt extends RepAction{

    Hex hex;
    boolean strong;


    public RaTowerBuilt(Hex hex, boolean strong) {
        this.hex = hex;
        this.strong = strong;
    }


    @Override
    public void initType() {
        type = TOWER_BUILT;
    }


    @Override
    public String saveInfo() {
        return convertHexToTwoTokens(hex) + strong;
    }


    @Override
    public void loadInfo(FieldManager fieldManager, String source) {
        ArrayList<String> strings = convertSourceStringToList(source);
        hex = getHexByTwoTokens(fieldManager, strings.get(0), strings.get(1));

        strong = Boolean.valueOf(strings.get(2));
    }


    @Override
    public void perform(GameController gameController) {
        if (strong) {
            gameController.fieldManager.addSolidObject(hex, Obj.STRONG_TOWER);
        } else {
            gameController.fieldManager.addSolidObject(hex, Obj.TOWER);
        }
    }


    @Override
    public String toString() {
        if (strong) {
            return "[Strong tower built: " +
                    hex +
                    "]";
        } else {
            return "[Tower built: " +
                    hex +
                    "]";
        }
    }
}
