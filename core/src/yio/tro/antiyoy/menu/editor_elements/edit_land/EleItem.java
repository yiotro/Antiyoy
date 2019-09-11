package yio.tro.antiyoy.menu.editor_elements.edit_land;

import yio.tro.antiyoy.stuff.CircleYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.SelectionEngineYio;

public class EleItem {

    EditLandElement editLandElement;
    public CircleYio viewPosition;
    PointYio delta;
    public EleActionType actionType;
    public int value;
    public CircleYio touchPosition;
    public SelectionEngineYio selectionEngineYio;


    public EleItem(EditLandElement editLandElement) {
        this.editLandElement = editLandElement;
        viewPosition = new CircleYio();
        delta = new PointYio();
        actionType = null;
        touchPosition = new CircleYio();
        selectionEngineYio = new SelectionEngineYio();
        value = -1;
    }


    void move() {
        updateViewPosition();
        updateTouchPosition();
        selectionEngineYio.move();
    }


    boolean isTouchedBy(PointYio touchPoint) {
        if (Math.abs(touchPosition.center.x - touchPoint.x) > touchPosition.radius) return false;
        if (Math.abs(touchPosition.center.y - touchPoint.y) > touchPosition.radius) return false;
        return true;
    }


    private void updateTouchPosition() {
        touchPosition.center.setBy(viewPosition.center);
    }


    private void updateViewPosition() {
        viewPosition.center.x = (float) (editLandElement.viewPosition.x + delta.x);
        viewPosition.center.y = (float) (editLandElement.viewPosition.y + delta.y);
    }


    public void setActionType(EleActionType actionType) {
        this.actionType = actionType;
    }


    public void setValue(int value) {
        this.value = value;
    }
}
