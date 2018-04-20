package yio.tro.antiyoy.menu.replay_selector;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class RsItem {

    ReplaySelector replaySelector;
    public RectangleYio position;
    PointYio delta;
    FactorYio selectionFactor;
    public PointYio titlePosition, descPosition;
    public String key, title, description;
    float titleOffset, descOffset;
    public PointYio removeIconPosition;


    public RsItem(ReplaySelector replaySelector) {
        this.replaySelector = replaySelector;

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
        removeIconPosition = new PointYio();
    }


    boolean isTouched(PointYio touchPoint) {
        return replaySelector.isTouchInsideRectangle(touchPoint.x, touchPoint.y, position, 0);
    }


    public boolean isVisible() {
        if (position.y > replaySelector.viewPosition.y + replaySelector.viewPosition.height) return false;
        if (position.y + position.height < replaySelector.viewPosition.y) return false;

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
        updateRemoveIconPosition();
    }


    private void updateRemoveIconPosition() {
        removeIconPosition.x = (float) (position.x + position.width - 2 * titleOffset);
        removeIconPosition.y = (float) (position.y + position.height - 2 * titleOffset);
    }


    private void updateDescPosition() {
        descPosition.x = (float) (position.x + titleOffset);
        descPosition.y = titlePosition.y - descOffset;
    }


    private void updateTitlePosition() {
        titlePosition.x = (float) (position.x + titleOffset);
        titlePosition.y = (float) (position.y + position.height - titleOffset);
    }


    private void updatePosition() {
        position.x = replaySelector.position.x + delta.x;
        position.y = replaySelector.position.y + delta.y + replaySelector.hook;
    }


    void moveSelection() {
        selectionFactor.move();
    }


    public void setKey(String key) {
        this.key = key;
    }


    @Override
    public String toString() {
        return "[RsItem: " +
                title + " - " + key +
                "]";
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}
