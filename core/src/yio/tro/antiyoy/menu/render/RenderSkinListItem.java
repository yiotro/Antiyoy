package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.SkinLiPreviewIcon;
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

        renderDarken();
        renderTextOptimized(slItem.title, slItem.customizableListYio.getFactor().get());
        renderPreview();
        renderIcon();
        renderDefaultSelection(slItem);
    }


    private void renderPreview() {
        GraphicsYio.setBatchAlpha(batch, slItem.customizableListYio.getFactor().get());
        for (SkinLiPreviewIcon previewIcon : slItem.previewIcons) {
            GraphicsYio.drawByCircle(batch, previewIcon.storage3xTexture.getNormal(), previewIcon.viewPosition);
        }
    }


    private void renderDarken() {
        if (!slItem.darken) return;
        GraphicsYio.setBatchAlpha(batch, 0.04 * slItem.customizableListYio.getFactor().get());
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), slItem.viewPosition);
    }


    private void renderIcon() {
        if (!slItem.isChosen()) return;
        GraphicsYio.setBatchAlpha(batch, 0.8 * slItem.customizableListYio.getFactor().get());
        GraphicsYio.drawByCircle(batch, iconTexture, slItem.iconPosition);
    }
}
