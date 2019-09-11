package yio.tro.antiyoy.menu.diplomatic_dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class AcButton {

    public static final int ACTION_YES = 0;
    public static final int ACTION_NO = 1;

    AbstractDiplomaticDialog dialog;
    public RectangleYio position;
    PointYio delta;
    public BitmapFont font;
    public String text;
    public PointYio textPosition;
    PointYio textDelta;
    public FactorYio selectionFactor;
    public AcActionType actionType;
    float touchOffset;


    public AcButton(AbstractDiplomaticDialog dialog) {
        this.dialog = dialog;

        position = new RectangleYio();
        delta = new PointYio();
        font = null;
        text = null;
        textPosition = new PointYio();
        textDelta = new PointYio();
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


    void updatePosition() {
        position.x = dialog.viewPosition.x + delta.x;
        position.y = dialog.viewPosition.y + delta.y;

        textPosition.x = (float) (position.x + textDelta.x);
        textPosition.y = (float) (position.y + textDelta.y);
    }


    void moveSelection() {
        selectionFactor.move();
    }


    public void setFont(BitmapFont font) {
        this.font = font;
    }


    public void setText(String text) {
        this.text = text;
    }


    void updateTextDelta() {
        if (font == null) return;
        if (text == null) return;

        float textWidth = GraphicsYio.getTextWidth(font, text);
        float textHeight = GraphicsYio.getTextHeight(font, text);

        textDelta.x = (float) (position.width / 2 - textWidth / 2);
        textDelta.y = (float) (position.height / 2 + textHeight / 2);
    }


    public void setAction(AcActionType type) {
        actionType = type;
    }


    public void setTouchOffset(float touchOffset) {
        this.touchOffset = touchOffset;
    }
}
