package yio.tro.antiyoy;

/**
 * Created by ivan on 11.11.2015.
 */
class NotificationHolder {

    private ButtonLighty button;
    private boolean autoHide;
    private long timeToHide;


    public NotificationHolder() {
        button = null;
        autoHide = false;
    }


    public void move() {
        if (button == null) return;
        if (autoHide && System.currentTimeMillis() > timeToHide) {
            button.menuControllerLighty.hideNotification();
            autoHide = false;
        }
    }


    public void setAutoHide(boolean autoHide) {
        this.autoHide = autoHide;
        if (autoHide) timeToHide = System.currentTimeMillis() + 1000;
    }


    public void setButton(ButtonLighty button) {
        this.button = button;
    }
}
