package yio.tro.antiyoy;

import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class KeyboardManager {

    private static KeyboardManager instance;


    public static void initialize() {
        instance = null;
    }


    public static KeyboardManager getInstance() {
        if (instance == null) {
            instance = new KeyboardManager();
        }

        return instance;
    }


    public void apply(AbstractKbReaction reaction) {
        apply("", reaction);
    }


    public void apply(String defValue, AbstractKbReaction reaction) {
        if (SettingsManager.nativeKeyboard) {
            Scenes.sceneNativeKeyboard.create();
            Scenes.sceneNativeKeyboard.setReaction(reaction);
            Scenes.sceneNativeKeyboard.setValue(defValue);
        } else {
            Scenes.sceneBasicKeyboard.create();
            Scenes.sceneBasicKeyboard.setReaction(reaction);
            if (defValue.length() > 0) {
                Scenes.sceneBasicKeyboard.setValue(defValue);
            }
        }
    }
}
