package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.context_list_menu.ContextListMenuElement;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneContextListMenu extends AbstractModalScene {


    public ContextListMenuElement contextListMenuElement;


    public SceneContextListMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        contextListMenuElement = null;
    }


    @Override
    public void create() {
        initContextListMenuElement();

        contextListMenuElement.appear();
        forceElementToTop(contextListMenuElement);
    }


    private void initContextListMenuElement() {
        if (contextListMenuElement != null) return;

        contextListMenuElement = new ContextListMenuElement();
        contextListMenuElement.setPosition(generateRectangle(0, 0, 1, GraphicsYio.convertToHeight(0.4)));
        menuControllerYio.addElementToScene(contextListMenuElement);
    }


    @Override
    public void hide() {
        contextListMenuElement.destroy();
    }
}
