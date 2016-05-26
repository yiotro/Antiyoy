package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

/**
 * Created by ivan on 22.07.14.
 */
class MenuViewLighty {
    private final YioGdxGame yioGdxGame;
    private final MenuControllerLighty menuControllerLighty;
    TextureRegion buttonPixel, shadowCorner, shadowSide, blackCircle, scrollerCircle, grayTransCircle;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private int cornerSize;
    private float w, h;
    private float x1, y1, x2, y2; // local variables for rendering
    private Color c; // local variable for rendering


    public MenuViewLighty(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        menuControllerLighty = yioGdxGame.menuControllerLighty;
        shapeRenderer = new ShapeRenderer();
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        cornerSize = (int) (0.02 * Gdx.graphics.getHeight());
        buttonPixel = GameView.loadTextureRegionByName("button_pixel.png", false);
        shadowCorner = GameView.loadTextureRegionByName("corner_shadow.png", true);
        shadowSide = GameView.loadTextureRegionByName("side_shadow.png", true);
        blackCircle = GameView.loadTextureRegionByName("anim_circle_high_res.png", false);
        scrollerCircle = GameView.loadTextureRegionByName("scroller_circle.png", false);
        grayTransCircle = GameView.loadTextureRegionByName("gray_transition_circle.png", false);
    }


    private void drawRoundRect(SimpleRectangle pos) {
        shapeRenderer.rect((float) pos.x + cornerSize, (float) pos.y, (float) pos.width - 2 * cornerSize, (float) pos.height);
        shapeRenderer.rect((float) pos.x, (float) pos.y + cornerSize, (float) pos.width, (float) pos.height - 2 * cornerSize);
        shapeRenderer.circle((float) pos.x + cornerSize, (float) pos.y + cornerSize, cornerSize, 16);
        shapeRenderer.circle((float) pos.x + (float) pos.width - cornerSize, (float) pos.y + cornerSize, cornerSize, 16);
        shapeRenderer.circle((float) pos.x + cornerSize, (float) pos.y + (float) pos.height - cornerSize, cornerSize, 16);
        shapeRenderer.circle((float) pos.x + (float) pos.width - cornerSize, (float) pos.y + (float) pos.height - cornerSize, cornerSize, 16);
    }


    private void drawRect(SimpleRectangle pos) {
        shapeRenderer.rect((float) pos.x, (float) pos.y, (float) pos.width, (float) pos.height);
    }


    private void renderShadow(ButtonLighty buttonLighty, SpriteBatch batch) {
        x1 = buttonLighty.x1;
        x2 = buttonLighty.x2;
        y1 = buttonLighty.y1;
        y2 = buttonLighty.y2;
        if (buttonLighty.factorModel.get() <= 1)
            batch.setColor(c.r, c.g, c.b, buttonLighty.factorModel.get());
        else batch.setColor(c.r, c.g, c.b, 1);
        batch.draw(shadowSide, x1 + cornerSize, y2 - cornerSize, 2 * (buttonLighty.hor - cornerSize), 2 * cornerSize);
        batch.draw(shadowSide, x1 + cornerSize, y1 + cornerSize, 0, 0, 2 * (buttonLighty.ver - cornerSize), 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowSide, x2 - cornerSize, y1 + cornerSize, 0, 0, 2 * (buttonLighty.hor - cornerSize), 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowSide, x2 - cornerSize, y2 - cornerSize, 0, 0, 2 * (buttonLighty.ver - cornerSize), 2 * cornerSize, 1, 1, 270);
        batch.draw(shadowCorner, x1 - cornerSize, y2 - cornerSize, 2 * cornerSize, 2 * cornerSize);
        batch.draw(shadowCorner, x1 + cornerSize, y1 - cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowCorner, x2 + cornerSize, y1 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowCorner, x2 - cornerSize, y2 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 270);
    }


