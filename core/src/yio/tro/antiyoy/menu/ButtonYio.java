package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.RectangleYio;
import yio.tro.antiyoy.SoundControllerYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.factor_yio.FactorYio;

import java.util.ArrayList;

/**
 * Created by ivan on 22.07.14.
 */
public class ButtonYio {

    public static final int ACTION_DELAY = 50;
    public static final int DEFAULT_TOUCH_DELAY = 1000;
    public final MenuControllerYio menuControllerYio;
    public RectangleYio position, animPos;
    public TextureRegion textureRegion;
    public FactorYio factorModel, selectionFactor, selAlphaFactor;
    public final int id; // must be unique for every menu button
    protected boolean touchable;
    private boolean visible;
    public static final int ANIM_DEFAULT = 0;
    public static final int ANIM_UP = 1;
    public static final int ANIM_DOWN = 2;
    public static final int ANIM_COLLAPSE_UP = 3;
    public static final int ANIM_SOLID = 4;
    public static final int ANIM_FROM_CENTER = 5;
    public static final int ANIM_COLLAPSE_DOWN = 6;
    private ReactBehavior reactBehavior;
    private long lastTimeTouched;
    public boolean currentlyTouched;
    private int touchDelay, animType;
    public final ArrayList<String> text;
    public final Color backColor;
    private boolean needToPerformAction;
    private long timeToPerformAction;
    public float hor, ver, cx, cy, touchX, touchY, animR;
    public float x1, x2, y1, y2;
    private float deltaSizeArgument, deltaSize, touchOffset;
    Sound pressSound;
    public boolean hasShadow, mandatoryShadow, rectangularMask, onlyShadow, touchAnimation, lockAction, deltaAnimationEnabled; // mandatory shadow - draw shadow right before button


    public ButtonYio(RectangleYio position, int id, MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;
        this.position = position;
        this.id = id;
        touchable = false;
        visible = false;
        touchDelay = DEFAULT_TOUCH_DELAY;
        timeToPerformAction = 0;
        factorModel = new FactorYio();
        selectionFactor = new FactorYio();
        selAlphaFactor = new FactorYio();
        text = new ArrayList<String>();
        backColor = new Color(0.5f, 0.5f, 0.5f, 1);
        hasShadow = true;
        mandatoryShadow = false;
        animPos = new RectangleYio(0, 0, 0, 0);
        deltaAnimationEnabled = false;
        pressSound = null;
    }


    public void move() {
        if (factorModel.needsToMove()) factorModel.move();
        if (selectionFactor.needsToMove()) {
            selectionFactor.move();
            if (lockAction && selectionFactor.get() == 1) lockAction = false;
        }
        if (currentlyTouched) selAlphaFactor.move();
        if (deltaAnimationEnabled) {
            deltaSizeArgument += 0.1f;
            deltaSize = 0.98f + 0.04f * (float) Math.cos(deltaSizeArgument);
        }
        if (currentlyTouched && System.currentTimeMillis() - lastTimeTouched > touchDelay && selAlphaFactor.get() == 0) {
            currentlyTouched = false;
        }
        float factor = factorModel.get();
        switch (animType) {
            case ANIM_DEFAULT:
                hor = (float) (0.5 * factor * position.width);
                ver = (float) (0.5 * factor * position.height);
                cx = (float) position.x + 0.5f * (float) position.width;
                cy = (float) position.y + 0.5f * (float) position.height;
                x1 = cx - hor;
                x2 = cx + hor;
                y1 = cy - ver;
                y2 = cy + ver;
                break;
            case ANIM_UP:
                x1 = (float) position.x;
                x2 = x1 + (float) position.width;
                hor = 0.5f * (float) position.width;
                ver = 0.5f * (float) position.height;
                y1 = (float) position.y + (float) ((1 - factor) * (menuControllerYio.yioGdxGame.h - position.y));
                y2 = y1 + (float) position.height;
                break;
            case ANIM_DOWN:
                x1 = (float) position.x;
                x2 = x1 + (float) position.width;
                hor = 0.5f * (float) position.width;
                ver = 0.5f * (float) position.height;
                y1 = (float) (factor * (position.y + position.height)) - (float) position.height;
                y2 = y1 + (float) position.height;
                break;
            case ANIM_COLLAPSE_UP:
                x1 = (float) position.x;
                x2 = x1 + (float) position.width;
                hor = 0.5f * (float) position.width;
                ver = 0.5f * (float) (factor * position.height);
                y1 = (float) position.y + (float) ((1 - factor) * (menuControllerYio.yioGdxGame.h - position.y));
                y2 = y1 + (float) (factor * position.height);
                break;
            case ANIM_SOLID:
                x1 = (float) position.x;
                x2 = x1 + (float) position.width;
                hor = 0.5f * (float) position.width;
                ver = 0.5f * (float) position.height;
                y1 = (float) position.y;
                y2 = y1 + (float) position.height;
                break;
            case ANIM_FROM_CENTER:
                hor = (float) (0.5 * factor * position.width);
                ver = (float) (0.5 * factor * position.height);
                cx = (float) position.x + 0.5f * (float) position.width;
                cy = (float) position.y + 0.5f * (float) position.height;
                cx -= (1 - factor) * (cx - 0.5f * menuControllerYio.yioGdxGame.w);
                cy -= (1 - factor) * (cy - 0.5f * menuControllerYio.yioGdxGame.h);
                x1 = cx - hor;
                x2 = cx + hor;
                y1 = cy - ver;
                y2 = cy + ver;
                break;
            case ANIM_COLLAPSE_DOWN:
                x1 = (float) position.x;
                x2 = x1 + (float) position.width;
                hor = 0.5f * (float) position.width;
                ver = 0.5f * (float) (factor * position.height);
                y1 = (float) position.y + (float) ((1 - factor) * (-position.height - position.y));
                y2 = y1 + (float) (factor * position.height);
                break;
        }
        if (deltaAnimationEnabled) {
            cx = x1 + hor;
            cy = y1 + ver;
            animPos.set(cx - deltaSize * hor, cy - deltaSize * ver, deltaSize * 2 * hor, deltaSize * 2 * ver);
        } else {
            animPos.set(x1, y1, 2 * hor, 2 * ver);
        }
    }


