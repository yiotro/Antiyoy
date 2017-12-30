package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.gameplay.game_view.GameView;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;

public class MenuViewYio {

    public final YioGdxGame yioGdxGame;
    private final MenuControllerYio menuControllerYio;
    TextureRegion buttonPixel, blackCircle, scrollerCircle, grayTransCircle;
    public TextureRegion shadowCorner, shadowSide;
    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;
    private int cornerSize;
    public float w, h;
    private float x1, y1, x2, y2; // local variables for rendering
    private Color c; // local variable for rendering
    public OrthographicCamera orthoCam;


    public MenuViewYio(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        menuControllerYio = yioGdxGame.menuControllerYio;
        shapeRenderer = yioGdxGame.shapeRenderer;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        cornerSize = (int) (0.02 * Gdx.graphics.getHeight());
        buttonPixel = GameView.loadTextureRegion("button_pixel.png", false);
        shadowCorner = GameView.loadTextureRegion("corner_shadow.png", true);
        shadowSide = GameView.loadTextureRegion("side_shadow.png", true);
        blackCircle = GameView.loadTextureRegion("anim_circle_high_res.png", false);
        scrollerCircle = GameView.loadTextureRegion("scroller_circle.png", false);
        grayTransCircle = GameView.loadTextureRegion("gray_transition_circle.png", false);
        createOrthoCam();

        batch = yioGdxGame.batch;
        if (batch == null) {
            System.out.println("fuck....");
        }
        MenuRender.updateRenderSystems(this);
    }


    private void createOrthoCam() {
        orthoCam = new OrthographicCamera(yioGdxGame.w, yioGdxGame.h);
        orthoCam.position.set(orthoCam.viewportWidth / 2f, orthoCam.viewportHeight / 2f, 0);
        orthoCam.update();
    }


    public void drawRoundRect(RectangleYio pos) {
        drawRoundRect(pos, cornerSize);
    }


    public void drawRoundRect(RectangleYio pos, int cornerSize) {
        shapeRenderer.rect((float) pos.x + cornerSize, (float) pos.y, (float) pos.width - 2 * cornerSize, (float) pos.height);
        shapeRenderer.rect((float) pos.x, (float) pos.y + cornerSize, (float) pos.width, (float) pos.height - 2 * cornerSize);
        shapeRenderer.circle((float) pos.x + cornerSize, (float) pos.y + cornerSize, cornerSize, 16);
        shapeRenderer.circle((float) pos.x + (float) pos.width - cornerSize, (float) pos.y + cornerSize, cornerSize, 16);
        shapeRenderer.circle((float) pos.x + cornerSize, (float) pos.y + (float) pos.height - cornerSize, cornerSize, 16);
        shapeRenderer.circle((float) pos.x + (float) pos.width - cornerSize, (float) pos.y + (float) pos.height - cornerSize, cornerSize, 16);
    }


    private void drawRect(RectangleYio pos) {
        shapeRenderer.rect((float) pos.x, (float) pos.y, (float) pos.width, (float) pos.height);
    }


    public void drawCircle(float x, float y, float r) {
        shapeRenderer.circle(x, y, r);
    }


    public void renderShadow(ButtonYio buttonYio, SpriteBatch batch) {
        x1 = buttonYio.x1;
        x2 = buttonYio.x2;
        y1 = buttonYio.y1;
        y2 = buttonYio.y2;

        if (buttonYio.appearFactor.get() <= 1) {
            batch.setColor(c.r, c.g, c.b, buttonYio.appearFactor.get());
        } else {
            batch.setColor(c.r, c.g, c.b, 1);
        }

        batch.draw(shadowSide, x1 + cornerSize, y2 - cornerSize, 2 * (buttonYio.hor - cornerSize), 2 * cornerSize);
        batch.draw(shadowSide, x1 + cornerSize, y1 + cornerSize, 0, 0, 2 * (buttonYio.ver - cornerSize), 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowSide, x2 - cornerSize, y1 + cornerSize, 0, 0, 2 * (buttonYio.hor - cornerSize), 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowSide, x2 - cornerSize, y2 - cornerSize, 0, 0, 2 * (buttonYio.ver - cornerSize), 2 * cornerSize, 1, 1, 270);
        batch.draw(shadowCorner, x1 - cornerSize, y2 - cornerSize, 2 * cornerSize, 2 * cornerSize);
        batch.draw(shadowCorner, x1 + cornerSize, y1 - cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowCorner, x2 + cornerSize, y1 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowCorner, x2 - cornerSize, y2 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 270);
    }