    private void renderShadow(SimpleRectangle rectangle, float factor, SpriteBatch batch) {
        float hor = 0.5f * factor * (float) rectangle.width;
        float ver = 0.5f * factor * (float) rectangle.height;
        float cx = (float) rectangle.x + 0.5f * (float) rectangle.width;
        float cy = (float) rectangle.y + 0.5f * (float) rectangle.height;
        x1 = cx - hor;
        x2 = cx + hor;
        y1 = cy - ver;
        y2 = cy + ver;
        if (factor <= 1)
            batch.setColor(c.r, c.g, c.b, factor);
        else batch.setColor(c.r, c.g, c.b, 1);
        batch.draw(shadowSide, x1 + cornerSize, y2 - cornerSize, 2 * (hor - cornerSize), 2 * cornerSize);
        batch.draw(shadowSide, x1 + cornerSize, y1 + cornerSize, 0, 0, 2 * (ver - cornerSize), 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowSide, x2 - cornerSize, y1 + cornerSize, 0, 0, 2 * (hor - cornerSize), 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowSide, x2 - cornerSize, y2 - cornerSize, 0, 0, 2 * (ver - cornerSize), 2 * cornerSize, 1, 1, 270);
        batch.draw(shadowCorner, x1 - cornerSize, y2 - cornerSize, 2 * cornerSize, 2 * cornerSize);
        batch.draw(shadowCorner, x1 + cornerSize, y1 - cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowCorner, x2 + cornerSize, y1 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowCorner, x2 - cornerSize, y2 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 270);
    }


    private boolean checkForSpecialMask(ButtonLighty buttonLighty) {
        switch (buttonLighty.id) {
            case 3:
                if (buttonLighty.factorModel.get() > 0.1)
                    shapeRenderer.circle(buttonLighty.cx, buttonLighty.cy, (float) (0.8 + 0.2 * buttonLighty.selectionFactor.get()) * buttonLighty.hor);
                return true;
//            case 84:
//            case 81:
//                shapeRenderer.rect(buttonLighty.x1, buttonLighty.y1 - 0.2f * buttonLighty.ver, 2 * buttonLighty.hor, 2.4f * buttonLighty.ver);
//                return true;
        }
        return false;
    }


    private boolean checkForSpecialAnimationMask(ButtonLighty buttonLighty) { // mask when circle fill animation on press
        SimpleRectangle pos = buttonLighty.animPos;
        switch (buttonLighty.id) {
            case 41: // main menu button
                shapeRenderer.rect((float) pos.x, (float) (pos.y + 0.5 * pos.height), (float) pos.width, 0.5f * (float) pos.height);
                return true;
            case 42: // resume button
                shapeRenderer.rect((float) pos.x, (float) pos.y, (float) pos.width, 0.5f * (float) pos.height);
                return true;
            case 43: // new game button
                shapeRenderer.rect((float) pos.x, (float) pos.y, (float) pos.width, (float) pos.height);
                return true;
            case 44: // restart button
                shapeRenderer.rect((float) pos.x, (float) pos.y, (float) pos.width, (float) pos.height);
                return true;
        }
        return false;
    }


    private boolean checkForSpecialAlpha(ButtonLighty buttonLighty) {
        switch (buttonLighty.id) {
//            case 11:
//                if (buttonLighty.factorModel.get() < 0.5) batch.setColor(c.r, c.g, c.b, 0);
//                else batch.setColor(c.r, c.g, c.b, 1);
//                return true;
            default:
                return false;
        }
    }


