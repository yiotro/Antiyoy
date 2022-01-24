package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderEndTurnButtonElement extends MenuRender{


    private TextureRegion iconTexture;
    private EndTurnButtonElement endTurnButtonElement;
    private float alpha;
    private TextureRegion blackCircleTexture;
    private TextureRegion darkGrayTexture;


    @Override
    public void loadTextures() {
        iconTexture = GraphicsYio.loadTextureRegion("end_turn.png", true);
        blackCircleTexture = GraphicsYio.loadTextureRegion("game/black_circle_big.png", false);
        darkGrayTexture = GraphicsYio.loadTextureRegion("pixels/very_dark.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        endTurnButtonElement = (EndTurnButtonElement) element;
        alpha = endTurnButtonElement.getAlpha();

        renderIcon();
        renderSelection();
        renderLtStuff();
        renderRmmSign();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderRmmSign() {
        if (!endTurnButtonElement.readyToMoveMode) return;
        GraphicsYio.setBatchAlpha(batch, endTurnButtonElement.getRmmAlpha());
        GraphicsYio.drawByRectangle(batch, darkGrayTexture, endTurnButtonElement.rmmBounds);
        BitmapFont font = endTurnButtonElement.rmmSign.font;
        font.setColor(0.85f, 0.85f, 0.85f, endTurnButtonElement.getRmmAlpha());
        GraphicsYio.renderText(batch, endTurnButtonElement.rmmSign);
        font.setColor(Color.BLACK);
    }


    private void renderLtStuff() {
        if (endTurnButtonElement.ltFactor.get() == 0) return;
        GraphicsYio.setBatchAlpha(batch, 0.3f * endTurnButtonElement.ltFactor.get());
        GraphicsYio.drawByCircle(batch, blackCircleTexture, endTurnButtonElement.ltPosition);
    }


    private void renderSelection() {
        if (!endTurnButtonElement.selectionEngineYio.isSelected()) return;
        if (!endTurnButtonElement.isInSimpleMode()) return;
        GraphicsYio.setBatchAlpha(batch, endTurnButtonElement.selectionEngineYio.getAlpha() * alpha);
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), endTurnButtonElement.viewPosition);
    }


    private void renderIcon() {
        GraphicsYio.setBatchAlpha(batch, alpha);
        GraphicsYio.drawByRectangle(batch, iconTexture, endTurnButtonElement.viewPosition);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
