package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.Masking;

public class RenderSlider extends MenuRender {

    TextureRegion blackCircle, accentPixel, untouchableValue, untouchablePixel;
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

        if (slider.getAlpha() == 0) return;

        if (slider.hasVisualHook() && slider.getAlpha() < 1) {
            renderSliderWithVisualHook();
            return;
        }

        renderInternals();
    }


    private void renderSliderWithVisualHook() {
        batch.end();
        Masking.begin();
        menuViewYio.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        menuViewYio.drawRoundRect(slider.parentElement.getViewPosition());
        menuViewYio.shapeRenderer.end();

        batch.begin();
        Masking.continueAfterBatchBegin();

        renderInternals();

        Masking.end(batch);
    }


    private void renderInternals() {
        checkToChangeBatchAlpha();

        renderBlackLine();
        renderAccent();
        renderSegments();
        renderValueCircle();
        renderValueText();
        renderTitle();

        // used only for debug
        renderTouchBorder();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderTitle() {
        BitmapFont titleFont = slider.titleFont;
        Color color = titleFont.getColor();
        titleFont.setColor(Color.BLACK);

        GraphicsYio.setFontAlpha(titleFont, slider.getAlpha());

        titleFont.draw(
                batch,
                slider.title,
                slider.titlePosition.x,
                slider.titlePosition.y
        );

        GraphicsYio.setFontAlpha(titleFont, 1);

        titleFont.setColor(color);
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


    private void renderTouchBorder() {
        if (!DebugFlags.showSliderBorder) return;

        GraphicsYio.setBatchAlpha(batch, 1);
        GraphicsYio.renderBorder(batch, getBlackPixel(), slider.getTouchRectangle());
    }


    private void renderValueText() {
        Color color = slider.valueFont.getColor();
        slider.valueFont.setColor(Color.BLACK);

        if (slider.getAlpha() < 1) {
            GraphicsYio.setFontAlpha(slider.valueFont, slider.getAlpha() * slider.getAlpha());
        }

        slider.valueFont.draw(
                batch,
                slider.getValueString(),
                slider.valueStringPosition.x,
                slider.valueStringPosition.y
        );

        if (slider.getAlpha() < 1) {
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
        if (slider.getAlpha() == 1) return;

        batch.setColor(c.r, c.g, c.b, slider.getAlpha());
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
                getBlackPixel(),
                slider.getViewX(),
                slider.currentVerticalPos - sliderLineHeightHalved,
                slider.getViewWidth(),
                sliderLineHeight
        );
    }
}
