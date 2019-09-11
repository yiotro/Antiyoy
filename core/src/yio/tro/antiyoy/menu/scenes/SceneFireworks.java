package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.fireworks_element.FireworksElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneFireworks extends AbstractScene{

    FireworksElement fireworksElement;


    public SceneFireworks(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        fireworksElement = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, true, true);

        createFireworks();

        menuControllerYio.spawnBackButton(560, Reaction.rbMainMenu);

        menuControllerYio.endMenuCreation();
    }


    private void createFireworks() {
        if (fireworksElement == null) {
            fireworksElement = new FireworksElement(menuControllerYio, -1);
            fireworksElement.position.set(0, 0, GraphicsYio.width, GraphicsYio.height);

            menuControllerYio.addElementToScene(fireworksElement);
        }

        fireworksElement.appear();
    }
}
