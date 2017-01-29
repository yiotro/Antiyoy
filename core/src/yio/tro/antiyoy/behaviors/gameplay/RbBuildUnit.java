package yio.tro.antiyoy.behaviors.gameplay;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 31.05.2015.
 */
public class RbBuildUnit extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (!getGameController(buttonYio).selectionController.isSomethingSelected()) {
            YioGdxGame.say("detected strange bug in RbBuildUnit");
            return;
        }

        int t = getGameController(buttonYio).selectionController.getTipType();
        if (t < 0) t = 0;
        t += 1;
        if (t > 4) t = 1;
        getGameController(buttonYio).selectionController.awakeTip(t);
        getGameController(buttonYio).detectAndShowMoveZoneForBuildingUnit(t);
    }
}
