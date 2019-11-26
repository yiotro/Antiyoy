package yio.tro.antiyoy.menu.customizable_list;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.menu.render.AbstractRenderCustomListItem;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class TitleListItem extends AbstractSingleLineItem{

    @Override
    protected BitmapFont getTitleFont() {
        return Fonts.gameFont;
    }


    @Override
    protected double getHeight() {
        return 0.06f * GraphicsYio.height;
    }


    @Override
    protected void onClicked() {

    }


    @Override
    protected void onPositionChanged() {
        title.delta.x = (float) (getWidth() / 2 - title.width / 2);
        title.delta.y = (float) (getHeight() / 2 + title.height / 2);
    }


    @Override
    public AbstractRenderCustomListItem getRender() {
        return MenuRender.renderTitleListItem;
    }
}
