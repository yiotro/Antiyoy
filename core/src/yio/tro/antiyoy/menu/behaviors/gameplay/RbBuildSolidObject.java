package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.gameplay.SelectionController;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

/**
 * Created by ivan on 31.05.2015.
 */
public class RbBuildSolidObject extends Reaction {

    int chain[];


    public RbBuildSolidObject() {
        chain = new int[]{
                SelectionController.TIP_INDEX_FARM,
                SelectionController.TIP_INDEX_TOWER,
                SelectionController.TIP_INDEX_STRONG_TOWER
        };
    }


    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (GameRules.slayRules) {
            getGameController(buttonYio).selectionController.awakeTip(SelectionController.TIP_INDEX_TOWER);
        } else {
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
            if (tipType == SelectionController.TIP_INDEX_FARM) {
                getGameController(buttonYio).detectAndShowMoveZoneForFarm();
            }
        }
    }
}
