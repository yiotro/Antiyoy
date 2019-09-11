package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.keyboard.BasicKeyboardElement;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;

public class SceneBasicKeyboard extends AbstractModalScene {


    public BasicKeyboardElement basicKeyboardElement;


    public SceneBasicKeyboard(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        basicKeyboardElement = null;
    }


    @Override
    public void create() {
        initKeyboardElement();
        basicKeyboardElement.appear();
        forceElementToTop(basicKeyboardElement);
    }


    public void setReaction(AbstractKbReaction reaction) {
        if (basicKeyboardElement == null) return;

        basicKeyboardElement.setReaction(reaction);
    }


    public void setValue(String value) {
        if (basicKeyboardElement == null) return;

        basicKeyboardElement.setValue(value);
    }


    private void initKeyboardElement() {
        if (basicKeyboardElement != null) return;

        basicKeyboardElement = new BasicKeyboardElement();
        basicKeyboardElement.position.setBy(generateRectangle(0, 0, 1, 0.3));
        menuControllerYio.addElementToScene(basicKeyboardElement);
    }


    @Override
    public void hide() {
        if (basicKeyboardElement != null) {
            basicKeyboardElement.destroy();
        }
    }
}
