package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomatic_dialogs.ReceiveAttackPropositionDialog;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneReceiveAttackPropositionDialog extends AbstractModalScene{

    public ReceiveAttackPropositionDialog dialog;


    public SceneReceiveAttackPropositionDialog(MenuControllerYio menuControllerYio) {
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

        dialog = new ReceiveAttackPropositionDialog(menuControllerYio);
        dialog.setPosition(generateRectangle(0, 0.15, 1, GraphicsYio.convertToHeight(0.65)));
        menuControllerYio.addElementToScene(dialog);
    }


    @Override
    public void hide() {
        dialog.destroy();
    }
}
