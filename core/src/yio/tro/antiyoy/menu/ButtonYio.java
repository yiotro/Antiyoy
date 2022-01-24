package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.slider.SliderParentElement;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.factor_yio.FactorYio;

import java.util.ArrayList;


public class ButtonYio implements SliderParentElement, UiChildrenHolder {

    public static final int ACTION_DELAY = 50;
    public static final int DEFAULT_TOUCH_DELAY = 1000;
    public final MenuControllerYio menuControllerYio;
    public RectangleYio position, animPos;
    public TextureRegion textureRegion, customBackgroundForText;
    public FactorYio appearFactor, selectionFactor, selAlphaFactor;
    public final int id; // must be unique for every menu button
    protected boolean touchable;
    private boolean visible;
    protected Reaction reaction;
    private long lastTimeTouched;
    public boolean currentlyTouched;
    private int touchDelay;
    private Animation animType;
    public final ArrayList<String> textLines;
    public final Color backColor;
    private boolean needToPerformAction;
    private long timeToPerformAction;
    public float hor, ver, cx, cy, touchX, touchY, animR;
    public float x1, x2, y1, y2;
    private float touchOffset, textOffset;
    Sound pressSound;
    String texturePath;
    public boolean hasShadow, rectangularMask; // mandatory shadow - draw shadow right before button
    public boolean onlyShadow, touchAnimation, lockAction, renderable, selectionRenderable;
    boolean ignorePauseResume;
    ButtonYio visualHook;
    private float f;


    public ButtonYio(RectangleYio position, int id, MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;
        this.position = position;
        this.id = id;
        touchable = false;
        visible = false;
        touchDelay = DEFAULT_TOUCH_DELAY;
        timeToPerformAction = 0;
        appearFactor = new FactorYio();
        selectionFactor = new FactorYio();
        selAlphaFactor = new FactorYio();
        textLines = new ArrayList<String>();
        backColor = new Color(0.5f, 0.5f, 0.5f, 1);
        hasShadow = true;
        texturePath = null;
        animPos = new RectangleYio(0, 0, 0, 0);
        pressSound = null;
        textOffset = 0;
        customBackgroundForText = null;
        ignorePauseResume = false;
        visualHook = null;
        renderable = true;
        selectionRenderable = true;
        animType = Animation.def;
    }


    public void move() {
        moveAppearFactor();
        moveSelection();
        moveSelAlphaFactor();
        checkToDisableCurrentlyTouched();
        updateAnimPos();
    }


    private void updateAnimPos() {
        f = appearFactor.get();

        switch (animType) {
            case def:
                animDefault();
                break;
            case up:
                animUp();
                break;
            case down:
                animDown();
                break;
            case none:
                animSolid();
                break;
            case from_center:
                animFromCenter();
                break;
            case fixed_down:
                animFixedDown();
                break;
            case fixed_up:
                animFixedUp();
                break;
            case left:
                animLeft();
                break;
        }

        animPos.set(x1, y1, 2 * hor, 2 * ver);
    }


    private void checkToDisableCurrentlyTouched() {
        if (currentlyTouched && System.currentTimeMillis() - lastTimeTouched > touchDelay && selAlphaFactor.get() == 0) {
            currentlyTouched = false;
        }
    }


    private void moveSelAlphaFactor() {
        if (!currentlyTouched) return;

        selAlphaFactor.move();
    }


    private void moveSelection() {
        if (!selectionFactor.hasToMove()) return;

        selectionFactor.move();
        if (lockAction && selectionFactor.get() == 1) {
            lockAction = false;
        }
    }


    private void moveAppearFactor() {
        if (!appearFactor.hasToMove()) return;

        appearFactor.move();
    }


    private void animLeft() {
        x1 = (float) (position.x - (1 - f) * (position.width));
        x2 = x1 + (float) position.width;
        hor = 0.5f * (float) position.width;
        ver = 0.5f * (float) position.height;
        y1 = (float) position.y;
        y2 = y1 + (float) position.height;
    }


