package yio.tro.antiyoy.menu.behaviors.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.FrameBufferYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.game_view.GameView;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class ColorStatsRenderer {

    MenuControllerYio menuControllerYio;
    private FrameBuffer frameBuffer;
    private SpriteBatch batch;
    TextureRegion backgroundTexture, greenPixel, redPixel, bluePixel, cyanPixel, yellowPixel, blackPixel;
    TextureRegion pixelColor1, pixelColor2, pixelColor3;
    private GameController gameController;
    private BitmapFont font;
    private float w;
    private float h;
    private float columnWidth;
    private float distanceBetweenColumns;
    private float maxNumber;
    private float maxColumnHeight;
    private float y;
    private int[] quantityArray;
    private ButtonYio statButton;


    public ColorStatsRenderer(MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;
        gameController = null; // updated in perform()

        batch = new SpriteBatch();
        backgroundTexture = GraphicsYio.loadTextureRegion("diplomacy/background.png", false);
        loadColors();
        blackPixel = GraphicsYio.loadTextureRegion("black_pixel.png", false);
    }


    public void loadColors() {
        greenPixel = loadColorPixel("pixel_green");
        redPixel = loadColorPixel("pixel_red");
        bluePixel = loadColorPixel("pixel_blue");
        cyanPixel = loadColorPixel("pixel_cyan");
        yellowPixel = loadColorPixel("pixel_yellow");
        pixelColor1 = loadColorPixel("pixel_color1");
        pixelColor2 = loadColorPixel("pixel_color2");
        pixelColor3 = loadColorPixel("pixel_color3");
    }


    private TextureRegion loadColorPixel(String name) {
        String pixelsFolderPath = menuControllerYio.yioGdxGame.skinManager.getPixelsFolderPath();
        return GraphicsYio.loadTextureRegion(pixelsFolderPath + "/" + name + ".png", false);
    }


    TextureRegion getPixelByIndex(int colorIndex) {
        int colorIndexWithOffset = gameController.getColorIndexWithOffset(colorIndex);
        switch (colorIndexWithOffset) {
            default:
            case 0:
                return greenPixel;
            case 1:
                return redPixel;
            case 2:
                return bluePixel;
            case 3:
                return cyanPixel;
            case 4:
                return yellowPixel;
            case 5:
                return pixelColor1;
            case 6:
                return pixelColor2;
            case 7:
                return pixelColor3;
        }
    }


    void performRendering(ButtonYio statButton, int quantityArray[]) {
        this.statButton = statButton;
        this.quantityArray = quantityArray;
        this.gameController = menuControllerYio.yioGdxGame.gameController;
        font = Fonts.buttonFont;

        beginRender();
        batch.begin();

        prepareMetrics();
        font.setColor(Color.BLACK);

        renderInternals();

        font.setColor(0, 0, 0, 1);
        batch.end();
        endRender();
    }


    private void renderInternals() {
        for (int i = 0; i < this.quantityArray.length; i++) {
            float stringWidth = YioGdxGame.getTextWidth(font, "" + this.quantityArray[i]);
            float columnX = columnWidth + distanceBetweenColumns * i;
            float currentColumnHeight = (float) this.quantityArray[i] / maxNumber;
            currentColumnHeight *= maxColumnHeight;

            font.draw(batch, "" + this.quantityArray[i], columnX - stringWidth / 2, y + 0.04f * h);
            batch.draw(getPixelByIndex(i), columnX - columnWidth / 2, 0.01f * h + y - currentColumnHeight, columnWidth, currentColumnHeight);
        }
        batch.draw(blackPixel, 0.025f * w, 0.0125f * h + y, 0.95f * w, 0.005f * h);

        String incomeString = LanguagesManager.getInstance().getString("income");
        float incomeWidth = GraphicsYio.getTextWidth(font, incomeString);
        font.draw(batch, incomeString, w / 2 - incomeWidth / 2, 0.02f * h);
    }


    private void prepareMetrics() {
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        columnWidth = 0.1f * w;
        distanceBetweenColumns = (w - 2 * columnWidth) / (quantityArray.length - 1);
        maxNumber = GameController.maxNumberFromArray(quantityArray);
        maxColumnHeight = 0.25f * h;
        y = maxColumnHeight + 0.07f * h;
    }


    private void beginRender() {
        if (frameBuffer != null) {
            frameBuffer.dispose();
        }
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2, false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(statButton.backColor.r, statButton.backColor.g, statButton.backColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Matrix4 matrix4 = new Matrix4();
        int orthoWidth = Gdx.graphics.getWidth();
        int orthoHeight = Gdx.graphics.getHeight() / 2;
        matrix4.setToOrtho2D(0, 0, orthoWidth, orthoHeight);
        batch.setProjectionMatrix(matrix4);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, orthoWidth, orthoHeight);
        batch.end();
    }


    void endRender() {
        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        float f = ((FrameBufferYio) frameBuffer).f;
        statButton.textureRegion = new TextureRegion(texture, (int) (statButton.position.width * f), (int) (statButton.position.height * f));
        frameBuffer.end();
        frameBuffer.dispose();
    }
}
