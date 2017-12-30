package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderSlider extends MenuRender {

    TextureRegion blackCircle, accentPixel, untouchableValue, untouchablePixel;
    TextureRegion buildCircle, carryCircle, cultivateCircle, defenseCircle;
    float sliderLineHeight, sliderLineHeightHalved;
    private SliderYio slider;


    public RenderSlider() {
        sliderLineHeight = 0.007f * Gdx.graphics.getWidth();
        sliderLineHeightHalved = sliderLineHeight / 2;
    }


    @Override
    public void loadTextures() {
        blackCircle = GraphicsYio.loadTextureRegion("menu/slider/black_circle.png", true);
        accentPixel = GraphicsYio.loadTextureRegion("pixels/slider_accent.png", false);
        untouchableValue = GraphicsYio.loadTextureRegion("menu/slider/untouchable_slider_value.png", true);
        untouchablePixel = GraphicsYio.loadTextureRegion("pixels/blue_pixel.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        slider = (SliderYio) element;

        checkToChangeBatchAlpha();

        renderBlackLine();
        renderAccent();
        renderSegments();
        renderValueCircle();
        renderValueText();
        renderTitle();

        // used only for debug
//        renderBorder();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderTitle() {
        Color color = slider.titleFont.getColor();
        slider.titleFont.setColor(Color.BLACK);

        GraphicsYio.setFontAlpha(slider.titleFont, slider.getFactor().get());

        slider.titleFont.draw(
                batch,
                slider.title,
                slider.titlePosition.x,
                slider.titlePosition.y
        );

        GraphicsYio.setFontAlpha(slider.titleFont, 1);

        slider.titleFont.setColor(color);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }


    private void renderAccent() {
        if (!slider.isAccentVisible()) return;
        batch.draw(getAccentPixel(slider), slider.getViewX(), slider.currentVerticalPos - sliderLineHeightHalved, slider.runnerValue * slider.getViewWidth(), sliderLineHeight);
    }


    private TextureRegion getAccentPixel(SliderYio sliderYio) {
        if (sliderYio.isTouchable()) {
            return accentPixel;
        } else {
            return untouchablePixel;
        }
    }


    private void renderBorder() {
        GraphicsYio.setBatchAlpha(batch, 1);
        GraphicsYio.renderBorder(slider.getTouchRectangle(), batch, getGameView().blackPixel);
    }


    private void renderValueText() {
        Color color = slider.valueFont.getColor();
        slider.valueFont.setColor(Color.BLACK);

        if (slider.getFactor().get() < 1) {
            GraphicsYio.setFontAlpha(slider.valueFont, slider.getFactor().get() * slider.getFactor().get());
        }

        slider.valueFont.draw(
                batch,
                slider.getValueString(),
                slider.valueStringPosition.x,
                slider.valueStringPosition.y
        );

        if (slider.getFactor().get() < 1) {
            GraphicsYio.setFontAlpha(slider.valueFont, 1);
        }

        slider.valueFont.setColor(color);
    }


    private void renderValueCircle() {
        GraphicsYio.drawFromCenter(batch, getValueCircle(slider), slider.getRunnerValueViewX(), slider.currentVerticalPos, slider.circleSize);
    }


    private TextureRegion getValueCircle(SliderYio sliderYio) {
        if (sliderYio.isTouchable()) {
            return blackCircle;
        } else {
            return untouchableValue;
        }
    }


    private void checkToChangeBatchAlpha() {
        if (slider.appearFactor.get() == 1) return;

        batch.setColor(c.r, c.g, c.b, slider.appearFactor.get());
    }


    private void renderSegments() {
        if (slider.isInternalSegmentsHidden()) {
            GraphicsYio.drawFromCenter(batch, blackCircle, slider.getViewX(), slider.currentVerticalPos, slider.getSegmentCircleSize());
            GraphicsYio.drawFromCenter(batch, blackCircle, slider.getViewX() + slider.getViewWidth(), slider.currentVerticalPos, slider.getSegmentCircleSize());
        } else {
            for (int i = 0; i < slider.numberOfSegments + 1; i++) {
                GraphicsYio.drawFromCenter(batch, blackCircle, slider.getSegmentLeftSidePos(i), slider.currentVerticalPos, slider.getSegmentCircleSize());
            }
        }
    }


    private void renderBlackLine() {
        batch.draw(
                getGameView().blackPixel,
                slider.getViewX(),
                slider.currentVerticalPos - sliderLineHeightHalved,
                slider.getViewWidth(),
                sliderLineHeight
        );
    }
}
