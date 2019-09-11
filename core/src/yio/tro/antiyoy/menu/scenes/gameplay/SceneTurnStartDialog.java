package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.TurnStartDialog;

public class SceneTurnStartDialog extends AbstractModalScene {

    public TurnStartDialog dialog;


    public SceneTurnStartDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        dialog = null;
    }


    @Override
    public void create() {
        initDialogOnce();

        forceElementToTop(dialog);

        dialog.appear();
    }


    private void initDialogOnce() {
        if (dialog != null) return;

        dialog = new TurnStartDialog(menuControllerYio);
        dialog.setPosition(generateRectangle(0, 0, 1, 1));
        menuControllerYio.addElementToScene(dialog);
    }


    @Override
    public void hide() {
        dialog.destroy();
    }
}
