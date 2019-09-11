package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.editor_elements.add_relation.AddEditorRelationElement;
import yio.tro.antiyoy.menu.editor_elements.add_relation.AerItem;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderAddEditorRelationElement extends MenuRender{


    private AddEditorRelationElement addEditorRelationElement;
    private float alpha;
    private ColorsManager colorsManager;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        addEditorRelationElement = (AddEditorRelationElement) element;
        alpha = addEditorRelationElement.getFactor().get();
        colorsManager = getGameView().yioGdxGame.gameController.colorsManager;

        GraphicsYio.setBatchAlpha(batch, alpha);
        renderItems();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderItems() {
        for (AerItem item : addEditorRelationElement.items) {
            renderSingleItem(item);
        }
    }


    private void renderSingleItem(AerItem item) {
        if (item.value == -1) {
            GraphicsYio.renderBorder(batch, getBlackPixel(), item.border);
            renderSelection(item);
            return;
        }

        switch (item.type) {
            case one:
            case two:
                GraphicsYio.drawByCircle(batch, getBackgroundColor(item.value), item.viewPosition);
                break;
            case relation:
                GraphicsYio.drawByCircle(batch, getRelationTexture(item.value), item.viewPosition);
                break;
        }
        renderSelection(item);
    }


    private void renderSelection(AerItem item) {
        if (!item.selectionEngineYio.isSelected()) return;
        GraphicsYio.setBatchAlpha(batch, alpha * item.selectionEngineYio.getAlpha());
        GraphicsYio.drawByCircle(batch, getBlackPixel(), item.viewPosition);
        GraphicsYio.setBatchAlpha(batch, alpha);
    }


    private TextureRegion getRelationTexture(int relation) {
        switch (relation) {
            default:
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


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