    public boolean checkToPerformAction() {
        if (needToPerformAction && System.currentTimeMillis() > timeToPerformAction && !lockAction) {
            needToPerformAction = false;
            reactBehavior.reactAction(this);
            return true;
        }
        return false;
    }


    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }


    public void setAnimType(int animType) {
        this.animType = animType;
    }


    public boolean checkTouch(int screenX, int screenY, int pointer, int button) {
        if (!touchable) return false;
        if (screenX > position.x - touchOffset &&
                screenX < position.x + position.width + touchOffset &&
                screenY > position.y - touchOffset &&
                screenY < position.y + position.height + touchOffset) {
            press(screenX, screenY);
            return true;
        }
        return false;
    }


    public void press() {
        press((int) (position.x + 0.5f * position.width), (int) (position.y + 0.5f * position.height));
    }


    public void press(int screenX, int screenY) {
        if (!touchable) return;
        currentlyTouched = true;
        lastTimeTouched = System.currentTimeMillis();
        playPressSound();
        selectionFactor.setValues(0.2, 0.02);
        selectionFactor.beginSpawning(0, 1);
        selAlphaFactor.setValues(1, 0);
        selAlphaFactor.beginDestroying(1, 0.5);
        touchX = screenX;
        touchY = screenY;
        animR = Math.max(touchX - (float) animPos.x, (float) (animPos.x + animPos.width - touchX));
//        if (touchAnimation) lockAction = true;
        lockAction = true;
        menuControllerYio.yioGdxGame.render();
        if (reactBehavior != null && System.currentTimeMillis() - timeToPerformAction > ACTION_DELAY) {
            needToPerformAction = true;
            timeToPerformAction = System.currentTimeMillis() + 100;
        }
    }


    private void playPressSound() {
        if (pressSound == null)
            SoundControllerYio.playSound(SoundControllerYio.soundPressButton);
        else
            SoundControllerYio.playSound(pressSound);
    }


    public void setPressSound(Sound pressSound) {
        this.pressSound = pressSound;
    }


    public void loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        textureRegion = new TextureRegion(texture);
        hasShadow = false;
    }


    public void resetTexture() {
        textureRegion = null;
    }


    public void destroy() {
        setTouchable(false);
        factorModel.setDy(0);
        factorModel.beginDestroying(MenuControllerYio.DESTROY_ANIM, MenuControllerYio.DESTROY_SPEED);
    }


    public void cleatText() {
        text.clear();
    }


    public void setTextLine(String line) {
        cleatText();
        addTextLine(line);
    }


    public ArrayList<String> getText() {
        return text;
    }


    public void enableDeltaAnimation() {
        deltaAnimationEnabled = true;
    }


    public void disableTouchAnimation() {
        touchAnimation = false;
    }


    public void addTextLine(String textLine) {
        text.add(textLine);
    }


    public void addManyLines(ArrayList<String> lines) {
        text.addAll(lines);
    }


    public void setBackgroundColor(float r, float g, float b) {
        backColor.set(r, g, b, 1);
    }


    public void setReactBehavior(ReactBehavior reactBehavior) {
        this.reactBehavior = reactBehavior;
    }


    public boolean isVisible() {
        if (factorModel.get() > 0 && visible) return true;
        return false;
    }


    public void enableRectangularMask() {
        rectangularMask = true;
    }


    public void setTouchOffset(float touchOffset) {
        this.touchOffset = touchOffset;
    }


    public void setTouchDelay(int touchDelay) {
        this.touchDelay = touchDelay;
    }


    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    public boolean isTouchable() {
        return touchable;
    }


    public MenuControllerYio getMenuControllerYio() {
        return menuControllerYio;
    }


    public boolean isCurrentlyTouched() {
        return currentlyTouched;
    }


    public boolean notRendered() {
        return textureRegion == null;
    }


    public void setShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
    }


    public boolean isShadowMandatory() {
        return mandatoryShadow;
    }


    public void setMandatoryShadow(boolean mandatoryShadow) {
        this.mandatoryShadow = mandatoryShadow;
    }


    public void setPosition(RectangleYio position) {
        this.position = position;
    }
}
