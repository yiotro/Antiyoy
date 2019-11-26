package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.replays.RepSlot;
import yio.tro.antiyoy.gameplay.replays.ReplaySaveSystem;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.CustomizableListYio;
import yio.tro.antiyoy.menu.customizable_list.ReplayListItem;
import yio.tro.antiyoy.menu.customizable_list.TitleListItem;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class SceneReplays extends AbstractScene{

    public CustomizableListYio customizableListYio;


    public SceneReplays(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        customizableListYio = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        changeBackground();

        createBackButton();
        createList();
        loadValues();

        menuControllerYio.endMenuCreation();
    }


    public void loadValues() {
        customizableListYio.clearItems();

        TitleListItem titleListItem = new TitleListItem();
        titleListItem.setTitle(getString("replays"));
        customizableListYio.addItem(titleListItem);

        ReplaySaveSystem instance = ReplaySaveSystem.getInstance();
        ArrayList<String> keys = instance.getKeys();

        for (String key : keys) {
            RepSlot slotByKey = instance.getSlotByKey(key);

            ReplayListItem replayListItem = new ReplayListItem();
            replayListItem.setTitle(makeItemTitle(slotByKey));
            replayListItem.setDescription(makeItemDescription(slotByKey));
            replayListItem.setKey(key);
            customizableListYio.addItem(replayListItem);
        }
    }


    private String makeItemDescription(RepSlot repSlot) {
        return repSlot.date + ", " + SceneSkirmishMenu.getHumansString(repSlot.numberOfHumans);
    }


    private String makeItemTitle(RepSlot repSlot) {
        if (ReplaySaveSystem.getInstance().isSlotRenamed(repSlot.key)) {
            return ReplaySaveSystem.getInstance().getCustomSlotName(repSlot.key);
        }

        LanguagesManager instance = LanguagesManager.getInstance();

        if (repSlot.campaignMode) {
            String typeString = instance.getString("choose_game_mode_campaign");
            if (repSlot.levelIndex == -1) {
                return typeString;
            } else {
                return typeString + ", " + repSlot.levelIndex;
            }
        } else {
            return instance.getString("choose_game_mode_skirmish");
        }
    }


    private void createList() {
        initList();
        customizableListYio.appear();
    }


    private void initList() {
        if (customizableListYio != null) return;

        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setPosition(generateRectangle(0.05, 0.05, 0.9, 0.8));
        customizableListYio.setAnimation(Animation.from_center);
        menuControllerYio.addElementToScene(customizableListYio);
    }


    private void createBackButton() {
        menuControllerYio.spawnBackButton(600, Reaction.rbLoadGame);
    }


    private void changeBackground() {
        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, false, true);
    }
}
