package yio.tro.antiyoy.menu.editor_elements.edit_land;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.editor.LeInputMode;
import yio.tro.antiyoy.gameplay.editor.LevelEditorManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class EditLandElement extends InterfaceElement{

    MenuControllerYio menuControllerYio;
    public FactorYio appearFactor;
    RectangleYio position;
    public RectangleYio viewPosition;
    boolean touched;
    PointYio currentTouch;
    public ArrayList<EleItem> items;
    EleItem targetItem;
    boolean readyToApply;


    public EditLandElement(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;
        appearFactor = new FactorYio();
        position = new RectangleYio();
        viewPosition = new RectangleYio();
        currentTouch = new PointYio();
        initItems();
    }


    private void initItems() {
        items = new ArrayList<>();
        for (int i = 0; i < GameRules.MAX_FRACTIONS_QUANTITY; i++) {
            addItem(EleActionType.def, i);
        }
        addItem(EleActionType.random, -1);
        addItem(EleActionType.delete, -1);
    }


    private void addItem(EleActionType actionType, int value) {
        EleItem eleItem = new EleItem(this);
        eleItem.setActionType(actionType);
        eleItem.setValue(value);
        items.add(eleItem);
    }


    @Override
    public void move() {
        appearFactor.move();
        updateViewPosition();
        moveItems();
    }


    private void moveItems() {
        for (EleItem item : items) {
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
        appearFactor.appear(2, 1.6);
        onAppear();
    }


    private void onAppear() {
        touched = false;
        readyToApply = false;
        targetItem = null;
        updateItemDeltas();
        move();
    }


    public EleItem getItem(EleActionType eleActionType) {
        for (EleItem item : items) {
            if (item.actionType != eleActionType) continue;
            return item;
        }
        return null;
    }


    public EleItem getItem(int value) {
        for (EleItem item : items) {
            if (item.value != value) continue;
            return item;
        }
        return null;
    }


    private void updateItemDeltas() {
        for (EleItem item : items) {
            item.touchPosition.setRadius(position.width / 16);
            item.viewPosition.setRadius(0.8 * item.touchPosition.radius);
        }

        for (int i = 0; i < 6; i++) {
            alignLeft(getItem(i), i, 1);
            alignLeft(getItem(i + 6), i, 0);
        }

        alignRight(getItem(EleActionType.delete), 0, 1);
        alignRight(getItem(EleActionType.random), 0, 0);
    }


    private void alignLeft(EleItem eleItem, int x, int y) {
        if (eleItem == null) return;
        float stepX = (float) (position.width / 8);
        float stepY = (float) (position.height / 2);
        eleItem.delta.x = stepX / 2 + stepX * x;
        eleItem.delta.y = stepY / 2 + stepY * y;
    }


    private void alignRight(EleItem eleItem, int x, int y) {
        float stepX = (float) (position.width / 8);
        float stepY = (float) (position.height / 2);
        eleItem.delta.x = (float) (position.width - stepX / 2 - stepX * x);
        eleItem.delta.y = stepY / 2 + stepY * y;
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        if (readyToApply) {
            readyToApply = false;
            applyTargetItem();
            return true;
        }
        return false;
    }


    private void applyTargetItem() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        LevelEditorManager levelEditorManager = gameController.levelEditorManager;

        switch (targetItem.actionType) {
            case delete:
                levelEditorManager.setInputMode(LeInputMode.delete);
                break;
            case random:
                levelEditorManager.setInputMode(LeInputMode.set_hex);
                levelEditorManager.setRandomizeFraction(true);
                break;
            case def:
                levelEditorManager.setInputMode(LeInputMode.set_hex);
                levelEditorManager.setInputFraction(targetItem.value);
                break;
        }

        Scenes.sceneEditorHexPanel.hide();
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
        EleItem currentlyTouchedItem = getCurrentlyTouchedItem();
        if (currentlyTouchedItem != null) {
            readyToApply = true;
            targetItem = currentlyTouchedItem;
            currentlyTouchedItem.selectionEngineYio.select();
            SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
        }
        return touched;
    }


    private EleItem getCurrentlyTouchedItem() {
        for (EleItem item : items) {
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


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderEditLandElement;
    }
}
