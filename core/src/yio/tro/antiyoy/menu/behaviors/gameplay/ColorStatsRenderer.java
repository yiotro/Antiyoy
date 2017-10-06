package yio.tro.antiyoy.menu.behaviors.gameplay;

import com.badlogic.gdx.Gdx;
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

public class ColorStatsRenderer {

    MenuControllerYio menuControllerYio;
    private FrameBuffer frameBuffer;
    private SpriteBatch batch;
    TextureRegion buttonBackground, greenPixel, redPixel, bluePixel, cyanPixel, yellowPixel, blackPixel;
    TextureRegion pixelColor1, pixelColor2, pixelColor3;
    private GameController gameController;


    public ColorStatsRenderer(MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;
        gameController = menuControllerYio.yioGdxGame.gameController;

        batch = new SpriteBatch();
        buttonBackground = GameView.loadTextureRegion("pixels/pixel_dark_gray.png", true);
        greenPixel = GameView.loadTextureRegion("pixels/pixel_green.png", false);
        redPixel = GameView.loadTextureRegion("pixels/pixel_red.png", false);
        bluePixel = GameView.loadTextureRegion("pixels/pixel_blue.png", false);
        cyanPixel = GameView.loadTextureRegion("pixels/pixel_cyan.png", false);
        yellowPixel = GameView.loadTextureRegion("pixels/pixel_yellow.png", false);
        pixelColor1 = GameView.loadTextureRegion("pixels/pixel_color1.png", false);
        pixelColor2 = GameView.loadTextureRegion("pixels/pixel_color2.png", false);
        pixelColor3 = GameView.loadTextureRegion("pixels/pixel_color3.png", false);
        blackPixel = GameView.loadTextureRegion("black_pixel.png", false);
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


    void setFontColorByIndex(int index) {
        BitmapFont font = Fonts.buttonFont;
        int colorIndexWithOffset = gameController.getColorIndexWithOffset(index);
        switch (colorIndexWithOffset) {
            case 0:
                font.setColor(0.37f, 0.7f, 0.36f, 1);
                break;
            case 1:
                font.setColor(0.7f, 0.36f, 0.46f, 1);
                break;
            case 2:
                font.setColor(0.45f, 0.36f, 0.7f, 1);
                break;
            case 3:
                font.setColor(0.36f, 0.7f, 0.69f, 1);
                break;
            case 4:
                font.setColor(0.7f, 0.71f, 0.39f, 1);
                break;
            case 5:
                font.setColor(0.68f, 0.22f, 0, 1);
                break;
            case 6:
                font.setColor(0.13f, 0.44f, 0.1f, 1);
                break;
            case 7:
                font.setColor(0.4f, 0.4f, 0.4f, 1);
                break;
        }
    }


    void renderStatButton(ButtonYio statButton, int playerHexCount[]) {
        beginRender(statButton, Fonts.gameFont);
        batch.begin();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float columnWidth = 0.1f * w;
        float distanceBetweenColumns = (w - 2 * columnWidth) / (playerHexCount.length - 1);
        float maxNumber = GameController.maxNumberFromArray(playerHexCount);
        float columnHeight = 0.25f * h;
        for (int i = 0; i < playerHexCount.length; i++) {
            setFontColorByIndex(i);
            float numberLineWidth = YioGdxGame.getTextWidth(Fonts.buttonFont, "" + playerHexCount[i]);
            float columnX = columnWidth + distanceBetweenColumns * i;
            batch.draw(blackPixel, columnX - numberLineWidth / 2 - 0.01f * w, 0.28f * h, numberLineWidth + 0.02f * w, 0.05f * h);
            Fonts.buttonFont.draw(batch, "" + playerHexCount[i], columnX - numberLineWidth / 2, 0.29f * h);

            float currentSize = (float) playerHexCount[i] / maxNumber;
            currentSize *= columnHeight;
            batch.draw(getPixelByIndex(i), columnX - columnWidth / 2, 0.01f * h + columnHeight - currentSize, columnWidth, currentSize);
        }
        batch.draw(blackPixel, 0.025f * w, 0.0125f * h + columnHeight, 0.95f * w, 0.005f * h);

        Fonts.buttonFont.setColor(0, 0, 0, 1);
        batch.end();
        endRender(statButton);
    }


    private void beginRender(ButtonYio buttonYio, BitmapFont font) {
        if (frameBuffer != null) {
            frameBuffer.dispose();
        }
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2, false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(buttonYio.backColor.r, buttonYio.backColor.g, buttonYio.backColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Matrix4 matrix4 = new Matrix4();
        int orthoWidth = Gdx.graphics.getWidth();
        int orthoHeight = Gdx.graphics.getHeight() / 2;
        matrix4.setToOrtho2D(0, 0, orthoWidth, orthoHeight);
        batch.setProjectionMatrix(matrix4);
        batch.begin();
        batch.draw(buttonBackground, 0, 0, orthoWidth, orthoHeight);
        batch.end();
    }


    void endRender(ButtonYio buttonYio) {
        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        float f = ((FrameBufferYio) frameBuffer).f;
        buttonYio.textureRegion = new TextureRegion(texture, (int) (buttonYio.position.width * f), (int) (buttonYio.position.height * f));
        frameBuffer.end();
        frameBuffer.dispose();
    }
}
