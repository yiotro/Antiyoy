package yio.tro.antiyoy.menu.diplomacy_element;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.*;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;
import yio.tro.antiyoy.stuff.scroll_engine.ScrollEngineYio;

import java.util.ArrayList;
import java.util.Collections;

public class DiplomacyElement extends InterfaceElement {


    MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    public RectangleYio internalBackground, topCover;
    public FactorYio appearFactor;
    PointYio currentTouch, lastTouch;
    public ArrayList<DeItem> items;
    float hook;
    float itemHeight;
    ScrollEngineYio scrollEngineYio;
    ClickDetector clickDetector;
    public BitmapFont titleFont, descFont;
    DeItem clickedItem, selectedItem;
    DeIcon clickedIcon;
    boolean touched, touchedScrollArea;
    float topLabelHeight;
    ObjectPoolYio<DeItem> poolItems;
    public ArrayList<DeIcon> icons;
    private float iconTouchOffset;
    private float iconRadius;
    public final DeLabel label;
    private ArrayList<DiplomaticContract> tempContracts;


    public DiplomacyElement(MenuControllerYio menuControllerYio, int id) {
        super(id);
        this.menuControllerYio = menuControllerYio;

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        currentTouch = new PointYio();
        lastTouch = new PointYio();
        clickDetector = new ClickDetector();
        touched = false;
        titleFont = Fonts.gameFont;
        descFont = Fonts.smallerMenuFont;
        label = new DeLabel(this);
        items = new ArrayList<>();
        clickedItem = null;
        internalBackground = new RectangleYio();
        topCover = new RectangleYio();
        touchedScrollArea = false;
        clickedIcon = null;
        selectedItem = null;
        tempContracts = new ArrayList<>();

        initPool();
        initMetrics();
        initScrollEngine();
        initIcons();
    }


    private void initIcons() {
        icons = new ArrayList<>();

        for (DipActionType dipActionType : DipActionType.values()) {
            addIcon(dipActionType);
        }

        for (DeIcon icon : icons) {
            icon.setTouchOffset(iconTouchOffset);
            icon.setRadius(iconRadius);
        }
    }


    private void addIcon(DipActionType action) {
        DeIcon deIcon = new DeIcon(this);

        deIcon.setAction(action);

        icons.add(deIcon);
    }


    private void initPool() {
        poolItems = new ObjectPoolYio<DeItem>() {
            @Override
            public DeItem makeNewObject() {
                return new DeItem(DiplomacyElement.this);
            }
        };
    }


    private void initScrollEngine() {
        scrollEngineYio = new ScrollEngineYio();

        scrollEngineYio.setSlider(0, 0); // will be updated later
        updateScrollEngineLimits();
        scrollEngineYio.setFriction(0.02);
        scrollEngineYio.setSoftLimitOffset(0.05f * GraphicsYio.width);
    }


    private void updateScrollEngineLimits() {
        scrollEngineYio.setLimits(0, getScrollLimit());
    }


    private double getScrollLimit() {
        return items.size() * itemHeight - topLabelHeight;
    }


    private void initMetrics() {
        itemHeight = 0.2f * GraphicsYio.width;
        topLabelHeight = itemHeight / 2;
        iconRadius = 0.42f * topLabelHeight;
        iconTouchOffset = iconRadius;
    }


    private void updateItems() {
        clearItems();

        GameController gameController = getGameController();
        DiplomacyManager diplomacyManager = getDiplomacyManager(gameController);

        DiplomaticEntity mainEntity = diplomacyManager.getEntity(gameController.turn);

        for (DiplomaticEntity relationEntity : mainEntity.relations.keySet()) {
            if (relationEntity.hidden) continue;
            addItem(relationEntity);
        }

        updateStatuses();
        sortItems();
    }


    private void updateStatuses() {
        GameController gameController = getGameController();
        DiplomacyManager diplomacyManager = getDiplomacyManager(gameController);

        DiplomaticEntity mainEntity = diplomacyManager.getMainEntity();
        if (!mainEntity.isHuman()) return;

        for (DeItem item : items) {
            DiplomaticEntity relationEntity = diplomacyManager.getEntity(item.fraction);
            item.setStatus(convertRelationIntoStatus(mainEntity, relationEntity));
            item.setBlackMarkEnabled(mainEntity.isBlackMarkedWith(relationEntity));
            item.setDescriptionString(getItemDescription(mainEntity, relationEntity));
        }
    }


    private DiplomacyManager getDiplomacyManager(GameController gameController) {
        return gameController.fieldManager.diplomacyManager;
    }


    private GameController getGameController() {
        return menuControllerYio.yioGdxGame.gameController;
    }


