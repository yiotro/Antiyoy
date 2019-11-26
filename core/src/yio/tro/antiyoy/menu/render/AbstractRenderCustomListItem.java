package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public abstract class AbstractRenderCustomListItem extends MenuRender{

    public abstract void loadTextures();


    public abstract void renderItem(AbstractCustomListItem item);


    protected void renderDefaultSelection(AbstractCustomListItem item) {
        if (!item.selectionEngineYio.isSelected()) return;

        GraphicsYio.setBatchAlpha(batch, item.selectionEngineYio.getAlpha() * item.customizableListYio.getFactor().get());
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), item.viewPosition);
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {

    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }


    protected float getAlpha(AbstractCustomListItem abstractCustomListItem) {
        return abstractCustomListItem.customizableListYio.getFactor().get();
    }


    protected void renderTextOptimized(RenderableTextYio renderableTextYio, double alpha) {
        BitmapFont font = renderableTextYio.font;
        Color fontColor = font.getColor();
        font.setColor(Color.BLACK);
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), renderableTextYio, (float) alpha);
        font.setColor(fontColor);
    }

}
