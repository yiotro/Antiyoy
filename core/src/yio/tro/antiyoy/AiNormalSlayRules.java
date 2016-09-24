package yio.tro.antiyoy;

import java.util.ArrayList;

/**
 * Created by ivan on 24.11.2015.
 */
class AiNormalSlayRules extends ArtificialIntelligence {

    public AiNormalSlayRules(GameController gameController, int color) {
        super(gameController, color);
    }


    @Override
    void makeMove() {
        ArrayList<Unit> unitsReadyToMove = detectUnitsReadyToMove();

        moveUnits(unitsReadyToMove);

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
            if (!province.hasEnoughIncomeToAffordUnit(i)) break;
            while (province.hasMoneyForUnit(i)) {
                if (!tryToBuiltUnitInsideProvince(province, i)) break;
            }
        }

        // this is to kick start province
        if (province.hasMoneyForUnit(1) && howManyUnitsInProvince(province) <= 1)
            tryToAttackWithStrength(province, 1);
    }
}
