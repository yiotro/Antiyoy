package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.LevelSelector;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneCampaignMenu extends AbstractScene{

    boolean updatedSelectorMetrics;
    LevelSelector levelSelector;


    public SceneCampaignMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        updatedSelectorMetrics = false;
        levelSelector = null;
    }


    @Override
    public void create() {
        Scenes.sceneMoreCampaignOptions.prepare();

        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);

        menuControllerYio.spawnBackButton(20, Reaction.rbChooseGameModeMenu);
        menuControllerYio.getButtonById(20).setTouchable(true);

        ButtonYio moreOptionsButton = buttonFactory.getButton(generateRectangle(0.75, 0.9, 0.2, 0.07), 21, getString("..."));
        moreOptionsButton.setReaction(Reaction.rbMoreCampaignOptions);
        moreOptionsButton.setAnimation(Animation.UP);
        moreOptionsButton.disableTouchAnimation();

        checkToCreateLevelSelector();
        levelSelector.appear();
        checkToUpdateSelectorMetrics();

        menuControllerYio.endMenuCreation();
        levelSelector.checkToReloadProgress();
    }


    private void checkToCreateLevelSelector() {
        if (levelSelector != null) return;

        levelSelector = new LevelSelector(menuControllerYio, 22);
        menuControllerYio.addElementToScene(levelSelector);
    }


    private void checkToUpdateSelectorMetrics() {
        if (updatedSelectorMetrics) return;
        if (levelSelector == null) return;
        updatedSelectorMetrics = true;

        levelSelector.updateTabsMetrics();
    }


    public void updateLevelSelector() {
        if (levelSelector == null) return;

        levelSelector.renderAllPanels();
    }
}