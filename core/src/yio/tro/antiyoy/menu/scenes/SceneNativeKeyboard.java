package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.keyboard.NativeKeyboardElement;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;

public class SceneNativeKeyboard extends AbstractModalScene{

    public NativeKeyboardElement nativeKeyboardElement;


    public SceneNativeKeyboard(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        nativeKeyboardElement = null;
    }


    @Override
    public void create() {
        initKeyboardElement();
        nativeKeyboardElement.appear();
        forceElementToTop(nativeKeyboardElement);
    }


    public void setReaction(AbstractKbReaction reaction) {
        if (nativeKeyboardElement == null) return;

        nativeKeyboardElement.setReaction(reaction);
    }


    public void setValue(String value) {
        if (nativeKeyboardElement == null) return;

        nativeKeyboardElement.setValue(value);
    }


    private void initKeyboardElement() {
        if (nativeKeyboardElement != null) return;

        nativeKeyboardElement = new NativeKeyboardElement(menuControllerYio);
        nativeKeyboardElement.position.setBy(generateRectangle(0, 0, 1, 0.3));
        menuControllerYio.addElementToScene(nativeKeyboardElement);
    }


    @Override
    public void hide() {
        if (nativeKeyboardElement != null) {
            nativeKeyboardElement.destroy();
        }
    }
}
