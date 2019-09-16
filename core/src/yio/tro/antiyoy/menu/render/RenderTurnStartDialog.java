package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.TurnStartDialog;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.Masking;

public class RenderTurnStartDialog extends MenuRender{

    TurnStartDialog dialog;
    private TextureRegion bckColors[];


    @Override
    public void loadTextures() {
        bckColors = new TextureRegion[GameRules.MAX_FRACTIONS_QUANTITY];
        for (int i = 0; i < bckColors.length; i++) {
            bckColors[i] = GraphicsYio.loadTextureRegion("diplomacy/color" + (i + 1) + ".png", false);
        }
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        dialog = (TurnStartDialog) element;

        GraphicsYio.setBatchAlpha(batch, dialog.alphaFactor.get());

        renderMain();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderMain() {
        if (dialog.isCircleModeEnabled()) {
            renderCircle();
            return;
        }

        if (dialog.isInDestroyState()) {
            renderDestroyState();
            return;
        }

        renderInternals();
    }


    private void renderDestroyState() {
        batch.end();

        Masking.begin();

        drawShapeRendererStuff();

        batch.begin();

        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        renderInternals();

        Masking.end(batch);
    }


    private void renderInternals() {
        GraphicsYio.drawByRectangle(
                batch,
                bckColors[dialog.color],
                dialog.position
        );

        renderTitle();
        renderDescription();
    }


    private void renderDescription() {
        BitmapFont descFont = dialog.descFont;

        Color color = descFont.getColor();
        descFont.setColor(Color.BLACK);
        GraphicsYio.setFontAlpha(descFont, dialog.alphaFactor.get());

        descFont.draw(
                batch,
                dialog.descString,
                dialog.descPosition.x,
                dialog.descPosition.y + dialog.getVerticalTextViewDelta()
        );

        GraphicsYio.setFontAlpha(descFont, 1);
        descFont.setColor(color);
    }


    private void renderTitle() {
        BitmapFont titleFont = dialog.titleFont;

        Color color = titleFont.getColor();
        titleFont.setColor(Color.BLACK);
        GraphicsYio.setFontAlpha(titleFont, dialog.alphaFactor.get());

        titleFont.draw(
                batch,
                dialog.titleString,
                dialog.titlePosition.x,
                dialog.titlePosition.y + dialog.getVerticalTextViewDelta()
        );

        GraphicsYio.setFontAlpha(titleFont, 1);
        titleFont.setColor(color);
    }


    private void renderCircle() {
        batch.end();

        Masking.begin();

        drawShapeRendererStuff();

        batch.begin();
        Masking.continueAfterBatchBegin();

        renderInternals();

        Masking.end(batch);
    }


    private void drawShapeRendererStuff() {
        ShapeRenderer shapeRenderer = menuViewYio.shapeRenderer;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.circle(
                dialog.circleCenter.x,
                dialog.circleCenter.y,
                dialog.circleRadius
        );

        shapeRenderer.end();
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
