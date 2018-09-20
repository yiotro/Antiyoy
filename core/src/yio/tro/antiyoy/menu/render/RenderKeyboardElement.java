package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.keyboard.KbButton;
import yio.tro.antiyoy.menu.keyboard.KbTextArea;
import yio.tro.antiyoy.menu.keyboard.KeyboardElement;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderKeyboardElement extends MenuRender{


    private KeyboardElement keyboardElement;
    private float f;
    private TextureRegion background;
    private BitmapFont font;
    private TextureRegion spaceIcon;
    private TextureRegion backspaceIcon;
    private KbTextArea textArea;
    private Color color;


    @Override
    public void loadTextures() {
        background = GraphicsYio.loadTextureRegion("pixels/kb_background.png", false);
        spaceIcon = GraphicsYio.loadTextureRegion("menu/keyboard/space_icon.png", true);
        backspaceIcon = GraphicsYio.loadTextureRegion("menu/keyboard/backspace_icon.png", true);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        keyboardElement = (KeyboardElement) element;
        f = keyboardElement.getFactor().get();
        font = keyboardElement.font;
        color = font.getColor();
        font.setColor(Color.BLACK);

        renderBlackout();
        resetBatchAlpha();
        GraphicsYio.setFontAlpha(font, f * f);

        renderBackground();
        renderTextArea();
        renderButtons();

        GraphicsYio.setBatchAlpha(batch, 1);
        font.setColor(color);
    }


    private void renderBlackout() {
        GraphicsYio.setBatchAlpha(batch, 0.15 * f);
        GraphicsYio.drawByRectangle(batch, getGameView().blackPixel, keyboardElement.blackoutPosition);
    }


    private void renderTextArea() {
        textArea = keyboardElement.textArea;

        GraphicsYio.setBatchAlpha(batch, f * f);
        GraphicsYio.drawByRectangle(batch, background, textArea.viewPosition);

        keyboardElement.font.draw(
                batch,
                textArea.value,
                textArea.textPosition.x,
                textArea.textPosition.y
        );

        GraphicsYio.setBatchAlpha(batch, Math.min(f * f, textArea.caretFactor.get()));
        GraphicsYio.drawLine(
                batch, getGameView().blackPixel, textArea.caretEndPos, textArea.caretPosition,
                GraphicsYio.borderThickness
        );

        resetBatchAlpha();
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(batch, background, keyboardElement.viewPosition);
    }


    private void resetBatchAlpha() {
        GraphicsYio.setBatchAlpha(batch, f);
    }


    private void renderButtons() {
        for (KbButton kbButton : keyboardElement.kbButtons) {
            renderButtonValue(kbButton);
            renderButtonSelection(kbButton);
        }
    }


    private void renderButtonSelection(KbButton kbButton) {
        if (!kbButton.isSelected()) return;

        GraphicsYio.setBatchAlpha(batch, 0.25 * kbButton.selectionFactor.get());
        GraphicsYio.drawByRectangle(batch, getGameView().blackPixel, kbButton.position);
        resetBatchAlpha();
    }


    private void renderButtonValue(KbButton kbButton) {
        if (kbButton.isIconButton()) {
            renderIconButton(kbButton);
            return;
        }

        font.draw(
                batch,
                kbButton.value,
                kbButton.textPosition.x,
                kbButton.textPosition.y
        );
    }


    private void renderIconButton(KbButton kbButton) {
        GraphicsYio.drawFromCenter(
                batch,
                getIconTexture(kbButton),
                kbButton.position.x + kbButton.position.width / 2,
                kbButton.position.y + kbButton.position.height / 2,
                kbButton.position.width / 2
        );
    }


    private TextureRegion getIconTexture(KbButton kbButton) {
        if (kbButton.key.equals("space")) {
            return spaceIcon;
        }

        if (kbButton.key.equals("backspace")) {
            return backspaceIcon;
        }

        return null;
    }


    private void renderButtonBorder(KbButton kbButton) {
        GraphicsYio.renderBorder(
                batch, getGameView().blackPixel, kbButton.position
        );
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
