package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.SimpleDipEntityItem;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderSimpleDipEntityItem extends AbstractRenderCustomListItem{


    private SimpleDipEntityItem simpleDipEntityItem;
    private float alpha;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderItem(AbstractCustomListItem item) {
        simpleDipEntityItem = (SimpleDipEntityItem) item;
        alpha = simpleDipEntityItem.customizableListYio.getFactor().get();

        int color = simpleDipEntityItem.backgroundColor;
        TextureRegion pixelByColor = MenuRender.renderDiplomacyElement.getBackgroundPixelByColor(color);
        GraphicsYio.drawByRectangle(batch, pixelByColor, simpleDipEntityItem.viewPosition);

        renderTextOptimized(simpleDipEntityItem.title, alpha);
        renderDefaultSelection(simpleDipEntityItem);
    }
}
