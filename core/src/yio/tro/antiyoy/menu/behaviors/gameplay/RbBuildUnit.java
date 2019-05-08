package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.gameplay.SelectionTipType;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.Reaction;

/**
 * Created by yiotro on 31.05.2015.
 */
public class RbBuildUnit extends Reaction {

    int chain[];


    public RbBuildUnit() {
        chain = new int[]{
                SelectionTipType.UNIT_1,
                SelectionTipType.UNIT_2,
                SelectionTipType.UNIT_3,
                SelectionTipType.UNIT_4
        };
    }


    @Override
    public void perform(ButtonYio buttonYio) {
        if (!getGameController(buttonYio).selectionManager.isSomethingSelected()) {
            YioGdxGame.say("detected strange bug in RbBuildUnit");
            return;
        }

        int tipType = getGameController(buttonYio).selectionManager.getTipType();
        int newTipType = -1;

        for (int i = 0; i < chain.length; i++) {
            if (tipType == chain[i]) {
                if (i == chain.length - 1) {
                    newTipType = chain[0];
                } else {
                    newTipType = chain[i + 1];
                }
            }
        }

        if (newTipType == -1) {
            newTipType = chain[0]; // default
        }

        tipType = newTipType;

        getGameController(buttonYio).selectionManager.awakeTip(tipType);
        getGameController(buttonYio).detectAndShowMoveZoneForBuildingUnit(tipType);
    }
}
