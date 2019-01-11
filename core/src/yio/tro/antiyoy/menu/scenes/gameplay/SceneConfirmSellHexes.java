package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomatic_dialogs.ConfirmSellHexesDialog;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneConfirmSellHexes extends AbstractGameplayScene{

    public ConfirmSellHexesDialog dialog;


    public SceneConfirmSellHexes(MenuControllerYio menuControllerYio) {
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

        dialog = new ConfirmSellHexesDialog(menuControllerYio);
        dialog.setPosition(generateRectangle(0, 0.15, 1, GraphicsYio.convertToHeight(0.72)));
        menuControllerYio.addElementToScene(dialog);
    }


    @Override
    public void hide() {
        dialog.destroy();
    }
}
