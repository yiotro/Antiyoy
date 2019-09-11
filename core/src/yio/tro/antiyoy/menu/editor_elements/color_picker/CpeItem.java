package yio.tro.antiyoy.menu.editor_elements.color_picker;

import yio.tro.antiyoy.stuff.CircleYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.SelectionEngineYio;

public class CpeItem {

    ColorPickerElement colorPickerElement;
    public CircleYio viewPosition;
    PointYio delta;
    public int color;
    public SelectionEngineYio selectionEngineYio;


    public CpeItem(ColorPickerElement colorPickerElement) {
        this.colorPickerElement = colorPickerElement;
        viewPosition = new CircleYio();
        delta = new PointYio();
        color = -1;
        selectionEngineYio = new SelectionEngineYio();
    }


    void move() {
        updateViewPosition();
        selectionEngineYio.move();
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
