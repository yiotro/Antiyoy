package yio.tro.antiyoy.menu.editor_elements.color_picker;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.CircleYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class ColorPickerElement extends InterfaceElement{

    MenuControllerYio menuControllerYio;
    public FactorYio appearFactor;
    RectangleYio position;
    public RectangleYio viewPosition;
    IColorChoiceListener listener;
    public ArrayList<CpeItem> items;
    boolean touched;
    PointYio currentTouch;
    public RectangleYio shadowPosition;
    public FactorYio fillFactor;
    public RectangleYio fillPosition;
    public int chosenColor;


    public ColorPickerElement(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;
        appearFactor = new FactorYio();
        position = new RectangleYio();
        viewPosition = new RectangleYio();
        listener = null;
        currentTouch = new PointYio();
        shadowPosition = new RectangleYio();
        fillFactor = new FactorYio();
        fillPosition = new RectangleYio();
        initItems();
    }


    private void initItems() {
        items = new ArrayList<>();
        for (int fraction = 0; fraction < GameRules.MAX_FRACTIONS_QUANTITY; fraction++) {
            if (fraction == GameRules.NEUTRAL_FRACTION) continue;
            CpeItem item = new CpeItem(this);
            item.color = fraction;
            items.add(item);
        }
    }


    @Override
    public void move() {
        appearFactor.move();
        updateViewPosition();
        moveItems();
        updateShadowPosition();
        moveFill();
    }


    private void moveFill() {
        fillFactor.move();
        if (fillFactor.get() == 0) return;

        CpeItem item = getItem(chosenColor);
        if (item == null) return;

        CircleYio pos = item.viewPosition;
        fillPosition.setBy(pos);
        fillPosition.x += fillFactor.get() * (viewPosition.x - (pos.center.x - pos.radius));
        fillPosition.y += fillFactor.get() * (viewPosition.y - (pos.center.y - pos.radius));
        fillPosition.width += fillFactor.get() * (viewPosition.width - 2 * pos.radius);
        fillPosition.height += fillFactor.get() * (viewPosition.height - 2 * pos.radius);
    }


    private void updateShadowPosition() {
        shadowPosition.x = 0;
        shadowPosition.width = GraphicsYio.width;
        shadowPosition.y = viewPosition.y + viewPosition.height - 0.04f * GraphicsYio.width;
        shadowPosition.height = 0.06f * GraphicsYio.width;
    }


    private void moveItems() {
        for (CpeItem item : items) {
            item.move();
        }
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
        appearFactor.destroy(2, 1.3);
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(2, 2);
        onAppear();
    }


    private void onAppear() {
        touched = false;
        chosenColor = -1;
        fillFactor.reset();
        removeRandomColorItem();
        updateItemDeltas();
        move();
    }


    private void removeRandomColorItem() {
        CpeItem rcItem = getItem(-1);
        if (rcItem == null) return;
        items.remove(rcItem);
    }


    public void addRandomColorItem() {
        CpeItem item = new CpeItem(this);
        item.color = -1;
        items.add(item);

        updateItemDeltas();
        move();
    }


    public CpeItem getItem(int color) {
        for (CpeItem item : items) {
            if (item.color != color) continue;
            return item;
        }
        return null;
    }


    private void updateItemDeltas() {
        int rowQuantity = 8;
        float radius = (float) (0.5 * position.width / (rowQuantity + 1));
        float delta = (float) (position.width / rowQuantity);
        float x = delta;
        float y = (float) (position.height - delta);
        for (CpeItem cpeItem : items) {
            cpeItem.viewPosition.setRadius(radius);
            cpeItem.delta.set(x, y);
            x += delta;
            if (x > 0.99 * position.width) {
                x = delta;
                y -= delta;
            }
        }
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
        touched = viewPosition.isPointInside(currentTouch);
        CpeItem currentlyTouchedItem = getCurrentlyTouchedItem();
        if (currentlyTouchedItem != null) {
            currentlyTouchedItem.selectionEngineYio.select();
            SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
            onTappedItem(currentlyTouchedItem);
        }
        return touched;
    }


    private void onTappedItem(CpeItem cpeItem) {
        if (listener != null) {
            listener.onColorChosen(cpeItem.color);
        }
        fillFactor.reset();
        fillFactor.appear(2, 2);
        chosenColor = cpeItem.color;
        Scenes.sceneColorPicker.hide();
    }


    private CpeItem getCurrentlyTouchedItem() {
        for (CpeItem item : items) {
            if (!item.isTouchedBy(currentTouch)) continue;
            return item;
        }
        return null;
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


    public void setListener(IColorChoiceListener listener) {
        this.listener = listener;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderColorPickerElement;
    }
}