    private void animFixedUp() {
        x1 = (float) position.x;
        x2 = x1 + (float) position.width;
        hor = 0.5f * (float) position.width;
        ver = 0.5f * (float) position.height;
        y1 = (float) (position.y + (1 - f) * 0.6 * GraphicsYio.height);
        y2 = y1 + (float) position.height;
    }


    private void animFixedDown() {
        x1 = (float) position.x;
        x2 = x1 + (float) position.width;
        hor = 0.5f * (float) position.width;
        ver = 0.5f * (float) position.height;
        y1 = (float) (position.y - (1 - f) * 0.6 * GraphicsYio.height);
        y2 = y1 + (float) position.height;
    }


    private void animFromCenter() {
        hor = (float) (0.5 * f * position.width);
        ver = (float) (0.5 * f * position.height);
        cx = (float) position.x + 0.5f * (float) position.width;
        cy = (float) position.y + 0.5f * (float) position.height;
        cx -= (1 - f) * (cx - 0.5f * menuControllerYio.yioGdxGame.w);
        cy -= (1 - f) * (cy - 0.5f * menuControllerYio.yioGdxGame.h);
        x1 = cx - hor;
        x2 = cx + hor;
        y1 = cy - ver;
        y2 = cy + ver;
    }


    private void animSolid() {
        x1 = (float) position.x;
        x2 = x1 + (float) position.width;
        hor = 0.5f * (float) position.width;
        ver = 0.5f * (float) position.height;
        y1 = (float) position.y;
        y2 = y1 + (float) position.height;
    }


    private void animDown() {
        x1 = (float) position.x;
        x2 = x1 + (float) position.width;
        hor = 0.5f * (float) position.width;
        ver = 0.5f * (float) position.height;
        y1 = (float) (f * (position.y + position.height)) - (float) position.height;
        y2 = y1 + (float) position.height;
    }


    private void animUp() {
        x1 = (float) position.x;
        x2 = x1 + (float) position.width;
        hor = 0.5f * (float) position.width;
        ver = 0.5f * (float) position.height;
        y1 = (float) position.y + (float) ((1 - f) * (menuControllerYio.yioGdxGame.h - position.y));
        y2 = y1 + (float) position.height;
    }


    private void animDefault() {
        hor = (float) (0.5 * f * position.width);
        ver = (float) (0.5 * f * position.height);
        cx = (float) position.x + 0.5f * (float) position.width;
        cy = (float) position.y + 0.5f * (float) position.height;
        x1 = cx - hor;
        x2 = cx + hor;
        y1 = cy - ver;
        y2 = cy + ver;
    }


