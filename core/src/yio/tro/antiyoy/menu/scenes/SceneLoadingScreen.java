package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.loading_screen.LoadingScreenElement;

public class SceneLoadingScreen extends AbstractScene{

    public LoadingScreenElement loadingScreenElement;


    public SceneLoadingScreen(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        loadingScreenElement = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        initLoadingScreenElement();
        loadingScreenElement.appear();
        
        menuControllerYio.endMenuCreation();
    }


    private void initLoadingScreenElement() {
        if (loadingScreenElement != null) return;

        loadingScreenElement = new LoadingScreenElement(menuControllerYio);
        menuControllerYio.addElementToScene(loadingScreenElement);
    }
}
