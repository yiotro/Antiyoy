package yio.tro.antiyoy.menu.fast_construction;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Obj;
import yio.tro.antiyoy.gameplay.SelectionController;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class FastConstructionPanel extends InterfaceElement{

    MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    FactorYio appearFactor;
    private float height;
    PointYio currentTouch;
    private float itemTouchOffset;
    public ArrayList<FcpItem> items;


    public FastConstructionPanel(MenuControllerYio menuControllerYio, int id) {
        super(id);
        this.menuControllerYio = menuControllerYio;

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        currentTouch = new PointYio();

        initMetrics();
        initPosition();
        initItems();
    }


    private void initItems() {
        itemTouchOffset = 0.05f * GraphicsYio.width;
        items = new ArrayList<>();

        addItem(FcpItem.ACTION_UNIT_1);
        addItem(FcpItem.ACTION_UNIT_2);
        addItem(FcpItem.ACTION_UNIT_3);
        addItem(FcpItem.ACTION_UNIT_4);
        addItem(FcpItem.ACTION_FARM);
        addItem(FcpItem.ACTION_TOWER);
        addItem(FcpItem.ACTION_STRONG_TOWER);

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
            FcpItem farmItem = getItemByAction(FcpItem.ACTION_FARM);
            farmItem.touchDelta.x = 0.5f * farmItem.radius;

            FcpItem towerItem = getItemByAction(FcpItem.ACTION_TOWER);
            towerItem.touchDelta.x = 0.3f * towerItem.radius;
        }
    }


    private void addItem(int action) {
        FcpItem fcpItem = new FcpItem(this);
        fcpItem.setAction(action);
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
        appearFactor.beginDestroying(2, 2);
    }


    @Override
    public void appear() {
        if (appearFactor.get() == 1) return;

        appearFactor.setValues(0.01, 0);
        appearFactor.beginSpawning(3, 2);

        onAppear();
    }


    private void onAppear() {
        rearrangeItems();
        updateTouchDeltas();
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
    }


    private void rearrangeByNormalRules() {
        placeItem(FcpItem.ACTION_STRONG_TOWER, 0, false);
        placeItem(FcpItem.ACTION_TOWER, 1, false);
        placeItem(FcpItem.ACTION_FARM, 2, false);

        placeItem(FcpItem.ACTION_UNIT_1, 3, true);
        placeItem(FcpItem.ACTION_UNIT_2, 2, true);
        placeItem(FcpItem.ACTION_UNIT_3, 1, true);
        placeItem(FcpItem.ACTION_UNIT_4, 0, true);
    }


    private void rearrangeBySlayRules() {
        placeItem(FcpItem.ACTION_TOWER, 0, false);

        placeItem(FcpItem.ACTION_UNIT_1, 3, true);
        placeItem(FcpItem.ACTION_UNIT_2, 2, true);
        placeItem(FcpItem.ACTION_UNIT_3, 1, true);
        placeItem(FcpItem.ACTION_UNIT_4, 0, true);
    }


    private void placeItem(int action, int place, boolean alignRight) {
        FcpItem itemByAction = getItemByAction(action);
        if (itemByAction == null) return;

        itemByAction.visible = true;
        if (alignRight) {
            itemByAction.delta.x = (float) (position.width - (1.4 * itemTouchOffset + place * (2.5f * itemTouchOffset)));
        } else {
            itemByAction.delta.x = itemTouchOffset + place * (2.5f * itemTouchOffset);
        }
        itemByAction.delta.y = height / 2;
    }


    private FcpItem getItemByAction(int action) {
        for (FcpItem item : items) {
            if (item.action == action) {
                return item;
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
        currentTouch.set(screenX, screenY);

        return checkToClickItems();
    }


    private boolean checkToClickItems() {
        for (FcpItem item : items) {
            if (!item.isVisible()) continue;

            if (item.isTouched(currentTouch)) {
                item.select();
                onItemClicked(item);
                return true;
            }
        }

        return false;
    }


    private void onItemClicked(FcpItem item) {
        if (!menuControllerYio.yioGdxGame.gameController.fieldController.isSomethingSelected()) return;

        switch (item.action) {
            case FcpItem.ACTION_UNIT_1:
                applyBuildUnit(1);
                break;
            case FcpItem.ACTION_UNIT_2:
                applyBuildUnit(2);
                break;
            case FcpItem.ACTION_UNIT_3:
                applyBuildUnit(3);
                break;
            case FcpItem.ACTION_UNIT_4:
                applyBuildUnit(4);
                break;
            case FcpItem.ACTION_FARM:
                applyBuildSolidObject(Obj.FARM);
                break;
            case FcpItem.ACTION_TOWER:
                applyBuildSolidObject(Obj.TOWER);
                break;
            case FcpItem.ACTION_STRONG_TOWER:
                applyBuildSolidObject(Obj.STRONG_TOWER);
                break;
        }

        destroy();
    }


    private void applyBuildUnit(int strength) {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;

        int tipType = -1;
        switch (strength) {
            case 1:
                tipType = SelectionController.TIP_INDEX_UNIT_1;
                break;
            case 2:
                tipType = SelectionController.TIP_INDEX_UNIT_2;
                break;
            case 3:
                tipType = SelectionController.TIP_INDEX_UNIT_3;
                break;
            case 4:
                tipType = SelectionController.TIP_INDEX_UNIT_4;
                break;
        }

        gameController.selectionController.awakeTip(tipType);
        gameController.detectAndShowMoveZoneForBuildingUnit(tipType);
    }


    private void applyBuildSolidObject(int type) {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;

        int tipType = -1;
        switch (type) {
            case Obj.FARM:
                tipType = SelectionController.TIP_INDEX_FARM;
                break;
            case Obj.TOWER:
                tipType = SelectionController.TIP_INDEX_TOWER;
                break;
            case Obj.STRONG_TOWER:
                tipType = SelectionController.TIP_INDEX_STRONG_TOWER;
                break;
        }

        gameController.selectionController.awakeTip(tipType);
        if (tipType == SelectionController.TIP_INDEX_FARM) {
            gameController.detectAndShowMoveZoneForFarm();
        }
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public boolean isButton() {
        return false;
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
