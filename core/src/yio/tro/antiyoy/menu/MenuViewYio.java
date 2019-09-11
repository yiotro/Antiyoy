package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class MenuViewYio {

    public final YioGdxGame yioGdxGame;
    private final MenuControllerYio menuControllerYio;
    private final RenderButtons renderButtons;
    TextureRegion scrollerCircle, grayTransCircle;
    public TextureRegion shadowCorner, shadowSide;
    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;
    private int cornerSize;
    public float w, h;
    private float x1, y1, x2, y2; // local variables for rendering
    private Color c; // local variable for rendering
    public OrthographicCamera orthoCam;
    RectangleYio tempRectangle;


    public MenuViewYio(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        menuControllerYio = yioGdxGame.menuControllerYio;
        shapeRenderer = yioGdxGame.shapeRenderer;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        cornerSize = (int) (0.02 * Gdx.graphics.getHeight());
        shadowCorner = GraphicsYio.loadTextureRegion("corner_shadow.png", true);
        shadowSide = GraphicsYio.loadTextureRegion("side_shadow.png", true);
        scrollerCircle = GraphicsYio.loadTextureRegion("scroller_circle.png", false);
        grayTransCircle = GraphicsYio.loadTextureRegion("gray_transition_circle.png", false);
        renderButtons = new RenderButtons(this);
        tempRectangle = new RectangleYio();
        createOrthoCam();

        batch = yioGdxGame.batch;
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


    void drawRect(RectangleYio pos) {
        shapeRenderer.rect((float) pos.x, (float) pos.y, (float) pos.width, (float) pos.height);
    }


    public void drawCircle(float x, float y, float r) {
        shapeRenderer.circle(x, y, r);
    }


    public void render(boolean renderAliveButtons, boolean renderDyingButtons) {
        ArrayList<ButtonYio> buttons = menuControllerYio.buttons;
        c = batch.getColor();

        renderButtons.render(renderAliveButtons, renderDyingButtons, buttons);
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


}
