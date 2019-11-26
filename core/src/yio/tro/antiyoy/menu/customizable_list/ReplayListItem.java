package yio.tro.antiyoy.menu.customizable_list;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.data_storage.LegacyImportManager;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.replays.RepSlot;
import yio.tro.antiyoy.gameplay.replays.Replay;
import yio.tro.antiyoy.gameplay.replays.ReplaySaveSystem;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.context_list_menu.LiEditable;
import yio.tro.antiyoy.menu.render.AbstractRenderCustomListItem;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class ReplayListItem extends AbstractCustomListItem implements LiEditable{

    public RenderableTextYio title;
    public RenderableTextYio description;
    public String key;


    @Override
    protected void initialize() {
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        description = new RenderableTextYio();
        description.setFont(Fonts.microFont);
        key = null;
    }


    public void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    public void setDescription(String string) {
        description.setString(string);
        description.updateMetrics();
    }


    @Override
    protected void move() {
        moveRenderableTextByDefault(title);
        moveRenderableTextByDefault(description);
    }


    @Override
    protected double getWidth() {
        return getDefaultWidth();
    }


    @Override
    protected double getHeight() {
        return 0.09f * GraphicsYio.height;
    }


    @Override
    protected void onPositionChanged() {
        title.delta.x = 0.04f * GraphicsYio.width;
        title.delta.y = (float) (getHeight() - 0.03f * GraphicsYio.width);
        description.delta.x = 0.04f * GraphicsYio.width;
        description.delta.y = title.delta.y - 0.038f * GraphicsYio.height;
    }


    @Override
    protected void onClicked() {
        ReplaySaveSystem instance = ReplaySaveSystem.getInstance();
        RepSlot slotByKey = instance.getSlotByKey(key);
        Replay replay = slotByKey.replay;

        replay.loadFromPreferences(slotByKey.key);

        LoadingParameters loadingParameters = new LoadingParameters();
        loadingParameters.loadingType = LoadingType.load_replay;
        MenuControllerYio menuControllerYio = customizableListYio.menuControllerYio;
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        LegacyImportManager legacyImportManager = gameController.gameSaver.legacyImportManager;
        legacyImportManager.applyFullLevel(loadingParameters, replay.initialLevelString);
        loadingParameters.replay = replay;
        loadingParameters.playersNumber = 0;
        loadingParameters.colorOffset = replay.tempColorOffset;
        loadingParameters.slayRules = replay.tempSlayRules;

        LoadingManager.getInstance().startGame(loadingParameters);
    }


    @Override
    protected void onLongTapped() {
        Scenes.sceneContextListMenu.create();
        Scenes.sceneContextListMenu.contextListMenuElement.setEditableItem(this);
    }


    public void setKey(String key) {
        this.key = key;
    }


    @Override
    public AbstractRenderCustomListItem getRender() {
        return MenuRender.renderReplayListItem;
    }


    @Override
    public void rename(String name) {
        ReplaySaveSystem.getInstance().applySlotRename(key, name);
        updateReplaysList();
    }


    @Override
    public void onDeleteRequested() {
        ReplaySaveSystem.getInstance().removeReplay(key);
        updateReplaysList();
    }


    private void updateReplaysList() {
        Scenes.sceneReplays.loadValues();
        Scenes.sceneReplays.customizableListYio.move();
        Scenes.sceneReplays.customizableListYio.move();
    }


    @Override
    public void onContextMenuDestroy() {
        customizableListYio.touched = false;
    }


    @Override
    public String getEditableName() {
        return title.string;
    }
}
