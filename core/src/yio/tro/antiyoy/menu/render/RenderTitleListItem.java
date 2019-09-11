package yio.tro.antiyoy.menu.render;

import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.TitleListItem;

public class RenderTitleListItem extends AbstractRenderCustomListItem{

    private TitleListItem titleItem;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderItem(AbstractCustomListItem item) {
        titleItem = (TitleListItem) item;

        renderTextOptimized(titleItem.title, titleItem.customizableListYio.getFactor().get());
    }
}
