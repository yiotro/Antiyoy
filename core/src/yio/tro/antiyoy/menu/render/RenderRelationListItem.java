package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.RelationListItem;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderRelationListItem extends AbstractRenderCustomListItem {


    private RelationListItem relationListItem;
    private float alpha;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderItem(AbstractCustomListItem item) {
        relationListItem = (RelationListItem) item;
        alpha = item.customizableListYio.getFactor().get();

        if (alpha < 0.3) return;
        if (item.customizableListYio.getFactor().getGravity() < 0 && alpha < 0.85) return;

        GraphicsYio.setBatchAlpha(batch, alpha);
        GraphicsYio.drawByRectangle(
                batch,
                getBackgroundColor(relationListItem.editorRelation.color1),
                relationListItem.outerBoundsOne
        );
        GraphicsYio.drawByRectangle(
                batch,
                getBackgroundColor(relationListItem.editorRelation.color2),
                relationListItem.outerBoundsTwo
        );
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), relationListItem.nameOne, alpha);
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), relationListItem.nameTwo, alpha);
        GraphicsYio.drawByCircle(
                batch,
                getRelationTexture(relationListItem.editorRelation.relation),
                relationListItem.iconPosition
        );
        renderDefaultSelection(relationListItem);
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    public TextureRegion getRelationTexture(int relation) {
        switch (relation) {
            default:
                return null;
            case DiplomaticRelation.NEUTRAL:
                return MenuRender.renderDiplomacyElement.neutralIcon;
            case DiplomaticRelation.ENEMY:
                return MenuRender.renderDiplomacyElement.enemyIcon;
            case DiplomaticRelation.FRIEND:
                return MenuRender.renderDiplomacyElement.friendIcon;
        }
    }


    private TextureRegion getBackgroundColor(int color) {
        return MenuRender.renderDiplomacyElement.getBackgroundPixelByColor(color);
    }
}
