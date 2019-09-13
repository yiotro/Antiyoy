package yio.tro.antiyoy.menu.diplomacy_element;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class DeItem implements ReusableYio, Comparable<DeItem> {


    public static final int STATUS_NEUTRAL = 0;
    public static final int STATUS_FRIEND = 1;
    public static final int STATUS_ENEMY = 2;
    public static final int STATUS_DEAD = 3;

    DiplomacyElement diplomacyElement;
    public RectangleYio position, bottomRectangle;
    PointYio delta;
    FactorYio selectionFactor;
    public PointYio titlePosition, statusPosition, descPosition;
    public String title, descriptionString;
    public int fraction;
    float titleOffset, statusOffset, descOffset, statusOffDelta;
    float titleHeight, titleWidth;
    public int status;
    public float statusRadius;
    public boolean keepSelection;
    public boolean blackMarkEnabled;
    public PointYio blackMarkPosition;
    public float blackMarkRadius;


    public DeItem(DiplomacyElement diplomacyElement) {
        this.diplomacyElement = diplomacyElement;

        position = new RectangleYio();
        delta = new PointYio();
        selectionFactor = new FactorYio();
        titlePosition = new PointYio();
        statusPosition = new PointYio();
        descPosition = new PointYio();
        bottomRectangle = new RectangleYio();
        blackMarkPosition = new PointYio();
    }


    @Override
    public void reset() {
        position.reset();
        delta.reset();
        titlePosition.reset();
        statusPosition.reset();
        blackMarkPosition.reset();
        fraction = -1;
        title = null;
        titleHeight = 0;
        titleWidth = 0;
        status = -1;
        initMetrics();
        descPosition.reset();
        descriptionString = null;
        blackMarkEnabled = false;
        bottomRectangle.reset();
        setKeepSelection(false);
    }


    private void initMetrics() {
        titleOffset = 0.025f * GraphicsYio.width;
        statusOffset = 0.05f * GraphicsYio.width;
        statusRadius = 0.05f * GraphicsYio.width;
        statusOffDelta = 0.01f * GraphicsYio.width;
        descOffset = 0.1f * GraphicsYio.width;
        blackMarkRadius = 0.025f * GraphicsYio.width;
    }


    boolean isTouched(PointYio touchPoint) {
        return diplomacyElement.isTouchInsideRectangle(touchPoint.x, touchPoint.y, position, 0);
    }


    public boolean isTopVisible() {
        if (position.y + position.height / 2 > diplomacyElement.internalBackground.y + diplomacyElement.internalBackground.height) return false;
        if (position.y + position.height < diplomacyElement.internalBackground.y) return false;

        return true;
    }


    public boolean isBottomVisible() {
        if (position.y > diplomacyElement.internalBackground.y + diplomacyElement.internalBackground.height) return false;
        if (position.y + position.height / 2 < diplomacyElement.internalBackground.y) return false;

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
        updateStatusPosition();
        updateDescriptionPosition();
        updateBottomRectangle();
        updateBlackMarkPosition();
    }


    private void updateBlackMarkPosition() {
        if (!blackMarkEnabled) return;

        blackMarkPosition.x = titlePosition.x + titleWidth + titleOffset + blackMarkRadius;
        blackMarkPosition.y = titlePosition.y - titleHeight / 2;
    }


    private void updateBottomRectangle() {
        bottomRectangle.setBy(position);
        bottomRectangle.height = position.height / 2;
    }


    private void updateDescriptionPosition() {
        descPosition.x = (float) (position.x + titleOffset);
        descPosition.y = (float) (position.y + position.height - descOffset);
    }


    private void updateStatusPosition() {
        statusPosition.x = (float) (position.x + position.width - statusOffset - statusRadius);
//        statusPosition.y = (float) (position.y + position.height / 2);
        statusPosition.y = (float) (position.y + position.height - titleOffset - statusOffDelta - titleHeight / 2);
    }


    private void updateTitlePosition() {
        titlePosition.x = (float) (position.x + titleOffset);
        titlePosition.y = (float) (position.y + position.height - titleOffset);
//        titlePosition.y = (float) (position.y + (position.height + titleHeight) / 2);
    }


    private void updatePosition() {
        position.x = diplomacyElement.viewPosition.x + delta.x;
        position.y = diplomacyElement.viewPosition.y + delta.y + diplomacyElement.hook;
    }


    void moveSelection() {
        if (keepSelection) return;

        selectionFactor.move();
    }


    public void setKeepSelection(boolean keepSelection) {
        this.keepSelection = keepSelection;
    }


    public void setFraction(int fraction) {
        this.fraction = fraction;
    }


    public void setTitle(String title) {
        this.title = title;

        updateTitleMetrics();
    }


    private void updateTitleMetrics() {
        titleHeight = GraphicsYio.getTextHeight(diplomacyElement.titleFont, title);
        titleWidth = GraphicsYio.getTextWidth(diplomacyElement.titleFont, title);
    }


    public void setStatus(int status) {
        this.status = status;
    }


    public void setBlackMarkEnabled(boolean blackMarkEnabled) {
        this.blackMarkEnabled = blackMarkEnabled;
    }


    public void setDescriptionString(String descriptionString) {
        this.descriptionString = descriptionString;
    }


    @Override
    public int compareTo(DeItem o) {
        int anotherFractionModifed = o.fraction;
        if (o.status == STATUS_DEAD) {
            anotherFractionModifed *= 1000;
        }

        int myFractionModified = fraction;
        if (status == STATUS_DEAD) {
            myFractionModified *= 1000;
        }

        return myFractionModified - anotherFractionModifed;
    }
}
