package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.FieldController;
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
    public void loadInfo(FieldController fieldController, String source) {
        ArrayList<String> strings = convertSourceStringToList(source);
        src = getHexByTwoTokens(fieldController, strings.get(0), strings.get(1));
        dst = getHexByTwoTokens(fieldController, strings.get(2), strings.get(3));

        strength = Integer.valueOf(strings.get(4));
    }


    @Override
    public void perform(GameController gameController) {
        FieldController fieldController = gameController.fieldController;
        Province provinceByHex = fieldController.getProvinceByHex(src);
        boolean success = fieldController.buildUnit(provinceByHex, dst, strength);

        if (!success) {
            System.out.println();
            System.out.println("Problem in RaUnitBuilt.perform()");
            System.out.println("src = " + src);
            System.out.println("dst = " + dst);
            System.out.println("strength = " + strength);
            System.out.println("provinceByHex = " + provinceByHex);
        }
    }


    @Override
    public String toString() {
        return "[Unit built: " +
                src + " to " + dst + " with strength " + strength +
                "]";
    }
}
