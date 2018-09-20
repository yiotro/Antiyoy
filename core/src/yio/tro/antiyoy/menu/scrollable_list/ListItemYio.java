package yio.tro.antiyoy.menu.scrollable_list;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.context_list_menu.LiEditable;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class ListItemYio implements LiEditable{


    ScrollableListYio scrollableListYio;
    public RectangleYio position;
    PointYio delta;
    FactorYio selectionFactor;
    public PointYio titlePosition, descPosition;
    public String key, title, description;
    float titleOffset, descOffset;
    public float descWidth, titleWidth;
    public int bckViewType;


    public ListItemYio(ScrollableListYio scrollableListYio) {
        this.scrollableListYio = scrollableListYio;

        position = new RectangleYio();
        delta = new PointYio();
        selectionFactor = new FactorYio();
        titlePosition = new PointYio();
        descPosition = new PointYio();
        key = null;
        title = null;
        description = null;
        titleOffset = 0.02f * GraphicsYio.width;
        descOffset = 0.045f * GraphicsYio.height;
        titleWidth = 0;
        bckViewType = -1;
        descWidth = 0;
    }


    boolean isTouched(PointYio touchPoint) {
        return scrollableListYio.isTouchInsideRectangle(touchPoint.x, touchPoint.y, position, 0);
    }


    public boolean isVisible() {
        if (position.y > scrollableListYio.viewPosition.y + scrollableListYio.viewPosition.height) return false;
        if (position.y + position.height < scrollableListYio.viewPosition.y) return false;

        return true;
    }


    void select() {
        selectionFactor.setValues(1, 0);
        selectionFactor.destroy(1, 2);
    }


    public boolean isSelected() {
        return selectionFactor.get() > 0;
    }


    public FactorYio getSelectionFactor() {
        return selectionFactor;
    }


    void move() {
        updatePosition();
        updateTitlePosition();
        updateDescPosition();
    }


    private void updateDescPosition() {
        descPosition.x = (float) (position.x + titleOffset);
        descPosition.y = titlePosition.y - descOffset;

//        if (scrollableListYio.appearFactor.get() < 1) {
//            descPosition.x -= (1 - scrollableListYio.appearFactor.get()) * scrollableListYio.position.width;
//        }
    }


    private void updateTitlePosition() {
        titlePosition.x = (float) (position.x + titleOffset);
        titlePosition.y = (float) (position.y + position.height - titleOffset);

//        if (scrollableListYio.appearFactor.get() < 1) {
//            titlePosition.x -= (1 - scrollableListYio.appearFactor.get()) * scrollableListYio.position.width;
//        }
    }


    private void updatePosition() {
        position.x = scrollableListYio.position.x + delta.x;
        position.y = scrollableListYio.position.y + delta.y + scrollableListYio.hook;
    }


    void moveSelection() {
        selectionFactor.move();
    }


    public void setBckViewType(int bckViewType) {
        this.bckViewType = bckViewType;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public void setTitle(String title) {
        this.title = title;

        titleWidth = GraphicsYio.getTextWidth(scrollableListYio.titleFont, title);
    }


    public void setDescription(String description) {
        this.description = description;

        descWidth = GraphicsYio.getTextWidth(scrollableListYio.descFont, description);
    }


    @Override
    public String toString() {
        return "[Item: " +
                key + ", " +
                title + ", " +
                description +
                "]";
    }


    @Override
    public void rename(String name) {
        if (name.length() == 0) return;

        setTitle(name);
        scrollableListYio.listBehaviorYio.onItemRenamed(this);
    }


    @Override
    public void onDeleteRequested() {
        scrollableListYio.listBehaviorYio.onItemDeleteRequested(this);
    }


    @Override
    public void onContextMenuDestroy() {
        scrollableListYio.touched = false; // to move selection
    }


    @Override
    public String getEditableName() {
        return title;
    }
}
