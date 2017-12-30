package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomatic_dialogs.FriendshipDialog;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneFriendshipDialog extends AbstractGameplayScene{

    public FriendshipDialog dialog;


    public SceneFriendshipDialog(MenuControllerYio menuControllerYio) {
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
        dialog = new FriendshipDialog(menuControllerYio);

        dialog.setPosition(generateRectangle(0, 0.15, 1, GraphicsYio.convertToHeight(0.6)));

        menuControllerYio.addElementToScene(dialog);
    }


    @Override
    public void hide() {
        dialog.destroy();
    }
}
