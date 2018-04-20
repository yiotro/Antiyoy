package yio.tro.antiyoy.menu.scenes.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.scrollable_list.ListBehaviorYio;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.menu.scrollable_list.ScrollableListYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneEditorSlots extends AbstractScene{

    ScrollableListYio list;


    public SceneEditorSlots(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        list = null;
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
                list.addItem(key, getString("slot") + " " + index, " ");
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
        menuControllerYio.addElementToScene(list);

        list.setListBehavior(new ListBehaviorYio() {
            @Override
            public void applyItem(ListItemYio item) {
                onListItemClicked(item);
            }
        });
    }
}
