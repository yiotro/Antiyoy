package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public abstract class AbstractEditorPanel extends AbstractScene{

    public AbstractEditorPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    public abstract void hide();


    public abstract boolean isCurrentlyOpened();


    public void onTumblerButtonPressed() {
        if (isCurrentlyOpened()) {
            hide();
        } else {
            menuControllerYio.hideAllEditorPanels();
            create();
        }
    }
}
