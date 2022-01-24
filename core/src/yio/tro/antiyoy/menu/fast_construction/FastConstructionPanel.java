package yio.tro.antiyoy.menu.fast_construction;

import com.badlogic.gdx.Input;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Obj;
import yio.tro.antiyoy.gameplay.SelectionTipType;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LongTapDetector;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class FastConstructionPanel extends InterfaceElement {

    MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    FactorYio appearFactor;
    private float height;
    PointYio currentTouch;
    private float itemTouchOffset;
    public ArrayList<FcpItem> items;
    LongTapDetector longTapDetector;
    FcpItem bufferItem;
    boolean touched;


    public FastConstructionPanel(MenuControllerYio menuControllerYio, int id) {
        super(id);
        this.menuControllerYio = menuControllerYio;

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        currentTouch = new PointYio();
        bufferItem = null;

        initMetrics();
        initPosition();
        initItems();
        initLongTapDetector();
    }


    private void initLongTapDetector() {
        longTapDetector = new LongTapDetector() {
            @Override
            public void onLongTapDetected() {
                FastConstructionPanel.this.onLongTapDetected();
            }
        };
    }


    void onLongTapDetected() {
        if (isInSimpleEndTurnMode()) return;
        if (bufferItem == null) return;
        if (bufferItem.actionType != FcpActionType.end_turn) return;
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        SoundManagerYio.playSound(SoundManagerYio.soundEndTurn);
        gameController.onEndTurnButtonPressed();
    }


    private void initItems() {
        itemTouchOffset = 0.05f * GraphicsYio.width;
        items = new ArrayList<>();

        addItem(FcpActionType.unit_1);
        addItem(FcpActionType.unit_2);
        addItem(FcpActionType.unit_3);
        addItem(FcpActionType.unit_4);
        addItem(FcpActionType.farm);
        addItem(FcpActionType.tower);
        addItem(FcpActionType.strong_tower);
        addItem(FcpActionType.undo);
        addItem(FcpActionType.end_turn);
        addItem(FcpActionType.diplomacy);
        addItem(FcpActionType.log);

        for (FcpItem spItem : items) {
            spItem.setRadius(0.37f * height);
            spItem.setTouchOffset(itemTouchOffset);
        }
    }


    private void updateTouchDeltas() {
        for (FcpItem item : items) {
            item.touchDelta.set(0, 0);
        }

        if (!GameRules.slayRules) {
            FcpItem farmItem = getItemByAction(FcpActionType.farm);
            farmItem.touchDelta.x = 0.5f * farmItem.radius;

            FcpItem towerItem = getItemByAction(FcpActionType.tower);
            towerItem.touchDelta.x = 0.3f * towerItem.radius;
        }
    }


    private void addItem(FcpActionType actionType) {
        FcpItem fcpItem = new FcpItem(this);
        fcpItem.setActionType(actionType);
        items.add(fcpItem);
    }


    private void initMetrics() {
        height = 0.08f * GraphicsYio.height;
    }


    private void initPosition() {
        position.x = 0;
        position.y = 0;
        position.width = GraphicsYio.width;
        position.height = height;
    }


    @Override
    public void move() {
        appearFactor.move();

        updateViewPosition();
        moveItems();
    }


    private void moveItems() {
        for (FcpItem item : items) {
            item.move();
        }
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);
        viewPosition.y -= (1 - appearFactor.get()) * position.height;
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
        if (appearFactor.get() == 1) {
            appearFactor.appear(3, 2);
            return;
        }

        appearFactor.setValues(0.01, 0);
        appearFactor.appear(3, 2);

        onAppear();
    }


    private void onAppear() {
        rearrangeItems();
        updateTouchDeltas();
        touched = false;
    }


    private void rearrangeItems() {
        for (FcpItem item : items) {
            item.visible = false;
        }

        if (GameRules.slayRules) {
            rearrangeBySlayRules();
        } else {
            rearrangeByNormalRules();
        }

        rearrangeSpecialItems();
    }


    private void rearrangeSpecialItems() {
        FcpItem undoItem = getItemByAction(FcpActionType.undo);
        undoItem.visible = true;
        undoItem.delta.x = getSpecialItemHorPos();
        undoItem.delta.y = 2.2f * height;
        undoItem.animDelta.set(getSpecialItemAnimDelta(), height);

        FcpItem endTurnItem = getItemByAction(FcpActionType.end_turn);
        endTurnItem.visible = true;
        endTurnItem.delta.x = getSpecialItemHorPos();
        endTurnItem.delta.y = 3.5f * height;
        endTurnItem.animDelta.set(getSpecialItemAnimDelta(), height);

        FcpItem diplomacyItem = getItemByAction(FcpActionType.diplomacy);
        diplomacyItem.visible = GameRules.diplomacyEnabled;
        diplomacyItem.delta.x = getSpecialItemHorPos();
        diplomacyItem.delta.y = 4.8f * height;
        diplomacyItem.animDelta.set(getSpecialItemAnimDelta(), height);

        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        if (diplomacyManager.log.hasSomethingToRead()) {
            FcpItem logItem = getItemByAction(FcpActionType.log);
            logItem.visible = GameRules.diplomacyEnabled;
            logItem.delta.x = getSpecialItemHorPos();
            logItem.delta.y = 6.1f * height;
            logItem.animDelta.set(getSpecialItemAnimDelta(), height);
        }
    }


    private float getSpecialItemAnimDelta() {
        if (SettingsManager.leftHandMode) {
            return itemTouchOffset;
        } else {
            return -itemTouchOffset;
        }
    }


    private float getSpecialItemHorPos() {
        if (SettingsManager.leftHandMode) {
            return GraphicsYio.width - itemTouchOffset;
        } else {
            return itemTouchOffset;
        }
    }


    private void rearrangeByNormalRules() {
        placeItem(FcpActionType.strong_tower, 0, false);
        placeItem(FcpActionType.tower, 1, false);
        placeItem(FcpActionType.farm, 2, false);

        placeItem(FcpActionType.unit_1, 3, true);
        placeItem(FcpActionType.unit_2, 2, true);
        placeItem(FcpActionType.unit_3, 1, true);
        placeItem(FcpActionType.unit_4, 0, true);
    }


    private void rearrangeBySlayRules() {
        placeItem(FcpActionType.tower, 0, false);

        placeItem(FcpActionType.unit_1, 3, true);
        placeItem(FcpActionType.unit_2, 2, true);
        placeItem(FcpActionType.unit_3, 1, true);
        placeItem(FcpActionType.unit_4, 0, true);
    }


    private void placeItem(FcpActionType actionType, int place, boolean alignRight) {
        FcpItem itemByAction = getItemByAction(actionType);
        if (itemByAction == null) return;

        itemByAction.visible = true;
        if (alignRight) {
            itemByAction.delta.x = (float) (position.width - (1.4 * itemTouchOffset + place * (2.5f * itemTouchOffset)));
        } else {
            itemByAction.delta.x = 1.4f * itemTouchOffset + place * (2.5f * itemTouchOffset);
        }
        itemByAction.delta.y = height / 2;
    }


    private FcpItem getItemByAction(FcpActionType actionType) {
        for (FcpItem item : items) {
            if (item.actionType != actionType) continue;
            return item;
        }

        return null;
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        longTapDetector.move();
        return false;
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        currentTouch.set(screenX, screenY);
        longTapDetector.onTouchDown(currentTouch);
        bufferItem = null;
        touched = true;

        return checkToClickItems();
    }


    private boolean checkToClickItems() {
        FcpItem closestItem = getClosestItem(currentTouch);
        if (closestItem != null && closestItem.isTouched(currentTouch)) {
            closestItem.select();
            onItemClicked(closestItem);
            return true;
        }

        return false;
    }


    private FcpItem getClosestItem(PointYio touchPoint) {
        FcpItem closestItem = null;
        double minDistance = 0;
        double currentDistance;

        for (FcpItem item : items) {
            if (!item.isVisible()) continue;

            currentDistance = touchPoint.distanceTo(item.position);
            if (closestItem == null || currentDistance < minDistance) {
                closestItem = item;
                minDistance = currentDistance;
            }
        }

        return closestItem;
    }


    private void onItemClicked(FcpItem item) {
        if (!menuControllerYio.yioGdxGame.gameController.fieldManager.isSomethingSelected()) return;

        SoundManagerYio.playSound(SoundManagerYio.soundPressButton);

        switch (item.actionType) {
            case unit_1:
                applyBuildUnit(1);
                break;
            case unit_2:
                applyBuildUnit(2);
                break;
            case unit_3:
                applyBuildUnit(3);
                break;
            case unit_4:
                applyBuildUnit(4);
                break;
            case farm:
                applyBuildSolidObject(Obj.FARM);
                break;
            case tower:
                applyBuildSolidObject(Obj.TOWER);
                break;
            case strong_tower:
                applyBuildSolidObject(Obj.STRONG_TOWER);
                break;
            case undo:
                applyUndoAction();
                break;
            case end_turn:
                applyEndTurn(item);
                break;
            case diplomacy:
                applyOpenDiplomacy();
                break;
            case log:
                applyOpenDiplomaticLog();
                break;
        }
    }


    private void applyOpenDiplomaticLog() {
        menuControllerYio.yioGdxGame.gameController.fieldManager.diplomacyManager.onDiplomaticLogButtonPressed();
    }


    private void applyOpenDiplomacy() {
        menuControllerYio.yioGdxGame.gameController.fieldManager.diplomacyManager.onDiplomacyButtonPressed();
    }


    private void applyEndTurn(FcpItem item) {
        if (!isInSimpleEndTurnMode()) {
            bufferItem = item;
            return;
        }
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        gameController.onEndTurnButtonPressed();
    }


    private boolean isInSimpleEndTurnMode() {
        if (GameRules.tutorialMode) return true;
        return !SettingsManager.cautiosEndTurnEnabled;
    }


    private void applyUndoAction() {
        menuControllerYio.yioGdxGame.gameController.undoAction();
    }


    private void applyBuildUnit(int strength) {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;

        int tipType = -1;
        switch (strength) {
            case 1:
                tipType = SelectionTipType.UNIT_1;
                break;
            case 2:
                tipType = SelectionTipType.UNIT_2;
                break;
            case 3:
                tipType = SelectionTipType.UNIT_3;
                break;
            case 4:
                tipType = SelectionTipType.UNIT_4;
                break;
        }

        gameController.selectionManager.awakeTip(tipType);
        gameController.detectAndShowMoveZoneForBuildingUnit(tipType);
    }


    private void applyBuildSolidObject(int type) {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;

        int tipType = -1;
        switch (type) {
            case Obj.FARM:
                tipType = SelectionTipType.FARM;
                break;
            case Obj.TOWER:
                tipType = SelectionTipType.TOWER;
                break;
            case Obj.STRONG_TOWER:
                tipType = SelectionTipType.STRONG_TOWER;
                break;
        }

        gameController.selectionManager.awakeTip(tipType);
        if (tipType == SelectionTipType.FARM) {
            gameController.detectAndShowMoveZoneForFarm();
        }
    }


    public void onKeyPressed(int keycode) {
        if (!menuControllerYio.yioGdxGame.gameController.selectionManager.isSomethingSelected()) return;

        switch (keycode) {
            case Input.Keys.NUM_1:
                applyBuildUnit(1);
                break;
            case Input.Keys.NUM_2:
                applyBuildSolidObject(Obj.FARM);
                break;
        }
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        longTapDetector.onTouchDrag(currentTouch);
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        longTapDetector.onTouchUp(currentTouch);
        touched = false;
        return false;
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
        return MenuRender.renderFastConstructionPanel;
    }
}
