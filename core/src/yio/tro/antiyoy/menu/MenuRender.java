package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import yio.tro.antiyoy.*;

import java.util.ArrayList;
import java.util.ListIterator;

public abstract class MenuRender {

    static ArrayList<MenuRender> list = new ArrayList<>();

    public static RenderLevelSelector renderLevelSelector = new RenderLevelSelector();
    public static RenderCheckButton renderCheckButton = new RenderCheckButton();

    protected MenuViewYio menuViewYio;
    protected SpriteBatch batch;
    protected Color c;
    public float w, h, shadowOffset;


    public MenuRender() {
        ListIterator iterator = list.listIterator();
        iterator.add(this);
    }


    public static void updateRenderSystems(MenuViewYio menuViewYio) {
        for (MenuRender menuRender : list) {
            menuRender.update(menuViewYio);
        }
    }


    void update(MenuViewYio menuViewYio) {
        this.menuViewYio = menuViewYio;
        batch = menuViewYio.batch;
        c = batch.getColor();
        w = menuViewYio.w;
        h = menuViewYio.h;
        shadowOffset = (int) (0.01 * h);
        loadTextures();
    }


    public abstract void loadTextures();


    public abstract void renderFirstLayer(InterfaceElement element);


    public abstract void renderSecondLayer(InterfaceElement element);


    public abstract void renderThirdLayer(InterfaceElement element);


    void renderShadow(RectangleYio rectangle, float factor, SpriteBatch batch) {
        batch.setColor(c.r, c.g, c.b, 0.5f * factor);
        batch.draw(getGameView().blackPixel, (float)rectangle.x + (float)rectangle.width, (float)rectangle.y - shadowOffset, shadowOffset, (float)rectangle.height);
        batch.draw(getGameView().blackPixel, (float)rectangle.x + shadowOffset, (float)rectangle.y - shadowOffset, (float)rectangle.width - shadowOffset, shadowOffset);
        batch.setColor(c.r, c.g, c.b, 1);
    }


    public GameView getGameView() {
        return menuViewYio.yioGdxGame.gameView;
    }
}
