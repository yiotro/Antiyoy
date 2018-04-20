package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.menu.speed_panel.SpeedPanel;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.OneTimeInfo;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SpecialActionController {

    MenuControllerYio menuControllerYio;


    public SpecialActionController(MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;
    }


    public void move() {

    }


    public void perform() {
        getDiplomacyManager().onDiplomacyButtonPressed();
    }


    private DiplomacyManager getDiplomacyManager() {
        return menuControllerYio.yioGdxGame.gameController.fieldController.diplomacyManager;
    }
}
