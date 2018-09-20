package yio.tro.antiyoy.menu.save_slot_selector;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.context_list_menu.LiEditable;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SsItem implements LiEditable{


    SaveSlotSelector saveSlotSelector;
    public RectangleYio position;
    PointYio delta;
    FactorYio selectionFactor;
    public PointYio titlePosition, descPosition;
    public String key, title, description;
    float titleOffset, descOffset;
    public int bckViewType;


    public SsItem(SaveSlotSelector saveSlotSelector) {
        this.saveSlotSelector = saveSlotSelector;

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
        bckViewType = -1;
    }


    boolean isTouched(PointYio touchPoint) {
        return saveSlotSelector.isTouchInsideRectangle(touchPoint.x, touchPoint.y, position, 0);
    }


    public boolean isVisible() {
        if (position.y > saveSlotSelector.viewPosition.y + saveSlotSelector.viewPosition.height) return false;
        if (position.y + position.height < saveSlotSelector.viewPosition.y) return false;

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

//        if (saveSlotSelector.appearFactor.get() < 1) {
//            descPosition.x -= (1 - saveSlotSelector.appearFactor.get()) * saveSlotSelector.position.width;
//        }
    }


    private void updateTitlePosition() {
        titlePosition.x = (float) (position.x + titleOffset);
        titlePosition.y = (float) (position.y + position.height - titleOffset);

//        if (saveSlotSelector.appearFactor.get() < 1) {
//            titlePosition.x -= (1 - saveSlotSelector.appearFactor.get()) * saveSlotSelector.position.width;
//        }
    }


    private void updatePosition() {
        position.x = saveSlotSelector.position.x + delta.x;
        position.y = saveSlotSelector.position.y + delta.y + saveSlotSelector.hook;
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
    }


    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public void rename(String name) {
        if (name.length() == 0) return;

        setTitle(name);
        saveSlotSelector.onSlotRenamed(this);
    }


    @Override
    public String getEditableName() {
        return title;
    }


    @Override
    public void onDeleteRequested() {
        saveSlotSelector.readyToDeleteItem = true;
        saveSlotSelector.targetItem = this;
    }


    @Override
    public void onContextMenuDestroy() {
        saveSlotSelector.touched = false; // to move selection
    }
}