    public boolean checkToPerformAction() {
        if (appearFactor.getGravity() < 0) return false;
        if (needToPerformAction && System.currentTimeMillis() > timeToPerformAction && !lockAction) {
            needToPerformAction = false;
            reaction.perform(this);
            return true;
        }
        return false;
    }


    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }


    public void setAnimation(Animation animType) {
        this.animType = animType;
    }


    public void appear() {
        appearFactor.appear(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
        appearFactor.setValues(0, 0.001);
        onAppear();
    }


    private void onAppear() {
        selectionFactor.reset();
    }


    public double getVpX() {
        if (animType == Animation.left) {
            return animPos.x;
        }
        return position.x;
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
        selectionFactor.appear(0, 1);
        selAlphaFactor.setValues(1, 0);
        selAlphaFactor.destroy(1, 0.5);
        touchX = screenX;
        touchY = screenY;
        animR = Math.max(touchX - (float) animPos.x, (float) (animPos.x + animPos.width - touchX));
        lockAction = true;
        menuControllerYio.yioGdxGame.render();
        if (reaction != null && System.currentTimeMillis() - timeToPerformAction > ACTION_DELAY) {
            needToPerformAction = true;
            timeToPerformAction = System.currentTimeMillis() + 100;
        }
    }


    private void playPressSound() {
        if (pressSound == null)
            SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
        else
            SoundManagerYio.playSound(pressSound);
    }


    public void setPressSound(Sound pressSound) {
        this.pressSound = pressSound;
    }


    public void setTexture(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
        hasShadow = false;
    }


    public void loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        textureRegion = new TextureRegion(texture);
        texturePath = path;
        hasShadow = false;
    }


    public void loadCustomBackground(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        customBackgroundForText = new TextureRegion(texture);
        texturePath = path;
    }


    public boolean hasCustomBackground() {
        return customBackgroundForText != null;
    }


    public void resetTexture() {
        textureRegion = null;
    }


    public void destroy() {
        setTouchable(false);
        appearFactor.setDy(0);
        appearFactor.destroy(MenuControllerYio.DESTROY_ANIM, MenuControllerYio.DESTROY_SPEED);
    }


    public void cleatText() {
        textLines.clear();
    }


    public void setTextLine(String line) {
        cleatText();
        addTextLine(line);
    }


    public ArrayList<String> getText() {
        return textLines;
    }


    public void addTextLine(String textLine) {
        textLines.add(textLine);
    }


    public void addManyLines(ArrayList<String> lines) {
        textLines.addAll(lines);
    }


    public void addEmptyLines(int quantity) {
        for (int k = 0; k < quantity; k++) {
            addTextLine(" ");
        }
    }


    public void applyNumberOfLines(int targetNumber) {
        while (textLines.size() < targetNumber) {
            addTextLine(" ");
        }
    }


    public void setBackgroundColor(float r, float g, float b) {
        backColor.set(r, g, b, 1);
    }


    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }


    public boolean isVisible() {
        if (appearFactor.get() > 0 && visible) return true;
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


    public void onAppPause() {
        if (ignorePauseResume) return;

        if (textureRegion != null) {
            textureRegion.getTexture().dispose();
        }

        if (customBackgroundForText != null) {
            customBackgroundForText.getTexture().dispose();
        }
    }


    public void onAppResume() {
        if (ignorePauseResume) return;

        if (hasCustomBackground()) {
            reloadCustomBackground();
        }

        if (hasText()) {
            menuControllerYio.buttonRenderer.renderButton(this);
        } else {
            reloadTexture();
        }
    }


    private void reloadCustomBackground() {
        customBackgroundForText = null;

        loadCustomBackground(texturePath);
    }


    public TextureRegion getCustomBackgroundForText() {
        return customBackgroundForText;
    }


    public void reloadTexture() {
        resetTexture();

        boolean sh = hasShadow;
        loadTexture(texturePath);
        setShadow(sh);
    }


    private boolean hasText() {
        return texturePath == null;
    }


    public void setPosition(RectangleYio position) {
        this.position = position;
    }


    public void setIgnorePauseResume(boolean ignorePauseResume) {
        this.ignorePauseResume = ignorePauseResume;
    }


    public float getTextOffset() {
        return textOffset;
    }


    public void setTextOffset(float textOffset) {
        this.textOffset = textOffset;
    }


    public void setRenderable(boolean renderable) {
        this.renderable = renderable;
    }


    public void setVisualHook(ButtonYio visualHook) {
        this.visualHook = visualHook;
    }


    public boolean hasVisualHook() {
        return visualHook != null;
    }


    public void setSelectionRenderable(boolean selectionRenderable) {
        this.selectionRenderable = selectionRenderable;
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public RectangleYio getViewPosition() {
        return animPos;
    }


    public void tagAsBackButton() {
        menuControllerYio.yioGdxGame.registerBackButtonId(id);
    }


    @Override
    public RectangleYio getHookPosition() {
        return getViewPosition();
    }


    @Override
    public RectangleYio getTargetPosition() {
        return position;
    }
}
