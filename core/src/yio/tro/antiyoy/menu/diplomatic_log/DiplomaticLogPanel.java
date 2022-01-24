package yio.tro.antiyoy.menu.diplomatic_log;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticLog;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticMessage;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.scrollable_list.ListBehaviorYio;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.menu.scrollable_list.ScrollableListYio;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.RectangleYio;

public class DiplomaticLogPanel extends ScrollableListYio {


    boolean readyToDie;
    public RectangleYio titleBackground;


    public DiplomaticLogPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        readyToDie = false;
        titleBackground = new RectangleYio();
        setTitleFont(Fonts.smallerMenuFont);

        initBehavior();
    }


    private void initBehavior() {
        setListBehavior(getListBehavior());
    }


    private ListBehaviorYio getListBehavior() {
        return new ListBehaviorYio() {
            @Override
            public void applyItem(ListItemYio item) {
                DiplomaticLogPanel.this.applyItem(item);
            }


            @Override
            public void onItemRenamed(ListItemYio item) {

            }


            @Override
            public void onItemDeleteRequested(ListItemYio item) {

            }
        };
    }


    private void applyItem(ListItemYio item) {
        DiplomaticLog log = menuControllerYio.yioGdxGame.gameController.fieldManager.diplomacyManager.log;

        if (item.key.equals("clear")) {
            log.onClearMessagesButtonClicked();
        } else {
            log.onListItemClicked(item.key);
        }

        loadValues();

        if (items.size() == 0) {
            Scenes.sceneDiplomaticLog.hide();
        }
    }


    @Override
    protected void updateViewPosition() {
        viewPosition.setBy(position);

        if (appearFactor.get() < 1) {
            viewPosition.y -= (float) ((1 - appearFactor.get()) * 1.05 * position.height);
        }
    }


    @Override
    public void move() {
        super.move();
        updateTitleBackground();
        centerClearItem();
    }


    private void centerClearItem() {
        if (items.size() == 0) return;

        ListItemYio clearItem = findClearItem();
        if (clearItem == null) return;

        clearItem.titlePosition.x = (float) (clearItem.position.x + clearItem.position.width / 2 - clearItem.titleWidth / 2);
    }


    private ListItemYio findClearItem() {
        for (ListItemYio item : items) {
            if (item.key.equals("clear")) {
                return item;
            }
        }

        return null;
    }


    private void updateTitleBackground() {
        titleBackground.setBy(viewPosition);

        titleBackground.height = getItemHeight();
        titleBackground.y = viewPosition.y + viewPosition.height - titleBackground.height;
    }


    @Override
    protected void updateLabelPosition() {
        labelPosition.x = (float) (viewPosition.x + viewPosition.width / 2 - labelWidth / 2);
        labelPosition.y = (float) (viewPosition.y + viewPosition.height - 0.02f * GraphicsYio.width);
    }


    @Override
    protected void onAppear() {
        super.onAppear();

        readyToDie = false;
    }


    public void loadValues() {
        clearItems();

        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        DiplomaticEntity mainEntity = diplomacyManager.getMainEntity();
        DiplomaticLog log = diplomacyManager.log;

        for (DiplomaticMessage message : log.messages) {
            if (message.recipient != mainEntity) continue;

            ListItemYio listItemYio = addItem(message.getKey(), message.getListName(), " ");
            int color = gameController.getColorByFraction(message.getSenderFraction());
            listItemYio.setBckViewType(color);
        }

        checkToAddClearAllItem();
        scrollEngineYio.resetToBottom();

        moveItems(); // update positions
        centerClearItem();
    }


    private void checkToAddClearAllItem() {
        if (items.size() == 0) return;

        ListItemYio listItemYio = addItem("clear", LanguagesManager.getInstance().getString("editor_clear"), " ");
        listItemYio.setBckViewType(-1);
    }


    @Override
    protected void updateEdgeRectangles() {
        super.updateEdgeRectangles();

        topEdge.height = 1f * GraphicsYio.height;
    }


    @Override
    protected float getItemHeight() {
        return 0.08f * GraphicsYio.height;
    }


    @Override
    public boolean checkToPerformAction() {
        if (readyToDie) {
            readyToDie = false;
            Scenes.sceneDiplomaticLog.hide();
            return true;
        }

        return super.checkToPerformAction();
    }


    public boolean isEmpty() {
        return items.size() == 0;
    }


    @Override
    protected void onTouchDown() {
        if (!touched) {
            readyToDie = true;
        }
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderDiplomaticLogPanel;
    }
}
