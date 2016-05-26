package yio.tro.antiyoy;

/**
 * Created by ivan on 22.07.14.
 */
class ButtonFactory {
    private final MenuControllerLighty menuControllerLighty;
    private final ButtonRenderer buttonRenderer;


    public ButtonFactory(MenuControllerLighty menuControllerLighty) {
        this.menuControllerLighty = menuControllerLighty;
        buttonRenderer = new ButtonRenderer();
    }


    public ButtonLighty getButton(SimpleRectangle position, int id, String text) {
        ButtonLighty buttonLighty = menuControllerLighty.getButtonById(id);
        if (buttonLighty == null) { // if it's the first time
            buttonLighty = new ButtonLighty(position, id, menuControllerLighty);
            if (text != null) {
                buttonLighty.addTextLine(text);
                buttonRenderer.renderButton(buttonLighty);
            }
            menuControllerLighty.addMenuBlockToArray(buttonLighty);
        }
        buttonLighty.setVisible(true);
        buttonLighty.setTouchable(true);
        buttonLighty.factorModel.beginSpawning(MenuControllerLighty.SPAWN_ANIM, MenuControllerLighty.SPAWN_SPEED);
        buttonLighty.factorModel.setValues(0, 0.001);
        buttonLighty.touchAnimation = true;
        return buttonLighty;
    }
}
