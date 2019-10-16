package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.editor_elements.color_picker.ColorPickerElement;
import yio.tro.antiyoy.menu.editor_elements.color_picker.IColorChoiceListener;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneColorPicker extends AbstractModalScene{


    private Reaction rbHide;
    ColorPickerElement colorPickerElement;


    public SceneColorPicker(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        colorPickerElement = null;
        initReactions();
    }


    @Override
    public void create() {
        createInvisibleCloseButton(rbHide);
        createColorPickerElement();
    }


    private void createColorPickerElement() {
        initColorPickerElement();
        forceElementToTop(invisibleCloseElement);
        forceElementToTop(colorPickerElement);
        colorPickerElement.appear();
    }


    private void initColorPickerElement() {
        if (colorPickerElement != null) return;
        colorPickerElement = new ColorPickerElement(menuControllerYio);
        colorPickerElement.setPosition(generateRectangle(0, 0, 1, GraphicsYio.convertToHeight(0.5)));
        menuControllerYio.addElementToScene(colorPickerElement);
    }


    public void setListener(IColorChoiceListener colorChoiceListener) {
        colorPickerElement.setListener(colorChoiceListener);
    }


    public void addRandomColorItem() {
        colorPickerElement.addRandomColorItem();
    }


    private void initReactions() {
        rbHide = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
    }


    @Override
    public void hide() {
        destroyByIndex(300, 309);
        if (colorPickerElement != null) {
            colorPickerElement.destroy();
        }
    }
}
