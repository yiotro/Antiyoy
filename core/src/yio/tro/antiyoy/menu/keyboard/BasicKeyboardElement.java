package yio.tro.antiyoy.menu.keyboard;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;

import java.util.ArrayList;

public class BasicKeyboardElement extends InterfaceElement {

    public RectangleYio position, viewPosition;
    public FactorYio appearFactor;
    public ArrayList<KbButton> kbButtons;
    public BitmapFont font;
    ArrayList<String> rowSources;
    PointYio currentTouch;
    public KbTextArea textArea;
    LongTapDetector longTapDetector;
    private boolean touched;
    AbstractKbReaction reaction;
    boolean upperCaseMode;
    public RectangleYio blackoutPosition;
    KbButton lastSelectedButton;


    public BasicKeyboardElement() {
        super(-1);

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        kbButtons = new ArrayList<>();
        font = Fonts.smallerMenuFont;
        currentTouch = new PointYio();
        textArea = new KbTextArea(this);
        reaction = null;
        blackoutPosition = new RectangleYio(0, 0, GraphicsYio.width, GraphicsYio.height);
        lastSelectedButton = null;

        initLongTapDetector();
        initRowSources();
        initKbButtons();
    }


    public void setReaction(AbstractKbReaction reaction) {
        this.reaction = reaction;
    }


    private void initLongTapDetector() {
        longTapDetector = new LongTapDetector() {
            @Override
            public void onLongTapDetected() {
                onLongTap();
            }
        };
    }


    private void onLongTap() {
        KbButton kbButton = findButtonTouchedByCurrentTouch();
        if (kbButton == null) return;

        if (kbButton.key.equals("backspace")) {
            textArea.clear();
            updateUpperCaseMode();
        }
    }


    private void initRowSources() {
        rowSources = new ArrayList<>();
        rowSources.add("1234567890");
        rowSources.add("qwertyuiop");
        rowSources.add("asdfghjkl");
        rowSources.add("zxcvbnm");
    }


    private void initKbButtons() {
        for (String rowSource : rowSources) {
            for (int i = 0; i < rowSource.length(); i++) {
                KbButton button = new KbButton(this);

                button.setKey("" + rowSource.charAt(i));
                button.setValue("" + rowSource.charAt(i));

                kbButtons.add(button);
            }
        }

        KbButton spaceButton = new KbButton(this);
        spaceButton.setKey("space");
        spaceButton.setIcon(true);
        kbButtons.add(spaceButton);

        KbButton backspaceButton = new KbButton(this);
        backspaceButton.setKey("backspace");
        backspaceButton.setIcon(true);
        kbButtons.add(backspaceButton);

        KbButton okButton = new KbButton(this);
        okButton.setKey("ok");
        okButton.setValue("Ok");
        kbButtons.add(okButton);
    }


    @Override
    public void move() {
        moveFactors();
        updateViewPosition();
        moveKbButtons();
        textArea.move();
        longTapDetector.move();
        selectCurrentlyTouchedButton();
    }


    private void selectCurrentlyTouchedButton() {
        if (!touched) return;
        if (lastSelectedButton == null) return;

        lastSelectedButton.select();
    }


