package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.menu.ButtonYio;

/**
 * Created by ivan on 11.11.2015.
 */
public class NotificationHolder {

    private ButtonYio button;
    private boolean autoHide;
    private long timeToHide;


    public NotificationHolder() {
        button = null;
        autoHide = false;
    }


    public void move() {
        if (button == null) return;
        if (autoHide && System.currentTimeMillis() > timeToHide) {
            button.menuControllerYio.hideNotification();
            autoHide = false;
        }
    }


    public void setAutoHide(boolean autoHide) {
        this.autoHide = autoHide;
        if (autoHide) timeToHide = System.currentTimeMillis() + 1000;
    }


    public void setButton(ButtonYio button) {
        this.button = button;
    }
}
