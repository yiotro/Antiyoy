package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.keyboard.KeyboardElement;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractGameplayScene;

public class SceneKeyboard extends AbstractGameplayScene{


    public KeyboardElement keyboardElement;


    public SceneKeyboard(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        keyboardElement = null;
    }


    @Override
    public void create() {
        initKeyboardElement();
        keyboardElement.appear();
        forceElementToTop(keyboardElement);
    }


    public void setReaction(AbstractKbReaction reaction) {
        if (keyboardElement == null) return;

        keyboardElement.setReaction(reaction);
    }


    public void setValue(String value) {
        if (keyboardElement == null) return;

        keyboardElement.setValue(value);
    }


    private void initKeyboardElement() {
        if (keyboardElement != null) return;

        keyboardElement = new KeyboardElement();
        keyboardElement.position.setBy(generateRectangle(0, 0, 1, 0.3));
        menuControllerYio.addElementToScene(keyboardElement);
    }


    @Override
    public void hide() {
        if (keyboardElement != null) {
            keyboardElement.destroy();
        }
    }
}
