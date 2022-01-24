package yio.tro.antiyoy.menu.render;

import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.ScrollListItem;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderScrollListItem extends AbstractRenderCustomListItem{


    private ScrollListItem scrollListItem;
    private float alpha;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderItem(AbstractCustomListItem item) {
        scrollListItem = (ScrollListItem) item;
        alpha = scrollListItem.customizableListYio.getFactor().get();

        renderHighlight();
        renderTextOptimized(scrollListItem.title, alpha);
        renderDefaultSelection(scrollListItem);
    }


    private void renderHighlight() {
        if (!scrollListItem.highlightEnabled) return;

        GraphicsYio.setBatchAlpha(batch, 0.04 * scrollListItem.customizableListYio.getFactor().get());
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), scrollListItem.viewPosition);
        GraphicsYio.setBatchAlpha(batch, 1);
    }
}