    private String getItemDescription(DiplomaticEntity mainEntity, DiplomaticEntity relationEntity) {
        if (!relationEntity.alive) return LanguagesManager.getInstance().getString("dead");

        updateTempContracts(mainEntity, relationEntity);

        String relationStringKey = getRelationStringKey(mainEntity, relationEntity);
        String relationString = LanguagesManager.getInstance().getString(relationStringKey);
        if (tempContracts.size() == 0 && !mainEntity.hasAnyDebts()) {
            return relationString;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(relationString);
        for (Debt debt : mainEntity.debts) {
            if (debt.value == 0) continue;
            if (debt.target != relationEntity) continue;
            stringBuilder.append(" [").append(Yio.getDeltaMoneyString(-debt.value)).append("]");
        }
        for (DiplomaticContract contract : tempContracts) {
            String dotationsString = contract.getDotationsStringFromEntityPerspective(mainEntity, false);
            int dotationsValue = contract.getDotationsFromEntityPerspective(mainEntity, true);
            if (contract.type != DiplomaticContract.TYPE_FRIENDSHIP && dotationsValue == 0) continue;
            if (contract.type == DiplomaticContract.TYPE_FRIENDSHIP && GameRules.diplomaticRelationsLocked) continue;
            stringBuilder.append(" [");
            String string = dotationsString + ", ";
            if (contract.type == DiplomaticContract.TYPE_FRIENDSHIP) {
                string = "";
            }
            stringBuilder.append(string);
            stringBuilder.append(contract.getExpireCountDown());
            stringBuilder.append("x]");
        }

        return stringBuilder.toString();
    }


    private void updateTempContracts(DiplomaticEntity mainEntity, DiplomaticEntity relationEntity) {
        tempContracts.clear();

        DiplomacyManager diplomacyManager = getDiplomacyManager(getGameController());

        for (DiplomaticContract diplomaticContract : diplomacyManager.contracts) {
            if (!diplomaticContract.equals(mainEntity, relationEntity, -1)) continue;
            tempContracts.add(diplomaticContract);
        }
    }


    private String getRelationStringKey(DiplomaticEntity mainEntity, DiplomaticEntity relationEntity) {
        int relation = mainEntity.getRelation(relationEntity);

        switch (relation) {
            default:
                return "-";
            case DiplomaticRelation.NEUTRAL:
                return "neutral";
            case DiplomaticRelation.FRIEND:
                return "friend";
            case DiplomaticRelation.ENEMY:
                return "enemy";
        }
    }


    private void sortItems() {
        Collections.sort(items);
    }


    private void addItem(DiplomaticEntity relationEntity) {
        DeItem deItem = poolItems.getNext();

        deItem.setFraction(relationEntity.fraction);
        deItem.setTitle(relationEntity.capitalName);

        items.add(deItem);
    }


    private int convertRelationIntoStatus(DiplomaticEntity mainEntity, DiplomaticEntity relationEntity) {
        if (!relationEntity.alive) return DeItem.STATUS_DEAD;

        int relation = mainEntity.getRelation(relationEntity);

        switch (relation) {
            default:
                return DeItem.STATUS_DEAD;
            case DiplomaticRelation.NEUTRAL:
                return DeItem.STATUS_NEUTRAL;
            case DiplomaticRelation.FRIEND:
                return DeItem.STATUS_FRIEND;
            case DiplomaticRelation.ENEMY:
                return DeItem.STATUS_ENEMY;
        }
    }


    private void clearItems() {
        for (DeItem item : items) {
            poolItems.add(item);
        }

        items.clear();
    }


    void updateMetrics() {
        float currentY = (float) position.height - topLabelHeight - itemHeight;

        for (DeItem item : items) {
            item.position.width = position.width;
            item.position.height = itemHeight;
            item.delta.x = 0;
            item.delta.y = currentY;
            currentY -= itemHeight;
        }
    }


    @Override
    public void move() {
        moveFactors();

        updateViewPosition();
        scrollEngineYio.move();
        updateHook();
        moveItems();
        moveIcons();
        label.move();
        updateInternalBackgroundPosition();
        updateCover();
    }


    private void moveIcons() {
        for (DeIcon icon : icons) {
            icon.move();
        }
    }


    private void updateCover() {
        topCover.setBy(viewPosition);
        topCover.height = topLabelHeight;
        topCover.y = viewPosition.y + viewPosition.height - topCover.height;
    }


    private void updateInternalBackgroundPosition() {
        internalBackground.x = viewPosition.x;
        internalBackground.y = viewPosition.y;
        internalBackground.width = viewPosition.width;
        internalBackground.height = viewPosition.height - topLabelHeight;
    }


    private void moveFactors() {
        if (!appearFactor.hasToMove()) return;

        appearFactor.move();
    }


    private void updateHook() {
        hook = +(float) scrollEngineYio.getSlider().a;
    }


    private void moveItems() {
        for (DeItem item : items) {
            item.move();

            if (!touched) {
                item.moveSelection();
            }
        }
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);

        if (appearFactor.get() < 1) {
            viewPosition.y -= (float) ((1 - appearFactor.get()) * 1.05 * position.height);
        }
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(1, 2.2);

        onDestroy();
    }


