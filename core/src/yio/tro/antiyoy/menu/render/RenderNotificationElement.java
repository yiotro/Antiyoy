package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.NotificationElement;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderNotificationElement extends MenuRender{


    private TextureRegion backgroundTexture;
    private NotificationElement notificationElement;
    private BitmapFont font;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("button_background_3.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {

    }


    private void renderShadow() {
        MenuRender.renderShadow.renderShadow(notificationElement.viewPosition, 1);
    }


    private void renderMessage() {
        Color color =  font.getColor();
        font.setColor(Color.BLACK);
        GraphicsYio.setFontAlpha(font, notificationElement.getFactor().get());

        font.draw(
                batch,
                notificationElement.message,
                notificationElement.textPosition.x,
                notificationElement.textPosition.y
        );

        GraphicsYio.setFontAlpha(font, 1);
        font.setColor(color);
    }


    private void renderBackground() {
//        GraphicsYio.setBatchAlpha(batch, notificationElement.getFactor().get());

        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                notificationElement.viewPosition
        );

//        GraphicsYio.setBatchAlpha(batch, 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {
        notificationElement = (NotificationElement) element;
        font = notificationElement.font;

        renderShadow();
        renderBackground();
        renderMessage();
    }
}
