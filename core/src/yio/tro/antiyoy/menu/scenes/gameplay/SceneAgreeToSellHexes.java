package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomatic_dialogs.AgreeToSellHexesDialog;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneAgreeToSellHexes extends AbstractModalScene {

    public AgreeToSellHexesDialog dialog;


    public SceneAgreeToSellHexes(MenuControllerYio menuControllerYio) {
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

        dialog = new AgreeToSellHexesDialog(menuControllerYio);
        dialog.setPosition(generateRectangle(0, 0.15, 1, GraphicsYio.convertToHeight(0.72)));
        menuControllerYio.addElementToScene(dialog);
    }


    @Override
    public void hide() {
        dialog.destroy();
    }
}
