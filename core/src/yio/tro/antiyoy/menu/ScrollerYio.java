package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.campaign.CampaignLevelFactory;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.game_view.GameView;

import java.util.ArrayList;
import java.util.ListIterator;

import static yio.tro.antiyoy.YioGdxGame.getDifficultyNameByPower;

/**
 * Created by ivan on 06.10.2014.
 */
public class ScrollerYio {

    public float pos, speed, topLimit, bottomLimit, lineHeight, maxOffset;
    private YioGdxGame yioGdxGame;
    public RectangleYio frame, animFrame;
    public ArrayList<TextureRegion> cache, icons;
    private ArrayList<String> strings;
    public FactorYio factorModel, selectionFactor, selAlphaFactor, speedCutFactor;
    private FrameBuffer frameBuffer;
    private SpriteBatch batch;
    private BitmapFont font;
    public TextureRegion bg1, bg2;
    public float startY, lastY, variation, selectX, animRadius;
    private boolean dragged;
    public int selectionIndex;


    public ScrollerYio(YioGdxGame yioGdxGame, RectangleYio frame, float lineHeight, SpriteBatch batch) {
        this.yioGdxGame = yioGdxGame;
        this.frame = frame;
        this.lineHeight = lineHeight;
        this.batch = batch;
        this.animFrame = new RectangleYio(frame);
        selectionIndex = -1;
        pos = 0;
        speed = 0;
        cache = new ArrayList<TextureRegion>();
        icons = new ArrayList<TextureRegion>();
        strings = new ArrayList<String>();
        bg1 = GameView.loadTextureRegionByName("scroller_bg1.png", false);
        bg2 = GameView.loadTextureRegionByName("scroller_bg2.png", false);
        factorModel = new FactorYio();
        selectionFactor = new FactorYio();
        selAlphaFactor = new FactorYio();
        speedCutFactor = new FactorYio();
        bottomLimit = 0;
        maxOffset = 3 * lineHeight;
        selectionIndex = yioGdxGame.selectedLevelIndex;
        animFrame.set(frame.x, frame.y, frame.width, frame.height);
        selectionFactor.beginSpawning(1, 5);
    }


    public void move() {
        factorModel.move();
        selectionFactor.move();
        selAlphaFactor.move();
//        speedCutFactor.move();
//        if (factorModel.getDy() > 0) animFrame.set(frame.x, frame.y, frame.width, frame.height);
//        else animFrame.set(frame.x, frame.y - (1 - factorModel.get()) * 1.6 * frame.height, frame.width, frame.height);
        limit();
        pos += speed;
//        speed *= speedCutFactor.get();
        speed *= 0.95;
    }


    void limit() {
        if (pos > topLimit) {
            pos -= 0.15 * lineHeight;
            speed *= 0.9;
            if (pos < topLimit) pos = topLimit;
            if (pos > topLimit + maxOffset) { // too far
                pos = topLimit + maxOffset;
                speed = 0;
            }
        }
        if (pos < bottomLimit) {
            pos += 0.15 * lineHeight;
            speed *= 0.9;
            if (pos > bottomLimit) pos = bottomLimit;
            if (pos < bottomLimit - maxOffset) { // too far
                pos = bottomLimit - maxOffset;
                speed = 0;
            }
        }
    }


    public String getLevelStringByIndex(LanguagesManager languagesManager, int index) {
        int difficulty = CampaignLevelFactory.getDifficultyByIndex(index);
        String diffString = "";
        diffString = getDifficultyNameByPower(languagesManager, difficulty);
        return index + " " + diffString;
    }


    private void updateTopLimit() {
        topLimit = lineHeight * cache.size() - (float) frame.height;
    }


    private void addIcon(TextureRegion icon) {
        ListIterator iterator = icons.listIterator();
        while (iterator.hasNext()) iterator.next();
        iterator.add(icon);
    }


