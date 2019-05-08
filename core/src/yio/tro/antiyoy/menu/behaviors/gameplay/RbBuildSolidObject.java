package yio.tro.antiyoy.menu.behaviors.gameplay;

import yio.tro.antiyoy.gameplay.SelectionTipType;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

/**
 * Created by yiotro on 31.05.2015.
 */
public class RbBuildSolidObject extends Reaction {

    int chain[];


    public RbBuildSolidObject() {
        chain = new int[]{
                SelectionTipType.FARM,
                SelectionTipType.TOWER,
                SelectionTipType.STRONG_TOWER
        };
    }


    @Override
    public void perform(ButtonYio buttonYio) {
        if (GameRules.slayRules) {
            getGameController(buttonYio).selectionManager.awakeTip(SelectionTipType.TOWER);
        } else {
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
            if (tipType == SelectionTipType.FARM) {
                getGameController(buttonYio).detectAndShowMoveZoneForFarm();
            }
        }
    }
}
