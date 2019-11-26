package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;

import java.util.ArrayList;

public class RaUnitBuilt extends RepAction{

    Hex src, dst;
    int strength;


    public RaUnitBuilt(Hex src, Hex dst, int strength) {
        this.src = src;
        this.dst = dst;
        this.strength = strength;
    }


    @Override
    public void initType() {
        type = UNIT_BUILT;
    }


    @Override
    public String saveInfo() {
        return convertHexToTwoTokens(src) + convertHexToTwoTokens(dst) + " " + strength;
    }


    @Override
    public void loadInfo(FieldManager fieldManager, String source) {
        ArrayList<String> strings = convertSourceStringToList(source);
        src = getHexByTwoTokens(fieldManager, strings.get(0), strings.get(1));
        dst = getHexByTwoTokens(fieldManager, strings.get(2), strings.get(3));

        strength = Integer.valueOf(strings.get(4));
    }


    @Override
    public void perform(GameController gameController) {
        FieldManager fieldManager = gameController.fieldManager;
        Province provinceByHex = fieldManager.getProvinceByHex(src);
        boolean success = fieldManager.buildUnit(provinceByHex, dst, strength);

        if (!success) {
            System.out.println("Problem in RaUnitBuilt.perform()");
        }
    }


    @Override
    public String toString() {
        return "[Unit built: " +
                src + " to " + dst + " with strength " + strength +
                "]";
    }
}