    public void renderShadow(RectangleYio rectangle, float factor, SpriteBatch batch) {
        float hor = 0.5f * factor * (float) rectangle.width;
        float ver = 0.5f * factor * (float) rectangle.height;
        float cx = (float) rectangle.x + 0.5f * (float) rectangle.width;
        float cy = (float) rectangle.y + 0.5f * (float) rectangle.height;
        x1 = cx - hor;
        x2 = cx + hor;
        y1 = cy - ver;
        y2 = cy + ver;

        GraphicsYio.setBatchAlpha(batch, factor);

        batch.draw(shadowSide, x1 + cornerSize, y2 - cornerSize, 2 * (hor - cornerSize), 2 * cornerSize);
        batch.draw(shadowSide, x1 + cornerSize, y1 + cornerSize, 0, 0, 2 * (ver - cornerSize), 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowSide, x2 - cornerSize, y1 + cornerSize, 0, 0, 2 * (hor - cornerSize), 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowSide, x2 - cornerSize, y2 - cornerSize, 0, 0, 2 * (ver - cornerSize), 2 * cornerSize, 1, 1, 270);
        batch.draw(shadowCorner, x1 - cornerSize, y2 - cornerSize, 2 * cornerSize, 2 * cornerSize);
        batch.draw(shadowCorner, x1 + cornerSize, y1 - cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowCorner, x2 + cornerSize, y1 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowCorner, x2 - cornerSize, y2 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 270);

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private boolean checkForSpecialMask(ButtonYio buttonYio) {
        switch (buttonYio.id) {
            case 3:
                if (buttonYio.appearFactor.get() > 0.1)
                    shapeRenderer.circle(buttonYio.cx, buttonYio.cy, (float) (0.8 + 0.2 * buttonYio.selectionFactor.get()) * buttonYio.hor);
                return true;
        }
        return false;
    }


    private boolean checkForSpecialAnimationMask(ButtonYio buttonYio) { // mask when circle fill animation on press
        RectangleYio pos = buttonYio.animPos;
        switch (buttonYio.id) {
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


    private boolean checkForSpecialAlpha(ButtonYio buttonYio) {
        switch (buttonYio.id) {
//            case 11:
//                if (buttonLighty.factorModel.get() < 0.5) batch.setColor(c.r, c.g, c.b, 0);
//                else batch.setColor(c.r, c.g, c.b, 1);
//                return true;
            default:
                return false;
        }
    }


    public void render(boolean renderAliveButtons, boolean renderDyingButtons) {
        ArrayList<ButtonYio> buttons = menuControllerYio.buttons;
        c = batch.getColor();

        //shadows
        renderShadow(renderAliveButtons, renderDyingButtons, buttons);

        // Drawing masks
        drawMasks(renderAliveButtons, renderDyingButtons, buttons);


        // Drawing buttons
        renderButtons(renderAliveButtons, renderDyingButtons, buttons);

//        specialInfoPanelRenderPiece();

        // render selection
        renderSelection(renderAliveButtons, renderDyingButtons, buttons);

        renderCheckButtons();
        renderInterfaceElements();
    }


    private void renderInterfaceElements() {
        ArrayList<InterfaceElement> interfaceElements = menuControllerYio.interfaceElements;

        batch.begin();

        // first layer
        for (InterfaceElement element : interfaceElements) {
            if (!element.isVisible()) continue;
            element.getRenderSystem().renderFirstLayer(element);
        }

        // second layer
        for (InterfaceElement element : interfaceElements) {
            if (!element.isVisible()) continue;
            element.getRenderSystem().renderSecondLayer(element);
        }

        // third layer
        for (InterfaceElement element : interfaceElements) {
            if (!element.isVisible()) continue;
            element.getRenderSystem().renderThirdLayer(element);
        }

        batch.end();
    }


    private void renderCheckButtons() {
        batch.begin();
        for (CheckButtonYio checkButton : menuControllerYio.checkButtons) {
            if (checkButton.isVisible()) MenuRender.renderCheckButton.renderCheckButton(checkButton);
        }
        batch.end();
    }


    private void renderSelection(boolean renderAliveButtons, boolean renderDyingButtons, ArrayList<ButtonYio> buttons) {
        for (ButtonYio buttonYio : buttons) {
            if (buttonYio.isVisible() &&
                    buttonYio.isCurrentlyTouched() &&
                    buttonYio.touchAnimation &&
                    buttonYio.selectionFactor.get() < 1 &&
                    ((renderAliveButtons && buttonYio.appearFactor.getDy() >= 0) || (renderDyingButtons && buttonYio.appearFactor.getDy() < 0) || buttonYio.selectionFactor.hasToMove())) {
                YioGdxGame.maskingBegin();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                checkForSpecialAnimationMask(buttonYio);
                drawRoundRect(buttonYio.animPos);
                shapeRenderer.end();

                batch.begin();
                YioGdxGame.maskingContinue();
                batch.setColor(c.r, c.g, c.b, 0.7f * buttonYio.selAlphaFactor.get());
                float r = buttonYio.selectionFactor.get() * buttonYio.animR;
                batch.draw(blackCircle, buttonYio.touchX - r, buttonYio.touchY - r, 2 * r, 2 * r);
                batch.end();
                batch.setColor(c.r, c.g, c.b, 1);
                YioGdxGame.maskingEnd();
            }
        }
    }


    private void renderButtons(boolean renderAliveButtons, boolean renderDyingButtons, ArrayList<ButtonYio> buttons) {
        batch.begin();
        if (yioGdxGame.useMenuMasks) {
            YioGdxGame.maskingContinue();
        }

        for (ButtonYio buttonYio : buttons) {
            renderSingleButton(renderAliveButtons, renderDyingButtons, buttonYio);
        }

        batch.setColor(c.r, c.g, c.b, 1);
        batch.end();

        if (yioGdxGame.useMenuMasks) {
            YioGdxGame.maskingEnd();
        }
    }


    private void renderSingleButton(boolean renderAliveButtons, boolean renderDyingButtons, ButtonYio buttonYio) {
        RectangleYio ap;

        if (!buttonYio.isVisible() || buttonYio.onlyShadow ||
                ((!renderAliveButtons || buttonYio.appearFactor.getGravity() < 0) && (!renderDyingButtons || buttonYio.appearFactor.getGravity() > 0))) {
            return;
        }

        if (buttonYio.mandatoryShadow) {
            renderShadow(buttonYio, batch);
        }

        if (!checkForSpecialAlpha(buttonYio)) {
            if (buttonYio.appearFactor.get() <= 1) {
                batch.setColor(c.r, c.g, c.b, buttonYio.appearFactor.get());
            } else {
                batch.setColor(c.r, c.g, c.b, 1);
            }
        }

        ap = buttonYio.animPos;
        batch.draw(buttonYio.textureRegion, (float) ap.x, (float) ap.y, (float) ap.width, (float) ap.height);

        if (buttonYio.isCurrentlyTouched() && (!buttonYio.touchAnimation || buttonYio.selectionFactor.get() > 0.99)) {
            batch.setColor(c.r, c.g, c.b, 0.7f * buttonYio.selAlphaFactor.get());
            batch.draw(buttonPixel, (float) ap.x, (float) ap.y, (float) ap.width, (float) ap.height);
        }
    }


    private void drawMasks(boolean renderAliveButtons, boolean renderDyingButtons, ArrayList<ButtonYio> buttons) {
        if (!yioGdxGame.useMenuMasks) return;

        YioGdxGame.maskingBegin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (ButtonYio buttonYio : buttons) {
            if (buttonYio.isVisible()) {
                if (checkForSpecialMask(buttonYio)) continue;

                if (buttonYio.rectangularMask &&
//                        !buttonYio.currentlyTouched &&
                        ((renderAliveButtons && buttonYio.appearFactor.getGravity() >= 0) || (renderDyingButtons && buttonYio.appearFactor.getGravity() <= 0))) {
                    drawRect(buttonYio.animPos);
                    continue;
                }

                drawRoundRect(buttonYio.animPos);
            }
        }
        shapeRenderer.end();
    }


    private void renderShadow(boolean renderAliveButtons, boolean renderDyingButtons, ArrayList<ButtonYio> buttons) {
        batch.begin();
        for (ButtonYio buttonYio : buttons) {
            if (buttonYio.isVisible() &&
//                    !buttonLighty.currentlyTouched &&
                    buttonYio.hasShadow &&
                    !buttonYio.mandatoryShadow &&
                    buttonYio.appearFactor.get() > 0.1 &&
                    ((renderAliveButtons && buttonYio.appearFactor.getGravity() >= 0) || (renderDyingButtons && buttonYio.appearFactor.getGravity() <= 0))) {
                renderShadow(buttonYio, batch);
            }
        }
        batch.end();
    }


    void specialInfoPanelRenderPiece() {
        ButtonYio infoPanel = menuControllerYio.getButtonById(11);
        if (infoPanel != null && menuControllerYio.getButtonById(11).isVisible()) {
            YioGdxGame.maskingBegin();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            drawRoundRect(infoPanel.animPos);
            shapeRenderer.end();
            YioGdxGame.maskingContinue();
            renderTransitionCircle(grayTransCircle,
                    menuControllerYio.infoPanelFactor.get() * menuControllerYio.infoPanelFactor.get(),
                    infoPanel.animPos,
                    batch,
                    (infoPanel.appearFactor.get() * 0.5f + 0.5f) * yioGdxGame.w / 2,
                    (float) (infoPanel.animPos.y + 0.95 * infoPanel.animPos.height - 0.65 * Math.sqrt(infoPanel.appearFactor.get()) * infoPanel.animPos.height));
            YioGdxGame.maskingEnd();
        }
    }


    private static float maxDistanceToCorners(float x, float y, RectangleYio frame) {
        if (x > frame.x + 0.5f * frame.width) {
            if (y > frame.y + 0.5f * frame.height) {
                return (float) Yio.distance(x, y, frame.x, frame.y);
            } else {
                return (float) Yio.distance(x, y, frame.x, frame.y + frame.height);
            }
        } else {
            if (y > frame.y + 0.5f * frame.height) {
                return (float) Yio.distance(x, y, frame.x + frame.width, frame.y);
            } else {
                return (float) Yio.distance(x, y, frame.x + frame.width, frame.y + frame.height);
            }
        }
    }


    private static void renderTransitionCircle(TextureRegion circleTexture, float factor, RectangleYio frame, SpriteBatch batch, float x, float y) {
        Color c = batch.getColor();
        if (factor < 0.5) batch.setColor(c.r, c.g, c.b, 1);
        else batch.setColor(c.r, c.g, c.b, 1 - 2f * factor);
//        float r = 0.5f * (float)Math.sqrt(2f * factor) * (float) Yio.distance(0, 0, frame.width, frame.height);
        float r = (float) Math.sqrt(2f * factor) * maxDistanceToCorners(x, y, frame);
        batch.begin();
        batch.draw(circleTexture, x - r, y - r, 2 * r, 2 * r);
        batch.end();
        batch.setColor(c.r, c.g, c.b, 1);
    }


    public int getCornerSize() {
        return cornerSize;
    }
}
