package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.LegacyMasking;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class RenderButtons {

    private final MenuViewYio menuViewYio;
    private boolean renderAliveButtons;
    private boolean renderDyingButtons;
    private ArrayList<ButtonYio> buttons;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    RectangleYio visualPosition;
    TextureRegion buttonPixel, blackCircle;
    private boolean useMenuMasks;
    PointYio tempPoint;
    private float vpDeltaOffset;


    public RenderButtons(MenuViewYio menuViewYio) {
        this.menuViewYio = menuViewYio;
        visualPosition = new RectangleYio();
        tempPoint = new PointYio();
        vpDeltaOffset = 0.1f * GraphicsYio.width;
        loadTextures();
    }


    void loadTextures() {
        buttonPixel = GraphicsYio.loadTextureRegion("button_pixel.png", false);
        blackCircle = GraphicsYio.loadTextureRegion("anim_circle_high_res.png", false);
    }


    void render(boolean renderAliveButtons, boolean renderDyingButtons, ArrayList<ButtonYio> buttons) {
        prepareToRender(renderAliveButtons, renderDyingButtons, buttons);

        renderShadows();
        drawMasks();
        renderButtons();
    }


    void prepareToRender(boolean renderAliveButtons, boolean renderDyingButtons, ArrayList<ButtonYio> buttons) {
        this.renderAliveButtons = renderAliveButtons;
        this.renderDyingButtons = renderDyingButtons;
        this.buttons = buttons;
        batch = menuViewYio.batch;
        shapeRenderer = menuViewYio.shapeRenderer;
        useMenuMasks = menuViewYio.yioGdxGame.useMenuMasks;
    }


    private float getSelectionAlpha(ButtonYio buttonYio) {
        if (cutEndConditions(buttonYio)) return 0;

        return Math.min(0.7f * buttonYio.selAlphaFactor.get(), buttonYio.appearFactor.get());
    }


    void renderButtons() {
        batch.begin();
        if (useMenuMasks) {
            LegacyMasking.continueAfterBatchBegin();
        }

        for (ButtonYio buttonYio : buttons) {
            renderSingleButton(buttonYio);
        }

        for (InterfaceElement interfaceElement : menuViewYio.yioGdxGame.menuControllerYio.interfaceElements) {
            if (!(interfaceElement instanceof CheckButtonYio)) continue;
            if (!interfaceElement.isVisible()) continue;
            MenuRender.renderCheckButton.renderElement(interfaceElement);
        }

        GraphicsYio.setBatchAlpha(batch, 1);
        batch.end();

        if (useMenuMasks) {
            LegacyMasking.end();
        }
    }


    void renderSingleButton(ButtonYio buttonYio) {
        if (cutEndConditions(buttonYio)) return;
        if (!buttonYio.renderable) return;
        if (!isButtonCurrentlyVisible(buttonYio)) return;
        if (buttonYio.textureRegion == null) return;

        updateBatchAlphaForSingleButton(buttonYio);
        updateVp(buttonYio);
        GraphicsYio.drawByRectangle(batch, buttonYio.textureRegion, visualPosition);

        if (isSelectionCurrentlyVisible(buttonYio)) {
            GraphicsYio.setBatchAlpha(batch, getSelectionAlpha(buttonYio));
            GraphicsYio.drawByRectangle(batch, buttonPixel, visualPosition);
        }
    }


    private void updateBatchAlphaForSingleButton(ButtonYio buttonYio) {
        if (buttonYio.appearFactor.get() <= 1) {
            GraphicsYio.setBatchAlpha(batch, buttonYio.appearFactor.get());
        } else {
            GraphicsYio.setBatchAlpha(batch, 1);
        }
    }


    private boolean isButtonCurrentlyVisible(ButtonYio buttonYio) {
        if (!buttonYio.isVisible()) return false;
        if (buttonYio.onlyShadow) return false;
        if (renderAliveButtons && buttonYio.appearFactor.getGravity() >= 0) return true;
        if (renderDyingButtons && buttonYio.appearFactor.getGravity() <= 0) return true;
        return false;
    }


    private boolean isSelectionCurrentlyVisible(ButtonYio buttonYio) {
        if (!buttonYio.isCurrentlyTouched()) return false;
        if (buttonYio.appearFactor.get() <= 0.35) return false;
        if (!buttonYio.selectionRenderable) return false;

        return true;
    }


    private boolean cutEndConditions(ButtonYio buttonYio) {
        return buttonYio.appearFactor.get() < 0.15 && buttonYio.appearFactor.getGravity() < 0;
    }


    private boolean checkForSpecialMask(ButtonYio buttonYio) {
        switch (buttonYio.id) {
            case 3:
                if (buttonYio.appearFactor.get() > 0.1) {
                    shapeRenderer.circle(buttonYio.cx, buttonYio.cy, (float) (0.8 + 0.2 * buttonYio.selectionFactor.get()) * buttonYio.hor);
                }
                return true;
        }
        return false;
    }


    void updateVp(ButtonYio buttonYio) {
        if (buttonYio.appearFactor.get() == 1) {
            visualPosition.setBy(buttonYio.position);
            return;
        }

        if (buttonYio.appearFactor.get() < 0.02) {
            visualPosition.set(0, 0, 0, 0);
            return;
        }

        if (buttonYio.hasVisualHook()) {
            updateVisualPositionByHook(buttonYio);
            return;
        }

        visualPosition.set(
                buttonYio.getVpX(),
                buttonYio.animPos.y,
                buttonYio.position.width,
                buttonYio.position.height
        );
    }


    private void updateVisualPositionByHook(ButtonYio buttonYio) {
        ButtonYio visualHook = buttonYio.visualHook;
        tempPoint.set(
                buttonYio.position.x - visualHook.position.x,
                buttonYio.position.y - visualHook.position.y
        );

        visualPosition.set(
                visualHook.position.x + tempPoint.x,
                visualHook.position.y - (1 - buttonYio.appearFactor.get()) * vpDeltaOffset + tempPoint.y,
                buttonYio.position.width,
                buttonYio.position.height
        );
    }


    void drawMasks() {
        if (!useMenuMasks) return;

        LegacyMasking.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (ButtonYio buttonYio : buttons) {
            if (!buttonYio.isVisible()) continue;
            if (checkForSpecialMask(buttonYio)) continue;
            if (!buttonYio.renderable) continue;

            if (buttonYio.rectangularMask &&
                    ((renderAliveButtons && buttonYio.appearFactor.getGravity() >= 0) || (renderDyingButtons && buttonYio.appearFactor.getGravity() <= 0))) {
                menuViewYio.drawRect(buttonYio.animPos);
                continue;
            }

            menuViewYio.drawRoundRect(buttonYio.animPos);
        }
        shapeRenderer.end();
    }


    void renderShadows() {
        batch.begin();
        for (ButtonYio buttonYio : buttons) {
            if (!buttonYio.isVisible()) continue;
            if (!buttonYio.hasShadow) continue;
            if (buttonYio.appearFactor.get() <= 0.25) continue;
            if (!buttonYio.renderable) continue;
            if ((!renderAliveButtons || buttonYio.appearFactor.getGravity() < 0) && (!renderDyingButtons || buttonYio.appearFactor.getGravity() > 0))
                continue;

            MenuRender.renderShadow.renderShadow(buttonYio.animPos, buttonYio.appearFactor.get());
        }
        batch.end();
    }
}