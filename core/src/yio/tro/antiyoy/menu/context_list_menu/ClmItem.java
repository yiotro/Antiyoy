package yio.tro.antiyoy.menu.context_list_menu;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class ClmItem {


    ContextListMenuElement contextListMenuElement;
    public RectangleYio position;
    PointYio delta;
    String key;
    public String value;
    public PointYio textPosition;
    float textWidth, textHeight;
    public FactorYio selectionFactor;


    public ClmItem(ContextListMenuElement contextListMenuElement) {
        this.contextListMenuElement = contextListMenuElement;

        position = new RectangleYio();
        delta = new PointYio();
        key = null;
        value = null;
        textPosition = new PointYio();
        textWidth = 0;
        textHeight = 0;
        selectionFactor = new FactorYio();
    }


    public void setValue(String value) {
        this.value = value;
        updateTextMetrics();
    }


    private void updateTextMetrics() {
        textWidth = GraphicsYio.getTextWidth(contextListMenuElement.font, value);
        textHeight = GraphicsYio.getTextHeight(contextListMenuElement.font, value);
    }


    void move() {
        updatePosition();
        updateTextPosition();
        selectionFactor.move();
    }


    public void select() {
        selectionFactor.setValues(1, 0);
        selectionFactor.destroy(1, 2);
    }


    public boolean isSelected() {
        return selectionFactor.get() > 0;
    }


    public boolean isTouched(PointYio touchPoint) {
        return position.isPointInside(touchPoint, 0.02f * GraphicsYio.width);
    }


    private void updateTextPosition() {
        textPosition.x = (float) (position.x + position.width / 2 - textWidth / 2);
        textPosition.y = (float) (position.y + position.height / 2 + textHeight / 2);
    }


    private void updatePosition() {
        position.x = contextListMenuElement.viewPosition.x + delta.x;
        position.y = contextListMenuElement.viewPosition.y + delta.y;
    }


    void setSize(double w, double h) {
        position.width = w;
        position.height = h;
    }


    public void setKey(String key) {
        this.key = key;
    }
}
