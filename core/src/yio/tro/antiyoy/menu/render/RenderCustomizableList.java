package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.CustomizableListYio;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.Masking;
import yio.tro.antiyoy.stuff.RectangleYio;

public class RenderCustomizableList extends MenuRender{

    private CustomizableListYio customizableListYio;
    private ShapeRenderer shapeRenderer;
    private float alpha;
    private TextureRegion backgroundTexture;
    private RectangleYio viewPosition;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("menu/background.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        customizableListYio = (CustomizableListYio) element;
        alpha = customizableListYio.getFactor().get();
        viewPosition = customizableListYio.viewPosition;

        if (alpha < 0.25) return;

        renderShadow();
        renderMain();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderShadow() {
        if (alpha <= 0.6) return;
        if (customizableListYio.isInEmbeddedMode()) return;

        MenuRender.renderShadow.disableInternalFillForOneDraw();
        MenuRender.renderShadow.renderShadow(viewPosition, 1);
    }


    private TextureRegion getBackgroundTexture() {
        if (customizableListYio.hasCustomBackground()) {
            return customizableListYio.customBackgroundTexture;
        }

        return backgroundTexture;
    }


    private void renderMain() {
        batch.end();
        Masking.begin();

        shapeRenderer = menuViewYio.shapeRenderer;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (customizableListYio.isInEmbeddedMode()) {
            shapeRenderer.rect(
                    (float) customizableListYio.maskPosition.x,
                    (float) customizableListYio.maskPosition.y,
                    (float) customizableListYio.maskPosition.width,
                    (float) customizableListYio.maskPosition.height
            );
        } else {
            menuViewYio.drawRoundRect(customizableListYio.maskPosition);
        }
        shapeRenderer.end();

        batch.begin();
        Masking.continueAfterBatchBegin();
        renderInternals();
        Masking.end(batch);
    }


    private void renderInternals() {
        renderBackground();
        Color previousColor = Fonts.smallerMenuFont.getColor();
        Fonts.smallerMenuFont.setColor(Color.BLACK);
        for (AbstractCustomListItem item : customizableListYio.items) {
            if (!item.isCurrentlyVisible()) continue;

            item.getRender().renderItem(item);
        }
        Fonts.smallerMenuFont.setColor(previousColor);
    }


    private void renderBackground() {
        if (customizableListYio.isInEmbeddedMode()) return;
        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                customizableListYio.viewPosition
        );
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}

