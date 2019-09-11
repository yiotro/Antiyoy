package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.CustomLanguageLoader;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scrollable_list.ListBehaviorYio;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.menu.scrollable_list.ScrollableListYio;
import yio.tro.antiyoy.stuff.LanguageChooseItem;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class SceneLanguages extends AbstractScene{

    ScrollableListYio scrollableListYio;
    private ButtonYio backButton;


    public SceneLanguages(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        scrollableListYio = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        backButton = menuControllerYio.spawnBackButton(330, Reaction.rbSettingsMenu);

        createList();

        menuControllerYio.endMenuCreation();
    }


    void applyLanguage(String key) {
        menuControllerYio.clear();

        CustomLanguageLoader.setAndSaveLanguage(key);

        SettingsManager.getInstance().loadAllSettings();
        Scenes.createScenes(menuControllerYio);
        Scenes.sceneMainMenu.create();
    }


    private void createList() {
        if (scrollableListYio == null) {
            ArrayList<LanguageChooseItem> chooseListItems = LanguagesManager.getInstance().getChooseListItems();

            scrollableListYio = new ScrollableListYio(menuControllerYio);
            scrollableListYio.setPosition(generateRectangle(0.05, 0.07, 0.9, 0.75));
            scrollableListYio.setTitle(getString("language"));
            menuControllerYio.addElementToScene(scrollableListYio);

            scrollableListYio.clearItems();
            for (LanguageChooseItem chooseListItem : chooseListItems) {
                scrollableListYio.addItem(chooseListItem.name, chooseListItem.title, chooseListItem.author);
            }

            scrollableListYio.setListBehavior(new ListBehaviorYio() {
                @Override
                public void applyItem(ListItemYio item) {
                    applyLanguage(item.key);
                }


                @Override
                public void onItemRenamed(ListItemYio item) {

                }


                @Override
                public void onItemDeleteRequested(ListItemYio item) {

                }
            });
        }

        scrollableListYio.appear();
    }
}
