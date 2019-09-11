package yio.tro.antiyoy.menu.editor_elements.add_relation;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.gameplay.editor.EditorRelation;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.editor_elements.color_picker.IColorChoiceListener;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

import java.util.ArrayList;

public class AddEditorRelationElement extends InterfaceElement implements IColorChoiceListener {

    MenuControllerYio menuControllerYio;
    public FactorYio appearFactor;
    RectangleYio position;
    public RectangleYio viewPosition;
    public ArrayList<AerItem> items;
    boolean touched;
    PointYio currentTouch;
    AerItem targetItem;


    public AddEditorRelationElement(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;
        appearFactor = new FactorYio();
        position = new RectangleYio();
        viewPosition = new RectangleYio();
        currentTouch = new PointYio();
        touched = false;
        initItems();
    }


    private void initItems() {
        items = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            AerItem aerItem = new AerItem(this);
            items.add(aerItem);
        }

        items.get(0).type = AerType.one;
        items.get(1).type = AerType.relation;
        items.get(2).type = AerType.two;
    }


    @Override
    public void move() {
        appearFactor.move();
        updateViewPosition();
        moveItems();
    }


    private void moveItems() {
        for (AerItem item : items) {
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
        appearFactor.destroy(2, 2);
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(2, 1.7);
        onAppear();
    }


    private void onAppear() {
        touched = false;
        targetItem = null;
        updateItemDeltas();
        loadValues(null);
    }


    public void loadValues(EditorRelation src) {
        if (src == null) {
            setValues(-1, DiplomaticRelation.NEUTRAL, -1);
            return;
        }

        setValues(src.color1, src.relation, src.color2);
    }


    public void setValues(int fraction1, int relation, int fraction2) {
        getItem(AerType.one).setValue(fraction1);
        getItem(AerType.relation).setValue(relation);
        getItem(AerType.two).setValue(fraction2);
    }


    private AerItem getItem(AerType type) {
        for (AerItem item : items) {
            if (item.type != type) continue;
            return item;
        }
        return null;
    }


    private void updateItemDeltas() {
        double delta = 1.2 * position.height;
        float x = (float) (position.width / 2 - delta);
        for (AerItem item : items) {
            item.viewPosition.setRadius(0.45 * position.height);
            item.delta.x = x;
            item.delta.y = (float) (position.height / 2);
            x += delta;
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
        AerItem currentlyTouchedItem = getCurrentlyTouchedItem();
        if (currentlyTouchedItem != null) {
            currentlyTouchedItem.selectionEngineYio.select();
            SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
            onTappedItem(currentlyTouchedItem);
        }
        return touched;
    }


    private void onTappedItem(AerItem currentlyTouchedItem) {
        switch (currentlyTouchedItem.type) {
            case one:
            case two:
                Scenes.sceneColorPicker.create();
                Scenes.sceneColorPicker.setListener(this);
                targetItem = currentlyTouchedItem;
                break;
            case relation:
                switchRelation();
                break;
        }
    }


    private void switchRelation() {
        AerItem relationItem = getItem(AerType.relation);
        if (relationItem.value != DiplomaticRelation.FRIEND) {
            relationItem.setValue(DiplomaticRelation.FRIEND);
        } else {
            relationItem.setValue(DiplomaticRelation.ENEMY);
        }
    }


    private AerItem getCurrentlyTouchedItem() {
        for (AerItem item : items) {
            if (item.viewPosition.center.distanceTo(currentTouch) >= item.viewPosition.radius) continue;
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


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderAddEditorRelationElement;
    }


    public int getFirstColor() {
        return getItem(AerType.one).value;
    }


    public int getSecondColor() {
        return getItem(AerType.two).value;
    }


    public int getRelation() {
        return getItem(AerType.relation).value;
    }


    @Override
    public void onColorChosen(int color) {
        targetItem.setValue(color);
    }
}
