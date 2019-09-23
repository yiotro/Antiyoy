package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.stuff.RectangleYio;


public class ButtonFactory {
    private final MenuControllerYio menuControllerYio;
    private final ButtonRenderer buttonRenderer;


    public ButtonFactory(MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;
        buttonRenderer = new ButtonRenderer();
    }


    public ButtonYio getButton(RectangleYio position, int id, String text) {
        ButtonYio buttonYio = menuControllerYio.getButtonById(id);
        if (buttonYio == null) { // if it's the first time
            buttonYio = new ButtonYio(position, id, menuControllerYio);
            if (text != null) {
                buttonYio.addTextLine(text);
                buttonRenderer.renderButton(buttonYio);
            }
            menuControllerYio.addButtonToArray(buttonYio);
        }
        buttonYio.setVisible(true);
        buttonYio.setTouchable(true);
        buttonYio.appear();
        return buttonYio;
    }
}
