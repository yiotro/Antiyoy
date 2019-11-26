package yio.tro.antiyoy.menu.render;

import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.ReplayListItem;

public class RenderReplayListItem extends AbstractRenderCustomListItem{


    private ReplayListItem replayListItem;
    private float alpha;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderItem(AbstractCustomListItem item) {
        replayListItem = (ReplayListItem) item;
        alpha = replayListItem.customizableListYio.getFactor().get();

        renderTextOptimized(replayListItem.title, alpha);
        renderTextOptimized(replayListItem.description, alpha);
        renderDefaultSelection(replayListItem);
    }
}
