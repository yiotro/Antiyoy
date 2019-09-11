package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.editor_elements.edit_land.EditLandElement;
import yio.tro.antiyoy.menu.editor_elements.edit_land.EleItem;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderEditLandElement extends MenuRender{


    private EditLandElement editLandElement;
    private float alpha;
    private TextureRegion deleteTexture;
    private TextureRegion randomHexTexture;


    @Override
    public void loadTextures() {
        deleteTexture = GraphicsYio.loadTextureRegion("menu/editor/remove_icon.png", true);
        randomHexTexture = GraphicsYio.loadTextureRegion("menu/editor/random_color_icon.png", true);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        editLandElement = (EditLandElement) element;
        alpha = editLandElement.getFactor().get();

        GraphicsYio.setBatchAlpha(batch, alpha);
        for (EleItem item : editLandElement.items) {
            renderItem(item);
        }
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderItem(EleItem item) {
        GraphicsYio.drawByCircle(batch, getItemTexture(item), item.viewPosition);

        if (item.selectionEngineYio.isSelected()) {
            GraphicsYio.setBatchAlpha(batch, alpha * item.selectionEngineYio.getAlpha());
            GraphicsYio.drawByCircle(batch, getBlackPixel(), item.touchPosition);
            GraphicsYio.setBatchAlpha(batch, alpha);
        }
    }


    private TextureRegion getItemTexture(EleItem item) {
        switch (item.actionType) {
            default:
                return null;
            case def:
                return getGameView().texturesManager.getHexTexture(item.value);
            case delete:
                return deleteTexture;
            case random:
                return randomHexTexture;
        }
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
