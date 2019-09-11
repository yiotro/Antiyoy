package yio.tro.antiyoy.menu.customizable_list;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.menu.render.AbstractRenderCustomListItem;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class SampleListItem extends AbstractSingleLineItem{


    @Override
    protected BitmapFont getTitleFont() {
        return Fonts.smallerMenuFont;
    }


    @Override
    protected double getHeight() {
        return 0.1f * GraphicsYio.height;
    }


    @Override
    protected void onClicked() {
        System.out.println("SampleListItem.onClicked");
    }


    @Override
    public AbstractRenderCustomListItem getRender() {
        return MenuRender.renderSingleListItem;
    }
}