    public void render(boolean renderAliveButtons, boolean renderDyingButtons) {
        ArrayList<ButtonLighty> buttons = menuControllerLighty.buttons;
        batch = yioGdxGame.batch;
        c = batch.getColor();

        //shadows
        batch.begin();
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible() &&
//                    !buttonLighty.currentlyTouched &&
                    buttonLighty.hasShadow &&
                    !buttonLighty.mandatoryShadow &&
                    buttonLighty.factorModel.get() > 0.1 &&
                    ((renderAliveButtons && buttonLighty.factorModel.getGravity() >= 0) || (renderDyingButtons && buttonLighty.factorModel.getGravity() <= 0))) {
                renderShadow(buttonLighty, batch);
            }
        }
        batch.end();

        // Drawing masks
        if (yioGdxGame.useMenuMasks) {
            YioGdxGame.maskingBegin();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (ButtonLighty buttonLighty : buttons) {
                if (buttonLighty.isVisible()) {
                    if (checkForSpecialMask(buttonLighty)) continue;
                    if (buttonLighty.rectangularMask &&
                            !buttonLighty.currentlyTouched &&
                            ((renderAliveButtons && buttonLighty.factorModel.getGravity() >= 0) || (renderDyingButtons && buttonLighty.factorModel.getGravity() <= 0))) {
                        drawRect(buttonLighty.animPos);
                        continue;
                    }
                    drawRoundRect(buttonLighty.animPos);
                }
            }
            shapeRenderer.end();
        }


        // Drawing buttons
        batch.begin();
        if (yioGdxGame.useMenuMasks) YioGdxGame.maskingContinue();
        SimpleRectangle ap;
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible() &&
                    !buttonLighty.onlyShadow &&
                    ((renderAliveButtons && buttonLighty.factorModel.getGravity() >= 0) || (renderDyingButtons && buttonLighty.factorModel.getGravity() <= 0))) {
                if (buttonLighty.mandatoryShadow) renderShadow(buttonLighty, batch);
                if (!checkForSpecialAlpha(buttonLighty)) {
                    if (buttonLighty.factorModel.get() <= 1)
                        batch.setColor(c.r, c.g, c.b, buttonLighty.factorModel.get());
                    else batch.setColor(c.r, c.g, c.b, 1);
                }
                ap = buttonLighty.animPos;
                batch.draw(buttonLighty.textureRegion, (float) ap.x, (float) ap.y, (float) ap.width, (float) ap.height);
                if (buttonLighty.isCurrentlyTouched() && (!buttonLighty.touchAnimation || buttonLighty.selectionFactor.get() > 0.99)) {
                    batch.setColor(c.r, c.g, c.b, 0.7f * buttonLighty.selAlphaFactor.get());
                    batch.draw(buttonPixel, (float) ap.x, (float) ap.y, (float) ap.width, (float) ap.height);
                }
            }
        }
        batch.setColor(c.r, c.g, c.b, 1);
        batch.end();
        if (yioGdxGame.useMenuMasks) YioGdxGame.maskingEnd();

