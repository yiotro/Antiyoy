package yio.tro.antiyoy.menu.editor_elements.color_picker;

import yio.tro.antiyoy.stuff.*;

public class CpeItem {

    ColorPickerElement colorPickerElement;
    public CircleYio viewPosition;
    PointYio delta;
    public int color;
    public SelectionEngineYio selectionEngineYio;
    public RectangleYio borderPosition;


    public CpeItem(ColorPickerElement colorPickerElement) {
        this.colorPickerElement = colorPickerElement;
        viewPosition = new CircleYio();
        delta = new PointYio();
        color = -1;
        selectionEngineYio = new SelectionEngineYio();
        borderPosition = new RectangleYio();
    }


    void move() {
        updateViewPosition();
        updateBorderPosition();
        selectionEngineYio.move();
    }


    private void updateBorderPosition() {
        borderPosition.setBy(viewPosition);
        borderPosition.increase(GraphicsYio.borderThickness / 2);
    }


    boolean isTouchedBy(PointYio touchPoint) {
        if (Math.abs(touchPoint.x - viewPosition.center.x) > viewPosition.radius) return false;
        if (Math.abs(touchPoint.y - viewPosition.center.y) > viewPosition.radius) return false;
        return true;
    }


    private void updateViewPosition() {
        viewPosition.center.x = (float) (colorPickerElement.viewPosition.x + delta.x);
        viewPosition.center.y = (float) (colorPickerElement.viewPosition.y + delta.y);
    }
}
