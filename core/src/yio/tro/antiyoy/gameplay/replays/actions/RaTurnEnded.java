package yio.tro.antiyoy.gameplay.replays.actions;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;

public class RaTurnEnded extends RepAction{

    @Override
    public void initType() {
        type = TURN_ENDED;
    }


    @Override
    public String saveInfo() {
        return "";
    }


    @Override
    public void loadInfo(FieldManager fieldManager, String source) {

    }


    @Override
    public void perform(GameController gameController) {
        gameController.applyReadyToEndTurn();
    }


    @Override
    public String toString() {
        return "[Turn ended]";
    }
}
