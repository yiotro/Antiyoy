package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.NotificationElement;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneNotification extends AbstractScene {


    public NotificationElement notificationElement;


    public SceneNotification(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        notificationElement = null;
    }


    @Override
    public void create() {
        checkToCreateNotificationElement();

        notificationElement.appear();
    }


    private void checkToCreateNotificationElement() {
        if (notificationElement != null) return;

        notificationElement = new NotificationElement(menuControllerYio, -1);
        double h = 0.05;
        notificationElement.setPosition(generateRectangle(0, 1 - h, 1, h));

        menuControllerYio.addElementToScene(notificationElement);
    }


    public void setValues(String messageKey, boolean autoHide) {
        notificationElement.setMessage(LanguagesManager.getInstance().getString(messageKey));

        if (autoHide) {
            notificationElement.enableAutoHide();
        }
    }


    public void show(String messageKey, boolean autoHide) {
        create();
        setValues(messageKey, autoHide);
    }


    public void show(String messageKey) {
        show(messageKey, true);
    }


    public void hideNotification() {
        if (notificationElement == null) return;

        notificationElement.destroy();
    }


}