    private void moveKbButtons() {
        for (KbButton kbButton : kbButtons) {
            kbButton.move();
        }
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);
        viewPosition.y -= 1.05f * (1 - appearFactor.get()) * position.height;
    }


    private void moveFactors() {
        appearFactor.move();
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(1, 2.5);
        onDestroy();
    }


    private void onDestroy() {
        //
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.001, 0);
        appearFactor.appear(3, 1.6);
        onAppear();
    }


    private void onAppear() {
        updateTextAreaPosition();
        updateButtonDeltas();
        upperCaseMode = false;
        textArea.clear();
        enableUpperCaseMode();
    }


    private void updateTextAreaPosition() {
        double leftDelta = 0.1f * position.width;
        textArea.position.x = position.x + leftDelta;
        textArea.position.width = position.x + position.width - 2 * leftDelta;
        textArea.position.y = position.y + position.height + textArea.mainPartDelta;
        textArea.position.height = 0.1f * GraphicsYio.height;
    }


    private void updateButtonDeltas() {
        double rowHeight = 0.25 * position.height;
        double topRowHeight = 0.2 * position.height;
        double y = position.height - topRowHeight;
        double bw = position.width / 10;

        initRow(rowSources.get(0), 0, y, bw, topRowHeight);
        y -= rowHeight;

        initRow(rowSources.get(1), 0, y, bw, rowHeight);
        y -= rowHeight;

        initRow(rowSources.get(2), (position.width - bw * rowSources.get(2).length()) / 2, y, bw, rowHeight);
        y -= rowHeight;

        initRow(rowSources.get(3), (position.width - bw * rowSources.get(3).length()) / 2, y, bw, rowHeight);
        initSpaceAndBackspace(y, bw, rowHeight);
        initOkButton();
    }


    private void initOkButton() {
        double bw = textArea.position.width / 3;
        double bh = textArea.position.height / 2;

        KbButton okButton = getButton("ok");
        okButton.setSize(bw, bh);
        okButton.setDelta(
                textArea.position.x + textArea.position.width - bw - position.x,
                textArea.position.y - position.y
        );
    }


    private void initSpaceAndBackspace(double y, double bw, double rowHeight) {
        KbButton spaceButton = getButton("space");
        spaceButton.setSize(bw, rowHeight);
        spaceButton.setDelta(0, y);

        KbButton backspaceButton = getButton("backspace");
        backspaceButton.setSize(bw, rowHeight);
        backspaceButton.setDelta(position.width - bw, y);
    }


    private void initRow(String rowSymbols, double sx, double sy, double bw, double bh) {
        float x = (float) sx;
        float y = (float) sy;

        for (int index = 0; index < rowSymbols.length(); index++) {
            String s = rowSymbols.substring(index, index + 1);
            KbButton b = getButton(s);
            b.setSize(bw, bh);
            b.setDelta(x, y);
            x += bw;
        }
    }


    private KbButton getButton(String key) {
        for (KbButton kbButton : kbButtons) {
            if (kbButton.key.equals(key)) {
                return kbButton;
            }
        }

        return null;
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);
        checkToSelectKbButtons();

        touched = isTouched();

        if (touched) {
            longTapDetector.onTouchDown(currentTouch);
        } else {
            destroy();
        }

        return true;
    }


    private boolean isTouched() {
        if (appearFactor.get() < 1) return false;
        if (currentTouch.y < viewPosition.y + viewPosition.height) return true;
        if (textArea.viewPosition.isPointInside(currentTouch, 0)) return true;

        return false;
    }


    private boolean checkToSelectKbButtons() {
        KbButton kbButton = findButtonTouchedByCurrentTouch();
        if (kbButton == null) return false;

        lastSelectedButton = kbButton;
        kbButton.select();
        SoundManagerYio.playSound(SoundManagerYio.soundKeyboardPress);
        onButtonPressed(kbButton);

        return true;
    }


    private KbButton findButtonTouchedByCurrentTouch() {
        for (KbButton kbButton : kbButtons) {
            if (!kbButton.isTouched(currentTouch)) continue;

            return kbButton;
        }

        return null;
    }


    private void onButtonPressed(KbButton kbButton) {
        if (kbButton.key.equals("space")) {
            textArea.addSymbol(" ");
            onInputChanged();
            return;
        }

        if (kbButton.key.equals("backspace")) {
            textArea.removeLastSymbol();
            onInputChanged();
            return;
        }

        if (kbButton.key.equals("ok")) {
            onOkButtonPressed();
            return;
        }

        addButtonSymbol(kbButton);
        onInputChanged();
    }


    private void addButtonSymbol(KbButton kbButton) {
        if (upperCaseMode) {
            textArea.addSymbol(kbButton.key.toUpperCase());
            return;
        }

        textArea.addSymbol(kbButton.key);
    }


    private void onOkButtonPressed() {
        if (reaction != null) {
            reaction.onInputFromKeyboardReceived(textArea.value);
        }

        destroy();
    }


    public boolean onPcKeyPressed(int keycode) {
        String key = Input.Keys.toString(keycode).toLowerCase();

        if (key.equals("enter")) {
            key = "ok";
        }

        if (key.equals("delete")) {
            key = "backspace";
        }

        KbButton button = getButton(key);
        if (button == null) return false;

        button.select();
        onButtonPressed(button);

        return true;
    }


    public void enableUpperCaseMode() {
        if (upperCaseMode) return; // already enabled

        upperCaseMode = true;

        for (String rowSource : rowSources) {
            for (int i = 0; i < rowSource.length(); i++) {
                KbButton button = getButton("" + rowSource.charAt(i));
                button.setValue(button.value.toUpperCase());
            }
        }
    }


    public void disableUpperCaseMode() {
        if (!upperCaseMode) return; // already disabled

        upperCaseMode = false;

        for (String rowSource : rowSources) {
            for (int i = 0; i < rowSource.length(); i++) {
                KbButton button = getButton("" + rowSource.charAt(i));
                button.setValue(button.key);
            }
        }
    }


    public void onInputChanged() {
        updateUpperCaseMode();
    }


    public void updateUpperCaseMode() {
        if (textArea.value.length() == 0) {
            enableUpperCaseMode();
        } else {
            disableUpperCaseMode();
        }
    }


    private void updateCurrentTouch(int screenX, int screenY) {
        currentTouch.set(screenX, screenY);
    }


    public void setValue(String value) {
        textArea.clear();

        for (int i = 0; i < value.length(); i++) {
            textArea.addSymbol(value.substring(i, i + 1));
        }

        onInputChanged();
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        updateCurrentTouch(screenX, screenY);

        if (touched) {
            longTapDetector.onTouchDrag(currentTouch);
        }

        return true;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);

        if (touched) {
            longTapDetector.onTouchUp(currentTouch);
        }

        touched = false;

        return true;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public void setPosition(RectangleYio position) {

    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderBasicKeyboardElement;
    }
}
