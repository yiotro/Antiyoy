package yio.tro.antiyoy.menu.income_graph;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class IncomeGraphElement extends InterfaceElement{

    MenuControllerYio menuControllerYio;
    public FactorYio appearFactor;
    RectangleYio position;
    public RectangleYio viewPosition;
    boolean touched;
    PointYio currentTouch;
    public RenderableTextYio title;
    public RectangleYio columnsArea;
    public RectangleYio separatorPosition;
    public ArrayList<IgeItem> items;
    ObjectPoolYio<IgeItem> poolItems;
    private int[] incomeArray;
    RepeatYio<IncomeGraphElement> repeatUpdateColumns;


    public IncomeGraphElement(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;
        appearFactor = new FactorYio();
        position = new RectangleYio();
        viewPosition = new RectangleYio();
        currentTouch = new PointYio();
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        title.setString(LanguagesManager.getInstance().getString("income"));
        title.updateMetrics();
        columnsArea = new RectangleYio();
        separatorPosition = new RectangleYio();
        items = new ArrayList<>();
        initPools();
        initRepeats();
    }


    private void initRepeats() {
        repeatUpdateColumns = new RepeatYio<IncomeGraphElement>(this, 10) {
            @Override
            public void performAction() {
                parent.updateColumnsDynamically();
            }
        };
    }


    private void initPools() {
        poolItems = new ObjectPoolYio<IgeItem>(items) {
            @Override
            public IgeItem makeNewObject() {
                return new IgeItem(IncomeGraphElement.this);
            }
        };
    }


    @Override
    public void move() {
        appearFactor.move();
        updateViewPosition();
        moveTitle();
        checkToMoveColumnsInRealTime();
        updateColumnsArea();
        updateSeparatorPosition();
        moveItems();
    }


    private void checkToMoveColumnsInRealTime() {
        if (!GameRules.aiOnlyMode) return;
        GameController gameController = menuControllerYio.getYioGdxGame().gameController;
        if (gameController.speedManager.getSpeed() == 0) return;

        repeatUpdateColumns.move();
    }


    private void updateColumnsDynamically() {
        updateIncomeArray();
        updateItemDeltas();
    }


    private void moveItems() {
        for (IgeItem item : items) {
            item.move();
        }
    }


    private void updateSeparatorPosition() {
        separatorPosition.setBy(columnsArea);
        separatorPosition.height = GraphicsYio.borderThickness;
    }


    private void updateColumnsArea() {
        columnsArea.width = 0.9 * position.width;
        columnsArea.x = viewPosition.x + viewPosition.width / 2 - columnsArea.width / 2;
        columnsArea.height = 0.72 * position.height;
        columnsArea.y = viewPosition.y + viewPosition.height / 2 - columnsArea.height / 2;
    }


    private void moveTitle() {
        title.centerHorizontal(viewPosition);
        title.position.y = (float) (viewPosition.y + viewPosition.height - 0.01f * GraphicsYio.height);
        title.updateBounds();
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);
        viewPosition.y -= (1 - appearFactor.get()) * (position.y + position.height + GraphicsYio.borderThickness);
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(2, 2);
        for (IgeItem item : items) {
            item.onDestroy();
        }
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(2, 2);
        onAppear();
    }


    private void onAppear() {
        touched = false;
        loadValues();
        move();
    }


    private void loadValues() {
        updateIncomeArray();
        createItemsList();
        updateItemDeltas();
    }


    private void updateItemDeltas() {
        updateColumnsArea();
        int maxIncomeValue = getMaxIncomeValue();
        float deltaX = (float) (columnsArea.width / (items.size() + 1));
        float x = deltaX / 2;
        float y = 2 * GraphicsYio.borderThickness;
        float cw = Math.min(0.09f * GraphicsYio.width, 0.9f * deltaX);
        float maxHeight = (float) (columnsArea.height - 4 * GraphicsYio.borderThickness);
        for (IgeItem item : items) {
            item.delta.x = x;
            item.delta.y = y;
            item.targetPosition.width = cw;
            item.setMaxHeight(maxHeight);
            item.setTargetHeight(((float)incomeArray[item.fraction] / maxIncomeValue) * maxHeight);
            item.text.setString(Yio.getCompactMoneyString(incomeArray[item.fraction]));
            item.text.updateMetrics();
            item.updateScoutedState();
            x += deltaX;
        }
    }


    float getLowerGap() {
        return (float) (columnsArea.y - viewPosition.y);
    }


    private int getMaxIncomeValue() {
        int maxValue = -1;
        for (int i = 0; i < incomeArray.length; i++) {
            if (maxValue == -1 || incomeArray[i] > maxValue) {
                maxValue = incomeArray[i];
            }
        }
        return maxValue;
    }


    private void createItemsList() {
        poolItems.clearExternalList();
        for (int fraction = 0; fraction < incomeArray.length; fraction++) {
            if (fraction == GameRules.NEUTRAL_FRACTION) continue;
            IgeItem freshObject = poolItems.getFreshObject();
            freshObject.setFraction(fraction);
        }
    }


    private void updateIncomeArray() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        incomeArray = gameController.fieldManager.getIncomeArray();
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


    private void updateCurrentTouch(int screenX, int screenY) {
        currentTouch.set(screenX, screenY);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);
//        touched = viewPosition.isPointInside(currentTouch);
        touched = false;
        return touched;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        if (!touched) return false;
        updateCurrentTouch(screenX, screenY);
        return true;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!touched) return false;
        updateCurrentTouch(screenX, screenY);
        touched = false;
        return true;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);
    }


    public BitmapFont getFont() {
        return Fonts.microFont;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderIncomeGraphElement;
    }
}
