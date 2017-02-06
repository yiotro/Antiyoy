package yio.tro.antiyoy.behaviors.gameplay;

import yio.tro.antiyoy.gameplay.SelectionController;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 31.05.2015.
 */
public class RbBuildUnit extends ReactBehavior {

    int chain[];


    public RbBuildUnit() {
        chain = new int[]{
                SelectionController.TIP_INDEX_UNIT_1,
                SelectionController.TIP_INDEX_UNIT_2,
                SelectionController.TIP_INDEX_UNIT_3,
                SelectionController.TIP_INDEX_UNIT_4
        };
    }


    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (!getGameController(buttonYio).selectionController.isSomethingSelected()) {
            YioGdxGame.say("detected strange bug in RbBuildUnit");
            return;
        }

        int tipType = getGameController(buttonYio).selectionController.getTipType();
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

        getGameController(buttonYio).selectionController.awakeTip(tipType);
        getGameController(buttonYio).detectAndShowMoveZoneForBuildingUnit(tipType);
    }
}
