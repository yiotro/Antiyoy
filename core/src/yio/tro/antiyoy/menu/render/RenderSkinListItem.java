package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.SkinListItem;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderSkinListItem extends AbstractRenderCustomListItem{


    private SkinListItem slItem;
    private TextureRegion iconTexture;


    @Override
    public void loadTextures() {
        iconTexture = GraphicsYio.loadTextureRegion("menu/v_icon.png", true);
    }


    @Override
    public void renderItem(AbstractCustomListItem item) {
        slItem = (SkinListItem) item;

        renderTextOptimized(slItem.title, slItem.customizableListYio.getFactor().get());
        renderDefaultSelection(slItem);
        renderIcon();
    }


    private void renderIcon() {
        if (!slItem.isChosen()) return;
        GraphicsYio.drawByCircle(batch, iconTexture, slItem.iconPosition);
    }
}
