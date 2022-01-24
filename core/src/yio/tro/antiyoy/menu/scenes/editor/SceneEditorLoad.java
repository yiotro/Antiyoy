package yio.tro.antiyoy.menu.scenes.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.editor.EditorSaveSystem;
import yio.tro.antiyoy.gameplay.editor.LevelEditorManager;
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

import java.util.ArrayList;

public class SceneEditorLoad extends AbstractScene{

    ScrollableListYio scrollableListYio;
    private Reaction rbBack;
    private ListItemYio targetItem;
    ArrayList<SaveSlotInfo> tempList;


    public SceneEditorLoad(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        scrollableListYio = null;
        tempList = new ArrayList<>();
        initReactions();
    }


    private void initReactions() {
        rbBack = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneEditorLobby.create();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);
        menuControllerYio.spawnBackButton(240, rbBack);
        createList();
        menuControllerYio.endMenuCreation();
    }


    private void createList() {
        initList();
        loadValues();
        scrollableListYio.appear();
    }


    private void initList() {
        if (scrollableListYio != null) return;

        scrollableListYio = new ScrollableListYio(menuControllerYio);
        scrollableListYio.setPosition(generateRectangle(0.05, SceneEditorOverlay.PANEL_HEIGHT, 0.9, 0.75));
        scrollableListYio.setTitle(getString("editor"));
        scrollableListYio.setListBehavior(getListBehavior());
        scrollableListYio.setEditable(true);
        menuControllerYio.addElementToScene(scrollableListYio);
    }


    private ListBehaviorYio getListBehavior() {
        return new ListBehaviorYio() {
            @Override
            public void applyItem(ListItemYio item) {
                onItemClicked(item);
            }


            @Override
            public void onItemRenamed(ListItemYio item) {
                SceneEditorLoad.this.onItemRenamed(item);
            }


            @Override
            public void onItemDeleteRequested(ListItemYio item) {
                if (item.key.equals(SaveSystem.AUTOSAVE_KEY)) return;

                targetItem = item;
                showConfirmDeleteDialog();
            }
        };
    }


    private void onItemRenamed(ListItemYio item) {
        String key = item.key;
        if (key.equals(SaveSystem.AUTOSAVE_KEY)) return;

        SaveSlotInfo saveSlotInfo = new SaveSlotInfo();

        Preferences slotPrefs = Gdx.app.getPreferences(key);
        saveSlotInfo.name = item.getEditableName();
        saveSlotInfo.description = SaveSystem.getDescriptionString(slotPrefs);
        saveSlotInfo.key = key;

        SaveSystem saveSystem = menuControllerYio.yioGdxGame.saveSystem;
        saveSystem.editSlot(key, saveSlotInfo, EditorSaveSystem.EDITOR_PREFS);
    }


    private void showConfirmDeleteDialog() {
        Scenes.sceneConfirmDeleteSlot.create();
        Scenes.sceneConfirmDeleteSlot.setCurrentYesReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                deleteTargetItem();
                create();
                loadValues();
            }
        });
    }


    public void deleteTargetItem() {
        Preferences preferences = getPreferences();
        preferences.putString(targetItem.key, ""); // clear slot data
        preferences.putString(targetItem.key + ":name", ""); // clear slot name
        preferences.flush();
        targetItem = null;
    }


    private void onItemClicked(ListItemYio item) {
        GameController gameController = getGameController();
        EditorSaveSystem editorSaveSystem = gameController.editorSaveSystem;
        int slotNumberByKey = editorSaveSystem.getSlotNumberByKey(item.key);
        editorSaveSystem.loadSlot(slotNumberByKey);
    }


    private void loadValues() {
        tempList.clear();

        Preferences preferences = getPreferences();
        int index = 0;

        while (true) {
            String key = LevelEditorManager.SLOT_NAME + index;
            if (index > 500 && !preferences.contains(key)) break;

            boolean empty = isEmpty(preferences, key);

            if (!empty) {
                String savedNameString = preferences.getString(key + ":name");
                String name = getString("slot") + " " + index;
                if (savedNameString.length() > 0) {
                    name = savedNameString;
                }

                SaveSlotInfo saveSlotInfo = new SaveSlotInfo();
                saveSlotInfo.key = key;
                saveSlotInfo.name = name;
                saveSlotInfo.description = " ";
                tempList.add(saveSlotInfo);
            }

            index++;
        }

        scrollableListYio.clearItems();
        for (int i = tempList.size() - 1; i >= 0; i--) {
            SaveSlotInfo saveSlotInfo = tempList.get(i);
            scrollableListYio.addItem(saveSlotInfo.key, saveSlotInfo.name, saveSlotInfo.description);
        }
    }


    private Preferences getPreferences() {
        return Gdx.app.getPreferences(EditorSaveSystem.EDITOR_PREFS);
    }


    private boolean isEmpty(Preferences preferences, String key) {
        boolean empty;
        String fullLevelString = preferences.getString(key);
        empty = (fullLevelString.length() < 10);
        return empty;
    }
}
