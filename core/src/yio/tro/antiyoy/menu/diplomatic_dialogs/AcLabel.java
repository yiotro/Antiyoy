package yio.tro.antiyoy.menu.diplomatic_dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class AcLabel implements ReusableYio {

    AbstractDiplomaticDialog dialog;
    public RenderableTextYio title;


    public AcLabel(AbstractDiplomaticDialog dialog) {
        this.dialog = dialog;

        title = new RenderableTextYio();
    }


    @Override
    public void reset() {
        title.reset();
    }


    void move() {
        title.position.x = (float) (dialog.viewPosition.x + title.delta.x);
        title.position.y = (float) (dialog.viewPosition.y + title.delta.y);
        title.updateBounds();
    }


    void setDelta(float x, float y) {
        title.delta.set(x, y);
    }


    public void setData(BitmapFont font, String string) {
        if (font == null) {
            font = Fonts.smallerMenuFont;
        }
        if (string == null) {
            string = "-";
        }
        title.setFont(font);
        title.setString(string);
        title.updateMetrics();
    }

}
