package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangePerformer;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeTypeListener;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.CustomizableListYio;
import yio.tro.antiyoy.menu.customizable_list.ScrollListItem;
import yio.tro.antiyoy.menu.customizable_list.SliReaction;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneChooseExchangeType extends AbstractModalScene{

    public CustomizableListYio customizableListYio;
    private Reaction rbHide;
    private SliReaction sliClick;
    ExchangeTypeListener listener;
    public boolean giveMode;


    public SceneChooseExchangeType(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        customizableListYio = null;
        listener = null;
        giveMode = false;
        initReactions();
    }


    @Override
    public void create() {
        createInvisibleCloseButton(rbHide);
        createCustomList();
        loadValues();
    }


    private void createCustomList() {
        initCustomList();
        customizableListYio.appear();
    }


    private void initCustomList() {
        if (customizableListYio != null) return;

        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setAnimation(Animation.down);
        customizableListYio.setPosition(generateRectangle(0.05, 0.1, 0.9, 0.6));
        menuControllerYio.addElementToScene(customizableListYio);
    }


    private void loadValues() {
        customizableListYio.clearItems();

        boolean highlight = true;
        for (ExchangeType exchangeType : ExchangeType.values()) {
            if (!isExchangeTypeAllowed(exchangeType)) continue;
            ScrollListItem scrollListItem = new ScrollListItem();
            scrollListItem.setKey("" + exchangeType);
            scrollListItem.setHeight(0.07f * GraphicsYio.height);
            scrollListItem.setTitle(LanguagesManager.getInstance().getString("" + exchangeType));
            scrollListItem.setClickReaction(sliClick);
            scrollListItem.setHighlightEnabled(highlight);
            highlight = !highlight;
            customizableListYio.addItem(scrollListItem);
        }
    }


    private boolean isExchangeTypeAllowed(ExchangeType exchangeType) {
        if (GameRules.diplomaticRelationsLocked) {
            FieldManager fieldManager = getGameController().fieldManager;
            ExchangePerformer exchangePerformer = fieldManager.diplomacyManager.exchangePerformer;
            return exchangePerformer.isExchangeTypeAllowedWhenRelationsLocked(exchangeType);
        }
        return true;
    }


    private void initReactions() {
        rbHide = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
        sliClick = new SliReaction() {
            @Override
            public void apply(AbstractCustomListItem item) {
                ScrollListItem scrollListItem = (ScrollListItem) item;
                ExchangeType exchangeType = ExchangeType.valueOf(scrollListItem.key);
                onExchangeTypeChosen(exchangeType);
            }
        };
    }


    void onExchangeTypeChosen(ExchangeType exchangeType) {
        if (listener != null) {
            listener.onExchangeTypeChosen(exchangeType);
        }
        hide();
    }


    public void setListener(ExchangeTypeListener listener) {
        this.listener = listener;
    }


    public void setGiveMode(boolean giveMode) {
        this.giveMode = giveMode;
    }


    @Override
    public void hide() {
        if (customizableListYio != null) {
            customizableListYio.destroy();
        }
        if (invisibleCloseElement != null) {
            invisibleCloseElement.destroy();
        }
    }
}
