package yio.tro.antiyoy.gameplay.loading;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.GameSaver;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.scenes.Scenes;

import java.util.ArrayList;
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

        switch (loadingParameters.mode) {
            case LoadingMode.TUTORIAL:
                createTutorial();
                break;
            case LoadingMode.SKIRMISH:
                createSkirmish();
                break;
            case LoadingMode.CAMPAIGN_CUSTOM:
                createCustomCampaignLevel();
                break;
            case LoadingMode.CAMPAIGN_RANDOM:
                createRandomCampaignLevel();
                break;
            case LoadingMode.LOAD_GAME:
                createLoadedGame();
                break;
            case LoadingMode.EDITOR_LOAD:
                createEditorLoaded();
                break;
            case LoadingMode.EDITOR_PLAY:
                createEditorPlay();
                break;
            case LoadingMode.EDITOR_NEW:
                createEditorNew();
                break;
            case LoadingMode.LOAD_REPLAY:
                createLoadedReplay();
                break;
            case LoadingMode.USER_LEVEL:
                createUserLevel();
                break;
        }

        endCreation();
    }


    private void createUserLevel() {
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
        recreateActiveHexesFromParameter();
        gameSaver.detectRules();
        GameRules.inEditorMode = true;
    }


    private void createEditorPlay() {
        recreateActiveHexesFromParameter();
        gameSaver.detectRules();
        applyEditorChosenColorFix();
        gameController.checkToEnableAiOnlyMode();
    }


    private void applyEditorChosenColorFix() {
        if (gameController.colorIndexViewOffset == 0) return;
        if (parameters.editorColorFixApplied) return;

        parameters.editorColorFixApplied = true;
        gameController.updateRuleset();

        ArrayList<Hex> activeHexes = gameController.fieldController.activeHexes;
        for (Hex activeHex : activeHexes) {
            if (!GameRules.slayRules && activeHex.isNeutral()) continue;

            activeHex.colorIndex = gameController.getInvertedColor(activeHex.colorIndex);
        }

        gameController.fieldController.detectProvinces();
        gameController.stopAllUnitsFromJumping();
        gameController.prepareCertainUnitsToMove();
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


    private void createCustomCampaignLevel() {
        gameController.predictableRandom = new Random(parameters.campaignLevelIndex);
        GameRules.campaignMode = true;
        CampaignProgressManager.getInstance().setCurrentLevelIndex(parameters.campaignLevelIndex);

        recreateActiveHexesFromParameter();

        gameSaver.detectRules();
    }


    private void createTutorial() {
        recreateActiveHexesFromParameter();

        GameRules.campaignMode = true;
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


    private void endCreation() {
        compensationManager.setGameController(gameController);
        compensationManager.perform();

        gameController.onEndCreation();
        gameController.updateInitialParameters(parameters);
        yioGdxGame.onEndCreation();

        if (GameRules.inEditorMode) {
            gameController.getLevelEditor().onEndCreation();
        }

        Scenes.sceneGameOverlay.create();

        if (GameRules.diplomacyEnabled) {
            gameController.fieldController.diplomacyManager.checkForSingleMessage();
        }
    }


    private void beginCreation() {
        System.out.println();
        System.out.println("Loading level...");

        gameSaver = gameController.gameSaver;
        gameController.defaultValues();
        yioGdxGame.beginBackgroundChange(4, false, true);
        gameController.predictableRandom = new Random(parameters.campaignLevelIndex);
        CampaignProgressManager.getInstance().setCurrentLevelIndex(-1);

        applyLoadingParameters();
//        parameters.showInConsole();

        gameController.fieldController.createField();
    }


    private void applyLoadingParameters() {
        gameController.setLevelSize(parameters.levelSize);
        gameController.setPlayersNumber(parameters.playersNumber);
        GameRules.setColorNumber(parameters.colorNumber);
        GameRules.setDifficulty(parameters.difficulty);
        gameController.colorIndexViewOffset = parameters.colorOffset;
        GameRules.setSlayRules(parameters.slayRules);
        GameRules.setFogOfWarEnabled(parameters.fogOfWar);
        GameRules.setDiplomacyEnabled(parameters.diplomacy);
    }
}
