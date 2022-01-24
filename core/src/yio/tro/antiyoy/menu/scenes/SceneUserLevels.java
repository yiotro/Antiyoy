package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.data_storage.LegacyImportManager;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.user_levels.AbstractLegacyUserLevel;
import yio.tro.antiyoy.gameplay.user_levels.AbstractUserLevel;
import yio.tro.antiyoy.gameplay.user_levels.UserLevelFactory;
import yio.tro.antiyoy.gameplay.user_levels.UserLevelsManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scrollable_list.ListBehaviorYio;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.menu.scrollable_list.ScrollableListYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class SceneUserLevels extends AbstractScene {

    ScrollableListYio scrollableListYio;
    private ButtonYio backButton;
    private ButtonYio filtersButton;
    private Reaction rbFilters;
    private boolean completedAllowed;
    private boolean historicalAllowed;
    private boolean singlePlayerAllowed;
    private boolean multiplayerAllowed;
    private String searchName;
    private boolean diplomacyAllowed;
    private boolean fogOfWarAllowed;
    public boolean hiddenLevelsAllowed;
    private ArrayList<AbstractLegacyUserLevel> tempLevelsList;


    public SceneUserLevels(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        scrollableListYio = null;
        tempLevelsList = new ArrayList<>();
        initReactions();
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        backButton = menuControllerYio.spawnBackButton(910, Reaction.rbChooseGameModeMenu);

        createList();
        createFiltersButton();

        menuControllerYio.endMenuCreation();
    }


    private void createFiltersButton() {
        filtersButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 912, getString("filters"));
        filtersButton.setReaction(rbFilters);
        filtersButton.setAnimation(Animation.up);
    }


    private void initReactions() {
        rbFilters = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneUlFilters.create();
            }
        };
    }


    private void createList() {
        if (scrollableListYio == null) {
            initListOnce();
        }

        loadValues();
        scrollableListYio.appear();
    }


    public void onLevelBecameHidden() {
        loadValues();
        scrollableListYio.move();
        scrollableListYio.move();
    }


    private void loadValues() {
        scrollableListYio.clearItems();

        updateFilterParams();
        tempLevelsList.clear();
        for (AbstractLegacyUserLevel userLevel : UserLevelFactory.getInstance().getLevels()) {
            userLevel.setGameController(getGameController());
            if (hasToSkipLevel(userLevel)) continue;
            tempLevelsList.add(userLevel);
        }

        removeHiddenLevelsFromTempList();
        for (AbstractLegacyUserLevel userLevel : tempLevelsList) {
            addUserLevelToList(userLevel);
        }

        checkToCreateAddMapItem();
    }


    private void updateFilterParams() {
        Preferences filterPrefs = SceneUlFilters.getFilterPrefs();
        completedAllowed = filterPrefs.getBoolean("completed", true);
        historicalAllowed = filterPrefs.getBoolean("historical", true);
        singlePlayerAllowed = filterPrefs.getBoolean("single_player", true);
        multiplayerAllowed = filterPrefs.getBoolean("multiplayer", true);
        searchName = filterPrefs.getString("search_name", "");
        diplomacyAllowed = filterPrefs.getBoolean("diplomacy", true);
        fogOfWarAllowed = filterPrefs.getBoolean("fog_of_war", true);
        hiddenLevelsAllowed = filterPrefs.getBoolean("hidden", false);
    }


    private void removeHiddenLevelsFromTempList() {
        if (hiddenLevelsAllowed) return;
        for (String hiddenKey : UserLevelsManager.getInstance().getHiddenKeys()) {
            if (hiddenKey == null) continue;
            if (hiddenKey.length() == 0) continue;
            removeFromTempLevelsList(hiddenKey);
        }
    }


    private void removeFromTempLevelsList(String key) {
        for (int i = tempLevelsList.size() - 1; i >= 0; i--) {
            AbstractLegacyUserLevel userLevel = tempLevelsList.get(i);
            if (!userLevel.getKey().equals(key)) continue;
            tempLevelsList.remove(userLevel);
            break;
        }
    }


    private void addUserLevelToList(AbstractLegacyUserLevel userLevel) {
        scrollableListYio.addItem(
                userLevel.getKey(),
                getMapTitle(userLevel),
                userLevel.getAuthor()
        );
    }


    private void checkToAddOneLevel() {
        if (scrollableListYio.items.size() > 0) return;

        AbstractLegacyUserLevel firstLevel = UserLevelFactory.getInstance().getLevels().get(0);
        addUserLevelToList(firstLevel);
    }


    private boolean hasToSkipLevel(AbstractLegacyUserLevel userLevel) {
        if (!completedAllowed) {
            boolean levelCompleted = UserLevelsManager.getInstance().isLevelCompleted(userLevel.getKey());
            if (levelCompleted) return true;
        }

        if (!historicalAllowed && userLevel.isHistorical()) return true;
        if (!singlePlayerAllowed && userLevel.isSinglePlayer()) return true;
        if (!multiplayerAllowed && userLevel.isMultiplayer()) return true;
        if (!diplomacyAllowed && userLevel.getDiplomacy()) return true;
        if (!fogOfWarAllowed && userLevel.getFogOfWar()) return true;

        if (searchName.length() > 0 && !userLevel.getMapName().toLowerCase().contains(searchName.toLowerCase())) return true;

        return false;
    }


    private void checkToCreateAddMapItem() {
        if (!isAddMapItemEnabled()) return;
        if (scrollableListYio.items.size() == 0) return;

        scrollableListYio.addItem(
                "add_my_map",
                LanguagesManager.getInstance().getString("add_my_map"),
                " "
        );
    }


    private boolean isAddMapItemEnabled() {
        float numberOfCompletedLevels = UserLevelsManager.getInstance().getNumberOfCompletedLevels();
        float allLevels = UserLevelFactory.getInstance().getLevels().size();
        float completionRatio = numberOfCompletedLevels / allLevels;

        return completionRatio > 0.8;
    }


    private String getMapTitle(AbstractLegacyUserLevel userLevel) {
        if (UserLevelsManager.getInstance().isLevelCompleted(userLevel.getKey())) {
            return "[+] " + userLevel.getMapName();
        }

        return userLevel.getMapName();
    }


    private void initListOnce() {
        scrollableListYio = new ScrollableListYio(menuControllerYio);
        scrollableListYio.setPosition(generateRectangle(0.05, 0.07, 0.9, 0.75));
        scrollableListYio.setTitle(LanguagesManager.getInstance().getString("user_levels"));
        scrollableListYio.setListBehavior(getListBehavior());
        scrollableListYio.setEmptySign(LanguagesManager.getInstance().getString("check_filters"));
        scrollableListYio.setEditable(true);
        scrollableListYio.setItemHeight(0.107f * GraphicsYio.height);
        scrollableListYio.setLongTapReaction(getLongTapReaction());
        menuControllerYio.addElementToScene(scrollableListYio);
    }


    private Reaction getLongTapReaction() {
        return new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                ListItemYio longTappedItem = scrollableListYio.longTappedItem;
                Scenes.sceneUlContextMenu.create();
                Scenes.sceneUlContextMenu.setKey(longTappedItem.key);
            }
        };
    }


    private ListBehaviorYio getListBehavior() {
        return new ListBehaviorYio() {
            @Override
            public void applyItem(ListItemYio item) {
                onItemClicked(item);
            }


            @Override
            public void onItemRenamed(ListItemYio item) {

            }


            @Override
            public void onItemDeleteRequested(ListItemYio item) {

            }
        };
    }


    private void onItemClicked(ListItemYio item) {
        if (item.key.equals("add_my_map")) {
            onAddMyMapClicked();
            return;
        }

        AbstractLegacyUserLevel level = UserLevelFactory.getInstance().getLevel(item.key);
        level.setGameController(getGameController());
        if (level instanceof AbstractUserLevel) {
            launchUserLevel(level);
            return;
        }

        launchLegacyUserLevel(level);
    }


    private void launchLegacyUserLevel(AbstractLegacyUserLevel level) {
        LoadingParameters instance = LoadingParameters.getInstance();
        instance.loadingType = LoadingType.user_level_legacy;
        GameController gameController = getGameController();
        LegacyImportManager legacyImportManager = gameController.gameSaver.legacyImportManager;
        legacyImportManager.applyFullLevel(instance, level.getFullLevelString());
        instance.colorOffset = level.getColorOffset();
        instance.fogOfWar = level.getFogOfWar();
        instance.diplomacy = level.getDiplomacy();
        instance.ulKey = level.getKey();
        LoadingManager.getInstance().startGame(instance);

        level.onLevelLoaded(getGameController());
        getGameController().fieldManager.onUserLevelLoaded();
    }


    private void launchUserLevel(AbstractLegacyUserLevel level) {
        AbstractUserLevel userLevel = (AbstractUserLevel) level;
        GameController gameController = getGameController();
        gameController.importManager.launchGame(LoadingType.user_level, userLevel.getLevelCode(), userLevel.getKey());
        gameController.fieldManager.onUserLevelLoaded();
        gameController.editorSaveSystem.checkToApplyGoalColorFix();
    }


    public void onAddMyMapClicked() {
        Scenes.sceneArticle.create("how_add_my_map", new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneUserLevels.create();
            }
        }, 913);
    }
}
