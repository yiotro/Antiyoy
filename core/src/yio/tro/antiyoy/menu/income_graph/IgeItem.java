package yio.tro.antiyoy.menu.income_graph;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class IgeItem implements ReusableYio{

    IncomeGraphElement incomeGraphElement;
    public RectangleYio viewPosition;
    PointYio delta;
    public int fraction;
    FactorYio appearFactor;
    FactorYio prepareFactor;
    float targetHeight;
    float maxHeight;
    public RenderableTextYio text;
    public FactorYio borderFactor;
    public RectangleYio borderPosition;


    public IgeItem(IncomeGraphElement incomeGraphElement) {
        this.incomeGraphElement = incomeGraphElement;
        viewPosition = new RectangleYio();
        delta = new PointYio();
        appearFactor = new FactorYio();
        prepareFactor = new FactorYio();
        text = new RenderableTextYio();
        text.setFont(incomeGraphElement.getFont());
        borderFactor = new FactorYio();
        borderPosition = new RectangleYio();
    }


    @Override
    public void reset() {
        viewPosition.reset();
        delta.reset();
        fraction = -1;
        appearFactor.reset();
        targetHeight = 0;
        prepareFactor.reset();
        maxHeight = 0;
        borderFactor.reset();
        borderPosition.reset();

        prepareFactor.appear(1, 2);
    }


    void move() {
        movePrepareFactor();
        appearFactor.move();
        updateViewPosition();
        moveText();
        moveBorder();
        updateBorderPosition();
    }


    private void updateBorderPosition() {
        borderPosition.setBy(viewPosition);
        borderPosition.increase(GraphicsYio.borderThickness / 2);
    }


    public void onDestroy() {
        borderFactor.destroy(1, 3);
    }


    private void moveBorder() {
        borderFactor.move();
        if (borderFactor.get() > 0) return;
        if (appearFactor.get() <= 0.01 || appearFactor.getGravity() <= 0) return;
        borderFactor.setValues(0.01, 0);
        borderFactor.appear(3, 1);
    }


    private void moveText() {
        text.centerHorizontal(viewPosition);
        text.position.y = (float) (incomeGraphElement.viewPosition.y + incomeGraphElement.getLowerGap() / 2 + text.height / 2);
        text.updateBounds();
    }


    private void movePrepareFactor() {
        if (!prepareFactor.hasToMove()) return;
        prepareFactor.move();

        if (!prepareFactor.hasToMove()) {
            appearFactor.appear(2, 1.4);
        }
    }


    private void updateViewPosition() {
        viewPosition.x = incomeGraphElement.columnsArea.x + delta.x;
        viewPosition.y = incomeGraphElement.columnsArea.y + delta.y;
        viewPosition.height = Math.min(targetHeight, appearFactor.get() * maxHeight);
    }


    public void setTargetHeight(float targetHeight) {
        this.targetHeight = targetHeight;
    }


    public void setFraction(int fraction) {
        this.fraction = fraction;
    }


    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
    }
}
