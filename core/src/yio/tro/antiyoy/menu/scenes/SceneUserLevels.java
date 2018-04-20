package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingMode;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.user_levels.AbstractUserLevel;
import yio.tro.antiyoy.gameplay.user_levels.UserLevelFactory;
import yio.tro.antiyoy.gameplay.user_levels.UserLevelProgressManager;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scrollable_list.ListBehaviorYio;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.menu.scrollable_list.ScrollableListYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class SceneUserLevels extends AbstractScene {

    ScrollableListYio scrollableListYio;
    private ButtonYio backButton;


    public SceneUserLevels(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        scrollableListYio = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        backButton = menuControllerYio.spawnBackButton(910, Reaction.rbChooseGameModeMenu);

        createList();

        menuControllerYio.endMenuCreation();
    }


    private void createList() {
        if (scrollableListYio == null) {
            initListOnce();
        }

        updateList();
        scrollableListYio.appear();
    }


    private void updateList() {
        scrollableListYio.clearItems();

        for (AbstractUserLevel userLevel : UserLevelFactory.getInstance().getLevels()) {
            scrollableListYio.addItem(
                    userLevel.getKey(),
                    getMapTitle(userLevel),
                    userLevel.getAuthor()
            );
        }

        scrollableListYio.addItem(
                "add_my_map",
                LanguagesManager.getInstance().getString("add_my_map"),
                " "
        );
    }


    private String getMapTitle(AbstractUserLevel userLevel) {
        if (UserLevelProgressManager.getInstance().isLevelCompleted(userLevel.getKey())) {
            return "[+] " + userLevel.getMapName();
        }

        return userLevel.getMapName();
    }


    private void initListOnce() {
        scrollableListYio = new ScrollableListYio(menuControllerYio);
        scrollableListYio.setPosition(generateRectangle(0.05, 0.07, 0.9, 0.75));
        scrollableListYio.setTitle(LanguagesManager.getInstance().getString("user_levels"));
        menuControllerYio.addElementToScene(scrollableListYio);

        scrollableListYio.setListBehavior(new ListBehaviorYio() {
            @Override
            public void applyItem(ListItemYio item) {
                OnItemClicked(item);
            }
        });
    }


    private void OnItemClicked(ListItemYio item) {
        if (item.key.equals("add_my_map")) {
            onAddMyMapClicked();
            return;
        }

        AbstractUserLevel level = UserLevelFactory.getInstance().getLevel(item.key);

        LoadingParameters instance = LoadingParameters.getInstance();
        instance.mode = LoadingMode.USER_LEVEL;
        instance.applyFullLevel(level.getFullLevelString());
        instance.colorOffset = 0;
        instance.ulKey = level.getKey();
        LoadingManager.getInstance().startGame(instance);
    }


    private void onAddMyMapClicked() {
        Scenes.sceneInfoMenu.create("how_add_my_map", new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneUserLevels.create();
            }
        }, 911);
    }
}
