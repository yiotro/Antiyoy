package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SpecialActionController {

    MenuControllerYio menuControllerYio;


    public SpecialActionController(MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;
    }


    public void move() {

    }


    public void perform() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        if (gameController.isInEditorMode()) {
            Scenes.sceneEditorDiplomacy.create();
            return;
        }

        if (getDiplomacyManager().log.hasSomethingToRead()) {
            getDiplomacyManager().onDiplomaticLogButtonPressed();
            return;
        }

        getDiplomacyManager().onDiplomacyButtonPressed();
    }


    private DiplomacyManager getDiplomacyManager() {
        return menuControllerYio.yioGdxGame.gameController.fieldManager.diplomacyManager;
    }
}
