package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.editor.*;

public class Scenes {

    public static SceneMainMenu sceneMainMenu;
    public static SceneSettingsMenu sceneSettingsMenu;
    public static SceneMoreSettingsMenu sceneMoreSettingsMenu;
    public static SceneLanguageMenu sceneLanguageMenu;
    public static SceneInfoMenu sceneInfoMenu;
    public static SceneMoreSkirmishOptions sceneMoreSkirmishOptions;
    public static SceneSkirmishMenu sceneSkirmishMenu;
    public static SceneTestMenu sceneTestMenu;
    public static SceneSaveSlots sceneSaveSlots;
    public static SceneEditorSlotsMenu sceneEditorSlotsMenu;
    public static SceneChoodeGameModeMenu sceneChoodeGameModeMenu;
    public static SceneTutorialIndex sceneTutorialIndex;
    public static SceneHelpIndex sceneHelpIndex;
    public static SceneCampaignMenu sceneCampaignMenu;
    public static SceneConfirmEndTurn sceneConfirmEndTurn;
    public static SceneConfirmReset sceneConfirmReset;
    public static SceneConfirmRestart sceneConfirmRestart;
    public static SceneEditorConfirmClear sceneEditorConfirmClear;
    public static SceneEditorActions sceneEditorActions;
    public static SceneEditorConfirmImport sceneEditorConfirmImport;
    public static SceneEditorOptionsPanel sceneEditorOptionsPanel;
    public static SceneEditorObjectPanel sceneEditorObjectPanel;
    public static SceneEditorHexPanel sceneEditorHexPanel;
    public static SceneEditorInstruments sceneEditorInstruments;
    public static SceneGameOverlay sceneGameOverlay;
    public static SceneBuildButtons sceneBuildButtons;
    public static ScenePauseMenu scenePauseMenu;
    public static SceneColorStats sceneColorStats;
    public static SceneTutorialTip sceneTutorialTip;
    public static SceneSurrenderDialog sceneSurrenderDialog;
    public static SceneNotification sceneNotification;
    public static SceneAfterGameMenu sceneAfterGameMenu;
    public static SceneStatisticsMenu sceneStatisticsMenu;
    public static SceneSingleMessage sceneSingleMessage;
    public static SceneExceptionReport sceneExceptionReport;
    public static SceneEditorAutomationPanel sceneEditorAutomationPanel;
    public static SceneEditorConfirmRandomize sceneEditorConfirmRandomize;
    public static SceneCheatScreen sceneCheatScreen;
    public static SceneEditorMoneyPanel sceneEditorMoneyPanel;
    public static SceneMoreCampaignOptions sceneMoreCampaignOptions;


    public static void createScenes(MenuControllerYio menuController) {
        sceneMainMenu = new SceneMainMenu(menuController);
        sceneSettingsMenu = new SceneSettingsMenu(menuController);
        sceneMoreSettingsMenu = new SceneMoreSettingsMenu(menuController);
        sceneLanguageMenu = new SceneLanguageMenu(menuController);
        sceneInfoMenu = new SceneInfoMenu(menuController);
        sceneMoreSkirmishOptions = new SceneMoreSkirmishOptions(menuController);
        sceneSkirmishMenu = new SceneSkirmishMenu(menuController);
        sceneTestMenu = new SceneTestMenu(menuController);
        sceneSaveSlots = new SceneSaveSlots(menuController);
        sceneEditorSlotsMenu = new SceneEditorSlotsMenu(menuController);
        sceneChoodeGameModeMenu = new SceneChoodeGameModeMenu(menuController);
        sceneTutorialIndex = new SceneTutorialIndex(menuController);
        sceneHelpIndex = new SceneHelpIndex(menuController);
        sceneCampaignMenu = new SceneCampaignMenu(menuController);
        sceneConfirmEndTurn = new SceneConfirmEndTurn(menuController);
        sceneConfirmReset = new SceneConfirmReset(menuController);
        sceneConfirmRestart = new SceneConfirmRestart(menuController);
        sceneEditorConfirmClear = new SceneEditorConfirmClear(menuController);
        sceneEditorActions = new SceneEditorActions(menuController);
        sceneEditorConfirmImport = new SceneEditorConfirmImport(menuController);
        sceneEditorOptionsPanel = new SceneEditorOptionsPanel(menuController);
        sceneEditorObjectPanel = new SceneEditorObjectPanel(menuController);
        sceneEditorHexPanel = new SceneEditorHexPanel(menuController);
        sceneEditorInstruments = new SceneEditorInstruments(menuController);
        sceneGameOverlay = new SceneGameOverlay(menuController);
        sceneBuildButtons = new SceneBuildButtons(menuController);
        scenePauseMenu = new ScenePauseMenu(menuController);
        sceneColorStats = new SceneColorStats(menuController);
        sceneTutorialTip = new SceneTutorialTip(menuController);
        sceneSurrenderDialog = new SceneSurrenderDialog(menuController);
        sceneNotification = new SceneNotification(menuController);
        sceneAfterGameMenu = new SceneAfterGameMenu(menuController);
        sceneStatisticsMenu = new SceneStatisticsMenu(menuController);
        sceneSingleMessage = new SceneSingleMessage(menuController);
        sceneExceptionReport = new SceneExceptionReport(menuController);
        sceneEditorAutomationPanel = new SceneEditorAutomationPanel(menuController);
        sceneEditorConfirmRandomize = new SceneEditorConfirmRandomize(menuController);
        sceneCheatScreen = new SceneCheatScreen(menuController);
        sceneEditorMoneyPanel = new SceneEditorMoneyPanel(menuController);
        sceneMoreCampaignOptions = new SceneMoreCampaignOptions(menuController);
    }
}
