package yio.tro.antiyoy.menu.keyboard;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class KbButton {


    BasicKeyboardElement basicKeyboardElement;
    public RectangleYio position;
    PointYio delta;
    public String value, key;
    float textWidth, textHeight;
    public PointYio textPosition;
    public FactorYio selectionFactor;
    boolean isIcon;


    public KbButton(BasicKeyboardElement basicKeyboardElement) {
        this.basicKeyboardElement = basicKeyboardElement;
        position = new RectangleYio();
        delta = new PointYio();
        key = null;
        value = null;
        textWidth = 0;
        textHeight = 0;
        textPosition = new PointYio();
        selectionFactor = new FactorYio();
        isIcon = false;
    }


    void move() {
        updatePosition();
        updateTextPosition();
        moveSelection();
    }


    private void moveSelection() {
        selectionFactor.move();
    }


    public boolean isSelected() {
        return selectionFactor.get() > 0;
    }


    public boolean isTouched(PointYio touchPoint) {
        return position.isPointInside(touchPoint, 0);
    }


    public boolean isIconButton() {
        return isIcon;
    }


    public void setIcon(boolean icon) {
        isIcon = icon;
    }


    public void select() {
        selectionFactor.setValues(1, 0);
        selectionFactor.destroy(1, 2);
    }


    private void updateTextPosition() {
        textPosition.x = (float) (position.x + position.width / 2 - textWidth / 2);
        textPosition.y = (float) (position.y + position.height / 2 + textHeight / 2);
    }


    private void updatePosition() {
        position.x = basicKeyboardElement.viewPosition.x + delta.x;
        position.y = basicKeyboardElement.viewPosition.y + delta.y;
    }


    void setSize(double width, double height) {
        position.width = width;
        position.height = height;
    }


    public void setValue(String value) {
        this.value = value;
        textWidth = GraphicsYio.getTextWidth(basicKeyboardElement.font, value);
        textHeight = GraphicsYio.getTextHeight(basicKeyboardElement.font, value);
    }


    public void setKey(String key) {
        this.key = key;
    }


    void setDelta(double x, double y) {
        delta.set(x, y);
    }
}
