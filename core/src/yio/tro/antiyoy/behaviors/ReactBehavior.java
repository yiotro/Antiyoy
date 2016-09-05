package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonYio;
import yio.tro.antiyoy.GameController;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.behaviors.editor.*;
import yio.tro.antiyoy.behaviors.gameplay.*;
import yio.tro.antiyoy.behaviors.help.*;
import yio.tro.antiyoy.behaviors.menu_creation.*;

/**
 * Created by ivan on 05.08.14.
 */
public abstract class ReactBehavior {

    public abstract void reactAction(ButtonYio buttonYio);


    protected YioGdxGame getYioGdxGame(ButtonYio buttonYio) {
        return buttonYio.menuControllerYio.yioGdxGame;
    }


    protected GameController getGameController(ButtonYio buttonYio) {
        return buttonYio.menuControllerYio.yioGdxGame.gameController;
    }


    public static final RbExit rbExit = new RbExit();
    public static final RbInfo rbInfo = new RbInfo();
    public static final RbMainMenu rbMainMenu = new RbMainMenu();
    public static final RbCampaignMenu rbCampaignMenu = new RbCampaignMenu();
    public static final RbStartGame rbStartGame = new RbStartGame();
    public static final RbInGameMenu rbInGameMenu = new RbInGameMenu();
    public static final RbResumeGame rbResumeGame = new RbResumeGame();
    public static final RbCloseTutorialTip rbCloseTutorialTip = new RbCloseTutorialTip();
    public static RbNothing rbNothing = new RbNothing();
    public static final RbEndTurn rbEndTurn = new RbEndTurn();
    public static final RbBuildUnit rbBuildUnit = new RbBuildUnit();
    public static final RbBuildTower rbBuildTower = new RbBuildTower();
    public static final RbUndo rbUndo = new RbUndo();
    public static RbDebugActions rbDebugActions = new RbDebugActions();
    public static final RbChooseGameModeMenu rbChooseGameModeMenu = new RbChooseGameModeMenu();
    public static final RbSkirmishMenu rbSkirmishMenu = new RbSkirmishMenu();
    public static RbTestMenu rbTestMenu = new RbTestMenu();
    public static final RbStatisticsMenu rbStatisticsMenu = new RbStatisticsMenu();
    public static final RbSaveGame rbSaveGame = new RbSaveGame();
    public static final RbLoadGame rbLoadGame = new RbLoadGame();
    public static final RbRestartGame rbRestartGame = new RbRestartGame();
    public static final RbTutorial rbTutorial = new RbTutorial();
    public static final RbCampaignLevel rbCampaignLevel = new RbCampaignLevel();
    public static final RbHelpIndex rbHelpIndex = new RbHelpIndex();
    public static final RbArticleUnits rbArticleUnits = new RbArticleUnits();
    public static final RbArticleTrees rbArticleTrees = new RbArticleTrees();
    public static final RbArticleTowers rbArticleTowers = new RbArticleTowers();
    public static final RbArticleMoney rbArticleMoney = new RbArticleMoney();
    public static final RbArticleTactics rbArticleTactics = new RbArticleTactics();
    public static final RbEditorSlotMenu rbEditorSlotMenu = new RbEditorSlotMenu();
    public static final RbStartEditorMode rbStartEditorMode = new RbStartEditorMode();
    public static final RbEditorActionsMenu rbEditorActionsMenu = new RbEditorActionsMenu();
    public static final RbInputModeHex rbInputModeHex = new RbInputModeHex();
    public static final RbInputModeMove rbInputModeMove = new RbInputModeMove();
    public static final RbShowHexPanel rbShowHexPanel = new RbShowHexPanel();
    public static final RbHideHexPanel rbHideHexPanel = new RbHideHexPanel();
    public static final RbInputModeDelete rbInputModeDelete = new RbInputModeDelete();
    public static final RbShowObjectPanel rbShowObjectPanel = new RbShowObjectPanel();
    public static final RbHideObjectPanel rbHideObjectPanel = new RbHideObjectPanel();
    public static final RbInputModeSetObject rbInputModeSetObject = new RbInputModeSetObject();
    public static final RbShowOptionsPanel rbShowOptionsPanel = new RbShowOptionsPanel();
    public static final RbHideOptionsPanel rbHideOptionsPanel = new RbHideOptionsPanel();
    public static RbEditorChangeLevelSize rbEditorChangeLevelSize = new RbEditorChangeLevelSize();
    public static final RbEditorChangePlayersNumber rbEditorChangePlayersNumber = new RbEditorChangePlayersNumber();
    public static final RbEditorChangeDifficulty rbEditorChangeDifficulty = new RbEditorChangeDifficulty();
    public static final RbEditorExport rbEditorExport = new RbEditorExport();
    public static final RbEditorImport rbEditorImport = new RbEditorImport();
    public static final RbEditorPlay rbEditorPlay = new RbEditorPlay();
    public static final RbEditorRandomize rbEditorRandomize = new RbEditorRandomize();
    public static final RbClearLevel rbClearLevel = new RbClearLevel();
    public static final RbSettingsMenu rbSettingsMenu = new RbSettingsMenu();
    public static final RbCloseSettingsMenu rbCloseSettingsMenu = new RbCloseSettingsMenu();
    public static final RbWinGame rbWinGame = new RbWinGame();
    public static final RbLoadGameFromSlot rbLoadGameFromSlot = new RbLoadGameFromSlot();
    public static final RbSaveGameToSlot rbSaveGameToSlot = new RbSaveGameToSlot();
    public static final RbArticleComplicatedMode rbArticleComplicatedMode = new RbArticleComplicatedMode();
    public static final RbShowColorStats rbShowColorStats = new RbShowColorStats();
    public static final RbHideColorStats rbHideColorStats = new RbHideColorStats();
    public static final RbNextLevel rbNextLevel = new RbNextLevel();
    public static final RbMoreSettings rbMoreSettings = new RbMoreSettings();
    public static final RbHideEndTurnConfirm rbHideEndTurnConfirm = new RbHideEndTurnConfirm();
    public static final RbSpecialThanksMenu rbSpecialThanksMenu = new RbSpecialThanksMenu();
    public static final RbMoreSkirmishOptions rbMoreSkirmishOptions = new RbMoreSkirmishOptions();
    public static final RbSaveMoreSkirmishOptions rbSaveMoreSkirmishOptions = new RbSaveMoreSkirmishOptions();
    public static final RbBackFromSkirmish rbBackFromSkirmish = new RbBackFromSkirmish();
}
