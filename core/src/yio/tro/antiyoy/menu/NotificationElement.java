package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class NotificationElement extends InterfaceElement{

    public static final int AUTO_HIDE_DELAY = 1500;

    MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    FactorYio appearFactor;
    boolean autoHide;
    public PointYio textPosition, textDelta;
    public String message;
    public BitmapFont font;
    private long timeToHide;
    float textOffset;


    public NotificationElement(MenuControllerYio menuControllerYio, int id) {
        super(id);
        this.menuControllerYio = menuControllerYio;

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        autoHide = false;
        message = "";
        timeToHide = 0;
        font = Fonts.smallerMenuFont;
        textOffset = 0.03f * GraphicsYio.width;
        textPosition = new PointYio();
        textDelta = new PointYio();
    }


    @Override
    public void move() {
        appearFactor.move();

        updateViewPosition();
        updateTextPosition();

        checkToDie();
    }


    private void updateTextPosition() {
        textPosition.x = (float) (viewPosition.x + textDelta.x);
        textPosition.y = (float) (viewPosition.y + textDelta.y);
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);

        viewPosition.y += (1 - appearFactor.get()) * 1.5f * position.height;
    }


    private void checkToDie() {
        if (autoHide && System.currentTimeMillis() > timeToHide) {
            destroy();
        }
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(1, 3);

        autoHide = false;
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(3, 1);
        move();
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public boolean isTouchable() {
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    public void enableAutoHide() {
        autoHide = true;

        timeToHide = System.currentTimeMillis() + AUTO_HIDE_DELAY;
    }


    public void setMessage(String message) {
        this.message = message;

        updateTextDelta();
    }


    private void updateTextDelta() {
        textDelta.x = textOffset;
        float textHeight = GraphicsYio.getTextHeight(font, message);
        textDelta.y = (float) (position.height / 2 + textHeight / 2);
    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);
    }


    @Override
    public boolean isAnotherSceneCreationIgnored() {
        return true;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderNotificationElement;
    }
}
