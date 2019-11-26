package yio.tro.antiyoy.menu.behaviors;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.editor.*;
import yio.tro.antiyoy.menu.behaviors.gameplay.*;
import yio.tro.antiyoy.menu.behaviors.help.*;
import yio.tro.antiyoy.menu.behaviors.menu_creation.*;

/**
 * Created by yiotro on 05.08.14.
 */
public abstract class Reaction {

    public abstract void perform(ButtonYio buttonYio);


    protected YioGdxGame getYioGdxGame(ButtonYio buttonYio) {
        return buttonYio.menuControllerYio.yioGdxGame;
    }


    protected GameController getGameController(ButtonYio buttonYio) {
        return buttonYio.menuControllerYio.yioGdxGame.gameController;
    }


    public static final RbExit rbExit = new RbExit();
    public static final RbAboutGame rbAboutGame = new RbAboutGame();
    public static final RbMainMenu rbMainMenu = new RbMainMenu();
    public static final RbCampaignMenu rbCampaignMenu = new RbCampaignMenu();
    public static final RbStartSkirmishGame rbStartSkirmishGame = new RbStartSkirmishGame();
    public static final RbPauseMenu rbPauseMenu = new RbPauseMenu();
    public static final RbResumeGame rbResumeGame = new RbResumeGame();
    public static final RbCloseTutorialTip rbCloseTutorialTip = new RbCloseTutorialTip();
    public static RbNothing rbNothing = new RbNothing();
    public static final RbEndTurn rbEndTurn = new RbEndTurn();
    public static final RbBuildUnit rbBuildUnit = new RbBuildUnit();
    public static final RbBuildSolidObject rbBuildSolidObject = new RbBuildSolidObject();
    public static final RbUndo rbUndo = new RbUndo();
    public static final RbChooseGameModeMenu rbChooseGameModeMenu = new RbChooseGameModeMenu();
    public static final RbSkirmishMenu rbSkirmishMenu = new RbSkirmishMenu();
    public static final RbStatisticsMenu rbStatisticsMenu = new RbStatisticsMenu();
    public static final RbSaveGame rbSaveGame = new RbSaveGame();
    public static final RbLoadGame rbLoadGame = new RbLoadGame();
    public static final RbRestartGame rbRestartGame = new RbRestartGame();
    public static final RbTutorialSlay rbTutorialSlay = new RbTutorialSlay();
    public static final RbTutorialGeneric rbTutorialGeneric = new RbTutorialGeneric();
    public static final RbHelpIndex rbHelpIndex = new RbHelpIndex();
    public static final RbArticleUnits rbArticleUnits = new RbArticleUnits();
    public static final RbArticleTrees rbArticleTrees = new RbArticleTrees();
    public static final RbArticleTowers rbArticleTowers = new RbArticleTowers();
    public static final RbArticleMoney rbArticleMoney = new RbArticleMoney();
    public static final RbArticleTactics rbArticleTactics = new RbArticleTactics();
    public static final RbInputModeMove rbInputModeMove = new RbInputModeMove();
    public static final RbSettingsMenu rbSettingsMenu = new RbSettingsMenu();
    public static final RbWinGame rbWinGame = new RbWinGame();
    public static final RbShowIncomeGraph rbShowIncomeGraph = new RbShowIncomeGraph();
    public static final RbNextLevel rbNextLevel = new RbNextLevel();
    public static final RbMoreSettings rbMoreSettings = new RbMoreSettings();
    public static final RbHideEndTurnConfirm rbHideEndTurnConfirm = new RbHideEndTurnConfirm();
    public static final RbSpecialThanksMenu rbSpecialThanksMenu = new RbSpecialThanksMenu();
    public static final RbMoreSkirmishOptions rbMoreSkirmishOptions = new RbMoreSkirmishOptions();
    public static final RbSaveMoreSkirmishOptions rbSaveMoreSkirmishOptions = new RbSaveMoreSkirmishOptions();
    public static final RbBackFromSkirmish rbBackFromSkirmish = new RbBackFromSkirmish();
    public static final RbMoreCampaignOptions rbMoreCampaignOptions = new RbMoreCampaignOptions();
    public static final RbExitToCampaign rbExitToCampaign = new RbExitToCampaign();
    public static final RbLanguageMenu rbLanguageMenu = new RbLanguageMenu();
    public static final RbResetProgress rbResetProgress = new RbResetProgress();
    public static final RbConfirmReset rbConfirmReset = new RbConfirmReset();
    public static final RbRefuseEarlyGameEnd rbRefuseEarlyGameEnd = new RbRefuseEarlyGameEnd();
    public static final RbSwitchFilterOnlyLand rbSwitchFilterOnlyLand = new RbSwitchFilterOnlyLand();
    public static final RbArticleRules rbArticleRules = new RbArticleRules();
    public static final RbUnlockLevels rbUnlockLevels = new RbUnlockLevels();
    public static final RbShowFps rbShowFps = new RbShowFps();
    public static final RbShowCheatSceen rbShowCheatSceen = new RbShowCheatSceen();
    public static final RbReplaysMenu rbReplaysMenu = new RbReplaysMenu();
    public static final RbStartInstantReplay rbStartInstantReplay = new RbStartInstantReplay();

}
