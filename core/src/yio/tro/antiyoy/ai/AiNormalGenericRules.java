package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.Unit;

import java.util.ArrayList;

public class AiNormalGenericRules extends ArtificialIntelligenceGeneric{

    public AiNormalGenericRules(GameController gameController, int fraction) {
        super(gameController, fraction);
    }


    @Override
    public void makeMove() {
        moveUnits();

        spendMoneyAndMergeUnits();
    }


    @Override
    void decideAboutUnit(Unit unit, ArrayList<Hex> moveZone, Province province) {
        if (checkChance(0.5)) return;
        super.decideAboutUnit(unit, moveZone, province);
    }


    @Override
    void tryToBuildUnits(Province province) {
        tryToBuildUnitsOnPalms(province);

        for (int i = 1; i <= 4; i++) {
            if (!province.canAiAffordUnit(i)) break;
            while (canProvinceBuildUnit(province, i)) {
                if (!tryToBuiltUnitInsideProvince(province, i)) break;
            }
        }

        // this is to kick start province
        if (canProvinceBuildUnit(province, 1) && howManyUnitsInProvince(province) <= 1) {
            tryToAttackWithStrength(province, 1);
        }
    }
}