    private void onDestroy() {
//        dropSelections();
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(3, 1.25);
        onAppear();
    }


    private void onAppear() {
        label.setVisible(true);
        selectedItem = null;

        updateAll();
        dropSelections();

        scrollEngineYio.resetToBottom();
    }


    public void onTurnStarted() {
        updateItems();
    }


    public void updateAll() {
        updateItems();
        updateMetrics();
        updateScrollEngineLimits();
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        if (clickedItem != null) {
            performItemClickAction();

            clickedItem = null;
            return true;
        }

        if (clickedIcon != null) {
            performIconClickAction();

            clickedIcon = null;
            return true;
        }

        return false;
    }


    private void performIconClickAction() {
        GameController gameController = getGameController();
        DiplomacyManager diplomacyManager = getDiplomacyManager(gameController);
        diplomacyManager.onUserClickedContextIcon(selectedItem.fraction, clickedIcon.action);
    }


    private void performItemClickAction() {

    }


    public void onRelationsChanged() {
        updateStatuses();
        showIcons();
    }


    public void onFirstPlayerTurnEnded() {
        updateStatuses();
    }


    private void resetToLabel() {
        dropSelections();
        label.setVisible(true);
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    private void updateCurrentTouch(int screenX, int screenY) {
        lastTouch.setBy(currentTouch);
        currentTouch.set(screenX, screenY);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!isVisible()) return false;

        updateCurrentTouch(screenX, screenY);
        touched = (currentTouch.y < position.y + position.height);
        touchedScrollArea = (currentTouch.y < position.y + position.height - topLabelHeight);

        if (touched) {
            clickDetector.onTouchDown(currentTouch);

            if (touchedScrollArea) {
                scrollEngineYio.onTouchDown();
                checkToSelectItems();
            } else {
                checkToSelectIcons();
            }
        } else {
            destroy();
        }

        return true;
    }


    private void checkToSelectIcons() {
        for (DeIcon icon : icons) {
            if (icon.isTouched(currentTouch)) {
                icon.select();
            }
        }
    }


    private void checkToSelectItems() {
        for (DeItem item : items) {
            if (item.isTouched(currentTouch)) {
                item.select();
            }
        }
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        if (touched) {
            updateCurrentTouch(screenX, screenY);

            if (touchedScrollArea) {
                scrollEngineYio.setSpeed(currentTouch.y - lastTouch.y);
            }

            clickDetector.onTouchDrag(currentTouch);
        }

        return touched;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);

        if (touchedScrollArea) {
            scrollEngineYio.onTouchUp();
        }

        if (touched) {
            touched = false;
            clickDetector.onTouchUp(currentTouch);

            if (clickDetector.isClicked()) {
                onClick();
            }

            return true;
        }

