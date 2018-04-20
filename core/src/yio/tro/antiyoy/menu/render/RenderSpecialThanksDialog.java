package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderSpecialThanksDialog extends RenderScrollableList{


    @Override
    protected TextureRegion getItemBackgroundTexture(ListItemYio item) {
        return backgroundTexture;
    }


    @Override
    protected void renderItemDescription(ListItemYio item) {
        if (scrollableList.textAlphaFactor.get() == 0) return;

        GraphicsYio.setFontAlpha(scrollableList.descFont, scrollableList.textAlphaFactor.get());

        scrollableList.descFont.draw(
                batch,
                item.description,
                (float) (item.position.x + item.position.width - 0.03f * GraphicsYio.width - item.descWidth),
                item.titlePosition.y
        );

        GraphicsYio.setFontAlpha(scrollableList.descFont, 1);
    }


    @Override
    protected void renderItemSelection(ListItemYio item) {
        // no selection here
    }
}
