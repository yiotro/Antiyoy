package yio.tro.antiyoy.menu.diplomatic_dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.StringTokenizer;

public abstract class AbstractDiplomaticDialog extends InterfaceElement {


    protected MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    public FactorYio appearFactor;
    boolean factorMoved;
    protected float leftOffset;
    protected float lineOffset;
    protected float titleOffset;
    protected float topOffset;
    public ArrayList<AcLabel> labels;
    ObjectPoolYio<AcLabel> poolLabels;
    PointYio currentTouch;
    public ArrayList<AcButton> buttons;
    protected float buttonHeight;
    boolean touched;
    protected AcButton yesButton;
    protected AcButton noButton;
    AcButton clickedButton;
    int tagColor;
    public RectangleYio tagPosition;


    public AbstractDiplomaticDialog(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        factorMoved = false;
        labels = new ArrayList<>();
        currentTouch = new PointYio();
        touched = false;
        resetClickedButton();
        tagColor = -1;
        tagPosition = new RectangleYio();

        initPools();
        initMetrics();
        initButtons();
    }


    protected void resetClickedButton() {
        clickedButton = null;
    }


    protected void initButtons() {
        buttons = new ArrayList<>();

        yesButton = new AcButton(this);
        yesButton.setFont(Fonts.smallerMenuFont);
        yesButton.setText(LanguagesManager.getInstance().getString("yes"));
        yesButton.setTouchOffset(0.05f * GraphicsYio.width);
        yesButton.setAction(AcButton.ACTION_YES);
        buttons.add(yesButton);

        noButton = new AcButton(this);
        noButton.setFont(Fonts.smallerMenuFont);
        noButton.setText(LanguagesManager.getInstance().getString("no"));
        noButton.setTouchOffset(0.05f * GraphicsYio.width);
        noButton.setAction(AcButton.ACTION_NO);
        buttons.add(noButton);
    }


    protected void updateButtonMetrics() {
        yesButton.position.set(0, 0, position.width / 2, buttonHeight);
        yesButton.delta.set(position.width / 2, 0);

        noButton.position.set(0, 0, position.width / 2, buttonHeight);
        noButton.delta.set(0, 0);

        for (AcButton button : buttons) {
            button.updateTextDelta();
        }
    }


    protected void initMetrics() {
        leftOffset = 0.08f * GraphicsYio.width;
        lineOffset = 0.09f * GraphicsYio.width;
        titleOffset = 0.16f * GraphicsYio.width;
        topOffset = 0.04f * GraphicsYio.width;
        buttonHeight = 0.1f * GraphicsYio.width;
    }


    protected void initPools() {
        poolLabels = new ObjectPoolYio<AcLabel>() {
            @Override
            public AcLabel makeNewObject() {
                return new AcLabel(AbstractDiplomaticDialog.this);
            }
        };
    }


    @Override
    public void move() {
        moveAppearFactor();
        updateViewPosition();
        moveLabels();
        moveButtons();

        if (factorMoved) {
            updateTagPosition();
        }
    }


    protected void updateTagPosition() {
        //
    }


    protected void moveButtons() {
        if (!areButtonsEnabled()) return;

        for (AcButton button : buttons) {
            if (factorMoved) {
                button.updatePosition();
            }

            if (!touched) {
                button.moveSelection();
            }
        }
    }


    protected void moveLabels() {
        if (factorMoved) {
            for (AcLabel label : labels) {
                label.updatePosition();
            }
        }
    }


    protected void moveAppearFactor() {
        if (appearFactor.hasToMove()) {
            appearFactor.move();
            factorMoved = true;
        } else {
            factorMoved = false;
        }
    }


    protected void updateViewPosition() {
        if (!factorMoved) return;

        viewPosition.setBy(position);
        viewPosition.y -= (1 - appearFactor.get()) * (position.y + position.height + 0.1f * GraphicsYio.width);
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(1, 2.2);
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(3, 1.25);

        onAppear();
    }


    protected void onAppear() {
        resetClickedButton();
        updateAll();
    }


    protected void clearLabels() {
        for (AcLabel label : labels) {
            poolLabels.add(label);
        }

        labels.clear();
    }


    protected void updateAll() {
        clearLabels();
        makeLabels();
    }


    protected abstract void makeLabels();


    protected void addLabel(String text, BitmapFont font, float dx, float dy) {
        AcLabel next = poolLabels.getNext();

        next.setText(text);
        next.setFont(font);
        next.setDelta(dx, dy);

        labels.add(next);
    }


    protected abstract void onYesButtonPressed();


    protected abstract void onNoButtonPressed();


    public abstract boolean areButtonsEnabled();


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        if (clickedButton != null) {
            switch (clickedButton.action) {
                case AcButton.ACTION_YES:
                    onYesButtonPressed();
                    break;
                case AcButton.ACTION_NO:
                    onNoButtonPressed();
                    break;
            }

            clickedButton = null;

            return true;
        }

        return false;
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    public void convertSourceLineToList(String source, ArrayList<String> list) {
        list.clear();

        StringTokenizer tokenizer = new StringTokenizer(source, " ");
        float x = leftOffset;
        float cut = (float) (position.width - 1.8 * leftOffset);
        StringBuilder builder = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            float textWidth = GraphicsYio.getTextWidth(Fonts.smallerMenuFont, token);

            if (x + textWidth > cut) {
                list.add(builder.toString());
                builder.delete(0, builder.length());
                x = leftOffset;
            }

            builder.append(token).append(" ");
            x += textWidth;
        }

        list.add(builder.toString());
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        currentTouch.set(screenX, screenY);

        boolean buttonsClicked = checkToClickButtons();

        if (!buttonsClicked && !viewPosition.isPointInside(currentTouch, 0)) {
            destroy();
        }

        return true;
    }


    boolean checkToClickButtons() {
        if (!areButtonsEnabled()) return false;

        for (AcButton button : buttons) {
            if (button.isTouched(currentTouch)) {
                button.select();
                onButtonClicked(button);
                return true;
            }
        }

        return false;
    }


    public int getTagColor() {
        return tagColor;
    }


    public void setTagColor(int tagColor) {
        this.tagColor = tagColor;
    }


    protected void onButtonClicked(AcButton button) {
        clickedButton = button;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        currentTouch.set(screenX, screenY);

        return true;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        currentTouch.set(screenX, screenY);

        return true;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public boolean isButton() {
        return false;
    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);

        onPositionChanged();
    }


    protected void onPositionChanged() {
        updateButtonMetrics();
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderDiplomaticDialog;
    }
}
