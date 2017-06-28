package yio.tro.antiyoy.gameplay.loading;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.GameSaver;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.scenes.Scenes;

import java.util.Random;

public class LoadingManager {


    private static LoadingManager instance = null;
    GameController gameController;
    YioGdxGame yioGdxGame;
    LoadingParameters parameters;
    private GameSaver gameSaver;


    public static LoadingManager getInstance() {
        if (instance == null) {
            instance = new LoadingManager();
        }

        return instance;
    }


    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        yioGdxGame = gameController.yioGdxGame;
    }


    public void startGame(LoadingParameters loadingParameters) {
        parameters = loadingParameters;
        beginCreation();

        switch (loadingParameters.mode) {
            case LoadingParameters.MODE_TUTORIAL:
                createTutorial();
                break;
            case LoadingParameters.MODE_SKIRMISH:
                createSkirmish();
                break;
            case LoadingParameters.MODE_CAMPAIGN_CUSTOM:
                createCustomCampaignLevel();
                break;
            case LoadingParameters.MODE_CAMPAIGN_RANDOM:
                createRandomCampaignLevel();
                break;
            case LoadingParameters.MODE_LOAD_GAME:
                createLoadedGame();
                break;
            case LoadingParameters.MODE_EDITOR_LOAD:
                createEditorLoaded();
                break;
            case LoadingParameters.MODE_EDITOR_PLAY:
                createEditorPlay();
                break;
            case LoadingParameters.MODE_EDITOR_NEW:
                createEditorNew();
                break;
        }

        endCreation();
    }


    private void createEditorNew() {
        parameters.activeHexes = "";

        recreateActiveHexesFromParameter();
        gameSaver.detectRules();
        GameRules.inEditorMode = true;
        GameRules.slay_rules = false;
    }


    private void createEditorLoaded() {
        recreateActiveHexesFromParameter();
        gameSaver.detectRules();
        GameRules.inEditorMode = true;
    }


    private void createEditorPlay() {
        recreateActiveHexesFromParameter();
        gameSaver.detectRules();
    }


    private void createLoadedGame() {
        recreateActiveHexesFromParameter();

        gameController.turn = parameters.turn;

        if (parameters.campaignLevelIndex > 0) {
            yioGdxGame.menuControllerYio.loadMoreCampaignOptions();
            GameRules.campaignMode = true;

            CampaignProgressManager.getInstance().setCurrentLevelIndex(parameters.campaignLevelIndex);
        }
    }


    private void createRandomCampaignLevel() {
        gameController.predictableRandom = new Random(parameters.campaignLevelIndex);
        GameRules.campaignMode = true;

        if (parameters.slayRules) {
            generateMapForSlayRules();
        } else {
            generateMapForGenericRules();
        }
    }


    private void generateMapForSlayRules() {
        int c = 0;
        FieldController fieldController = gameController.fieldController;
        while (c < 6) {
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
        while (c < 6) {
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

        recreateActiveHexesFromParameter();

        gameSaver.detectRules();
    }


    private void createTutorial() {
        recreateActiveHexesFromParameter();

        GameRules.campaignMode = true;
    }


    private void recreateActiveHexesFromParameter() {
        gameSaver.setActiveHexesString(parameters.activeHexes);
        gameSaver.beginRecreation();
    }


    private void createSkirmish() {
        gameController.fieldController.generateMap();
    }


    private void endCreation() {
        gameController.onEndCreation();
        gameController.updateInitialParameters(parameters);
        yioGdxGame.onEndCreation();
        if (GameRules.inEditorMode) {
            gameController.getLevelEditor().onEndCreation();
        }

        Scenes.sceneGameOverlay.create();
    }


    private void beginCreation() {
        gameSaver = gameController.gameSaver;
        gameController.defaultValues();
        yioGdxGame.beginBackgroundChange(4, false, true);
        gameController.predictableRandom = new Random(parameters.campaignLevelIndex);

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
    }
}
