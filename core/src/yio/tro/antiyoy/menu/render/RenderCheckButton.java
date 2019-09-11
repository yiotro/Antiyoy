package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.Masking;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class RenderCheckButton extends MenuRender{


    private TextureRegion blackPixel;
    private TextureRegion iconTexture;
    private CheckButtonYio checkButtonYio;
    private RectangleYio pos;
    private float alpha;
    private RenderableTextYio title;


    @Override
    public void loadTextures() {
        blackPixel = GraphicsYio.loadTextureRegion("pixels/black_pixel.png", false);
        iconTexture = GraphicsYio.loadTextureRegion("menu/check_button/chk_active.png", true);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        // due to masking check buttons will be rendered separately from other interface elements
    }


    public void renderElement(InterfaceElement element) {
        checkButtonYio = (CheckButtonYio) element;
        pos = checkButtonYio.viewPosition;
        alpha = checkButtonYio.getFactor().get();
        title = checkButtonYio.title;

        renderInternals();
    }


    private void renderInternals() {
        renderTitle();
        renderIcon();
        renderSelection();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderIcon() {
        GraphicsYio.setBatchAlpha(batch, alpha * alpha);
        GraphicsYio.renderBorder(batch, blackPixel, checkButtonYio.iconPosition);

        if (checkButtonYio.iconFactor.get() > 0) {
            GraphicsYio.setBatchAlpha(batch, alpha * checkButtonYio.iconFactor.get());
            GraphicsYio.drawByRectangle(batch, iconTexture, checkButtonYio.iconPosition);
        }
    }


    private void renderSelection() {
        if (!checkButtonYio.selectionEngineYio.isSelected()) return;
        GraphicsYio.setBatchAlpha(batch, checkButtonYio.selectionEngineYio.getAlpha());
        GraphicsYio.drawByRectangle(batch, blackPixel, pos);
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderTitle() {
        if (alpha < 0.15) return;
        Color previousColor = title.font.getColor();
        title.font.setColor(Color.BLACK);
        GraphicsYio.renderTextOptimized(batch, blackPixel, title, alpha);
        title.font.setColor(previousColor);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
