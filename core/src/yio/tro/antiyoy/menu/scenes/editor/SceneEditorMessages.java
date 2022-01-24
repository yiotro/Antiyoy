package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.KeyboardManager;
import yio.tro.antiyoy.gameplay.messages.MessagesManager;
import yio.tro.antiyoy.gameplay.messages.PreparedMessage;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.*;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;

public class SceneEditorMessages extends AbstractModalScene {

    private Reaction rbHide;
    private ButtonYio basePanel;
    private double panelHeight;
    private CustomizableListYio customizableListYio;
    private SliReaction sliRelationClick;


    public SceneEditorMessages(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        panelHeight = 0.5;
        initReactions();
    }


    @Override
    public void create() {
        createInvisibleCloseButton(rbHide);
        createBasePanel();
        createList();
        loadValues();
    }


    private void loadValues() {
        customizableListYio.clearItems();

        TitleListItem titleListItem = new TitleListItem();
        titleListItem.setTitle(getString("messages"));
        customizableListYio.addItem(titleListItem);

        MessagesManager messagesManager = getMessagesManager();
        for (PreparedMessage message : messagesManager.messages) {
            ScrollListItem scrollListItem = new ScrollListItem();
            scrollListItem.setTitle(message.value);
            scrollListItem.setKey("" + message.getKey());
            scrollListItem.setClickReaction(sliRelationClick);
            customizableListYio.addItem(scrollListItem);
        }

        ScrollListItem addItem = new ScrollListItem();
        addItem.setTitle("+");
        addItem.setCentered(true);
        addItem.setClickReaction(getAddMessageReaction());
        customizableListYio.addItem(addItem);
    }


    private MessagesManager getMessagesManager() {
        return getGameController().messagesManager;
    }


    private SliReaction getAddMessageReaction() {
        return new SliReaction() {
            @Override
            public void apply(AbstractCustomListItem item) {
                onAddMessageItemClicked();
            }
        };
    }


    private void onAddMessageItemClicked() {
        hide();
        KeyboardManager.getInstance().apply(new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                if (input.length() > 0) {
                    getMessagesManager().addMessage(input);
                }
                create();
            }
        });
    }


    private void onMessageItemClicked(final ScrollListItem scrollListItem) {
        hide();
        KeyboardManager.getInstance().apply(scrollListItem.title.string, new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                Integer key = Integer.valueOf(scrollListItem.key);
                if (input.length() == 0) {
                    getMessagesManager().removeMessage(key);
                } else {
                    getMessagesManager().modifyMessage(key, input);
                }
                create();
            }
        });
    }


    private void createList() {
        initCustomList();
        customizableListYio.appear();
    }


    private void initCustomList() {
        if (customizableListYio != null) return;
        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setAnimation(Animation.down);
        customizableListYio.setEmbeddedMode(true);
        customizableListYio.setPosition(generateRectangle(0.02, SceneEditorOverlay.PANEL_HEIGHT, 0.96, panelHeight - 0.02));

        menuControllerYio.addElementToScene(customizableListYio);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0, SceneEditorOverlay.PANEL_HEIGHT, 1, panelHeight), 421, null);
        if (basePanel.notRendered()) {
            basePanel.cleatText();
            basePanel.addEmptyLines(1);
            basePanel.loadCustomBackground("gray_pixel.png");
            basePanel.setIgnorePauseResume(true);
            menuControllerYio.buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.fixed_down);
        basePanel.enableRectangularMask();
        basePanel.setShadow(true);
    }


    private void initReactions() {
        rbHide = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
        sliRelationClick = new SliReaction() {
            @Override
            public void apply(AbstractCustomListItem item) {
                onMessageItemClicked((ScrollListItem) item);
            }
        };
    }


    @Override
    public void hide() {
        destroyByIndex(420, 429);
        if (customizableListYio != null) {
            customizableListYio.destroy();
        }
    }
}
