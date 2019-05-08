package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomatic_dialogs.HexSaleDialog;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneHexSaleDialog extends AbstractGameplayScene{

    public HexSaleDialog dialog;


    public SceneHexSaleDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        dialog = null;
    }


    @Override
    public void create() {
        initDialog();

        dialog.appear();
    }


    private void initDialog() {
        if (dialog != null) return;

        dialog = new HexSaleDialog(menuControllerYio);
        dialog.setPosition(generateRectangle(0, 0.15, 1, GraphicsYio.convertToHeight(0.72)));
        menuControllerYio.addElementToScene(dialog);
    }


    @Override
    public void hide() {
        dialog.destroy();
    }
}
