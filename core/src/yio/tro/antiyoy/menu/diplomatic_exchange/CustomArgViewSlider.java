package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.stuff.*;

public class CustomArgViewSlider {

    ExchangeProfitView exchangeProfitView;
    public RectangleYio position;
    float verticalDelta;
    public CavsMode mode;
    public int values[];
    public PointYio leftLinePoint;
    public PointYio rightLinePoint;
    public RenderableTextYio title;
    public RenderableTextYio tag;
    public int valueIndex;
    public CircleYio accentPosition;
    boolean currentlyTouched;


    public CustomArgViewSlider(ExchangeProfitView exchangeProfitView) {
        this.exchangeProfitView = exchangeProfitView;

        position = new RectangleYio();
        verticalDelta = 0;
        position.width = 0.6f * GraphicsYio.width;
        position.height = 0.08f * GraphicsYio.height;
        mode = CavsMode.def;
        values = null;
        leftLinePoint = new PointYio();
        rightLinePoint = new PointYio();
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        tag = new RenderableTextYio();
        tag.setFont(Fonts.smallerMenuFont);
        valueIndex = -1;
        accentPosition = new CircleYio();
        accentPosition.setRadius(3 * GraphicsYio.borderThickness);
        currentlyTouched = false;
    }


    void move() {
        updatePosition();
        updateLine();
        moveTitle();
        moveTag();
        moveAccent();
    }


    private void moveAccent() {
        accentPosition.center.y = leftLinePoint.y;
        float f = (float) valueIndex / (values.length - 1);
        accentPosition.center.x = leftLinePoint.x + f * (rightLinePoint.x - leftLinePoint.x);
    }


    void onValueIndexChanged() {
        updateTag();
    }


    public void setValueIndex(int valueIndex) {
        if (this.valueIndex == valueIndex) return;
        this.valueIndex = valueIndex;
        onValueIndexChanged();
    }


    public void setIndexByActualValue(int actualValue) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] < actualValue) continue;
            setValueIndex(i);
            break;
        }
    }


    private void updateTag() {
        tag.setString(getTagString());
        tag.updateMetrics();
    }


    private String getTagString() {
        int actualValue = getActualValue();
        switch (mode) {
            case def:
            default:
                return "" + actualValue;
            case money:
                return "$" + actualValue;
            case duration:
                return actualValue + "x";
        }
    }


    public int getActualValue() {
        return values[valueIndex];
    }


    private void moveTag() {
        tag.position.x = (float) (position.x + position.width - tag.width);
        tag.position.y = rightLinePoint.y + 0.015f * GraphicsYio.height + tag.height;
        tag.updateBounds();
    }


    private void moveTitle() {
        title.position.x = (float) position.x;
        title.position.y = leftLinePoint.y + 0.02f * GraphicsYio.height + title.height;
        title.updateBounds();
    }


    public void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    private void updateLine() {
        leftLinePoint.set(position.x, position.y + 0.2 * position.height);
        rightLinePoint.set(position.x + position.width, position.y + 0.2 * position.height);
    }


    private void updatePosition() {
        position.x = GraphicsYio.width / 2 - position.width / 2;
        position.y = exchangeProfitView.position.y + verticalDelta;
    }


    void applyTouch(PointYio touchPoint) {
        float length = rightLinePoint.x - leftLinePoint.x;
        float v = touchPoint.x - leftLinePoint.x;
        float step = length / values.length;
        int index = (int) ((v - 0.5 * step) / step);

        if (index < 0) {
            index = 0;
        }
        if (index > values.length - 1) {
            index = values.length - 1;
        }

        setValueIndex(index);
    }


    boolean isTouchedBy(PointYio touchPoint) {
        return position.isPointInside(touchPoint);
    }


    void onTouchDown(PointYio touchPoint) {
        currentlyTouched = isTouchedBy(touchPoint);
        if (!currentlyTouched) return;

        applyTouch(touchPoint);
    }


    void onTouchDrag(PointYio touchPoint) {
        if (!currentlyTouched) return;

        applyTouch(touchPoint);
    }


    void onTouchUp(PointYio touchPoint) {
        currentlyTouched = false;
    }


    public void setValues(int[] values) {
        this.values = values;
    }


    public void setMode(CavsMode mode) {
        this.mode = mode;
    }
}
