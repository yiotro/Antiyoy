package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.*;

import java.util.ArrayList;

public class RaUnitMoved extends RepAction{

    Hex src, dst;


    public RaUnitMoved(Hex src, Hex dst) {
        this.src = src;
        this.dst = dst;
    }


    @Override
    public void initType() {
        type = UNIT_MOVED;
    }


    @Override
    public String saveInfo() {
        return convertHexToTwoTokens(src) + convertHexToTwoTokens(dst);
    }


    @Override
    public void loadInfo(FieldManager fieldManager, String source) {
        ArrayList<String> strings = convertSourceStringToList(source);
        src = getHexByTwoTokens(fieldManager, strings.get(0), strings.get(1));
        dst = getHexByTwoTokens(fieldManager, strings.get(2), strings.get(3));
    }


    @Override
    public void perform(GameController gameController) {
        Unit unit = src.unit;
        if (unit == null) return;

        Province provinceByHex = gameController.fieldManager.getProvinceByHex(src);

        if (!dst.sameFraction(src) && !dst.isNeutral() && !dst.canBeAttackedBy(unit)) {
            System.out.println();
            System.out.println("Problem in RaUnitMoved.perform(), forbidden attack");
            System.out.println("src = " + src);
            System.out.println("unit.strength = " + unit.strength);
            System.out.println("dst = " + dst);
            System.out.println("dst.getDefenseNumber() = " + dst.getDefenseNumber());
        }

        if (unit == null) {
            System.out.println();
            System.out.println("Problem in RaUnitMoved.perform(). Unit is null");
            System.out.println("src = " + src);
            System.out.println("dst = " + dst);
            return;
        }

        gameController.moveUnit(unit, dst, provinceByHex);
    }


    @Override
    public String toString() {
        return "[Unit moved from " +
                src + " to " + dst +
                "]";
    }
}
