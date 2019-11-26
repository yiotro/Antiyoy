package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.ProfitDetailItem;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderProfitDetailItem extends AbstractRenderCustomListItem{


    private ProfitDetailItem profitDetailItem;
    private float alpha;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderItem(AbstractCustomListItem item) {
        profitDetailItem = (ProfitDetailItem) item;
        alpha = profitDetailItem.customizableListYio.getFactor().get();

        renderHighlight();
        renderTitleAndValue();
    }


    private void renderHighlight() {
        if (!profitDetailItem.highlightEnabled) return;

        GraphicsYio.setBatchAlpha(batch, 0.04 * profitDetailItem.customizableListYio.getFactor().get());
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), profitDetailItem.viewPosition);
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderTitleAndValue() {
        BitmapFont font = profitDetailItem.title.font;
        Color fontColor = font.getColor();
        font.setColor(Color.BLACK);
        renderTextOptimized(profitDetailItem.title, alpha);
        renderTextOptimized(profitDetailItem.value, alpha);
        font.setColor(fontColor);
    }
}
