package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.TextViewElement;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderTextViewElement extends MenuRender{


    private TextViewElement textViewElement;
    private BitmapFont font;
    private Color color;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        textViewElement = (TextViewElement) element;
        font = textViewElement.font;

//        GraphicsYio.renderBorder(batch, getGameView().blackPixel, textViewElement.viewPosition);

        color = font.getColor();
        font.setColor(Color.WHITE);
        GraphicsYio.setFontAlpha(font, textViewElement.appearFactor.get());

        font.draw(
                batch,
                textViewElement.textValue,
                textViewElement.textPosition.x,
                textViewElement.textPosition.y
        );

        font.setColor(color);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
