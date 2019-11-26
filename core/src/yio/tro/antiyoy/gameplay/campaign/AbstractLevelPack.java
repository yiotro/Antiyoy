package yio.tro.antiyoy.gameplay.campaign;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.data_storage.DecodeManager;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.rules.GameRules;

public abstract class AbstractLevelPack {


    protected final CampaignLevelFactory campaignLevelFactory;
    protected int index;


    public AbstractLevelPack(CampaignLevelFactory campaignLevelFactory) {
        this.campaignLevelFactory = campaignLevelFactory;
    }


    boolean check() {
        index = campaignLevelFactory.index;

        String levelFromPack = getLevelFromPack();
        if (levelFromPack.equals("-")) return false;
        if (GameRules.slayRules) return false;

        if (levelFromPack.contains("antiyoy_level_code")) {
            createLevel(levelFromPack);
            return true;
        }

        createLegacyLevel(levelFromPack);
        return true;
    }


    private void createLevel(String levelCode) {
        DecodeManager decodeManager = campaignLevelFactory.gameController.decodeManager;
        LoadingParameters instance = LoadingParameters.getInstance();
        decodeManager.setSource(levelCode);
        instance.loadingType = LoadingType.campaign_custom;
        instance.levelCode = levelCode;
        instance.campaignLevelIndex = index;
        instance.levelSize = decodeManager.extractLevelSize(levelCode);
        instance.slayRules = GameRules.slayRules;
        instance.fractionsQuantity = decodeManager.extractFractionsQuantity(levelCode);
        instance.colorOffset = campaignLevelFactory.readColorOffsetFromHolder(instance.fractionsQuantity);
        instance.playersNumber = 1;
        instance.difficulty = decodeManager.extractDifficulty(levelCode);
        instance.editorProvincesData = decodeManager.getSection("provinces");
        instance.editorRelationsData = decodeManager.getSection("relations");
        LoadingManager.getInstance().startGame(instance);
    }


    private void createLegacyLevel(String levelFromPack) {
        LoadingParameters instance = LoadingParameters.getInstance();
        instance.loadingType = LoadingType.campaign_custom_legacy;
        campaignLevelFactory.gameController.gameSaver.legacyImportManager.applyFullLevel(instance, levelFromPack);
        instance.campaignLevelIndex = index;
        instance.slayRules = GameRules.slayRules;
        instance.colorOffset = campaignLevelFactory.readColorOffsetFromHolder(instance.fractionsQuantity);
        applySpecialParameters(instance);
        LoadingManager.getInstance().startGame(instance);

        onLevelLoaded();
        campaignLevelFactory.checkForHelloMessage(index);
    }


    protected void applySpecialParameters(LoadingParameters instance) {

    }


    protected void onLevelLoaded() {

    }


    protected void setProvinceMoney(int i, int j, int money) {
        FieldManager fieldManager = campaignLevelFactory.gameController.fieldManager;
        Hex hex = fieldManager.field[i][j];
        Province provinceByHex = fieldManager.getProvinceByHex(hex);
        provinceByHex.money = money;
    }


    abstract String getLevelFromPack();
}
