package yio.tro.antiyoy.gameplay.campaign;

import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.rules.GameRules;

public abstract class AbstractLevelPack {


    protected final CampaignLevelFactory campaignLevelFactory;


    public AbstractLevelPack(CampaignLevelFactory campaignLevelFactory) {
        this.campaignLevelFactory = campaignLevelFactory;
    }


    boolean checkForlevelPack() {
        int index = campaignLevelFactory.index;

        String levelFromPackTwo = getLevelFromPack(index);
        if (levelFromPackTwo.equals("-")) return false;
        if (GameRules.slayRules) return false;

        LoadingParameters instance = LoadingParameters.getInstance();
        instance.mode = LoadingParameters.MODE_CAMPAIGN_CUSTOM;
        instance.applyFullLevel(levelFromPackTwo);
        instance.campaignLevelIndex = index;
        instance.slayRules = GameRules.slayRules;
        instance.colorOffset = campaignLevelFactory.readColorOffsetFromSlider(instance.colorNumber);
        LoadingManager.getInstance().startGame(instance);

        campaignLevelFactory.checkForHelloMessage(index);

        return true;
    }


    abstract String getLevelFromPack(int index);
}