//        specialInfoPanelRenderPiece();

        // render selection
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible() &&
                    buttonLighty.isCurrentlyTouched() &&
                    buttonLighty.touchAnimation &&
                    buttonLighty.selectionFactor.get() < 1 &&
                    ((renderAliveButtons && buttonLighty.factorModel.getDy() >= 0) || (renderDyingButtons && buttonLighty.factorModel.getDy() < 0) || buttonLighty.selectionFactor.needsToMove())) {
                YioGdxGame.maskingBegin();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                checkForSpecialAnimationMask(buttonLighty);
                drawRoundRect(buttonLighty.animPos);
                shapeRenderer.end();

                batch.begin();
                YioGdxGame.maskingContinue();
                batch.setColor(c.r, c.g, c.b, 0.7f * buttonLighty.selAlphaFactor.get());
                float r = buttonLighty.selectionFactor.get() * buttonLighty.animR;
                batch.draw(blackCircle, buttonLighty.touchX - r, buttonLighty.touchY - r, 2 * r, 2 * r);
                batch.end();
                batch.setColor(c.r, c.g, c.b, 1);
                YioGdxGame.maskingEnd();
            }
        }

        for (SliderYio sliderYio : menuControllerLighty.sliders) {
            if (sliderYio.isVisible()) renderSlider(sliderYio);
        }
    }


    void specialInfoPanelRenderPiece() {
        ButtonLighty infoPanel = menuControllerLighty.getButtonById(11);
        if (infoPanel != null && menuControllerLighty.getButtonById(11).isVisible()) {
            YioGdxGame.maskingBegin();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            drawRoundRect(infoPanel.animPos);
            shapeRenderer.end();
            YioGdxGame.maskingContinue();
            renderTransitionCircle(grayTransCircle,
                    menuControllerLighty.infoPanelFactor.get() * menuControllerLighty.infoPanelFactor.get(),
                    infoPanel.animPos,
                    batch,
                    (infoPanel.factorModel.get() * 0.5f + 0.5f) * yioGdxGame.w / 2,
                    (float) (infoPanel.animPos.y + 0.95 * infoPanel.animPos.height - 0.65 * Math.sqrt(infoPanel.factorModel.get()) * infoPanel.animPos.height));
            YioGdxGame.maskingEnd();
        }
    }


    public void renderScroller() {
        if (menuControllerLighty.scrollerYio != null && menuControllerLighty.scrollerYio.isVisible())
            renderScroller(menuControllerLighty.scrollerYio);
    }


    private void renderScroller(ScrollerYio scrollerYio) {
        batch = yioGdxGame.batch;
        c = batch.getColor();
        batch.begin();
        if (scrollerYio.factorModel.get() > 0.9) renderShadow(scrollerYio.frame, scrollerYio.factorModel.get(), batch);
        batch.end();
        if (scrollerYio.factorModel.get() > 0.5) {
            Color c = batch.getColor();
            batch.setColor(c.r, c.g, c.b, (scrollerYio.factorModel.get() - 0.5f) * 2);
            YioGdxGame.maskingBegin();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            drawRoundRect(scrollerYio.animFrame);
            shapeRenderer.end();
            batch.begin();
            YioGdxGame.maskingContinue();
            batch.draw(scrollerYio.bg1, (float) scrollerYio.animFrame.x, (float) scrollerYio.animFrame.y, (float) scrollerYio.animFrame.width, (float) scrollerYio.animFrame.height);
            float y = (float) scrollerYio.animFrame.y + (float) scrollerYio.animFrame.height + scrollerYio.pos;
            int index = 0;
            for (TextureRegion textureRegion : scrollerYio.cache) {
                if (y <= scrollerYio.animFrame.y + scrollerYio.animFrame.height + scrollerYio.lineHeight && y >= scrollerYio.animFrame.y) {
                    batch.draw(textureRegion, (float) scrollerYio.frame.x, y - scrollerYio.lineHeight, (float) scrollerYio.frame.width, scrollerYio.lineHeight);
                    if (index == scrollerYio.selectionIndex && scrollerYio.selectionFactor.get() > 0.99) {
                        batch.setColor(c.r, c.g, c.b, 0.4f + 0.5f * scrollerYio.selAlphaFactor.get());
                        batch.draw(buttonPixel, (float) scrollerYio.frame.x, y - scrollerYio.lineHeight, (float) scrollerYio.frame.width, scrollerYio.lineHeight);
                        batch.setColor(c.r, c.g, c.b, 1);
                    }
                }
                y -= scrollerYio.lineHeight;
                index++;
            }
            batch.end();
            batch.setColor(c.r, c.g, c.b, 1);
            if (scrollerYio.selectionFactor.get() <= 0.99) {
                y = (float) scrollerYio.animFrame.y + (float) scrollerYio.animFrame.height + scrollerYio.pos - scrollerYio.selectionIndex * scrollerYio.lineHeight;
                if (y > scrollerYio.animFrame.y + scrollerYio.animFrame.height + scrollerYio.lineHeight && y < scrollerYio.animFrame.y)
                    return;
                YioGdxGame.maskingBegin();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                float ay = y - scrollerYio.lineHeight;
                float ah = scrollerYio.lineHeight;
                if (ay < scrollerYio.frame.y) {
                    float d = (float) scrollerYio.frame.y - ay;
                    ay += d;
                    ah -= d;
                } else if (ay + ah > scrollerYio.frame.y + scrollerYio.frame.height) {
                    float d = (float) (y + scrollerYio.lineHeight - scrollerYio.frame.y - scrollerYio.frame.height);
                    ah -= d - scrollerYio.lineHeight;
                }
                shapeRenderer.rect((float) scrollerYio.frame.x, ay, (float) scrollerYio.frame.width, ah);
                shapeRenderer.end();
                batch.begin();
                batch.setColor(c.r, c.g, c.b, 0.4f + 0.5f * scrollerYio.selAlphaFactor.get());
                YioGdxGame.maskingContinue();
                float cx = scrollerYio.selectX;
                float cy = (float) (y - 0.5 * scrollerYio.lineHeight);
                float dw = 1.1f * scrollerYio.selectionFactor.get() * scrollerYio.animRadius;
                batch.draw(blackCircle, cx - dw, cy - dw, 2 * dw, 2 * dw);
                batch.end();
                batch.setColor(c.r, c.g, c.b, 1);
            }
            YioGdxGame.maskingEnd();
            batch.setColor(c.r, c.g, c.b, 1);
        } else {

        }
        if (scrollerYio.factorModel.get() < 0.99) {
            YioGdxGame.maskingBegin();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            drawRoundRect(scrollerYio.animFrame);
            shapeRenderer.end();
            YioGdxGame.maskingContinue();
            renderTransitionCircle(scrollerCircle, scrollerYio.factorModel.get(), scrollerYio.animFrame, batch, yioGdxGame.pressX, yioGdxGame.pressY);
            YioGdxGame.maskingEnd();
        }
    }


    private void renderSlider(SliderYio slider) {
        batch.begin();
        batch.draw(yioGdxGame.gameView.blackPixel, slider.getViewX(), slider.currentVerticalPos - 0.0025f * h, slider.getViewWidth(), 0.005f * h);
        for (int i = 0; i < slider.numberOfSegments + 1; i++) {
            GameView.drawFromCenter(batch, yioGdxGame.gameView.blackCircleTexture, slider.getSegmentLeftSidePos(i), slider.currentVerticalPos, slider.getSegmentCenterSize(i));
        }
        GameView.drawFromCenter(batch, yioGdxGame.gameView.blackCircleTexture, slider.getViewX() + slider.runnerValue * slider.getViewWidth(), slider.currentVerticalPos, slider.circleSize);
        if (slider.textVisible())
            YioGdxGame.gameFont.draw(batch, slider.getValueString(), slider.getViewX() + slider.getViewWidth() - slider.textWidth, slider.currentVerticalPos + 0.05f * h);
        batch.end();
    }


    private static float maxDistanceToCorners(float x, float y, SimpleRectangle frame) {
        if (x > frame.x + 0.5f * frame.width) {
            if (y > frame.y + 0.5f * frame.height) {
                return (float) YioGdxGame.distance(x, y, frame.x, frame.y);
            } else {
                return (float) YioGdxGame.distance(x, y, frame.x, frame.y + frame.height);
            }
        } else {
            if (y > frame.y + 0.5f * frame.height) {
                return (float) YioGdxGame.distance(x, y, frame.x + frame.width, frame.y);
            } else {
                return (float) YioGdxGame.distance(x, y, frame.x + frame.width, frame.y + frame.height);
            }
        }
    }


    private static void renderTransitionCircle(TextureRegion circleTexture, float factor, SimpleRectangle frame, SpriteBatch batch, float x, float y) {
        Color c = batch.getColor();
        if (factor < 0.5) batch.setColor(c.r, c.g, c.b, 1);
        else batch.setColor(c.r, c.g, c.b, 1 - 2f * factor);
//        float r = 0.5f * (float)Math.sqrt(2f * factor) * (float) YioGdxGame.distance(0, 0, frame.width, frame.height);
        float r = (float) Math.sqrt(2f * factor) * maxDistanceToCorners(x, y, frame);
        batch.begin();
        batch.draw(circleTexture, x - r, y - r, 2 * r, 2 * r);
        batch.end();
        batch.setColor(c.r, c.g, c.b, 1);
    }
}
