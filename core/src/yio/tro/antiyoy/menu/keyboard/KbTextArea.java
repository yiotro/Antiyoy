package yio.tro.antiyoy.menu.keyboard;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class KbTextArea {


    BasicKeyboardElement basicKeyboardElement;
    public RectangleYio position, viewPosition;
    private float f;
    public PointYio textPosition;
    public String value;
    float leftDelta, topDelta;
    float textWidth;
    public PointYio caretPosition;
    public PointYio caretEndPos;
    float caretHeight;
    public FactorYio caretFactor;
    public float mainPartDelta;


    public KbTextArea(BasicKeyboardElement basicKeyboardElement) {
        this.basicKeyboardElement = basicKeyboardElement;

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        textPosition = new PointYio();
        value = "";
        leftDelta = 0.04f * GraphicsYio.width;
        topDelta = 0.02f * GraphicsYio.width;
        caretHeight = 0.025f * GraphicsYio.height;
        mainPartDelta = 0.15f * GraphicsYio.height;
        caretPosition = new PointYio();
        caretEndPos = new PointYio();
        caretFactor = new FactorYio();
    }


    void clear() {
        value = "";
        updateTextWidth();
    }


    void addSymbol(String symbol) {
        value = value + symbol;
        updateTextWidth();
        checkToCutSymbol();
    }


    private void checkToCutSymbol() {
        updateCaret();
        updateViewPosition();
        if (caretPosition.x < viewPosition.x + viewPosition.width - 0.05f * GraphicsYio.width) return;

        removeLastSymbol();
    }


    private void updateTextWidth() {
        textWidth = GraphicsYio.getTextWidth(basicKeyboardElement.font, value);
    }


    void removeLastSymbol() {
        if (value.length() == 0) return;

        value = value.substring(0, value.length() - 1);
        updateTextWidth();
    }


    void move() {
        updateViewPosition();
        updateTextPosition();
        updateCaret();
        moveCaretFactor();
    }


    private void moveCaretFactor() {
        caretFactor.move();

        if (caretFactor.get() == 0) {
            caretFactor.setValues(1, 0);
            caretFactor.destroy(1, 0.2);
        }
    }


    private void updateCaret() {
        caretPosition.x = textPosition.x + textWidth + 0.005f * GraphicsYio.width;
        caretPosition.y = textPosition.y;

        caretEndPos.setBy(caretPosition);
        caretEndPos.y -= caretHeight;
    }


    private void updateTextPosition() {
        textPosition.x = (float) (viewPosition.x + leftDelta);
        textPosition.y = (float) (viewPosition.y + viewPosition.height - topDelta);
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);
        viewPosition.y = basicKeyboardElement.viewPosition.y + basicKeyboardElement.viewPosition.height + mainPartDelta;
    }
}
