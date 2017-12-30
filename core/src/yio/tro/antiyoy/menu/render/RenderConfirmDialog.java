package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.diplomatic_dialogs.AbstractDiplomaticDialog;
import yio.tro.antiyoy.menu.diplomatic_dialogs.AcButton;
import yio.tro.antiyoy.menu.diplomatic_dialogs.AcLabel;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderConfirmDialog extends MenuRender{

    private TextureRegion backgroundTexture;
    private TextureRegion selectionPixel;
    AbstractDiplomaticDialog dialog;
    private TextureRegion yesBckTexture;
    private TextureRegion noBckTexture;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("diplomacy/background.png", false);
        selectionPixel = GraphicsYio.loadTextureRegion("pixels/black_pixel.png", false);

        yesBckTexture = GraphicsYio.loadTextureRegion("button_background_3.png", false);
        noBckTexture = GraphicsYio.loadTextureRegion("button_background_1.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        dialog = (AbstractDiplomaticDialog) element;

        renderShadow();

        renderBlackout();
        renderBackground();
        renderLabels();
        renderButtons();
    }


    private void renderButtons() {
        if (!dialog.areButtonsEnabled()) return;

        for (AcButton button : dialog.buttons) {
            GraphicsYio.drawByRectangle(
                    batch,
                    getButtonBackground(button),
                    button.position
            );

            Color color = button.font.getColor();
            button.font.setColor(Color.BLACK);

            button.font.draw(
                    batch,
                    button.text,
                    button.textPosition.x,
                    button.textPosition.y
            );

            button.font.setColor(color);

            if (button.isSelected()) {
                GraphicsYio.setBatchAlpha(batch, 0.6 * button.selectionFactor.get());

                GraphicsYio.drawByRectangle(
                        batch,
                        selectionPixel,
                        button.position
                );

                GraphicsYio.setBatchAlpha(batch, 1);
            }
        }
    }


    private TextureRegion getButtonBackground(AcButton button) {
        switch (button.action) {
            default: return null;
            case AcButton.ACTION_YES: return yesBckTexture;
            case AcButton.ACTION_NO: return noBckTexture;
        }
    }


    private void renderLabels() {
        for (AcLabel label : dialog.labels) {
            Color color = label.font.getColor();
            label.font.setColor(Color.BLACK);

            label.font.draw(
                    batch,
                    label.text,
                    label.position.x,
                    label.position.y
            );

            label.font.setColor(color);
        }
    }


    private void renderBlackout() {
        GraphicsYio.setBatchAlpha(batch, 0.5 * dialog.appearFactor.get());

        batch.draw(
                selectionPixel,
                0, 0,
                GraphicsYio.width, GraphicsYio.height
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                dialog.viewPosition
        );
    }


    private void renderShadow() {
        menuViewYio.renderShadow(dialog.viewPosition, 1, batch);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
