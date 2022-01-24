package yio.tro.antiyoy.menu.context_list_menu;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.KeyboardManager;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

import java.util.ArrayList;

public class ContextListMenuElement extends InterfaceElement{

    public RectangleYio position, viewPosition;
    public FactorYio appearFactor;
    PointYio currentTouch;
    private boolean touched;
    public RectangleYio blackoutPosition;
    LiEditable editableItem;
    public ArrayList<ClmItem> items;
    public BitmapFont font;
    boolean readyToPerform;


    public ContextListMenuElement() {
        super(-1);

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        currentTouch = new PointYio();
        blackoutPosition = new RectangleYio(0, 0, GraphicsYio.width, GraphicsYio.height);
        editableItem = null;
        font = Fonts.smallerMenuFont;
        readyToPerform = false;
        initItems();
    }


    private void initItems() {
        items = new ArrayList<>();

        ClmItem deleteItem = new ClmItem(this);
        deleteItem.setKey("delete");
        deleteItem.setValue(LanguagesManager.getInstance().getString("delete"));
        items.add(deleteItem);

        ClmItem renameItem = new ClmItem(this);
        renameItem.setKey("rename");
        renameItem.setValue(LanguagesManager.getInstance().getString("rename"));
        items.add(renameItem);
    }


    private ClmItem findItem(String key) {
        for (ClmItem item : items) {
            if (!item.key.equals(key)) continue;

            return item;
        }

        return null;
    }


    public void setEditableItem(LiEditable editableItem) {
        this.editableItem = editableItem;
    }


    @Override
    public void move() {
        updateViewPosition();
        appearFactor.move();
        moveItems();
    }


    private void moveItems() {
        for (ClmItem item : items) {
            item.move();
        }
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);
        viewPosition.y -= (1 - appearFactor.get()) * 1.05f * position.height;
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(2, 3);
        onDestroy();
    }


    private void onDestroy() {
        if (editableItem != null) {
            editableItem.onContextMenuDestroy();
        }
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(3, 1.5);
        onAppear();
    }


    private void onAppear() {
        updateItemDeltas();
        readyToPerform = false;
    }


    private void updateItemDeltas() {
        double bw = 0.7 * position.width;
        double bh = 0.08 * GraphicsYio.width;
        double y = 0.08 * GraphicsYio.width;
        double x = position.x + (position.width - bw) / 2;
        double delta = 0.06 * GraphicsYio.width;

        for (ClmItem item : items) {
            item.setSize(bw, bh);
            item.delta.set(x, y);
            y += bh + delta;
        }
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        if (readyToPerform) {
            readyToPerform = false;
            ClmItem item = findItemByCurrentTouch();
            performItem(item);
            destroy();
            return true;
        }

        return false;
    }


    private void performItem(ClmItem item) {
        if (item == null) return;

        if (item.key.equals("delete")) {
            editableItem.onDeleteRequested();
            return;
        }

        if (item.key.equals("rename")) {
            KeyboardManager.getInstance().apply(editableItem.getEditableName(), new AbstractKbReaction() {
                @Override
                public void onInputFromKeyboardReceived(String input) {
                    onRenameInputReceived(input);
                }
            });
            return;
        }
    }


    private void onRenameInputReceived(String input) {
        if (input.length() == 0) return;

        // see SaveSlotSelector.loadSlot() for a reason behind this cycle
        while (input.length() < 3) {
            input = input + " ";
        }

        editableItem.rename(input);
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

        touched = (currentTouch.y < viewPosition.y + viewPosition.height);

        if (touched) {
            checkToSelectItems();
        } else {
            destroy();
        }

        return true;
    }


    private void checkToSelectItems() {
        ClmItem itemByCurrentTouch = findItemByCurrentTouch();
        if (itemByCurrentTouch == null) return;

        readyToPerform = true;
        itemByCurrentTouch.select();
    }


    private ClmItem findItemByCurrentTouch() {
        for (ClmItem item : items) {
            if (!item.isTouched(currentTouch)) continue;

            return item;
        }

        return null;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        updateCurrentTouch(screenX, screenY);

        //

        return true;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);

        //

        return true;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderContextListMenuElement;
    }
}
