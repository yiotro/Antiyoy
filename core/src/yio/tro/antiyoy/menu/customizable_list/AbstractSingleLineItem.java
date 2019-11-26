package yio.tro.antiyoy.menu.customizable_list;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public abstract class AbstractSingleLineItem extends AbstractCustomListItem{

    public RenderableTextYio title;


    @Override
    protected void initialize() {
        title = new RenderableTextYio();
        title.setFont(getTitleFont());
    }


    protected abstract BitmapFont getTitleFont();


    @Override
    protected void move() {
        moveRenderableTextByDefault(title);
    }


    public void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    @Override
    protected double getWidth() {
        return getDefaultWidth();
    }


    public void setFont(BitmapFont font) {
        title.setFont(font);
        title.updateMetrics();
    }


    @Override
    protected void onPositionChanged() {
        title.delta.x = 0.04f * GraphicsYio.width;
        title.delta.y = (float) (getHeight() / 2 + title.height / 2);
    }


    @Override
    protected void onLongTapped() {

    }
}
