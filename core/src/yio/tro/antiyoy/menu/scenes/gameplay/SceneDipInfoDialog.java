package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomatic_dialogs.DipInfoDialog;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneDipInfoDialog extends AbstractModalScene {

    public DipInfoDialog dialog;


    public SceneDipInfoDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        dialog = null;
    }


    @Override
    public void create() {
        if (dialog == null) {
            initDipInfoDialog();
        }

        dialog.appear();
    }


    private void initDipInfoDialog() {
        dialog = new DipInfoDialog(menuControllerYio);

        dialog.setPosition(generateRectangle(0, 0.15, 1, GraphicsYio.convertToHeight(0.6)));

        menuControllerYio.addElementToScene(dialog);
    }


    @Override
    public void hide() {
        dialog.destroy();
    }
}
