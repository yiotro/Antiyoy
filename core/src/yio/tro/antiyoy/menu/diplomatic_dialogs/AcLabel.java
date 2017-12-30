package yio.tro.antiyoy.menu.diplomatic_dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class AcLabel implements ReusableYio {

    AbstractDiplomaticDialog dialog;
    public PointYio position;
    public BitmapFont font;
    public String text;
    PointYio delta;


    public AcLabel(AbstractDiplomaticDialog dialog) {
        this.dialog = dialog;

        position = new PointYio();
        delta = new PointYio();
    }


    @Override
    public void reset() {
        position.reset();
        delta.reset();
        font = null;
        text = null;
    }


    void updatePosition() {
        position.x = (float) (dialog.viewPosition.x + delta.x);
        position.y = (float) (dialog.viewPosition.y + delta.y);
    }


    void setDelta(float x, float y) {
        delta.set(x, y);
    }


    public void setFont(BitmapFont font) {
        this.font = font;
    }


    public void setText(String text) {
        this.text = text;
    }
}
