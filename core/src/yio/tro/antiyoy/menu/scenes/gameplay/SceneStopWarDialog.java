package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomatic_dialogs.StopWarDialog;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneStopWarDialog extends AbstractModalScene {

    public StopWarDialog dialog;


    public SceneStopWarDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        dialog = null;
    }


    @Override
    public void create() {
        if (dialog == null) {
            initDialog();
        }

        dialog.appear();
    }


    private void initDialog() {
        dialog = new StopWarDialog(menuControllerYio);

        dialog.setPosition(generateRectangle(0, 0.15, 1, GraphicsYio.convertToHeight(0.72)));

        menuControllerYio.addElementToScene(dialog);
    }


    @Override
    public void hide() {
        dialog.destroy();
    }
}
