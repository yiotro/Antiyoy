package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.GraphicsYio;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuRender;

public class RenderCheckButton extends MenuRender {

    TextureRegion backgroundTexture, activeTexture, blackPixel;


    @Override
    public void loadTextures() {
        // background texture IS NOT USED currently!!!
        backgroundTexture = GraphicsYio.loadTextureRegionByName("menu/check_button/chk_bck.png", false);

        activeTexture = GraphicsYio.loadTextureRegionByName("menu/check_button/chk_active.png", true);
        blackPixel = GraphicsYio.loadTextureRegionByName("pixels/black_pixel.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
//        CheckButtonYio checkButtonYio = (CheckButtonYio) element;

//        renderCheckButton(checkButtonYio);
    }


    public void renderCheckButton(CheckButtonYio checkButtonYio) {
        if (checkButtonYio.getViewFactor().get() > 0) {
            GraphicsYio.setBatchAlpha(batch, 0.2 * checkButtonYio.getViewFactor().get());
            GraphicsYio.drawByRectangle(batch, blackPixel, checkButtonYio.getTouchPosition());
            GraphicsYio.setBatchAlpha(batch, 1);
        }
        if (checkButtonYio.getFactor().get() < 1) {
            GraphicsYio.setBatchAlpha(batch, checkButtonYio.getFactor().get());
        }
//        GraphicsYio.drawByRectangle(batch, backgroundTexture, checkButtonYio.viewPosition);
        GraphicsYio.renderBorder(checkButtonYio.animPos, batch, blackPixel);
        if (checkButtonYio.selectionFactor.get() > 0) {
            GraphicsYio.setBatchAlpha(batch, Math.min(checkButtonYio.selectionFactor.get(), checkButtonYio.getFactor().get()));
            GraphicsYio.drawByRectangle(batch, activeTexture, checkButtonYio.animPos);
        }
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