        return false;
    }


    private void onClick() {
        if (touchedScrollArea) {
            onClickInsideScrollArea();
        } else {
            onClickIcons();
        }
    }


    private void onClickIcons() {
        for (DeIcon icon : icons) {
            if (icon.isTouched(currentTouch)) {
                onIconClicked(icon);
            }
        }
    }


    private void onIconClicked(DeIcon icon) {
        SoundManagerYio.playSound(SoundManagerYio.soundPressButton);

        clickedIcon = icon;
    }


    private void onClickInsideScrollArea() {
        if (!internalBackground.isPointInside(currentTouch, 0)) return;

        scrollEngineYio.setSpeed(0);

        for (DeItem item : items) {
            if (item.isTouched(currentTouch)) {
                SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
                onItemClicked(item);
            }
        }
    }


    public DeItem getItem(int fraction) {
        for (DeItem item : items) {
            if (item.fraction != fraction) continue;
            return item;
        }
        return null;
    }


    public void applyClickByFraction(int fraction) {
        DeItem item = getItem(fraction);
        if (item == null) return;

        onItemClicked(item);
        focusOnItem(item);
    }


    public void focusOnItem(DeItem item) {
        scrollEngineYio.resetToBottom();

        int c = 150;
        while (!isItemInFocus(item) && c > 0) {
            c--;
            scrollEngineYio.getSlider().relocate(0.02f * GraphicsYio.height);
            viewPosition.setBy(position);
            scrollEngineYio.move();
            updateHook();
            moveItems();
        }

        updateViewPosition();
        scrollEngineYio.move();
        updateHook();
        moveItems();
    }


    private boolean isItemInFocus(DeItem item) {
        return item.position.y > viewPosition.y;
    }


    private void onItemClicked(DeItem item) {
        if (item.keepSelection) {
            resetToLabel();
            return;
        }

        dropSelections();
        if (item.status == DeItem.STATUS_DEAD) return;

        clickedItem = item;
        selectedItem = item;

        clickedItem.select();
        clickedItem.setKeepSelection(true);

        showIcons();
    }


    void dropSelections() {
        for (DeItem item : items) {
            item.setKeepSelection(false);
        }

        resetIconsVisibility();
    }


    private void resetIconsVisibility() {
        for (DeIcon icon : icons) {
            icon.visible = false;
        }
    }


    void showIcons() {
        if (selectedItem == null) return;
        if (appearFactor.get() == 0) return;

        label.setVisible(false);
        resetIconsVisibility();

        GameController gameController = getGameController();
        DiplomacyManager diplomacyManager = getDiplomacyManager(gameController);
        int fraction = selectedItem.fraction;
        DiplomaticEntity selectedEntity = diplomacyManager.getEntity(fraction);
        DiplomaticEntity mainEntity = diplomacyManager.getMainEntity();
        int relation = mainEntity.getRelation(selectedEntity);

        switch (relation) {
            case DiplomaticRelation.NEUTRAL:
                enableIcon(DipActionType.like);
                enableIcon(DipActionType.dislike);
                if (diplomacyManager.isBlackMarkAllowed(mainEntity, selectedEntity)) {
                    enableIcon(DipActionType.black_mark);
                }
                enableIcon(DipActionType.info);
                enableIcon(DipActionType.exchange);
                enableIcon(DipActionType.mail);
                break;
            case DiplomaticRelation.FRIEND:
                enableIcon(DipActionType.exchange);
                enableIcon(DipActionType.dislike);
                enableIcon(DipActionType.info);
                enableIcon(DipActionType.buy_hexes);
                enableIcon(DipActionType.mail);
                enableIcon(DipActionType.attack);
                break;
            case DiplomaticRelation.ENEMY:
                enableIcon(DipActionType.like);
                enableIcon(DipActionType.exchange);
                if (diplomacyManager.isBlackMarkAllowed(mainEntity, selectedEntity)) {
                    enableIcon(DipActionType.black_mark);
                }
                enableIcon(DipActionType.mail);
                break;
        }

        alignIcons();
        appearIcons();
    }


    private void appearIcons() {
        for (DeIcon icon : icons) {
            icon.appear();
        }
    }


    private void alignIcons() {
        int n = getNumberOfVisibleIcons();
        float iDelta = 1.5f * iconTouchOffset + 2 * iconRadius;
        float fullWidth = iDelta * (n - 1);
        float currentX = (float) (position.width / 2 - fullWidth / 2);

        for (DeIcon icon : icons) {
            if (!icon.visible) continue;

            icon.delta.x = currentX;
            icon.delta.y = (float) (position.height - topLabelHeight / 2);

            currentX += iDelta;
        }
    }


    int getNumberOfVisibleIcons() {
        int c = 0;

        for (DeIcon icon : icons) {
            if (icon.visible) {
                c++;
            }
        }

        return c;
    }


    void enableIcon(DipActionType actionType) {
        if (!isDipActionAllowed(actionType)) return;
        getIcon(actionType).visible = true;
    }


    private boolean isDipActionAllowed(DipActionType actionType) {
        if (GameRules.diplomaticRelationsLocked) {
            if (actionType == DipActionType.like) return false;
            if (actionType == DipActionType.dislike) return false;
            if (actionType == DipActionType.black_mark) return false;
            if (actionType == DipActionType.attack) return false;
        }
        return true;
    }


    DeIcon getIcon(DipActionType action) {
        for (DeIcon icon : icons) {
            if (icon.action == action) {
                return icon;
            }
        }

        return null;
    }


    @Override
    public boolean onMouseWheelScrolled(int amount) {
        if (amount == 1) {
            scrollEngineYio.giveImpulse(0.02 * GraphicsYio.width);
        } else if (amount == -1) {
            scrollEngineYio.giveImpulse(-0.02 * GraphicsYio.width);
        }

        return true;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    public boolean needToRenderInternalBackground() {
        return scrollEngineYio.isOverTop() || scrollEngineYio.isBelowBottom();
    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);

        onPositionChanged();
    }


    private void onPositionChanged() {
        updateMetrics();
        scrollEngineYio.setSlider(0, position.height - itemHeight);
        updateScrollEngineLimits();
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderDiplomacyElement;
    }
}
