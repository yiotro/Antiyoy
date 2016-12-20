package yio.tro.antiyoy.behaviors.gameplay;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.GameController;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 31.05.2015.
 */
public class RbBuildSolidObject extends ReactBehavior {

    @Override
    public void reactAction(ButtonYio buttonYio) {
        if (GameController.slay_rules) {
            getGameController(buttonYio).awakeTip(0);
        } else {
            int tipType = getGameController(buttonYio).getTipType();
            switch (tipType) {
                case 0: tipType = 5; break;
                case 5: tipType = 6; break;
                default:
                case 6: tipType = 0; break;
            }
            getGameController(buttonYio).awakeTip(tipType);
            if (tipType == 5) {
                getGameController(buttonYio).detectAndShowMoveZoneForFarm();
            }
        }
    }
}