    private void addString(String string) {
        ListIterator iterator = strings.listIterator();
        while (iterator.hasNext()) iterator.next();
        iterator.add(string);
    }


    private void addCacheLine(TextureRegion cl) {
        ListIterator iterator = cache.listIterator();
        while (iterator.hasNext()) iterator.next();
        iterator.add(cl);
    }


    void addLine(TextureRegion icon, String string) {
        addIcon(icon);
        addString(string);
        addCacheLine(renderLine(icon, string, cache.size()));
        updateTopLimit();
    }


    void addRenderedLine(TextureRegion icon, String string, TextureRegion cache) {
        addIcon(icon);
        addString(string);
        addCacheLine(cache);
        updateTopLimit();
    }


    void updateCacheLine(int index) {
        cache.set(index, renderLine(icons.get(index), strings.get(index), index));
    }


    private TextureRegion renderLine(TextureRegion icon, String string, int n) {
        TextureRegion result;
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        TextureRegion bg;
        if (n % 2 == 0) bg = bg1;
        else bg = bg2;
        batch.begin();
        batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        font = Fonts.buttonFont;

        batch.begin();
        batch.draw(icon, lineHeight, lineHeight, 0, 0, lineHeight, lineHeight, 1, 1, 180);
        font.draw(batch, string, 1.1f * lineHeight, 0.3f * lineHeight);
        batch.end();

        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        float f = ((FrameBufferYio) frameBuffer).f;
        result = new TextureRegion(texture, (int) (frame.width * f), (int) (lineHeight * f));
        frameBuffer.end();
        frameBuffer.dispose();
        return result;
    }


    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!isVisible() || screenX < frame.x || screenX > frame.x + frame.width || screenY < frame.y || screenY > frame.y + frame.height)
            return false;
        if (factorModel.get() < 0.95) return false;
        speedCutFactor.setValues(1, 0);
        dragged = false;
        lastY = screenY;
        startY = screenY;
        variation = 0;
        return true;
    }


    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!isVisible() || screenX < frame.x || screenX > frame.x + frame.width || screenY < frame.y || screenY > frame.y + frame.height)
            return false;
        if (factorModel.get() < 0.95) return false;
        speedCutFactor.beginDestroying(1, 0.5);
        speedCutFactor.setDy(0.05);
        if (variation < 0.2f * lineHeight && Math.abs(screenY - startY) < 0.2f * lineHeight) {
            int sel = (int) ((frame.y + frame.height + pos - screenY) / lineHeight);
            if (sel == selectionIndex) return false;
            selectionIndex = sel;
            yioGdxGame.setSelectedLevelIndex(selectionIndex);
            selectionFactor.setValues(0.1, 0);
            selectionFactor.beginSpawning(1, 4);
            selAlphaFactor.setValues(1, 0);
            selAlphaFactor.beginDestroying(1, 1);
            selectX = screenX;
            animRadius = Math.max((float) (selectX - frame.x), (float) (frame.x + frame.width - selectX));
            return true;
        }
        return false;
    }


    void increaseSelection() {
        selectionIndex++;
        limitSelection();
    }


    private void limitSelection() {
        if (selectionIndex > CampaignProgressManager.getIndexOfLastLevel()) {
            selectionIndex = CampaignProgressManager.getIndexOfLastLevel();
        }
    }


    public void touchDragged(int screenX, int screenY, int pointer) {
        if (factorModel.get() < 0.9 || screenX < frame.x || screenX > frame.x + frame.width || screenY < frame.y || screenY > frame.y + frame.height)
            return;
        dragged = true;
        float dy = screenY - lastY;
        if (Math.abs(dy) >= 0.5f * Math.abs(speed)) speed = dy;
        else speed *= 0.5f;
        lastY = screenY;
        variation += Math.abs(dy);
    }


    public void setSelectionIndex(int selectionIndex) {
        this.selectionIndex = selectionIndex;
    }


    boolean isVisible() {
        return factorModel.get() > 0.01;
    }
}
