package yio.tro.antiyoy.menu.diplomatic_dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.*;

public class AcButton {

    AbstractDiplomaticDialog dialog;
    public RectangleYio position;
    PointYio delta;
    public RenderableTextYio title;
    public FactorYio selectionFactor;
    public AcActionType actionType;
    float touchOffset;


    public AcButton(AbstractDiplomaticDialog dialog) {
        this.dialog = dialog;

        position = new RectangleYio();
        delta = new PointYio();
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        selectionFactor = new FactorYio();
        actionType = null;
        touchOffset = 0;
    }


    void select() {
        selectionFactor.setValues(1, 0);
        selectionFactor.destroy(1, 2);
    }


    public boolean isSelected() {
        return selectionFactor.get() > 0;
    }


    boolean isTouched(PointYio touchPoint) {
        if (touchPoint.x < position.x) return false;
        if (touchPoint.y < position.y - touchOffset) return false;
        if (touchPoint.x > position.x + position.width) return false;
        if (touchPoint.y > position.y + position.height + touchOffset) return false;
        return true;
    }


    void move() {
        updatePosition();
        updateTitlePosition();
    }


    private void updateTitlePosition() {
        title.centerVertical(position);
        title.centerHorizontal(position);
        title.updateBounds();
    }


    private void updatePosition() {
        position.x = dialog.viewPosition.x + delta.x;
        position.y = dialog.viewPosition.y + delta.y;
    }


    void moveSelection() {
        selectionFactor.move();
    }


    public void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    public void setAction(AcActionType type) {
        actionType = type;
    }


    public void setTouchOffset(float touchOffset) {
        this.touchOffset = touchOffset;
    }
}
