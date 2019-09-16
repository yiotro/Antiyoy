package yio.tro.antiyoy.gameplay.loading;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.data_storage.GameSaver;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.scenes.Scenes;

import java.util.Random;

public class LoadingManager {


    public static final int MAX_LOADING_DELAY = 4000;
    private static LoadingManager instance = null;
    GameController gameController;
    YioGdxGame yioGdxGame;
    LoadingParameters parameters;
    private GameSaver gameSaver;
    private WideScreenCompensationManager compensationManager;


    public LoadingManager() {
        compensationManager = new WideScreenCompensationManager();
    }


    public static void initialize() {
        instance = null;
    }


    public static LoadingManager getInstance() {
        if (instance == null) {
            instance = new LoadingManager();
        }

        return instance;
    }


    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        yioGdxGame = gameController.yioGdxGame;

        compensationManager.setGameController(gameController);
    }


    public void startGame(LoadingParameters loadingParameters) {
        parameters = loadingParameters;

        beginCreation();

        switch (loadingParameters.loadingType) {
            case tutorial:
                createTutorial();
                break;
            case skirmish:
                createSkirmish();
                break;
            case campaign_custom_legacy:
                createLegacyCustomCampaignLevel();
                break;
            case campaign_custom:
                createCampaignCustom();
                break;
            case campaign_random:
                createRandomCampaignLevel();
                break;
            case load_game:
                createLoadedGame();
                break;
            case editor_load:
                createEditorLoaded();
                break;
            case editor_play:
                createEditorPlay();
                break;
            case editor_new:
                createEditorNew();
                break;
            case load_replay:
                createLoadedReplay();
                break;
            case user_level_legacy:
                createLegacyUserLevel();
                break;
            case user_level:
                createUserLevel();
                break;
            case editor_import:
                createEditorImport();
                break;
        }

        endCreation();
    }


    private void createCampaignCustom() {
        gameController.predictableRandom = new Random(parameters.campaignLevelIndex);
        GameRules.campaignMode = true;
        CampaignProgressManager.getInstance().setCurrentLevelIndex(parameters.campaignLevelIndex);

        gameController.fieldController.createFieldMatrix();
        gameController.decodeManager.perform(parameters.levelCode);
        GameRules.fogOfWarEnabled = GameRules.editorFog;
        GameRules.diplomacyEnabled = GameRules.editorDiplomacy;
        gameController.fieldController.detectProvinces();
        gameSaver.detectRules();
    }


    private void createEditorImport() {
        GameRules.inEditorMode = true;
        gameController.fieldController.createFieldMatrix();
        gameController.decodeManager.perform(parameters.levelCode);
    }


    private void createUserLevel() {
        GameRules.campaignMode = false;
        GameRules.userLevelMode = true;
        GameRules.ulKey = parameters.ulKey;
        gameController.fieldController.createFieldMatrix();
        gameController.decodeManager.perform(parameters.levelCode);
        GameRules.fogOfWarEnabled = GameRules.editorFog;
        GameRules.diplomacyEnabled = GameRules.editorDiplomacy;
        int colorOffset = 0;
        if (GameRules.editorChosenColor > 0) {
            colorOffset = gameController.convertSliderIndexToColorOffset(GameRules.editorChosenColor, GameRules.MAX_FRACTIONS_QUANTITY);
        }
        gameController.colorsManager.setColorOffset(colorOffset);
        parameters.editorColorFixApplied = false;
        applyEditorChosenColorFix();
        gameSaver.detectRules();
        gameController.fieldController.onUserLevelLoaded();
    }


    private void createLegacyUserLevel() {
        GameRules.campaignMode = false;
        GameRules.userLevelMode = true;
        GameRules.ulKey = parameters.ulKey;

        recreateActiveHexesFromParameter();

        gameSaver.detectRules();
    }


    private void createLoadedReplay() {
        recreateActiveHexesFromParameter();

        GameRules.replayMode = true;
        GameRules.campaignMode = (parameters.campaignLevelIndex != -1);

        gameController.checkToEnableAiOnlyMode();
        gameController.replayManager.setReplay(parameters.replay);
        gameController.replayManager.onLoadingFromSlotFinished(gameController.fieldController);
        gameController.stopAllUnitsFromJumping();
    }


    private void createEditorNew() {
        parameters.activeHexes = "";

        recreateActiveHexesFromParameter();
        gameSaver.detectRules();
        GameRules.inEditorMode = true;
        GameRules.slayRules = false;
    }


    private void createEditorLoaded() {
        GameRules.inEditorMode = true;
        recreateActiveHexesFromParameter();
        gameSaver.detectRules();
    }


    private void createEditorPlay() {
        recreateActiveHexesFromParameter();
        gameSaver.detectRules();
        applyEditorChosenColorFix();
        gameController.checkToEnableAiOnlyMode();
    }


    public void applyEditorChosenColorFix() {
        if (gameController.colorsManager.colorOffset == 0) {
            gameController.fieldController.detectProvinces();
            return;
        }
        if (parameters.editorColorFixApplied) {
            gameController.stopAllUnitsFromJumping();
            gameController.prepareCertainUnitsToMove();
            return;
        }

        parameters.editorColorFixApplied = true;
        gameController.colorsManager.applyEditorChosenColorFix();
    }


    private void createLoadedGame() {
        recreateActiveHexesFromParameter();

        gameController.turn = parameters.turn;

        if (parameters.campaignLevelIndex > 0) {
            GameRules.campaignMode = true;

            CampaignProgressManager.getInstance().setCurrentLevelIndex(parameters.campaignLevelIndex);
        }

        GameRules.userLevelMode = parameters.userLevelMode;
        GameRules.ulKey = parameters.ulKey;
        GameRules.editorColorFixApplied = parameters.editorColorFixApplied;

        gameController.checkToEnableAiOnlyMode();
    }


    private void createRandomCampaignLevel() {
        gameController.predictableRandom = new Random(parameters.campaignLevelIndex);
        GameRules.campaignMode = true;
        CampaignProgressManager.getInstance().setCurrentLevelIndex(parameters.campaignLevelIndex);

        if (parameters.slayRules) {
            generateMapForSlayRules();
        } else {
            generateMapForGenericRules();
        }
    }


    private void generateMapForSlayRules() {
        int c = 0;
        FieldController fieldController = gameController.fieldController;
        long startTime = System.currentTimeMillis();
        while (c < 6 && System.currentTimeMillis() - startTime < MAX_LOADING_DELAY) {
            fieldController.clearAnims();
            fieldController.createField();
            fieldController.generateMap(true);
            if (fieldController.getPredictionForWinner() == 0) break;
            c++;
        }
    }


    private void generateMapForGenericRules() {
        int c = 0;
        FieldController fieldController = gameController.fieldController;
        long startTime = System.currentTimeMillis();
        while (c < 6 && System.currentTimeMillis() - startTime < MAX_LOADING_DELAY) {
            fieldController.clearAnims();
            fieldController.createField();
            fieldController.generateMap(false);
            if (fieldController.areConditionsGoodForPlayer()) break;
            c++;
        }
    }


    private void createLegacyCustomCampaignLevel() {
        gameController.predictableRandom = new Random(parameters.campaignLevelIndex);
        GameRules.campaignMode = true;
        CampaignProgressManager.getInstance().setCurrentLevelIndex(parameters.campaignLevelIndex);

        recreateActiveHexesFromParameter();

        gameSaver.detectRules();
    }


    private void createTutorial() {
        recreateActiveHexesFromParameter();

        GameRules.campaignMode = true;
        CampaignProgressManager.getInstance().setCurrentLevelIndex(0);
        gameController.fieldController.giveMoneyToPlayerProvinces(90);
    }


    private void recreateActiveHexesFromParameter() {
        gameSaver.setActiveHexesString(parameters.activeHexes);
        gameSaver.beginRecreation();
    }


    private void createSkirmish() {
        gameController.fieldController.generateMap();
        gameController.checkToEnableAiOnlyMode();
    }


    public void endCreation() {
        compensationManager.setGameController(gameController);
        compensationManager.perform();

        gameController.onEndCreation();
        gameController.updateInitialParameters(parameters);
        gameController.yioGdxGame.gameView.rList.renderBackgroundCache.setUpdateAllowed(true);
        yioGdxGame.onEndCreation();

        if (GameRules.inEditorMode) {
            gameController.getLevelEditor().onEndCreation();
        }

        Scenes.sceneGameOverlay.create();

        if (GameRules.diplomacyEnabled) {
            gameController.fieldController.diplomacyManager.checkForWinConditionsMessage();
        }

        gameController.levelEditor.checkToApplyAdditionalData();
        gameController.messagesManager.checkToApplyAdditionalData();
    }


    private void beginCreation() {
        if (!DebugFlags.testMode) {
            System.out.println();
            System.out.println("Loading level...");
        }

        gameSaver = gameController.gameSaver;
        gameController.defaultValues();
        yioGdxGame.beginBackgroundChange(4, false, true);
        gameController.predictableRandom = new Random(parameters.campaignLevelIndex);
        CampaignProgressManager.getInstance().setCurrentLevelIndex(-1);

        applyLoadingParameters();
//        parameters.showInConsole();

        gameController.fieldController.createField();
        gameController.yioGdxGame.gameView.rList.renderBackgroundCache.setUpdateAllowed(false);
    }


    private void applyLoadingParameters() {
        gameController.levelSizeManager.setLevelSize(parameters.levelSize);
        gameController.setPlayersNumber(parameters.playersNumber);
        GameRules.setFractionsQuantity(parameters.fractionsQuantity);
        GameRules.setDifficulty(parameters.difficulty);
        gameController.colorsManager.setColorOffset(parameters.colorOffset);
        GameRules.setSlayRules(parameters.slayRules);
        GameRules.setFogOfWarEnabled(parameters.fogOfWar);
        GameRules.setDiplomacyEnabled(parameters.diplomacy);
    }
}
