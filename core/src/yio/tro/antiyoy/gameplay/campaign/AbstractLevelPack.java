package yio.tro.antiyoy.gameplay.campaign;

import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.rules.GameRules;

public abstract class AbstractLevelPack {


    protected final CampaignLevelFactory campaignLevelFactory;
    protected int index;


    public AbstractLevelPack(CampaignLevelFactory campaignLevelFactory) {
        this.campaignLevelFactory = campaignLevelFactory;
    }


    boolean checkForlevelPack() {
        index = campaignLevelFactory.index;

        String levelFromPackTwo = getLevelFromPack();
        if (levelFromPackTwo.equals("-")) return false;
        if (GameRules.slayRules) return false;

        LoadingParameters instance = LoadingParameters.getInstance();
        instance.mode = LoadingParameters.MODE_CAMPAIGN_CUSTOM;
        instance.applyFullLevel(levelFromPackTwo);
        instance.campaignLevelIndex = index;
        instance.slayRules = GameRules.slayRules;
        instance.colorOffset = campaignLevelFactory.readColorOffsetFromSlider(instance.colorNumber);
        applySpecialParameters(instance);
        LoadingManager.getInstance().startGame(instance);

        onLevelLoaded();
        campaignLevelFactory.checkForHelloMessage(index);

        return true;
    }


    protected void applySpecialParameters(LoadingParameters instance) {

    }


    protected void onLevelLoaded() {

    }


    protected void setProvinceMoney(int i, int j, int money) {
        FieldController fieldController = campaignLevelFactory.gameController.fieldController;
        Hex hex = fieldController.field[i][j];
        Province provinceByHex = fieldController.getProvinceByHex(hex);
        provinceByHex.money = money;
    }


    abstract String getLevelFromPack();
}
