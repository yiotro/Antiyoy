package yio.tro.antiyoy.menu.editor_elements.add_relation;

import yio.tro.antiyoy.stuff.CircleYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.SelectionEngineYio;

public class AerItem {

    AddEditorRelationElement addEditorRelationElement;
    public AerType type;
    public CircleYio viewPosition;
    public RectangleYio border;
    public int value;
    PointYio delta;
    public SelectionEngineYio selectionEngineYio;


    public AerItem(AddEditorRelationElement addEditorRelationElement) {
        this.addEditorRelationElement = addEditorRelationElement;
        type = null;
        viewPosition = new CircleYio();
        delta = new PointYio();
        border = new RectangleYio();
        selectionEngineYio = new SelectionEngineYio();
        value = -1;
    }


    void move() {
        updateViewPosition();
        updateBorder();
        selectionEngineYio.move();
    }


    private void updateBorder() {
        border.setBy(viewPosition);
    }


    private void updateViewPosition() {
        viewPosition.center.x = (float) (addEditorRelationElement.viewPosition.x + delta.x);
        viewPosition.center.y = (float) (addEditorRelationElement.viewPosition.y + delta.y);
    }


    public void setValue(int value) {
        this.value = value;
    }
}
