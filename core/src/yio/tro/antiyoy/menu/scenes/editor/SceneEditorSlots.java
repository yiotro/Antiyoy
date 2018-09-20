package yio.tro.antiyoy.menu.scenes.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.save_slot_selector.SaveSlotInfo;
import yio.tro.antiyoy.menu.save_slot_selector.SaveSystem;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.scrollable_list.ListBehaviorYio;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.menu.scrollable_list.ScrollableListYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneEditorSlots extends AbstractScene{

    ScrollableListYio list;
    ListItemYio targetItem;


    public SceneEditorSlots(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        list = null;
        targetItem = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);

        createList();

        menuControllerYio.spawnBackButton(130, Reaction.rbChooseGameModeMenu);

        menuControllerYio.endMenuCreation();
    }


    private void createList() {
        initListOnce();
        updateList();

        list.appear();
    }


    private void onListItemClicked(ListItemYio item) {
        getLevelEditor().setCurrentSlotNumber(getSlotNumber(item));
        Scenes.sceneEditorActions.create();
    }


    private int getSlotNumber(ListItemYio item) {
        return Integer.valueOf(item.key.substring(4));
    }


    private LevelEditor getLevelEditor() {
        return menuControllerYio.yioGdxGame.gameController.getLevelEditor();
    }


    private void updateList() {
        list.clearItems();

        Preferences preferences = getPreferences();
        int index = 1;
        boolean atLeastOneEmpty = false;

        while (true) {
            String key = LevelEditor.SLOT_NAME + index;
            if (index > 8 && !preferences.contains(key)) break;

            boolean empty;
            empty = isEmpty(preferences, key);

            if (!empty) {
                String savedNameString = preferences.getString(key + ":name");
                String name = getString("slot") + " " + index;
                if (savedNameString.length() > 0) {
                    name = savedNameString;
                }
                list.addItem(key, name, " ");
            } else {
                list.addItem(key, getString("slot") + " " + index, getString("empty"));
                atLeastOneEmpty = true;
            }

            index++;
        }

        if (!atLeastOneEmpty) {
            // add new empty slot
            String key = LevelEditor.SLOT_NAME + index;
            list.addItem(key, getString("slot") + " " + index, getString("empty"));
        }
    }


    private boolean isEmpty(Preferences preferences, String key) {
        boolean empty;
        String fullLevelString = preferences.getString(key);
        empty = (fullLevelString.length() < 10);
        return empty;
    }


    private Preferences getPreferences() {
        return Gdx.app.getPreferences(LevelEditor.EDITOR_PREFS);
    }


    private void initListOnce() {
        if (list != null) return;

        list = new ScrollableListYio(menuControllerYio);
        list.setPosition(generateRectangle(0.05, 0.07, 0.9, 0.75));
        list.setTitle(LanguagesManager.getInstance().getString("editor"));
        list.setEditable(true);
        menuControllerYio.addElementToScene(list);

        list.setListBehavior(new ListBehaviorYio() {
            @Override
            public void applyItem(ListItemYio item) {
                onListItemClicked(item);
            }


            @Override
            public void onItemRenamed(ListItemYio item) {
                String key = item.key;
                if (key.equals(SaveSystem.AUTOSAVE_KEY)) return;

                SaveSlotInfo saveSlotInfo = new SaveSlotInfo();

                Preferences slotPrefs = Gdx.app.getPreferences(key);
                saveSlotInfo.name = item.getEditableName();
                saveSlotInfo.description = SaveSystem.getDescriptionString(slotPrefs);
                saveSlotInfo.key = key;

                SaveSystem saveSystem = menuControllerYio.yioGdxGame.saveSystem;
                saveSystem.editSlot(key, saveSlotInfo, LevelEditor.EDITOR_PREFS);
            }


            @Override
            public void onItemDeleteRequested(ListItemYio item) {
                if (item.key.equals(SaveSystem.AUTOSAVE_KEY)) return;

                targetItem = item;
                showConfirmDeleteDialog();
            }
        });
    }


    private void showConfirmDeleteDialog() {
        Scenes.sceneConfirmDeleteSlot.create();
        Scenes.sceneConfirmDeleteSlot.setCurrentYesReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                deleteTargetItem();
                create();
                updateList();
            }
        });
    }


    public void deleteTargetItem() {
        Preferences preferences = getPreferences();
        preferences.putString(targetItem.key, ""); // clear slot data
        preferences.putString(targetItem.key + ":name", ""); // clear slot name
        targetItem = null;
    }
}
